package com.silverservers.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class ResponseReader<T> extends Thread {
    private final Supplier<InputStream> getInputStream;
    private final Function<InputStream, T> readStream;
    private final Consumer<T> onSuccess;
    private final Consumer<Exception> onError;

    ResponseReader(Supplier<InputStream> getInputStream, Function<InputStream, T> readStream, Consumer<T> onComplete, Consumer<Exception> onError) {
        this.getInputStream = getInputStream;
        this.readStream = readStream;
        this.onSuccess = onComplete;
        this.onError = onError;
    }

    @Override
    public void run() {
        BufferedInputStream stream;
        try {
            stream = new BufferedInputStream(getInputStream.get());
        } catch (UncheckedIOException exception) {
            onError.accept(exception);
            return;
        }

        T body = readStream.apply(stream);
        onSuccess.accept(body);

        try {
            stream.close();
        } catch (IOException exception) {
            System.err.println("Unable to close response stream");
            exception.printStackTrace(System.err);
        }
    }
}
