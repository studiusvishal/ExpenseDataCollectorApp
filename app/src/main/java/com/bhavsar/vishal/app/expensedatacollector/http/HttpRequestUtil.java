package com.bhavsar.vishal.app.expensedatacollector.http;

import static com.bhavsar.vishal.app.expensedatacollector.Constants.AUTHORIZATION;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bhavsar.vishal.app.expensedatacollector.BudgetApp;
import com.bhavsar.vishal.app.expensedatacollector.BuildConfig;
import com.bhavsar.vishal.app.expensedatacollector.model.GenericRequest;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpRequestUtil {
    // https://stackoverflow.com/questions/25994514/volley-timeout-error
    public void setRetryPolicy(final Request<?> request) {
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return BuildConfig.CURRENT_TIMEOUT;
            }

            @Override
            public int getCurrentRetryCount() {
                return BuildConfig.CURRENT_RETRY_COUNT;
            }

            @Override
            public void retry(final VolleyError error) {
                Toast.makeText(BudgetApp.getContext(), "Timeout occurred. Retrying...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void sendRequest(final GenericRequest<?> genericRequest) {
        final Class<?> clazz = genericRequest.getRequestBody().getClass();
        Log.d("Generic Request", clazz.getName());
        if (clazz == JSONObject.class) {
            sendJsonRequest((GenericRequest<JSONObject>) genericRequest);
        } else if (clazz == String.class) {
            sendStringRequest((GenericRequest<String>) genericRequest);
        } else {
            throw new RuntimeException("Invalid request type: " + clazz.getName());
        }
    }

    private static void sendStringRequest(final GenericRequest<String> request) {
        final RequestQueueSingleton requestQueueSingleton = RequestQueueSingleton.getInstance(BudgetApp.getContext());
        final RequestQueue requestQueue = requestQueueSingleton.getRequestQueue();
        final String postUrl = BuildConfig.BASE_URL + request.getEndpoint();

        final StringRequest stringRequest = new StringRequest(request.getMethodType(),
                postUrl, request.getResponseListener(), request.getErrorListener()) {

            @Override
            public byte[] getBody() {
                return request.getRequestBody().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(final NetworkResponse response) {
                Objects.requireNonNull(response, "Response cannot be null");
                Objects.requireNonNull(response.headers, "Response headers cannot be null");
                final String parsed = (response.headers).get(AUTHORIZATION);
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        HttpRequestUtil.setRetryPolicy(stringRequest);
        requestQueue.add(stringRequest);
    }

    private static void sendJsonRequest(final GenericRequest<JSONObject> request) {
        final RequestQueueSingleton requestQueueSingleton = RequestQueueSingleton.getInstance(BudgetApp.getContext());
        final RequestQueue requestQueue = requestQueueSingleton.getRequestQueue();
        final String postUrl = BuildConfig.BASE_URL + request.getEndpoint();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                request.getMethodType(),
                postUrl,
                request.getRequestBody(),
                request.getResponseListener(),
                request.getErrorListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> superHeaders = super.getHeaders();
                final Map<String, String> headers = new HashMap<>(superHeaders);
                headers.putAll(request.getHeaders());
                return headers;
            }
        };
        HttpRequestUtil.setRetryPolicy(jsonObjectRequest);
        requestQueue.add(jsonObjectRequest);
    }
}
