package com.silverservers.app;

import android.app.Application;
import android.content.Intent;

import com.silverservers.service.location.LocationService;
import com.silverservers.service.location.LocationServiceIntent;
import com.silverservers.web.Api;
import com.silverservers.web.ServerApi;
import com.silverservers.web.TestApi;

public class App extends Application {
    public static String generateId() {
        return java.util.UUID.randomUUID().toString();
    }

    public static ServerApi getServerApi() { return SERVER_API; }
    public static TestApi getTestApi() { return TEST_API; }

    private static final ServerApi SERVER_API = new ServerApi();
    private static final TestApi TEST_API = new TestApi();

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
