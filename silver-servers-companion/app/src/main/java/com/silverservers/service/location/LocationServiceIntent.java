package com.silverservers.service.location;

import android.content.Context;
import android.content.Intent;

import com.silverservers.app.App;

public class LocationServiceIntent extends Intent {
    public static String PARAMETER_MODE = App.generateId();

    public LocationServiceIntent(Context context, LocationService.Mode mode) {
        super(context, LocationService.class);
        putExtra(PARAMETER_MODE, mode.ordinal());
    }

    public static LocationService.Mode extractMode(Intent intent) {
        return LocationService.Mode.values()[
            intent.getIntExtra(LocationServiceIntent.PARAMETER_MODE, 0)
        ];
    }
}
