package com.silverservers.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.silverservers.authentication.Session;
import com.silverservers.companion.R;
import com.silverservers.service.geofence.GeofenceService;
import com.silverservers.service.location.LocationService;

import java.security.Permissions;
import java.util.Arrays;
import java.util.stream.Stream;

public class DashboardActivity extends AppCompatActivity {
    private enum ServiceStatus {
        INACTIVE,
        PERMISSIONS,
        ACTIVE,
    }

    public static final String KEY_SESSION = App.generateId();

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        session = (Session)intent.getSerializableExtra(KEY_SESSION);

        TextView textViewLoggedIn = findViewById(R.id.textView_dash_loggedIn);
        textViewLoggedIn.setText(String.format(getString(R.string.dashboard_logged_in), session.userName));

        setServiceStatus(ServiceStatus.INACTIVE);

        App.getPermissionsPrompt().getPermissions(this);
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
        startLocationService();
        startGeofenceService();
    }

    private void startLocationService() { LocationService.start(this, getLocationIntent()); }
    private void startGeofenceService() { GeofenceService.start(this, getGeofenceIntent()); }

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
}