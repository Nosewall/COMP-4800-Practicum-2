package com.silverservers.permission;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class PermissionsPrompt {
    private static int currentId = -1;
    private static int getNextRequestId() {
        currentId += 1;
        return currentId;
    }

    @SuppressLint("InlinedApi")
    private static final String[] permissions = new String[] {
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
    };

    public static int getPermissions(Activity context) {
        int id = getNextRequestId();
        ActivityCompat.requestPermissions(context, permissions, id);
        return id;
    }
}
