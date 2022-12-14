package com.silverservers.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.silverservers.authentication.Session;
import com.silverservers.companion.R;
import com.silverservers.permission.PermissionsPrompt;
import com.silverservers.service.geofence.GeofenceService;
import com.silverservers.service.location.LocationService;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DashboardActivity extends AppCompatActivity {
    public static final String KEY_SESSION = App.generateId();
    public static final String KEY_AUTHENTICATION = App.generateId();

    private Session session;
    private Intent locationIntent;
    private PendingIntent geofenceIntent;
    private Runnable biometricsLauncher;

    private enum ServiceStatus {
        INACTIVE,
        PERMISSIONS,
        ACTIVE,
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        session = (Session)intent.getSerializableExtra(KEY_SESSION);

        TextView textViewLoggedIn = findViewById(R.id.textView_dash_user);
        textViewLoggedIn.setText(String.format(getString(R.string.dashboard_logged_in), session.userName));

        setServiceStatus(ServiceStatus.INACTIVE);

        LocationService.listenUpdate(this, location -> {
            TextView textViewLocation = findViewById(R.id.textView_dash_location);
            textViewLocation.setText(location.getLatitude() + ", " + location.getLongitude());
        });

        GeofenceService.listenUpdate(this, geofenceIds -> {
            TextView textViewGeofences = findViewById(R.id.textView_dash_geo);
            textViewGeofences.setText(
                Arrays.stream(geofenceIds)
                    .map(id -> id + "\n")
                    .collect(Collectors.joining())
            );
        });

        biometricsLauncher = BiometricsActivity.getBiometricsLauncher(
            this,
            session,
            () -> System.out.println("Biometrics verification success"),
            this::logout,
            () -> System.out.println("Biometrics verification error")
        );

        App.listenAuthenticate(this, this::logout);

        PermissionsPrompt.getPermissions(this);
    }

    private void setServiceStatus(ServiceStatus status) {
        TextView textViewService = findViewById(R.id.textView_dash_serviceStatus);
        String statusText;
        int statusColor;
        switch (status) {
            default:
                statusText = getString(R.string.dashboard_service_inactive);
                statusColor = getColor(R.color.danger);
                break;
            case PERMISSIONS:
                statusText = getString(R.string.dashboard_service_permissions);
                statusColor = getColor(R.color.warning);
                break;
            case ACTIVE:
                statusText = getString(R.string.dashboard_service_active);
                statusColor = getColor(R.color.primary);
                break;
        }
        textViewService.setText(statusText);
        textViewService.setTextColor(statusColor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        boolean permissionsDenied = Arrays.stream(grantResults)
                                          .anyMatch(result -> result == PackageManager.PERMISSION_DENIED);

        if (permissionsDenied) {
            setServiceStatus(ServiceStatus.PERMISSIONS);
            return;
        }

        setServiceStatus(ServiceStatus.ACTIVE);
        startServices();
    }

    private void startServices() {
        locationIntent = LocationService.start(this);
        geofenceIntent = GeofenceService.start(this);
    }

    public void logout(View view) {
        logout();
    }

    private void logout() {
        LocationService.stop(this, locationIntent);
        if (geofenceIntent != null) {
            GeofenceService.stop(geofenceIntent);
        }

        session.clearPreferences(this);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void goToBiometrics(View view) {
        biometricsLauncher.run();
    }
}