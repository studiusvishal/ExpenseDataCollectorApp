package com.bhavsar.vishal.app.expensedatacollector.model;

import com.android.volley.Response;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericRequest<T> {
    private final String endpoint;
    private final int methodType;
    private final T requestBody;
    private final Response.ErrorListener errorListener;
    private final Response.Listener<T> responseListener;
    private final Map<String, String> headers;
}
