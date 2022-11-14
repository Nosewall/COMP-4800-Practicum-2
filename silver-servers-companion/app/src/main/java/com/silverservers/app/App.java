package com.silverservers.app;

import android.app.Application;

import com.silverservers.web.ServerApi;
import com.silverservers.web.TestApi;

import org.json.JSONException;

public class App extends Application {
    public static final String EMULATOR_LOCALHOST = "10.0.2.2";

    private static final ServerApi SERVER_API = ServerApi.useLocal();
    private static final TestApi TEST_API = new TestApi();

    public static String generateId() {
        return java.util.UUID.randomUUID().toString();
    }

    public static ServerApi getServerApi() { return SERVER_API; }
    public static TestApi getTestApi() { return TEST_API; }

    /**
     * Launches app initialization tasks.
     */
    @Override
    public void onCreate() { super.onCreate(); }

    /**
     * An example of how we can use the api interfaces
     * make a request and navigate a json response.
     */
    private static void testApiRequest() {
        App.getTestApi().requestFact().read((json) -> {
            System.out.println("Test response JSON object: ");
            System.out.println(json);
            System.out.println("Test response JSON 'fact' entry: ");
            try {
                System.out.println(json.getString("fact"));
            } catch (JSONException exception) {
                exception.printStackTrace(System.err);
            }
        }, System.err::println);
    }
}
