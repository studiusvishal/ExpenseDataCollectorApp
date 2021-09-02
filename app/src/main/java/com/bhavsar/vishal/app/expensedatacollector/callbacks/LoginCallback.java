package com.bhavsar.vishal.app.expensedatacollector.callbacks;

public interface LoginCallback {
    interface ResponseListener {
        void onSuccess(String result);
    }

    interface ErrorListener {
        void onError(Throwable t);
    }
}
