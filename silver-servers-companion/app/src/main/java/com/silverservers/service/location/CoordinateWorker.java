package com.silverservers.service.location;

import android.annotation.SuppressLint;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CoordinateWorker implements Runnable {
    private static final int THREAD_POOL_SIZE = 1;

    private static final int DELAY = 1;
    private static final int INTERVAL = 5;
    private static final TimeUnit COUNTER = TimeUnit.SECONDS;

    private final FusedLocationProviderClient client;
    private final CancellationTokenSource cancelSource = new CancellationTokenSource();

    public CoordinateWorker(FusedLocationProviderClient client) {
        super();
        this.client = client;
    }

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

    @SuppressLint("MissingPermission")
    private void routine() {
        System.out.println("Request coordinates: " + LocalDateTime.now().toLocalTime());

        // TODO: Request permissions
        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancelSource.getToken()
        ).addOnSuccessListener(location -> {
            if (location == null) {
                System.out.println("Coordinates not found");
            } else {
                System.out.println(
                    ""
                    + location.getLatitude()
                    + ", "
                    + location.getLongitude()
                );
            }
        });
    }
}
