package com.silverservers.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.silverservers.authentication.Session;
import com.silverservers.companion.R;

import org.json.JSONException;

import java.io.IOException;

public class PasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
    }

    @SuppressLint("SetTextI18n")
    public void login(View view) {
        TextView textViewErrorMsg = findViewById(R.id.textView_pw_err);
        EditText editTextPublicKey = findViewById(R.id.editText_pw_pubKey);
        EditText editTextPrivateKey = findViewById(R.id.editText_pw_privKey);
        if (TextUtils.isEmpty(editTextPublicKey.getText())
                || TextUtils.isEmpty(editTextPrivateKey.getText())) {
            textViewErrorMsg.setText("Both fields are required!");
        } else {
            App.getServerApi().requestLogin(
                editTextPublicKey.getText().toString(),
                editTextPrivateKey.getText().toString(),
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
                        textViewErrorMsg.setText("");
                        try {
                            Session session = Session.fromJson(successBody);

                            // Saves login/session info into persistent memory
                            session.writePreferences(getPreferences(Context.MODE_PRIVATE));

                            Intent intent = new Intent(this, DashboardActivity.class);
                            intent.putExtra(DashboardActivity.KEY_SESSION, session);
                            startActivity(intent);
                        } catch (JSONException e) {
                            textViewErrorMsg.setText("Error encountered with JSONObject.");
                            System.err.println("JSON Error encountered.");
                            e.printStackTrace(System.err);
                        }
                    }, errBody -> {
                        switch (finalStatusCode) {
                            case 400:
                                textViewErrorMsg.setText("Missing username/email and/or password.");
                                break;
                            case 401:
                                textViewErrorMsg.setText("Incorrect credentials.");
                                break;
                            case 500:
                                textViewErrorMsg.setText("Server encountered errors, try again.");
                                break;
                        }
                    });
                }
            );
        }
    }
}