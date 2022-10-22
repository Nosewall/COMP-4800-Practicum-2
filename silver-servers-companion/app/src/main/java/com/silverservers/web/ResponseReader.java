package com.silverservers.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class ResponseReader<T> extends Thread {
    private final Supplier<InputStream> getInputStream;
    private final Function<InputStream, T> readStream;
    private final Consumer<T> onComplete;

    ResponseReader(Supplier<InputStream> getInputStream, Function<InputStream, T> readStream, Consumer<T> onComplete) {
        this.getInputStream = getInputStream;
        this.readStream = readStream;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        BufferedInputStream stream = new BufferedInputStream(getInputStream.get());
        T body = readStream.apply(stream);
        onComplete.accept(body);

        try {
            stream.close();
        } catch (IOException exception) {
            System.err.println("Unable to close response stream");
            exception.printStackTrace(System.err);
        }
    }
}
