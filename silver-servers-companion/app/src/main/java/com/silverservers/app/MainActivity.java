package com.silverservers.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.silverservers.companion.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToPasswordAuthentication(View view) {
        Intent intent = new Intent(this, PasswordActivity.class);
        startActivity(intent);
    }

    public void goToOauthAuthentication(View view) {
        Intent intent = new Intent(this, OauthActivity.class);
        startActivity(intent);
    }
}