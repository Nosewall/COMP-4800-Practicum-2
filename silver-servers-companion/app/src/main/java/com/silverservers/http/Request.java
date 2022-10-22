package com.silverservers.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import kotlin.text.Charsets;

public class Request extends Thread {
    private final URL url;
    private final Consumer<String> onResponse;

    Request(URL url, Consumer<String> onResponse) {
        this.url = url;
        this.onResponse = onResponse;
    }

    @Override
    public void run() {
        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection)url.openConnection();
        } catch (IOException exception) {
            System.err.println("Unable to connect to URL: " + url);
            exception.printStackTrace(System.err);
            return;
        }

        try {
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);

            List<Character> buffer = new ArrayList();

            char nextCharacter;
            Predicate<Character> isEndOfString = (character) -> character != (char)(-1);
            do {
                nextCharacter = (char)reader.read();
                if (isEndOfString.test(nextCharacter)) { buffer.add(nextCharacter); }
            } while (isEndOfString.test(nextCharacter));

            onResponse.accept(buffer.stream()
                .map(String::valueOf)
                .collect(Collectors.joining()));

            buffer.clear();
            reader.close();
            stream.close();
        } catch (IOException exception) {
            System.err.println("Unable to get response for URL: " + url);
            exception.printStackTrace(System.err);
        } finally {
            connection.disconnect();
        }
    }
}
