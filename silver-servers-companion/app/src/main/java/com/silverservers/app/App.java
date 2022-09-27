package com.silverservers.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import com.silverservers.service.LocationService;

public class App extends Application {
    private NotificationManager notificationManager;

    public static String generateUniqueId() {
        return java.util.UUID.randomUUID().toString();
    }

    private static int currentServiceId = 1;
    public static int getNextServiceId() {
        return currentServiceId++;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = getSystemService(NotificationManager.class);
        Intent locationService = createLocationService();
        startService(locationService);
    }

    private Intent createLocationService() {
        NotificationChannel channel = createNotificationChannel(
            LocationService.DISPLAY_NAME,
            LocationService.DESCRIPTION
        );

        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra(
            LocationService.PARAMETER_CHANNEL_ID,
            channel.getId()
        );

        return intent;
    }

    private NotificationChannel createNotificationChannel(String name, String description) {
        NotificationChannel channel = new NotificationChannel(
            generateUniqueId(),
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
        return channel;
    }
}
