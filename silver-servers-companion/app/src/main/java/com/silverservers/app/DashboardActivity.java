package com.silverservers.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.silverservers.companion.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        String userId = intent.getStringExtra(getString(R.string.user_id_key));
        String userName = intent.getStringExtra(getString(R.string.user_name_key));
        String sessionId = intent.getStringExtra(getString(R.string.session_id_key));
        String keepAliveKey = intent.getStringExtra(getString(R.string.keep_alive_key_key));

        TextView textViewLoggedIn = findViewById(R.id.textView_dash_loggedIn);
        textViewLoggedIn.setText(String.format(getString(R.string.dashboard_logged_in), userName));
    }
}