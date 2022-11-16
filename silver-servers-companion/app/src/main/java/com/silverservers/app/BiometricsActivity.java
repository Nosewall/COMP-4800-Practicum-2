package com.silverservers.app;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.silverservers.companion.R;

import java.util.concurrent.Executor;

public class BiometricsActivity extends AppCompatActivity {

    private TextView textView;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometrics);

        ImageView fingerprintButton = findViewById(R.id.imageView_biometrics);
        textView = findViewById(R.id.textview_biometrics);

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(BiometricsActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                textView.setText("Authentication failed for some reason");
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                textView.setText("Authentication Succeeded!");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                textView.setText("Authentication failed, imposter!");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometrics Authentication for Silver Servers")
            .setSubtitle("Login using your fingerprint")
            .setNegativeButtonText("User App Password")
            .build();

        fingerprintButton.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));
    }
}