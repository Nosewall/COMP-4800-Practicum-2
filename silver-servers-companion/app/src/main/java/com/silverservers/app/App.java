package com.silverservers.app;

import android.app.Application;
import android.content.Intent;

import com.silverservers.service.location.LocationService;
import com.silverservers.service.location.LocationServiceIntent;

public class App extends Application {
    public static String generateId() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Launches app initialization tasks.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Intent locationService = createLocationService();
        startForegroundService(locationService);
    }

    /**
     * Launches the location background service
     */
    private LocationServiceIntent createLocationService() {
        return new LocationServiceIntent(
            this,
            LocationService.Mode.Location
        );
    }


}
