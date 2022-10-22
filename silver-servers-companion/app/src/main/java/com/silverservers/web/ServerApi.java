package com.silverservers.web;

public class ServerApi extends Api {
    public static final String EMULATOR_LOCALHOST = "10.0.2.2";

    private static final Api.Protocol API_LOCAL_PROTOCOL = Api.Protocol.HTTP;
    private static final String API_LOCAL_HOST = EMULATOR_LOCALHOST;
    private static final int API_LOCAL_PORT = 8000;

    public ServerApi() {
        super(API_LOCAL_PROTOCOL, API_LOCAL_HOST, API_LOCAL_PORT);
    }

    public Request requestGeofenceData() {
        return request("geofence-data");
    }
}
