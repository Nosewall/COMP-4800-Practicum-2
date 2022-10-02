package com.silverservers.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.silverservers.companion.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Transition to dashboard activity if user session still active
        // Else stay/prompt for auth again
    }
}