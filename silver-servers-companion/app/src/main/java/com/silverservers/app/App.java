package com.silverservers.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import com.silverservers.service.location.LocationService;
import com.silverservers.service.location.LocationServiceIntent;

public class App extends Application {
    private static int currentServiceId = 1;
    private static int getNextServiceId() {
        return currentServiceId++;
    }

    public static String generateUniqueId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent locationService = createLocationService();
        startForegroundService(locationService);
    }

    private LocationServiceIntent createLocationService() {
        NotificationChannel channel = createNotificationChannel(
            LocationService.LABEL,
            LocationService.DESCRIPTION
        );

        LocationServiceIntent intent = new LocationServiceIntent(
            this,
            getNextServiceId(),
            channel.getId(),
            LocationService.Mode.Location
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
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        return channel;
    }
}
