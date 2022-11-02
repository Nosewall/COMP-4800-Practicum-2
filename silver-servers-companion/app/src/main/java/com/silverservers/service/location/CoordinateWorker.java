package com.silverservers.service.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CoordinateWorker implements Runnable {
    private static final int INTERVAL = 5000;

    private final FusedLocationProviderClient client;
    private final Consumer<Location> onUpdate;

    public CoordinateWorker(FusedLocationProviderClient client, Consumer<Location> onUpdate) {
        super();
        this.client = client;
        this.onUpdate = onUpdate;
    }

    /**
     * Runs the worker.
     *
     * Runs `routine()` periodically.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        client.requestLocationUpdates(
            buildLocationRequest(),
            onUpdate::accept,
            Looper.myLooper()
        );
    }

    private LocationRequest buildLocationRequest() {
        return new LocationRequest.Builder(INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build();
    }
}
