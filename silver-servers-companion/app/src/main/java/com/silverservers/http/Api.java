package com.silverservers.http;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

public class Api {
    private final String basePath;

    public Api(String basePath) {
        this.basePath = basePath;
    }

    public Request request(String requestPath, Consumer<String> onResponse) {
        String path = basePath
            + File.separatorChar
            + requestPath;

        System.out.println(path);

        URL url;
        try {
            url = new URL(path);
        } catch (MalformedURLException exception) {
            System.err.println("Invalid request URL: " + path);
            exception.printStackTrace(System.err);
            return null;
        }

        return new Request(url, onResponse);
    }
}
