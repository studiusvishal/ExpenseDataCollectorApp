package com.bhavsar.vishal.app.expensedatacollector.model;

import com.bhavsar.vishal.app.expensedatacollector.callbacks.LoginCallback;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    private LoginRecord body;
    private LoginCallback.ResponseListener responseListener;
    private LoginCallback.ErrorListener errorListener;
}
