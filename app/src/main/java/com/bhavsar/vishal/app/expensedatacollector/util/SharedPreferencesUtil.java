package com.bhavsar.vishal.app.expensedatacollector.util;

import static com.bhavsar.vishal.app.expensedatacollector.Constants.APP_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;

import com.bhavsar.vishal.app.expensedatacollector.BudgetApp;

import lombok.experimental.UtilityClass;

public class SharedPreferencesUtil {
    public static SharedPreferencesUtil instance;
    private static SharedPreferences sharedPreferences;

    private SharedPreferencesUtil() {
        final Context context = BudgetApp.getContext();
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesUtil();
        }
        return instance;
    }

    public void add(final String key, final String value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(final String key, final Object defaultValue) {
        return sharedPreferences.getString(key, (String) defaultValue);
    }
}
