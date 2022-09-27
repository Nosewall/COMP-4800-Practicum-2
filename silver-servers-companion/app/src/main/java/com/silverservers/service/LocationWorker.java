package com.silverservers.service;

import android.annotation.SuppressLint;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.sql.Time;
import java.time.LocalDateTime;

public class LocationWorker extends Thread {
    private FusedLocationProviderClient locationProvider;

    public LocationWorker(FusedLocationProviderClient locationProvider) {
        this.locationProvider = locationProvider;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        while (true) {
            System.out.println("Getting next location: " + LocalDateTime.now().toLocalTime());
            // TODO: Request permissions
            CancellationTokenSource tokenSource = new CancellationTokenSource();

            locationProvider.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                tokenSource.getToken()
            ).addOnSuccessListener(location -> {
                if (location == null) {
                    System.out.println("null");
                } else {
                    System.out.println("" + location.getLatitude() + ", " + location.getLongitude());
                }
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
