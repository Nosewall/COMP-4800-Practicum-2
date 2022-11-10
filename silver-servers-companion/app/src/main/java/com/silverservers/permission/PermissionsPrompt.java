package com.silverservers.permission;
import android.Manifest;
import android.app.Activity;
import androidx.core.app.ActivityCompat;

public class PermissionsPrompt {
    private int currentId = -1;
    private int getNextRequestId() {
        currentId += 1;
        return currentId;
    }

    private static final String[] permissions = new String[] {
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    };


    public int getPermissions(Activity context) {
        int id = getNextRequestId();
        ActivityCompat.requestPermissions(context, permissions, id);
        return id;
    }
}
