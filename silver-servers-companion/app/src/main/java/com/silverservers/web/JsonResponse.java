package com.silverservers.web;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;

public class JsonResponse extends Response<JSONObject> {

    JsonResponse(HttpURLConnection connection) {
        super(connection);
    }

    @Override
    protected JSONObject decodeStream(InputStream stream) {
        String data = StringResponse.streamToString(stream);

        JSONObject jsonData;
        try {
            jsonData = new JSONObject(data);
        } catch (JSONException exception) {
            System.err.println("Invalid json format");
            System.err.println(data);
            exception.printStackTrace(System.err);
            return null;
        }

        return jsonData;
    }
}
