package com.silverservers.service.location;

import android.annotation.SuppressLint;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.time.LocalDateTime;

public class GeofencingWorker extends Thread {
    private final GeofencingClient client;

    public GeofencingWorker(GeofencingClient client) {
        this.client = client;
    }

    @SuppressLint("MissingPermission")
    @Override
    public final void run() {
        System.out.println("Geofencing not implemented");
    }
}
