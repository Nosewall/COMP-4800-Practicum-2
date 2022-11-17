package com.silverservers.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.silverservers.authentication.Session;
import com.silverservers.companion.R;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class BiometricsActivity extends AppCompatActivity {
    public static final String KEY_SESSION = App.generateId();
    public static final String KEY_RESULT_MESSAGE = App.generateId();

    public static final int RESULT_ERROR = 1;
    public static final int RESULT_ERROR_AUTHENTICATION = 2;

    private TextView textView;
    private Session session;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            textView.setText("Authentication Error:" + "\n" + errString);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Thread thread = new Thread(() -> {
                App.getServerApi().requestVerifyBiometrics(session, (response) -> {
                    int statusCode;
                    try {
                        statusCode = response.getStatusCode();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                        return;
                    }

                    int finalStatusCode = statusCode;
                    response.read(
                        message -> {
                            System.out.println(message);
                            setResult(RESULT_OK);
                            finish();
                        },
                        error -> {
                            if (finalStatusCode == 401) {
                                setResult(RESULT_ERROR_AUTHENTICATION);
                                finish();
                            } else {
                                System.err.println(
                                    "Error requesting biometric verification: " + finalStatusCode
                                );
                                error.printStackTrace(System.err);
                                setResult(RESULT_ERROR);
                                finish();
                            }
                        }
                    );
                });
            });
            thread.start();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            textView.setText("Failed authentication");
        }
    };

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometrics);

        Intent intent = getIntent();
        session = (Session)intent.getSerializableExtra(KEY_SESSION);

        ImageView fingerprintButton = findViewById(R.id.imageView_biometrics);
        textView = findViewById(R.id.textview_biometrics);

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(BiometricsActivity.this, executor, callback);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometrics Authentication for Silver Servers")
            .setSubtitle("Login using your fingerprint")
            .setNegativeButtonText("User App Password")
            .build();

        fingerprintButton.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));
    }

    public static Runnable getBiometricsLauncher(
        AppCompatActivity context,
        Session session,
        Runnable onSuccess,
        Runnable onAuthenticate,
        Runnable onError
    ) {
        ActivityResultLauncher<Intent> launcher = context.registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case RESULT_OK: onSuccess.run(); break;
                    case RESULT_ERROR_AUTHENTICATION: onAuthenticate.run(); break;
                    default: onError.run(); break;
                }
            }
        );
        Intent intent = new Intent(context, BiometricsActivity.class);
        intent.putExtra(BiometricsActivity.KEY_SESSION, session);
        return () -> launcher.launch(intent);
    }
}