package com.silverservers.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {
    private final URL url;

    public Request(URL url) {
        this.url = url;
    }

    public StringResponse getStringResponse() {
        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException exception) {
            System.err.println("Unable to connect to URL: " + url);
            exception.printStackTrace(System.err);
            return null;
        }

        return new StringResponse(connection);
    }
}
