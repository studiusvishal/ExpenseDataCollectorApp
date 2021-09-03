package com.bhavsar.vishal.app.expensedatacollector.service;

import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_AUTHORIZATION;

import com.bhavsar.vishal.app.expensedatacollector.Constants;
import com.bhavsar.vishal.app.expensedatacollector.model.CategoryRecord;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRecord;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface IExpenseService {
    @POST("/saveData")
    Call<ExpenseRecord> saveData(@Header (KEY_AUTHORIZATION) final String authToken,
                                 @Body ExpenseRecord expenseRecord);

    @GET("/expense/getAllCategories")
    Call<List<CategoryRecord>> getAllCategories(@Header(KEY_AUTHORIZATION) final String authToken);
}
