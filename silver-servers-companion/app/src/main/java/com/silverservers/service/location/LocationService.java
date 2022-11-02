package com.silverservers.service.location;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.silverservers.app.App;
import com.silverservers.companion.R;
import com.silverservers.service.ServiceNotifier;

import java.time.LocalDateTime;

public class LocationService extends Service {
    private static final String NAME = "Location Service";
    private static final String DESCRIPTION = "Tracking location in background";
    private static final int ICON = R.drawable.ic_location_service;

    private ServiceNotifier notifier;

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
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        CoordinateWorker worker = new CoordinateWorker(locationClient, this::onReceiveCoordinates);

        worker.run();

        notifier = new ServiceNotifier(this, NAME, DESCRIPTION);

        startForeground(
            ServiceNotifier.FOREGROUND_NOTIFICATION_ID,
            notifier.buildNotification(this::setNotificationDefaults)
        );

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Prints location coordinates to console and updates the service
     * notification with new coordinates.
     */
    private void onReceiveCoordinates(Location location) {
        if (location == null) {
            System.out.println("Could not receive new coordinates");
            return;
        }

        String coordinateString = ""
            + location.getLatitude()
            + ", "
            + location.getLongitude();

        notifier.updateNotification(ServiceNotifier.FOREGROUND_NOTIFICATION_ID, builder -> {
            setNotificationDefaults(builder);
            builder.setContentText(
                "Latest coordinates: "
                + coordinateString
            );
        });

        App.getServerApi().requestUpdateLocation(
            LocalDateTime.now(),
            location.getLatitude(),
            location.getLongitude(),
            (response) -> {
                response.read(System.out::println);
            }
        );
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
}
