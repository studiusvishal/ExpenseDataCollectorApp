package com.bhavsar.vishal.app.expensedatacollector.activities;

import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.BASE_URL;
import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.PASSWORD;
import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.USERNAME;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_BASE_URL;
import static com.bhavsar.vishal.app.expensedatacollector.util.LoginUtility.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bhavsar.vishal.app.expensedatacollector.BuildConfig;
import com.bhavsar.vishal.app.expensedatacollector.R;
import com.bhavsar.vishal.app.expensedatacollector.util.LoginUtility;
import com.bhavsar.vishal.app.expensedatacollector.util.SharedPreferencesUtil;
import com.bhavsar.vishal.app.expensedatacollector.util.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar loginProgressBar;
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("BASE_URL", BASE_URL);

        sharedPreferencesUtil.add(KEY_BASE_URL, BASE_URL);
        loginProgressBar = findViewById(R.id.progressBar);

        // Launch main screen for development
        if (BuildConfig.DEBUG && BuildConfig.SKIP_LOGIN) {
            startMainActivity();
            return;
        }

        editTextUsername = findViewById(R.id.textUsername);
        editTextPassword = findViewById(R.id.passwordTextbox);
        final Button loginButton = findViewById(R.id.loginButton);
        editTextUsername.requestFocus();
        loginButton.setOnClickListener(this::onClickLoginButton);
    }

    private void onClickLoginButton(final View view) {
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        // TODO: Add errorEnabled
        if (username.isEmpty() || password.isEmpty()) {
            ToastUtil.showToast("Please enter all details.");
            return;
        }
        loginProgressBar.setVisibility(View.VISIBLE);
        LoginUtility.login(username, password);
    }

    private void startMainActivity() {
        login(USERNAME, PASSWORD);
    }
}
