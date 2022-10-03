package com.silverservers.service.location;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CoordinateWorker implements Runnable {
    private static final int THREAD_POOL_SIZE = 1;

    private static final int DELAY = 1;
    private static final int INTERVAL = 5;
    private static final TimeUnit COUNTER = TimeUnit.SECONDS;

    private final FusedLocationProviderClient client;
    private final Consumer<Location> onUpdate;

    private final CancellationTokenSource cancelSource = new CancellationTokenSource();

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
    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        scheduledExecutorService.scheduleAtFixedRate(
            this::routine,
            DELAY,
            INTERVAL,
            COUNTER
        );
    }

    /**
     * Gets the users current location and send the result
     * to the `onUpdate` handler.
     */
    @SuppressLint("MissingPermission")
    private void routine() {
        System.out.println("Request coordinates: " + LocalDateTime.now().toLocalTime());

        // TODO: Request permissions
        // Probably in base app rather that the service.
        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancelSource.getToken()
        ).addOnSuccessListener(onUpdate::accept);
    }
}
