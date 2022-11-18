package com.silverservers.app;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.silverservers.companion.R;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

public class OauthActivity extends AppCompatActivity {

    AuthState currentAuthenticationState;

    AuthorizationServiceConfiguration silverServersServiceConfiguration =
            new AuthorizationServiceConfiguration(
                    Uri.parse("https://example.com/authorize"), //TODO Authorize endpoint
                    Uri.parse("https://example.com/token") //TODO  Token endpoint
            );
    ClientAuthentication silverServersCLientAuthentication =
            new ClientSecretBasic("Example-Secret"); //TODO Client Secret (Static secret not recommended)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        try {
            currentAuthenticationState = readAuthState();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void authorizeWithOauth(View view){
        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
                silverServersServiceConfiguration,
                "our-client-id", //TODO client id
                ResponseTypeValues.CODE,
                Uri.parse("com.example://oauth-callback")
        ).build();

        AuthorizationService service = new AuthorizationService(this);

        Intent intent = service.getAuthorizationRequestIntent(authRequest);//TODO Redirect URI
        //ActivityResultContracts.StartActivityForResult(intent, REQUEST_CODE_AUTH); //TODO Uncommebnt with new Request-Auth-code function
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode != requestAuthCodePlaceholder()) {
            return;
        }

        AuthorizationResponse authResponse = AuthorizationResponse.fromIntent(intent);
        AuthorizationException authException = AuthorizationException.fromIntent(intent);

        currentAuthenticationState = new AuthState(authResponse, authException);

        // TODO Handle authorization response error here

        retrieveTokens(authResponse);
    }

    private int requestAuthCodePlaceholder(){
        return 69;
    }

    private void retrieveTokens(AuthorizationResponse authResponse) {
        TokenRequest tokenRequest = authResponse.createTokenExchangeRequest();

        AuthorizationService service = new AuthorizationService(this);

        service.performTokenRequest(tokenRequest, silverServersCLientAuthentication,
                new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(TokenResponse tokenResponse,
                                                        AuthorizationException tokenException) {
                        currentAuthenticationState.update(tokenResponse, tokenException);

                        // TODO Handle token response error here

                        writeAuthState(currentAuthenticationState);
                    }
                });
    }

    public AuthState readAuthState() throws JSONException {
        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        String stateJson = authPrefs.getString("stateJson", null);
        if (stateJson != null) {
            return AuthState.jsonDeserialize(stateJson);
        } else {
            return new AuthState();
        }
    }

    public void writeAuthState(AuthState state) {
        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        authPrefs.edit()
                .putString("stateJson", state.jsonSerializeString())
                .apply();
    }


    //TODO Add API Call
//    private static void executeApiCall(String accessToken) {
//        new AsyncTask<String, Void, String>() {
//            @Override
//            protected String doInBackground(String... params) {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url("https://example.com/api/...") // API URL
//                        .addHeader("Authorization",
//                                String.format("Bearer %s", params[0]))
//                        .build();
//
//                try {
//                    Response response = client.newCall(request).execute();
//                    return response.body().string();
//                } catch (Exception e) {
//                    // Handle API error here
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String response) {
//            ...
//            }
//        }.execute(accessToken);
//    }

}