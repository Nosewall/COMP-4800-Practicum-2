package com.silverservers.authentication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.io.Serializable;

public class Session implements Serializable {
    private final static String PREFERENCES_FILE = "preferences_session";

    public final static String KEY_USER_NAME = "user_name";
    public final static String KEY_USER_ID = "user_id";
    public final static String KEY_SESSION_ID = "session_id";
    public final static String KEY_KEEP_ALIVE_KEY = "keep_alive_key";

    public final String userName;
    public final String userId;
    public final String sessionId;
    public final String keepAliveKey;

    public Session(String userName, String userId, String sessionId, String keepAliveKey) {
        this.userName = userName;
        this.userId = userId;
        this.sessionId = sessionId;
        this.keepAliveKey = keepAliveKey;
    }

    public void writePreferences(Context context) {
        SharedPreferences.Editor preferences = getPreferences(context).edit();
        preferences.putString(KEY_USER_ID, userId);
        preferences.putString(KEY_USER_NAME, userName);
        preferences.putString(KEY_SESSION_ID, sessionId);
        preferences.putString(KEY_KEEP_ALIVE_KEY, keepAliveKey);
        preferences.apply();
    }

    public static Session fromJson(JSONObject json) throws JSONException {
        return new Session(
            json.getString(KEY_USER_NAME),
            json.getString(KEY_USER_ID),
            json.getString(KEY_SESSION_ID),
            json.getString(KEY_KEEP_ALIVE_KEY)
        );
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
    }

    public static Session fromPreferences(Context context) throws InvalidObjectException {
        SharedPreferences preferences = getPreferences(context);

        String userName = preferences.getString(KEY_USER_NAME, null);
        String userId = preferences.getString(KEY_USER_ID, null);
        String sessionId = preferences.getString(KEY_SESSION_ID, null);
        String keepAliveKey = preferences.getString(KEY_KEEP_ALIVE_KEY, null);

        if (
            userName == null
            || userId == null
            || sessionId == null
            || keepAliveKey == null
        ) {
            throw new InvalidObjectException("Invalid preferences");
        }

        return new Session(
            userName,
            userId,
            sessionId,
            keepAliveKey
        );
    }
}
