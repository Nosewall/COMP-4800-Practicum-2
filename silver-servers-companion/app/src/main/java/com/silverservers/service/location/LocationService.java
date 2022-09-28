package com.silverservers.service.location;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.silverservers.app.App;
import com.silverservers.companion.R;

public class LocationService extends Service {
    public enum Mode {
        Location,
        Geofencing,
    }

    public static final String LABEL = "Location Service";
    public static final String DESCRIPTION = "Tracking location in background";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int serviceId = LocationServiceIntent.extractServiceId(intent);
        String channelId = LocationServiceIntent.extractChannelId(intent);
        Mode mode = LocationServiceIntent.extractMode(intent);

        Runnable worker;
        switch (mode) {
            case Geofencing: {
                GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
                worker = new GeofencingWorker(geofencingClient);
                break;
            }

            default:
                FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
                worker = new CoordinateWorker(locationClient);
                break;
        }

        worker.run();

        start(serviceId, channelId);

        return super.onStartCommand(intent, flags, startId);
    }

    private void start(int serviceId, String channelId) {
        startForeground(serviceId, new NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_location_service)
            .setContentTitle(LABEL)
            .setContentText(DESCRIPTION)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        );
    }
}
