package com.silverservers.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class JsonArrayResponse extends JsonResponse<JSONArray> {

    JsonArrayResponse(HttpURLConnection connection) {
        super(connection);
    }

    @Override
    protected JSONArray parse(String data) throws JSONException {
        return new JSONArray(data);
    }
}
