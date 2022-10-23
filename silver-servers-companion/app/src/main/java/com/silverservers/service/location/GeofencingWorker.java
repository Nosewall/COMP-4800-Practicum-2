package com.silverservers.service.location;

import android.annotation.SuppressLint;

import com.google.android.gms.location.GeofencingClient;

public class GeofencingWorker extends Thread {
    private final GeofencingClient client;

    public GeofencingWorker(GeofencingClient client) {
        this.client = client;
    }

    @SuppressLint("MissingPermission")
    @Override
    public final void run() { System.out.println("Geofencing not implemented"); }
}
