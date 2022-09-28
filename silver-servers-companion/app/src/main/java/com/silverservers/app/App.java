package com.silverservers.app;

import android.app.Application;
import android.content.Intent;

import com.silverservers.service.location.LocationService;
import com.silverservers.service.location.LocationServiceIntent;

public class App extends Application {
    public static String generateId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent locationService = createLocationService();
        startForegroundService(locationService);
    }

    private LocationServiceIntent createLocationService() {
        LocationServiceIntent intent = new LocationServiceIntent(
            this,
            LocationService.Mode.Location
        );

        return intent;
    }


}
