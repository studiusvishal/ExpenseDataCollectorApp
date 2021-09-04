package com.bhavsar.vishal.app.expensedatacollector.activities;

import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.BASE_URL;
import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.PASSWORD;
import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.USERNAME;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_BASE_URL;
import static com.bhavsar.vishal.app.expensedatacollector.util.LoginUtility.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bhavsar.vishal.app.expensedatacollector.BuildConfig;
import com.bhavsar.vishal.app.expensedatacollector.R;
import com.bhavsar.vishal.app.expensedatacollector.databinding.ActivityLoginBinding;
import com.bhavsar.vishal.app.expensedatacollector.util.LoginUtility;
import com.bhavsar.vishal.app.expensedatacollector.util.SharedPreferencesUtil;

import org.apache.commons.lang3.StringUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar loginProgressBar;
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private ActivityLoginBinding activity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_login);
        Log.d("LOGIN_ACTIVITY", "Base URL: "+ BASE_URL);
        sharedPreferencesUtil.add(KEY_BASE_URL, BASE_URL);

        // Launch main screen for development
        if (BuildConfig.DEBUG && BuildConfig.SKIP_LOGIN) {
            startMainActivity();
            return;
        }

        editTextUsername = activity.usernameEditText;
        editTextPassword = activity.passwordEditText;
        editTextUsername.requestFocus();
        activity.loginButton.setOnClickListener(this::onClickLoginButton);
    }

    private void onClickLoginButton(final View view) {
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        boolean isValid = true;
        if (StringUtils.isEmpty(username)) {
            activity.loginUsernametextInputLayout.setError("Please enter username.");
            isValid = false;
        }
        if (StringUtils.isEmpty(password)) {
            activity.loginPasswordTextInputLayout.setError("Please enter password.");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        activity.loginProgressBar.setVisibility(View.VISIBLE);
        login(username, password);
    }

    private void startMainActivity() {
        login(USERNAME, PASSWORD);
    }
}
