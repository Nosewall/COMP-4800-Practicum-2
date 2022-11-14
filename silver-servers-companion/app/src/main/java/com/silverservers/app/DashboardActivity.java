package com.silverservers.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.silverservers.authentication.Session;
import com.silverservers.companion.R;
import com.silverservers.permission.PermissionsPrompt;
import com.silverservers.service.geofence.GeofenceService;
import com.silverservers.service.location.LocationService;

import java.util.Arrays;

public class DashboardActivity extends AppCompatActivity {
    public static final String KEY_SESSION = App.generateId();

    private enum ServiceStatus {
        INACTIVE,
        PERMISSIONS,
        ACTIVE,
    }

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
        startLocationService();
        startGeofenceService();
    }

    private void startLocationService() { LocationService.start(this); }
    private void startGeofenceService() { GeofenceService.start(this); }
}