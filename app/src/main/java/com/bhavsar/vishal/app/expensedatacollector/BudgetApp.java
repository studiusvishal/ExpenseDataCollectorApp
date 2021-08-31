package com.bhavsar.vishal.app.expensedatacollector;

import android.app.Application;
import android.content.Context;

import lombok.Getter;

/** https://stackoverflow.com/questions/9445661/how-to-get-the-context-from-anywhere */
public class BudgetApp extends Application {
    @Getter private static BudgetApp instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static Context getContext() {
        return instance;
    }
}
