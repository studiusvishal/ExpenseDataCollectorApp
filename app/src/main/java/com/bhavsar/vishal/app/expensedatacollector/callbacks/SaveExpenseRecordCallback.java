package com.bhavsar.vishal.app.expensedatacollector.callbacks;

import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseResponse;

public interface SaveExpenseRecordCallback {
    interface ResponseListener {
        void onSuccess(final ExpenseResponse expenseRecord);
    }

    interface ErrorListener {
        void onError(final Throwable t);
    }
}
