package com.silverservers.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.util.function.Consumer;

abstract class Response<T> {
    private final HttpURLConnection connection;

    Response(HttpURLConnection connection) {
        this.connection = connection;
    }

    public void read(Consumer<T> onSuccess, Consumer<Exception> onError) {
        ResponseReader<T> reader;
        reader = new ResponseReader<>(
            this::getInputStream,
            this::decodeStream,
            (body) -> {
                onSuccess.accept(body);
                connection.disconnect();
            },
            onError
        );

        reader.start();
    }

    private InputStream getInputStream() {
        try {
            return connection.getInputStream();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    protected abstract T decodeStream(InputStream stream);
}
