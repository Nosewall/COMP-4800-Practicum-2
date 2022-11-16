package com.silverservers.service.location;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.silverservers.app.App;
import com.silverservers.app.PasswordActivity;
import com.silverservers.authentication.Session;
import com.silverservers.companion.R;
import com.silverservers.service.ServiceNotifier;
import com.silverservers.service.geofence.GeofenceService;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class LocationService extends Service {
    private static final String NAME = "Location Service";
    private static final String DESCRIPTION = "Tracking location in background";
    private static final int ICON = R.drawable.ic_location_service;

    private static final int INTERVAL = 5000;

    public static final String KEY_BROADCAST_UPDATE = App.generateId();
    public static final String KEY_BROADCAST_UPDATE_LOCATION = App.generateId();

    private ServiceNotifier notifier;
    private FusedLocationProviderClient client;

    private final LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            onReceiveCoordinates(locationResult.getLastLocation());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when the service is started.
     *
     * Sets the parameters from the given intent.
     * Initializes a service worker based on `mode` intent parameter.
     * Runs the service worker.
     * Registers the service as a foreground service with a base notification.
     */
    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        client = LocationServices.getFusedLocationProviderClient(this);

        client.requestLocationUpdates(
            buildLocationRequest(),
            callback,
            Looper.myLooper()
        );

        notifier = new ServiceNotifier(this, NAME, DESCRIPTION);

        startForeground(
            ServiceNotifier.FOREGROUND_NOTIFICATION_ID,
            notifier.buildNotification(this::setNotificationDefaults)
        );

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        client.removeLocationUpdates(callback);
    }

    private LocationRequest buildLocationRequest() {
        return new LocationRequest.Builder(INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build();
    }

    /**
     * Prints location coordinates to console and updates the service
     * notification with new coordinates.
     */
    private void onReceiveCoordinates(Location location) {
        if (location == null) {
            System.err.println("Error receiving new coordinates");
            return;
        }

        Session session;
        try {
            session = Session.fromPreferences(this);
        } catch (InvalidObjectException e) {
            e.printStackTrace(System.err);
            App.broadcastAuthenticate(this);
            return;
        }

        updateNotificationLocation(location);
        broadcastUpdate(location);

        App.getServerApi().requestUpdateLocation(
            session,
            LocalDateTime.now(),
            location.getLatitude(),
            location.getLongitude(),
            (response) -> {
                int statusCode;
                try {
                    statusCode = response.getStatusCode();
                    System.out.println("Status code: " + statusCode);
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    return;
                }

                int finalStatusCode = statusCode;
                response.read(
                    System.out::println,
                    error -> {
                        if (finalStatusCode == 401) {
                            App.broadcastAuthenticate(this);
                        } else {
                            System.err.println(
                                "Error requesting location update: " + finalStatusCode
                            );
                            error.printStackTrace(System.err);
                        }
                    }
                );
            }
        );
    }

    private void updateNotificationLocation(Location location) {
        String coordinateString = ""
            + location.getLatitude()
            + ", "
            + location.getLongitude();

        notifier.updateNotification(ServiceNotifier.FOREGROUND_NOTIFICATION_ID, builder -> {
            setNotificationDefaults(builder);
            builder.setContentText(
                "Latest coordinates: " + coordinateString
            );
        });
    }

    /**
     * Sets notification builder to default values
     * for all notifications for this service.
     *
     * Currently, just the icon.
     */
    private void setNotificationDefaults(NotificationCompat.Builder builder) {
        builder.setSmallIcon(ICON);
        builder.setSilent(true);
    }

    public static Intent start(Context context) {
        Intent intent = getIntent(context);
        context.startForegroundService(intent);
        return intent;
    }

    public static void stop(Context context, Intent intent) {
        context.stopService(intent);
    }

    private static Intent getIntent(Context context) {
        return new Intent(
            context,
            LocationService.class
        );
    }

    private void broadcastUpdate(Location location) {
        Intent intent = new Intent(KEY_BROADCAST_UPDATE);
        intent.putExtra(KEY_BROADCAST_UPDATE_LOCATION, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void listenUpdate(Context context, Consumer<Location> onUpdate) {
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location location = intent.getParcelableExtra(KEY_BROADCAST_UPDATE_LOCATION);
                onUpdate.accept(location);
            }
        }, new IntentFilter(KEY_BROADCAST_UPDATE));
    }
}
