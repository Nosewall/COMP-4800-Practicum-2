package com.silverservers.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.silverservers.companion.R;

import org.json.JSONException;
import org.json.JSONObject;

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
        App.getServerApi().requestLogin(
                publicKey.getText().toString(),
                privateKey.getText().toString(),
                (response) -> {
                    response.read((body) -> {
                        try {
                            assert body != null;
                            if (body.has("user_id")) {
                                System.out.println(body);
                                Intent intent = new Intent(this, DashboardActivity.class);
                                intent.putExtra("userId", body.get("user_id").toString());
                                intent.putExtra("username", body.get("user_name").toString());
                                intent.putExtra("sessionId", body.get("session_id").toString());
                                intent.putExtra("keepAliveKey", body.get("keep_alive_key").toString());
                                startActivity(intent);
                            } else {
                                System.out.println(body);
                                errorMsg.setText(body.get("msg").toString());
                            }
                        } catch (AssertionError e) {
                            errorMsg.setText("Error encountered with request/response.");
                            System.err.println("Response body is empty.");
                            e.printStackTrace();
                        } catch (JSONException e) {
                            errorMsg.setText("Error encountered with JSONObject.");
                            System.err.println("JSON Error encountered.");
                            e.printStackTrace();
                        }
                    });
                }
        );
    }
}