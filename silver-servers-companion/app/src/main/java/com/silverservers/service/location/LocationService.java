package com.silverservers.service.location;

import android.app.Notification;
import android.app.NotificationManager;
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

import java.util.function.Consumer;

public class LocationService extends Service {
    public enum Mode {
        Location,
        Geofencing,
    }

    public static final String LABEL = "Location Service";
    public static final String DESCRIPTION = "Tracking location in background";

    private int serviceId;
    private String channelId;
    private Mode mode;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setParameters(Intent intent) {
        serviceId = LocationServiceIntent.extractServiceId(intent);
        channelId = LocationServiceIntent.extractChannelId(intent);
        mode = LocationServiceIntent.extractMode(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setParameters(intent);

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
            serviceId,
            buildNotification(null)
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

        updateNotification(builder -> builder.setContentText(
            "Latest coordinates: "
                + coordinateString
        ));
    }

    private Notification buildNotification(@Nullable Consumer<NotificationCompat.Builder> build) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_location_service)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(LABEL)
            .setContentText(DESCRIPTION);

        if (build != null) { build.accept(builder); }

        return builder.build();
    }

    private void updateNotification(Consumer<NotificationCompat.Builder> build) {
        getSystemService(NotificationManager.class).notify(
            serviceId,
            buildNotification(build)
        );
    }
}
