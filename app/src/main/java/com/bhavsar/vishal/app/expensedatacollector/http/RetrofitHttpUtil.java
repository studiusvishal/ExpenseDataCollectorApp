package com.bhavsar.vishal.app.expensedatacollector.http;

import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.BASE_URL;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_AUTHORIZATION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bhavsar.vishal.app.expensedatacollector.callbacks.GetCategoriesCallback;
import com.bhavsar.vishal.app.expensedatacollector.model.CategoryRecord;
import com.bhavsar.vishal.app.expensedatacollector.model.CategoryRequest;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRecord;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRequest;
import com.bhavsar.vishal.app.expensedatacollector.model.LoginRequest;
import com.bhavsar.vishal.app.expensedatacollector.service.IExpenseService;
import com.bhavsar.vishal.app.expensedatacollector.service.IUserService;
import com.bhavsar.vishal.app.expensedatacollector.util.SharedPreferencesUtil;
import com.bhavsar.vishal.app.expensedatacollector.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lombok.experimental.UtilityClass;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@UtilityClass
public class RetrofitHttpUtil {
    final HttpLoggingInterceptor httpLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    final OkHttpClient httpClient =
            new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();

    final Gson gson = new GsonBuilder().setLenient().create();

    final Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

    public static void login(final LoginRequest loginRequest) {
        final IUserService userService = retrofit.create(IUserService.class);
        final Call<String> stringCall = userService.login(loginRequest.getBody());
        stringCall.enqueue(getLoginCallbacks(loginRequest));
    }

    // https://newbedev.com/how-can-i-return-value-from-onresponse-of-retrofit-v2
    @NonNull
    private static Callback<String> getLoginCallbacks(final LoginRequest loginRequest) {
        return new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull final Call<String> call, @NonNull final Response<String> response) {
                if (response.code() != 200) {
                    onFailure(call, new RuntimeException("Invalid credentials!!!"));
                    return;
                }
                final Headers headers = response.headers();
                final String authHeader = headers.get(KEY_AUTHORIZATION);
                Log.d("AUTH_HEADER", authHeader);
                loginRequest.getResponseListener().onSuccess(authHeader);
            }

            @Override
            public void onFailure(@NonNull final Call<String> call, @NonNull final Throwable t) {
                Log.e("LOGIN_ERROR", Arrays.toString(t.getStackTrace()));
                loginRequest.getErrorListener().onError(t);
            }
        };
    }

    public static void saveExpenseRecord(final ExpenseRequest expenseRequest) {
        final IExpenseService expenseService = retrofit.create(IExpenseService.class);
        final Call<ExpenseRecord> call =
                expenseService.saveData(
                        expenseRequest.getHeaders().get(KEY_AUTHORIZATION),
                        expenseRequest.getRequestBody());
        call.enqueue(getAddExpenseCallbacks());
    }

    @NonNull
    private static Callback<ExpenseRecord> getAddExpenseCallbacks() {
        return new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull final Call<ExpenseRecord> call,
                    @NonNull final Response<ExpenseRecord> response) {
                final int code = response.code();
                if (code != 200) {
                    final String msg =
                            String.format(Locale.getDefault(), "Response code: %d", code);
                    onFailure(call, new RuntimeException(msg));
                    return;
                }
                ToastUtil.showToast("Data saved successfully.");
            }

            @Override
            public void onFailure(@NonNull final Call<ExpenseRecord> call, final Throwable t) {
                Log.e("ADD_EXPENSE_ERROR", Arrays.toString(t.getStackTrace()));
                ToastUtil.showToast("Unable to save expense data. " + t.getLocalizedMessage());
            }
        };
    }

    public static void getCategories(final CategoryRequest categoryRequest) {
        final IExpenseService expenseService = retrofit.create(IExpenseService.class);
        final Call<List<CategoryRecord>> call =
                expenseService.getAllCategories(
                        SharedPreferencesUtil.getInstance().getString(KEY_AUTHORIZATION, null));
        call.enqueue(
                getCategoryCallbacks(
                        categoryRequest.getResponseListener(), categoryRequest.getErrorListener()));
    }

    private static Callback<List<CategoryRecord>> getCategoryCallbacks(
            final GetCategoriesCallback.ResponseListener responseListener,
            final GetCategoriesCallback.ErrorListener errorListener) {
        return new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull final Call<List<CategoryRecord>> call,
                    @NonNull final Response<List<CategoryRecord>> response) {
                if (response.code() != 200) {
                    onFailure(call, new RuntimeException("Failed to populate expense categories. Status code: "
                                            + response.code()));
                }
                responseListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(
                    @NonNull final Call<List<CategoryRecord>> call, @NonNull final Throwable t) {
                errorListener.onError(t);
            }
        };
    }
}
