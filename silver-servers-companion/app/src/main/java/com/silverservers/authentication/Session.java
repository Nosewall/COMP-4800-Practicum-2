package com.silverservers.authentication;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Session implements Serializable {
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

    public void writePreferences(SharedPreferences preferences) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(KEY_USER_ID, userId);
        prefEditor.putString(KEY_USER_NAME, userName);
        prefEditor.putString(KEY_SESSION_ID, sessionId);
        prefEditor.putString(KEY_KEEP_ALIVE_KEY, keepAliveKey);
        prefEditor.apply();
    }

    public static Session fromJson(JSONObject json) throws JSONException {
        return new Session(
            json.getString(KEY_USER_NAME),
            json.getString(KEY_USER_ID),
            json.getString(KEY_SESSION_ID),
            json.getString(KEY_KEEP_ALIVE_KEY)
        );

    }
}
