package com.silverservers.web;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class JsonObjectResponse extends JsonResponse<JSONObject> {

    JsonObjectResponse(HttpURLConnection connection) {
        super(connection);
    }

    @Override
    protected JSONObject parse(String data) throws JSONException {
        return new JSONObject(data);
    }
}
