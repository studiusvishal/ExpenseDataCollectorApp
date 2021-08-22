package com.bhavsar.vishal.app.expensedatacollector.activities;

import static com.bhavsar.vishal.app.expensedatacollector.Constants.APP_PREFERENCES;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.AUTHORIZATION;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bhavsar.vishal.app.expensedatacollector.BudgetApp;
import com.bhavsar.vishal.app.expensedatacollector.DatePickerFragment;
import com.bhavsar.vishal.app.expensedatacollector.R;
import com.bhavsar.vishal.app.expensedatacollector.callbacks.SaveRecordCallback;
import com.bhavsar.vishal.app.expensedatacollector.http.HttpRequestUtil;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRecord;
import com.bhavsar.vishal.app.expensedatacollector.model.GenericRequest;
import com.bhavsar.vishal.app.expensedatacollector.util.DateUtility;

import org.apache.commons.lang3.StringUtils;
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
    private Context context;

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

        context = BudgetApp.getContext();
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
        if (expenseCategory.equalsIgnoreCase("Select")) {
            Toast.makeText(context, "Please select expense category.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String strExpenseAmount = expenseAmountEditText.getText().toString();
        if (StringUtils.isEmpty(strExpenseAmount)) {
            Toast.makeText(context, "Please enter expense amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        final double expenseAmount = Double.parseDouble(strExpenseAmount);

        final String expenseDescription = expenseDescriptionEditText.getText().toString();
        final ExpenseRecord expenseRecord = ExpenseRecord.builder()
                .expenseDate(expenseDate)
                .category(expenseCategory)
                .expenseAmount(expenseAmount)
                .description(expenseDescription)
                .id(generateRandomId())
                .build();
        saveRecord(expenseRecord, this::onSaveSuccess);
    }

    private void saveRecord(final ExpenseRecord expenseRecord, final SaveRecordCallback callback) {
        progressBar.setVisibility(View.VISIBLE);
        final GenericRequest<JSONObject> genericRequest = GenericRequest.<JSONObject>builder()
                .endpoint("/saveData")
                .errorListener(this::onErrorResponse)
                .responseListener(callback::onSaveSuccess)
                .requestBody(prepareBody(expenseRecord))
                .headers(getHeaders())
                .methodType(Request.Method.POST)
                .build();
        HttpRequestUtil.sendRequest(genericRequest);
    }

    private Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, sharedpreferences.getString(AUTHORIZATION, null));
        return headers;
    }

    @SneakyThrows
    private void onSaveSuccess(final JSONObject response) {
        progressBar.setVisibility(View.GONE);
        if (response == null) {
            Toast.makeText(context, "Failed to save record!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context,
                "Record saved with id " + response.getString("id"),
                Toast.LENGTH_LONG)
                .show();
    }

    private void onErrorResponse(final VolleyError error) {
        Log.e("Save Record: ", Arrays.toString(error.getStackTrace()));
        progressBar.setVisibility(View.GONE);
        Toast.makeText(context, "Failed to save data!!!", Toast.LENGTH_SHORT).show();
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
            Log.e("ADD_EXPENSE", e.toString());
        }
        return jsonBody;
    }

    private long generateRandomId() {
        return new Random().nextLong();
    }
}