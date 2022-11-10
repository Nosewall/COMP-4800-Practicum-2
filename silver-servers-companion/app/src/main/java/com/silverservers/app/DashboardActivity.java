package com.silverservers.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.silverservers.authentication.Session;
import com.silverservers.companion.R;

public class DashboardActivity extends AppCompatActivity {

    public static final String KEY_SESSION = App.generateId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        Session session = (Session)intent.getSerializableExtra(KEY_SESSION);

        TextView textViewLoggedIn = findViewById(R.id.textView_dash_loggedIn);
        textViewLoggedIn.setText(String.format(getString(R.string.dashboard_logged_in), session.userName));
    }
}