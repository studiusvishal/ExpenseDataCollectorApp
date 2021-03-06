package com.bhavsar.vishal.app.expensedatacollector;

import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String APP_PREFERENCES = "Preferences";
    private static final String AUTHORIZATION = "Authorization";
    private static final String KEY_BASE_URL = "BASE_URL";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar loginProgressBar;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.textUsername);
        editTextPassword = findViewById(R.id.passwordTextbox);
        final Button loginButton = findViewById(R.id.loginButton);
        loginProgressBar = findViewById(R.id.progressBar);
        editTextUsername.requestFocus();

        loginButton.setOnClickListener(this::onClickLoginButton);
        sharedpreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        addToSharedPreferences(KEY_BASE_URL, BASE_URL);
    }

    private void onClickLoginButton(final View view) {
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter all details.", Toast.LENGTH_LONG).show();
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);

        login(username, password, this::onLoginSuccess);
    }

    private void login(final String username, final String password, final LoginCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String postUrl = BASE_URL + "/login";

        final String requestBody = prepareBody(username, password);
        final Response.ErrorListener errorListener = error -> Log.e("Login: ", Arrays.toString(error.getStackTrace()));
        final Response.Listener<String> responseListener = callback::onSuccess;

        // https://stackoverflow.com/questions/33573803/how-to-send-a-post-request-using-volley-with-string-body
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST,
                postUrl, responseListener, errorListener) {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(final NetworkResponse response) {
                final String parsed = Objects.requireNonNull(Objects.requireNonNull(response.headers).get(AUTHORIZATION));
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        // https://stackoverflow.com/questions/25994514/volley-timeout-error
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(final VolleyError error) {
                Toast.makeText(LoginActivity.this, "Timeout occurred while logging in!!!", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @NonNull
    private String prepareBody(final String username, final String password) {
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (final JSONException e) {
            e.printStackTrace();
        }
        return jsonBody.toString();
    }

    private void onLoginSuccess(final String result) {
        if (result.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Incorrect username or password!!!", Toast.LENGTH_LONG).show();
            loginProgressBar.setVisibility(View.GONE);
            return;
        }

        final Intent intent = new Intent(this, MainActivity.class);
        addToSharedPreferences(AUTHORIZATION, result);
        startActivity(intent);
        Toast.makeText(LoginActivity.this, "Login successful!!!", Toast.LENGTH_LONG).show();
    }

    private void addToSharedPreferences(final String key, final String value) {
        final SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }
}