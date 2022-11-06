package com.silverservers.web;

import org.json.JSONException;

import java.io.InputStream;
import java.net.HttpURLConnection;

abstract class JsonResponse<T> extends Response<T>  {

    JsonResponse(HttpURLConnection connection) {
        super(connection);
    }

    protected abstract T parse(String data) throws JSONException;

    @Override
    protected T decodeStream(InputStream stream) {
        String data = StringResponse.streamToString(stream);

        T json;
        try {
            json = parse(data);
        } catch (JSONException exception) {
            System.err.println("Invalid json format");
            System.err.println(data);
            exception.printStackTrace(System.err);
            return null;
        }

        return json;
    }
}
