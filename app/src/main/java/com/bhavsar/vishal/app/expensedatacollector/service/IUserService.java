package com.bhavsar.vishal.app.expensedatacollector.service;

import com.bhavsar.vishal.app.expensedatacollector.model.LoginRecord;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IUserService {
    @POST("/login")
    Call<String> login(@Body LoginRecord loginRequest);
}
