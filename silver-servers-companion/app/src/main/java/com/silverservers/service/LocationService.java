package com.silverservers.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.silverservers.app.App;
import com.silverservers.companion.R;

public class LocationService extends Service {
    public static final String DISPLAY_NAME = "Location Service";
    public static final String DESCRIPTION = "Tracking location in background";

    public static String PARAMETER_CHANNEL_ID = App.generateUniqueId();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start(
            App.getNextServiceId(),
            intent.getStringExtra(PARAMETER_CHANNEL_ID)
        );
        return super.onStartCommand(intent, flags, startId);
    }

    private void start(int serviceId, String channelId) {
        startForeground(serviceId, new NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_location_service)
            .setContentTitle(DISPLAY_NAME)
            .setContentText(DESCRIPTION)
            .build());
    }
}
