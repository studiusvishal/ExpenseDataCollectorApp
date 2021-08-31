package com.bhavsar.vishal.app.expensedatacollector.util;

import android.content.Context;
import android.widget.Toast;

import com.bhavsar.vishal.app.expensedatacollector.BudgetApp;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ToastUtil {

    public void showToast(@NonNull final String message) {
        final Context context = BudgetApp.getContext();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
