package com.bhavsar.vishal.app.expensedatacollector.callbacks;

import com.bhavsar.vishal.app.expensedatacollector.model.CategoryRecord;

import java.util.List;

public interface GetCategoriesCallback {
    interface ResponseListener {
        void onSuccess(final List<CategoryRecord> expenseRecord);
    }

    interface ErrorListener {
        void onError(final Throwable t);
    }
}
