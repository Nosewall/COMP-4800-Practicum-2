package com.silverservers.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.function.Consumer;

abstract class Response<T> {
    private final HttpURLConnection connection;

    Response(HttpURLConnection connection) {
        this.connection = connection;
    }

    public void read(Consumer<T> onComplete) {
        ResponseReader<T> reader;
        reader = new ResponseReader<>(
            this::getInputStream,
            this::decodeStream,
            (body) -> {
                onComplete.accept(body);
                connection.disconnect();
            }
        );
        reader.start();
    }

    private InputStream getInputStream() {
        InputStream stream;
        try {
            stream = connection.getInputStream();
        } catch (IOException exception) {
            System.err.println("Unable to stream response");
            exception.printStackTrace(System.err);
            return null;
        }
        return stream;
    }

    protected abstract T decodeStream(InputStream stream);
}
