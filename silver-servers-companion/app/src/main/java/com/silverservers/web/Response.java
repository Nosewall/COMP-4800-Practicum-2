package com.silverservers.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import kotlin.text.Charsets;

public class Response {
    private final HttpURLConnection connection;

    Response(HttpURLConnection connection) {
        this.connection = connection;
    }

    public void read(Consumer<String> onComplete) {
        ResponseReader<String> reader;
        reader = new ResponseReader<String>(
            this::getInputStream,
            Response::streamToString,
            onComplete
        );
        reader.start();
    }

    public void close() {
        connection.disconnect();
    }

    private InputStream getInputStream() {
        InputStream stream;
        try {
            stream = connection.getInputStream();
        } catch (IOException exception) {
            System.err.println("Unable to get response");
            exception.printStackTrace(System.err);
            return null;
        }
        return stream;
    }

    private static String streamToString(InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
        List<Character> buffer = new ArrayList<>();

        char nextCharacter;
        Predicate<Character> isEndOfString = (character) -> character == (char)(-1);

        try {
            do {
                nextCharacter = (char)reader.read();
                if (!isEndOfString.test(nextCharacter)) { buffer.add(nextCharacter); }
            } while (!isEndOfString.test(nextCharacter));
        } catch (IOException exception) {
            System.err.println("Unable to read response buffer");
            exception.printStackTrace(System.err);
        }

        try {
            reader.close();
        } catch (IOException exception) {
            System.err.println("Unable to close response stream reader");
            exception.printStackTrace(System.err);
        }

        String text = buffer.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());

        buffer.clear();

        return text;
    }
}
