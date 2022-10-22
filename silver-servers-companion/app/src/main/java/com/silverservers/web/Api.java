package com.silverservers.web;

import androidx.annotation.NonNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

public class Api {
    private static final String PROTOCOL_SEPARATOR = "://";
    private static final String PORT_SEPARATOR = ":";

    public enum Protocol {
        HTTP,
        HTTPS;

        @NonNull
        @Override
        public String toString() {
            return name();
        }
    }

    private final String basePath;

    Api(Protocol protocol, String host) {
        this.basePath = assembleBasePath(protocol, host);
    }

    Api(Protocol protocol, String host, int port) {
        this.basePath = assembleBasePath(protocol, host, port);
    }

    private String assembleBasePath(Protocol protocol, String host) {
        return protocol.toString()
            + PROTOCOL_SEPARATOR
            + host;
    }

    private String assembleBasePath(Protocol protocol, String host, int port) {
        return assembleBasePath(protocol, host)
            + PORT_SEPARATOR
            + port;
    }

    Request request(String requestPath) {
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

        return new Request(url);
    }
}
