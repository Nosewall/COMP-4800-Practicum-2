package com.silverservers.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.silverservers.service.geofence.GeofenceService;
import com.silverservers.service.location.LocationService;
import com.silverservers.web.ServerApi;
import com.silverservers.web.TestApi;

import org.json.JSONException;

public class App extends Application {
    public static final String EMULATOR_LOCALHOST = "10.0.2.2";

    public static String generateId() {
        return java.util.UUID.randomUUID().toString();
    }

    public static ServerApi getServerApi() { return SERVER_API; }
    public static TestApi getTestApi() { return TEST_API; }

    private static final ServerApi SERVER_API = ServerApi.useLocal();
    private static final TestApi TEST_API = new TestApi();

    /**
     * Launches app initialization tasks.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        startLocationService();
        startGeofenceService();
    }

    private void startLocationService() {
        LocationService.start(this, getLocationIntent());
    }

    @SuppressLint("MissingPermission")
    private void startGeofenceService() {
        GeofenceService.start(this, getGeofenceIntent());
    }

    private Intent getLocationIntent() {
        return new Intent(
            this,
            LocationService.class
        );
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getGeofenceIntent() {
        Intent intent = new Intent(this, GeofenceService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * An example of how we can use the api interfaces
     * make a request and navigate a json response.
     */
    private static void testApiRequest() {
        App.getTestApi().requestFact().read((json) -> {
            System.out.println("Test response JSON object: ");
            System.out.println(json);
            System.out.println("Test response JSON 'fact' entry: ");
            try {
                System.out.println(json.getString("fact"));
            } catch (JSONException exception) {
                exception.printStackTrace(System.err);
            }
        }, System.err::println);
    }
}
