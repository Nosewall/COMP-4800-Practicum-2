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
import com.silverservers.companion.R;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setParameters(intent);

        notifier = new ServiceNotifier(this, NAME, DESCRIPTION);

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
            notifier.FOREGROUND_NOTIFICATION_ID,
            notifier.buildNotification(this::setNotificationDefaults)
        );

        return super.onStartCommand(intent, flags, startId);
    }

    private void onReceiveCoordinates(Location location) {
        String coordinateString = ""
            + location.getLatitude()
            + ", "
            + location.getLongitude();

        System.out.println(
            "Receive coordinates: "
            + coordinateString
        );

        notifier.updateNotification(notifier.FOREGROUND_NOTIFICATION_ID, builder -> {
            setNotificationDefaults(builder);
            builder.setContentText(
                "Latest coordinates: "
                + coordinateString
            );
        });
    }

    private void setNotificationDefaults(NotificationCompat.Builder builder) {
        builder.setSmallIcon(ICON);
    }
}
