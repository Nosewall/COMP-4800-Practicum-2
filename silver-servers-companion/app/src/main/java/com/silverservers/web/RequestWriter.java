package com.silverservers.web;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

class RequestWriter<T> extends Thread {
    private final T data;
    private final Supplier<OutputStream> getOutputStream;
    private final BiConsumer<OutputStream, T> writeStream;
    private final Runnable onComplete;
    private final Consumer<Exception> onError;

    RequestWriter(T data, Supplier<OutputStream> getOutputStream, BiConsumer<OutputStream, T> writeStream, Runnable onComplete, Consumer<Exception> onError) {
        this.data = data;
        this.getOutputStream = getOutputStream;
        this.writeStream = writeStream;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    @Override
    public void run() {
        OutputStream stream;
        try {
            stream = new BufferedOutputStream(getOutputStream.get());
        } catch (UncheckedIOException exception) {
            onError.accept(exception);
            return;
        }

        writeStream.accept(stream, data);
        onComplete.run();

        try {
            stream.close();
        } catch (IOException exception) {
            System.err.println("Unable to close request stream");
            exception.printStackTrace(System.err);
        }
    }
}
