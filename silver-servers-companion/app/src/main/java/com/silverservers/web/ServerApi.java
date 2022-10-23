package com.silverservers.web;

import com.silverservers.app.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.function.Consumer;

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

    public void requestUpdateLocation(LocalDateTime time, double latitude, double longitude, Consumer<StringResponse> onResponse) {
        JSONObject json = new JSONObject();
        try {
            json.put("time", time.toString());
            json.put("latitude", latitude);
            json.put("longitude", longitude);
        } catch (JSONException exception) {
            exception.printStackTrace(System.err);
        }

        request("update-location").write(json, (request) -> {
            onResponse.accept(request.getStringResponse());
        });
    }

    public JsonResponse requestGeofenceData() {
        return request("geofence-data").getJsonResponse();
    }
}
