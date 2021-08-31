package com.bhavsar.vishal.app.expensedatacollector.model;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExpenseRequest {
    private String endpoint;

    @Builder.Default private int methodType = Request.Method.POST;

    private JSONObject requestBody;
    private Response.ErrorListener errorListener;
    private Response.Listener<JSONObject> responseListener;
    private Map<String, String> headers;
}
