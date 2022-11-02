package com.silverservers.service.location;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.silverservers.app.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GeofenceService extends IntentService {
    public GeofenceService() {
        super(GeofenceService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            System.err.println("Error retrieving geofencing event");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER: {
                System.out.println("Geofence Enter: " + geofences);
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_EXIT: {
                System.out.println("Geofence Exit: " + geofences);
                break;
            }
        }
    }

    public static GeofencingRequest buildGeofenceRequest(List<Geofence> geofences) {
        return new GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build();
    }

    public static Geofence buildGeofence(String id, double latitude, double longitude, float radius) {
        return new Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
            .build();
    }

    public static void requestGeofences(Consumer<List<Geofence>> onResponse) {
        App.getServerApi().requestGeofenceData().read(json -> {
            List<Geofence> geofences = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject point;
                try {
                    point = json.getJSONObject(i);
                    Geofence geofence = buildGeofence(
                        point.getString("id"),
                        point.getDouble("latitude"),
                        point.getDouble("longitude"),
                        (float)point.getDouble("radius")
                    );
                    geofences.add(geofence);
                } catch (JSONException exception) {
                    exception.printStackTrace(System.err);
                }
            }
            onResponse.accept(geofences);
        });
    }
}
