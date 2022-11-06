package com.silverservers.web;

public class TestApi extends Api {
    private static final Api.Protocol PROTOCOL = Api.Protocol.HTTPS;
    private static final String HOST = "catfact.ninja";

    public TestApi() {
        super(PROTOCOL, HOST);
    }

    public JsonObjectResponse requestFact() {
        return request("fact").getJsonObjectResponse();
    }
}
