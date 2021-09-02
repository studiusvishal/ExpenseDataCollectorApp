package com.bhavsar.vishal.app.expensedatacollector.util;

import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_AUTHORIZATION;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bhavsar.vishal.app.expensedatacollector.BudgetApp;
import com.bhavsar.vishal.app.expensedatacollector.activities.MainActivity;
import com.bhavsar.vishal.app.expensedatacollector.http.RetrofitHttpUtil;
import com.bhavsar.vishal.app.expensedatacollector.model.LoginRequest;
import com.bhavsar.vishal.app.expensedatacollector.model.LoginRecord;

import java.util.Arrays;
import java.util.Objects;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LoginUtility {
    private static final SharedPreferencesUtil sharedPreferences = SharedPreferencesUtil.getInstance();

    public static void login(final String username, final String password) {
        final LoginRequest loginRequest = LoginRequest.builder()
                .body(prepareBody(username, password))
                .responseListener(LoginUtility::onSuccess)
                .errorListener(LoginUtility::onError)
                .build();
        RetrofitHttpUtil.login(loginRequest);
    }

    @NonNull
    private static LoginRecord prepareBody(final String username, final String password) {
        return LoginRecord.builder().username(username).password(password).build();
    }

    private static void onSuccess(final String result) {
        if (result.isEmpty()) {
            loginFailed(new RuntimeException("Authorization failed!!!"));
            return;
        }
        sharedPreferences.add(KEY_AUTHORIZATION, result);

        final Context context = BudgetApp.getContext();
        final Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void onError(final Throwable error) {
        Log.e("APP_LOGIN", Arrays.toString(error.getStackTrace()));
        loginFailed(error);
    }

    private static void loginFailed(final Throwable throwable) {
        final String message = throwable.getMessage();
        Objects.requireNonNull(message);
        ToastUtil.showToast(message);
    }
}
