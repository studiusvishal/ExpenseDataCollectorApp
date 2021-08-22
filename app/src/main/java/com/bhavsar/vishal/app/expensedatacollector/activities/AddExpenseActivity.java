package com.bhavsar.vishal.app.expensedatacollector.activities;

import static com.bhavsar.vishal.app.expensedatacollector.BuildConfig.BASE_URL;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.APP_PREFERENCES;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.AUTHORIZATION;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bhavsar.vishal.app.expensedatacollector.DatePickerFragment;
import com.bhavsar.vishal.app.expensedatacollector.R;
import com.bhavsar.vishal.app.expensedatacollector.callbacks.SaveRecordCallback;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRecord;
import com.bhavsar.vishal.app.expensedatacollector.util.DateUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.SneakyThrows;

public class AddExpenseActivity extends AppCompatActivity {

    private static final List<String> itemList = Arrays.asList("Select",
            "Apartment Rent",
            "Credit card",
            "Fuel");
    private EditText expenseDateEditText;
    private Spinner categorySpinner;
    private EditText expenseAmountEditText;
    private EditText expenseDescriptionEditText;
    private SharedPreferences sharedpreferences;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        sharedpreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        // https://stackoverflow.com/questions/4989817/set-the-textsize-to-a-text-in-spinner-in-android-programatically
        // https://stackoverflow.com/questions/9476665/how-to-change-spinner-text-size-and-text-color
        // TODO: Get from DB
        categorySpinner = findViewById(R.id.spinnerExpenseCategory);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, itemList);
        categorySpinner.setAdapter(arrayAdapter);

        expenseDateEditText = findViewById(R.id.editTextDate);
        final ImageButton selectDateButton = findViewById(R.id.selectDateButton);
        expenseDateEditText.setText(DateUtility.getCurrentDate());
        expenseAmountEditText = findViewById(R.id.editTextExpenseAmount);
        expenseDescriptionEditText = findViewById(R.id.editTextDescription);
        final Button resetButton = findViewById(R.id.buttonReset);
        final Button saveButton = findViewById(R.id.buttonOk);
        progressBar = findViewById(R.id.saveExpenseRecordProgressBar);

        // attach listeners
        selectDateButton.setOnClickListener(this::onClickSelectDateButton);
        resetButton.setOnClickListener(this::onClickResetButton);
        saveButton.setOnClickListener(this::onClickSaveButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onClickSelectDateButton(final View view) {
        final DialogFragment datePickerFragment = new DatePickerFragment(expenseDateEditText);
        datePickerFragment.show(getSupportFragmentManager(), "date picker");
    }

    public void onClickResetButton(final View view) {
        expenseDateEditText.setText(DateUtility.getCurrentDate());
        categorySpinner.setSelection(0);
        expenseAmountEditText.setText(R.string.default_expense_amount);
        expenseDescriptionEditText.setText("");
    }

    public void onClickSaveButton(final View view) {
        final Date expenseDate = DateUtility.parseDate(expenseDateEditText.getText().toString());
        final String expenseCategory = categorySpinner.getSelectedItem().toString();
        final double expenseAmount = Double.parseDouble(expenseAmountEditText.getText().toString());
        final String expenseDescription = expenseDescriptionEditText.getText().toString();
        final ExpenseRecord expenseRecord = ExpenseRecord.builder()
                .expenseDate(expenseDate)
                .category(expenseCategory)
                .expenseAmount(expenseAmount)
                .description(expenseDescription)
                .id(generateRandomId())
                .build();
        saveRecord(expenseRecord, this::onSaveSuccess);
        // stringSave(expenseRecord, this::onStringSuccess);
    }

    private void saveRecord(final ExpenseRecord expenseRecord, final SaveRecordCallback callback) {
        progressBar.setVisibility(View.VISIBLE);
        final String authHeader = sharedpreferences.getString(AUTHORIZATION, null);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String postUrl = BASE_URL + "/saveData";

        final JSONObject requestBody = prepareBody(expenseRecord);

        final Response.ErrorListener errorListener = this::onErrorResponse;
        final Response.Listener<JSONObject> responseListener = callback::onSaveSuccess;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                postUrl, requestBody, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> superHeaders = super.getHeaders();
                final Map<String, String> headers = new HashMap<>(superHeaders);
                headers.put(AUTHORIZATION, authHeader);
                return headers;
            }
        };
        // https://stackoverflow.com/questions/25994514/volley-timeout-error
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(final VolleyError error) {
                Toast.makeText(AddExpenseActivity.this, "Timeout occurred while saving record!!!", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @SneakyThrows
    private void onSaveSuccess(final JSONObject recordId) {
        progressBar.setVisibility(View.GONE);
        if (recordId == null) {
            Toast.makeText(AddExpenseActivity.this, "Failed to save record!!!", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(AddExpenseActivity.this, "Record saved with id=" + recordId.getString("id"), Toast.LENGTH_LONG).show();
    }

    @NonNull
    private JSONObject prepareBody(final ExpenseRecord expenseRecord) {
        final JSONObject jsonBody = new JSONObject();
        /*
        {
            "expenseDate": "10/12/2020",
            "description": "description",
            "category": "category",
            "expenseAmount": 152.32,
            "id": 5
        }
         */
        try {
            final Date expenseDate = expenseRecord.getExpenseDate();
            jsonBody.put("expenseDate", DateUtility.getDateFormat().format(expenseDate));
            jsonBody.put("description", expenseRecord.getDescription());
            jsonBody.put("category", expenseRecord.getCategory());
            jsonBody.put("expenseAmount", expenseRecord.getExpenseAmount());
            jsonBody.put("id", expenseRecord.getId());
        } catch (final JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    private long generateRandomId() {
        return new Random().nextLong();
    }

    private void onErrorResponse(final VolleyError error) {
        Log.e("Save Record: ", Arrays.toString(error.getStackTrace()));
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "Failed to save data!!!", Toast.LENGTH_SHORT).show();
    }
}