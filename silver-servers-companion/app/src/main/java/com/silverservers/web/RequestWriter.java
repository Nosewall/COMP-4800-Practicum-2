package com.silverservers.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class RequestWriter<T> extends Thread {
    private final T data;
    private final Supplier<OutputStream> getOutputStream;
    private final BiConsumer<OutputStream, T> writeStream;
    private final Runnable onComplete;

    RequestWriter(T data, Supplier<OutputStream> getOutputStream, BiConsumer<OutputStream, T> writeStream, Runnable onComplete) {
        this.data = data;
        this.getOutputStream = getOutputStream;
        this.writeStream = writeStream;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        OutputStream stream = new BufferedOutputStream(getOutputStream.get());
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
