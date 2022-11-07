package com.silverservers.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.silverservers.companion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
    }

    public void login(View view) {
        TextView errorMsg = findViewById(R.id.textView_pw_err);
        EditText publicKey = findViewById(R.id.editText_pw_pubKey);
        EditText privateKey = findViewById(R.id.editText_pw_privKey);
        if (TextUtils.isEmpty(publicKey.getText())
                || TextUtils.isEmpty(privateKey.getText())) {
            errorMsg.setText("Both fields are required!");
        } else {
            App.getServerApi().requestLogin(
                publicKey.getText().toString(),
                privateKey.getText().toString(),
                (response) -> {
                    int statusCode = 0;
                    try {
                        statusCode = response.getStatusCode();
                        System.out.println("Status code: " + statusCode);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }

                    int finalStatusCode = statusCode;
                    response.read(successBody -> {
                        errorMsg.setText("");
                        try {
                            System.out.println(successBody);
                            String userId = getString(R.string.user_id_key);
                            String userName = getString(R.string.user_name_key);
                            String sessionId = getString(R.string.session_id_key);
                            String keepAliveKey = getString(R.string.keep_alive_key_key);

                            // Saves login/session info into persistent memory
                            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefEditor = prefs.edit();

                            prefEditor.putString(userId, successBody.get(userId).toString());
                            prefEditor.putString(userName, successBody.get(userName).toString());
                            prefEditor.putString(sessionId, successBody.get(sessionId).toString());
                            prefEditor.putString(keepAliveKey, successBody.get(keepAliveKey).toString());

                            prefEditor.apply();

                            Intent intent = new Intent(this, DashboardActivity.class);
                            intent.putExtra(userId, successBody.get(userId).toString());
                            intent.putExtra(userName, successBody.get(userName).toString());
                            intent.putExtra(sessionId, successBody.get(sessionId).toString());
                            intent.putExtra(keepAliveKey, successBody.get(keepAliveKey).toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            errorMsg.setText("Error encountered with JSONObject.");
                            System.err.println("JSON Error encountered.");
                            e.printStackTrace(System.err);
                        }
                    }, errBody -> {
                        switch (finalStatusCode) {
                            case 400:
                                errorMsg.setText("Missing username/email and/or password.");
                                break;
                            case 401:
                                errorMsg.setText("Incorrect credentials.");
                                break;
                            case 500:
                                errorMsg.setText("Server encountered errors, try again.");
                                break;
                        }
                    });
                }
            );
        }
    }
}