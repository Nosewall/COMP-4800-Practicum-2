package com.silverservers.service.location;

import android.content.Context;
import android.content.Intent;

import com.silverservers.app.App;

public class LocationServiceIntent extends Intent {
    public static String PARAMETER_SERVICE_ID = App.generateUniqueId();
    public static String PARAMETER_CHANNEL_ID = App.generateUniqueId();
    public static String PARAMETER_MODE = App.generateUniqueId();

    public LocationServiceIntent(
        Context context,
        int serviceId,
        String channelId,
        LocationService.Mode mode
    ) {
        super(context, LocationService.class);

        putExtra(
            PARAMETER_SERVICE_ID,
            serviceId
        );

        putExtra(
            PARAMETER_CHANNEL_ID,
            channelId
        );

        putExtra(
            PARAMETER_MODE,
            mode
        );
    }

    public static int extractServiceId(Intent intent) {
        return intent.getIntExtra(LocationServiceIntent.PARAMETER_SERVICE_ID, 0);
    }

    public static String extractChannelId(Intent intent) {
        return intent.getStringExtra(LocationServiceIntent.PARAMETER_CHANNEL_ID);
    }

    public static LocationService.Mode extractMode(Intent intent) {
        return LocationService.Mode.values()[
            intent.getIntExtra(LocationServiceIntent.PARAMETER_MODE, 0)
        ];
    }
}
