package com.silverservers.service.geofence;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.silverservers.app.App;
import com.silverservers.authentication.Session;
import com.silverservers.service.location.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GeofenceService extends IntentService {
    public static final String KEY_BROADCAST_UPDATE = App.generateId();
    public static final String KEY_BROADCAST_UPDATE_GEOFENCES = App.generateId();

    public GeofenceService() {
        super(GeofenceService.class.getName());
    }

    private List<String> geofences = new ArrayList<>();

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            System.err.println("Error retrieving geofencing event");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

        if (geofences == null) {
            System.err.println("Error retrieving triggered geofences");
            return;
        }

        Session session;
        try {
            session = Session.fromPreferences(this);
        } catch (InvalidObjectException e) {
            e.printStackTrace(System.err);
            return;
        }

        for (Geofence geofence : geofences) {
            String id = geofence.getRequestId();

            switch (geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER: {
                    onGeofenceEnter(session, id);
                    break;
                }
                case Geofence.GEOFENCE_TRANSITION_EXIT: {
                    onGeofenceExit(session, id);
                    break;
                }
            }
        }

        broadcastUpdate();
    }

    private void onGeofenceEnter(Session session, String id) {
        geofences.add(id);
        App.getServerApi().requestGeofenceEnter(
            session,
            id,
            (response) -> response.read(System.out::println, System.err::println)
        );
    }

    private void onGeofenceExit(Session session, String id) {
        geofences.remove(id);
        App.getServerApi().requestGeofenceExit(
            session,
            id,
            (response) -> response.read(System.out::println, System.err::println)
        );
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
        App.getServerApi().requestGeofenceData(response -> response.read(json -> {
            List<Geofence> geofences = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject geofencePoint;
                try {
                    geofencePoint = json.getJSONObject(i);
                    Geofence geofence = buildGeofence(
                        geofencePoint.getString("id"),
                        geofencePoint.getDouble("latitude"),
                        geofencePoint.getDouble("longitude"),
                        (float)geofencePoint.getDouble("radius")
                    );
                    geofences.add(geofence);
                } catch (JSONException exception) {
                    exception.printStackTrace(System.err);
                }
            }
            onResponse.accept(geofences);
        }, System.err::println));
    }

    @SuppressLint("MissingPermission")
    public static void start(Context context) {
        GeofenceService.requestGeofences(geofences -> {
            GeofencingClient client = LocationServices.getGeofencingClient(context);
            GeofencingRequest request = GeofenceService.buildGeofenceRequest(geofences);
            client.addGeofences(
                request,
                GeofenceService.getIntent(context)
            ).addOnSuccessListener(success -> {
                System.out.println("Geofencing initialized");
                System.out.println(request.getGeofences());
            }).addOnFailureListener(failure -> {
                System.out.println("Geofencing error");
                failure.printStackTrace(System.err);
            });
        });
    }

    private static PendingIntent getIntent(Context context) {
        Intent intent = new Intent(context, GeofenceService.class);
        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
    }

    private void broadcastUpdate() {
        Intent intent = new Intent(KEY_BROADCAST_UPDATE);
        intent.putExtra(KEY_BROADCAST_UPDATE_GEOFENCES, geofences.toArray(new String[0]));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void listenUpdate(Context context, Consumer<String[]> onUpdate) {
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String[] geofenceIds = intent.getStringArrayExtra(KEY_BROADCAST_UPDATE_GEOFENCES);
                onUpdate.accept(geofenceIds);
            }
        }, new IntentFilter(KEY_BROADCAST_UPDATE));
    }
}
