package com.silverservers.web;

import com.silverservers.app.App;

public class ServerApi extends Api {
    private static final Api.Protocol API_LOCAL_PROTOCOL = Api.Protocol.HTTP;
    private static final String API_LOCAL_HOST = App.EMULATOR_LOCALHOST;
    private static final int API_LOCAL_PORT = 8000;

    private static final Api.Protocol API_REMOTE_PROTOCOL = Api.Protocol.HTTPS;
    private static final String API_REMOTE_HOST = "ourHostedServerUrlInTheFuture.com";

    private ServerApi(Protocol protocol, String host, int port) {
        super(protocol, host, port);
    }

    private ServerApi(Protocol protocol, String host) {
        super(protocol, host);
    }

    public static ServerApi useLocal() {
        return new ServerApi(API_LOCAL_PROTOCOL, API_LOCAL_HOST, API_LOCAL_PORT);
    }

    public static ServerApi useRemote() {
        return new ServerApi(API_REMOTE_PROTOCOL, API_REMOTE_HOST);
    }

    public JsonResponse requestGeofenceData() {
        return request("geofence-data").getJsonResponse();
    }
}
