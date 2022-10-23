package com.silverservers.web;

import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

import kotlin.text.Charsets;

public class Request {
    private final HttpURLConnection connection;

    public Request(URL url) {
        this.connection = openConnection(url);
    }

    private HttpURLConnection openConnection(URL url) {
        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException exception) {
            System.err.println("Unable to connect to URL: " + url);
            exception.printStackTrace(System.err);
            return null;
        }

        return connection;
    }

    public void write(String body, Consumer<Request> onComplete) {
        connection.setDoOutput(true);
        connection.setRequestProperty("content-type", "text/plain");

        RequestWriter<String> writer = new RequestWriter<>(
            body,
            this::getOutputStream,
            this::encodeStream,
            () -> onComplete.accept(this)
        );
        writer.start();
    }

    public void write(JSONObject body, Consumer<Request> onComplete) {
        connection.setDoOutput(true);
        connection.setRequestProperty("content-type", "application/json");

        RequestWriter<String> writer = new RequestWriter<>(
            body.toString(),
            this::getOutputStream,
            this::encodeStream,
            () -> onComplete.accept(this)
        );
        writer.start();
    }

    private OutputStream getOutputStream() {
        OutputStream stream;
        try {
            stream = connection.getOutputStream();
        } catch (IOException exception) {
            System.err.println("Unable to stream request");
            exception.printStackTrace(System.err);
            return null;
        }
        return stream;
    }


    private void encodeStream(OutputStream stream, String data) {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, Charsets.UTF_8));

        try {
            writer.write(data);
        } catch (IOException exception) {
            System.err.println("Unable to write request");
            exception.printStackTrace(System.err);
        }

        try {
            writer.close();
        } catch (IOException exception) {
            System.err.println("Unable to close request stream writer");
            exception.printStackTrace(System.err);
        }
    }

    public StringResponse getStringResponse() {
        return new StringResponse(connection);
    }

    public JsonResponse getJsonResponse() {
        return new JsonResponse(connection);
    }
}
