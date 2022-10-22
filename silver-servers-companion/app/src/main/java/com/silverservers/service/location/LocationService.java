package com.silverservers.service.location;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.silverservers.app.App;
import com.silverservers.companion.R;
import com.silverservers.web.Api;
import com.silverservers.service.ServiceNotifier;

public class LocationService extends Service {
    public enum Mode {
        Location,
        Geofencing,
    }

    private static final String NAME = "Location Service";
    private static final String DESCRIPTION = "Tracking location in background";
    private static final int ICON = R.drawable.ic_location_service;

    private Mode mode;
    private ServiceNotifier notifier;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setParameters(Intent intent) {
        mode = LocationServiceIntent.extractMode(intent);
    }

    /**
     * Called when the service is started.
     *
     * Sets the parameters from the given intent.
     * Initializes a service worker based on `mode` intent parameter.
     * Runs the service worker.
     * Registers the service as a foreground service with a base notification.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setParameters(intent);

        notifier = new ServiceNotifier(this, NAME, DESCRIPTION);

        // May create two services later, or implement this in a different way,
        // However this should suffice for rudimentary testing.
        Runnable worker;
        switch (mode) {
            case Geofencing: {
                GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
                worker = new GeofencingWorker(geofencingClient);
                break;
            }

            default: {
                FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
                worker = new CoordinateWorker(locationClient, this::onReceiveCoordinates);
                break;
            }
        }
        worker.run();

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

        System.out.println(
            "Receive coordinates: "
            + coordinateString
        );

        notifier.updateNotification(ServiceNotifier.FOREGROUND_NOTIFICATION_ID, builder -> {
            setNotificationDefaults(builder);
            builder.setContentText(
                "Latest coordinates: "
                + coordinateString
            );
        });

        App.getServerApi()
            .requestGeofenceData()
            .getResponse()
            .read(System.out::println);
    }

    /**
     * Sets notification builder to default values
     * for all notifications for this service.
     *
     * Currently, just the icon.
     */
    private void setNotificationDefaults(NotificationCompat.Builder builder) {
        builder.setSmallIcon(ICON);
    }
}
