package com.bhavsar.vishal.app.expensedatacollector.activities;

import static com.bhavsar.vishal.app.expensedatacollector.BudgetApp.getContext;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_AUTHORIZATION;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_CATEGORY;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_DESCRIPTION;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_EXPENSE_AMOUNT;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_EXPENSE_DATE;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bhavsar.vishal.app.expensedatacollector.R;
import com.bhavsar.vishal.app.expensedatacollector.callbacks.SaveRecordCallback;
import com.bhavsar.vishal.app.expensedatacollector.http.HttpRequestUtil;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRecord;
import com.bhavsar.vishal.app.expensedatacollector.model.GenericRequest;
import com.bhavsar.vishal.app.expensedatacollector.util.DateUtility;
import com.bhavsar.vishal.app.expensedatacollector.util.SharedPreferencesUtil;
import com.bhavsar.vishal.app.expensedatacollector.util.ToastUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import lombok.SneakyThrows;
import lombok.val;

public class AddExpenseActivity extends AppCompatActivity {

    private static final List<String> categoriesList =
            Arrays.asList("Select", "Apartment Rent", "Credit card", "Fuel");
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

    private EditText expenseDateEditText;
    private EditText expenseAmountEditText;
    private EditText expenseDescriptionEditText;
    private ProgressBar progressBar;
    private Context context;

    private AutoCompleteTextView expenseCategoryEditText;
    private TextInputLayout expenseAmountInputLayout;
    private TextInputLayout expenseCategoryInputLayout;
    private TextInputLayout expenseDateInputLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        context = getContext();

        // initialize views
        expenseDateInputLayout = findViewById(R.id.selectDate);
        expenseDateEditText = expenseDateInputLayout.getEditText();

        populateExpenseCategories();

        expenseAmountInputLayout = findViewById(R.id.expenseAmountEditText);
        expenseAmountEditText = expenseAmountInputLayout.getEditText();

        final TextInputLayout expenseDescriptionInputLayout = findViewById(R.id.expenseDescription);
        expenseDescriptionEditText = expenseDescriptionInputLayout.getEditText();

        final Button resetButton = findViewById(R.id.buttonReset);
        final Button saveButton = findViewById(R.id.saveExpense);
        progressBar = findViewById(R.id.saveExpenseRecordProgressBar);

        // attach listeners
        saveButton.setOnClickListener(this::onClickSaveButton);
        resetButton.setOnClickListener(this::onClickResetButton);

        Objects.requireNonNull(expenseDateEditText);
        expenseDateEditText.setOnClickListener(this::selectDate);
        //        final TextInputLayout expenseTextInputLayout =
        // findViewById(R.id.expenseDateField);
        //        expenseDateEditText = expenseTextInputLayout.getEditText();
        //        //
        // https://www.semicolonworld.com/question/45922/how-to-disable-keypad-popup-when-on-edittext
        //        Objects.requireNonNull(expenseDateEditText).setOnTouchListener((v, event) -> {
        //            // v.onTouchEvent(event);
        //            hideDefaultKeyboard(v);
        //            onClickSelectDateButton(v);
        //            return true;
        //        });
        // Objects.requireNonNull(expenseDateEditText).setOnClickListener(this::onClickSelectDateButton);
        //
        //        //
        // https://stackoverflow.com/questions/4989817/set-the-textsize-to-a-text-in-spinner-in-android-programatically
        //        //
        // https://stackoverflow.com/questions/9476665/how-to-change-spinner-text-size-and-text-color
        //        // TODO: Get from DB
        //        categorySpinner = findViewById(R.id.spinnerExpenseCategory);
        //        final ArrayAdapter<String> arrayAdapter =
        //                new ArrayAdapter<>(this, R.layout.spinner_item, itemList);
        //        categorySpinner.setAdapter(arrayAdapter);
        //
        //        expenseDateEditText = findViewById(R.id.editTextDate);
        //        final ImageButton selectDateButton = findViewById(R.id.selectDateButton);
        //        expenseDateEditText.setText(DateUtility.getCurrentDate());
        //        expenseAmountEditText = findViewById(R.id.editTextExpenseAmount);
        //        expenseDescriptionEditText = findViewById(R.id.editTextDescription);
        //
        //        // attach listeners
        //        selectDateButton.setOnClickListener(this::onClickSelectDateButton);
        //
        //        saveButton.setOnClickListener(this::onClickSaveButton);
    }

    private void populateExpenseCategories() {
        expenseCategoryInputLayout = findViewById(R.id.expenseCategoryMenu);
        val adapter = new ArrayAdapter<>(context, R.layout.spinner_item, categoriesList);
        expenseCategoryEditText = ((AutoCompleteTextView) expenseCategoryInputLayout.getEditText());
        Objects.requireNonNull(expenseCategoryEditText).setAdapter(adapter);
        expenseCategoryEditText.setText("Select", false);
    }

    public void onClickResetButton(final View view) {
        expenseDateEditText.setText(DateUtility.getCurrentDate());
        expenseCategoryEditText.setSelection(0);
        expenseAmountEditText.setText(R.string.default_expense_amount);
        expenseDescriptionEditText.setText("");
    }

    public void onClickSaveButton(final View view) {
        val expenseDate = expenseDateEditText.getText().toString();
        val expenseCategory = expenseCategoryEditText.getText().toString();
        val strExpenseAmount = expenseAmountEditText.getText().toString();
        val expenseDescription = expenseDescriptionEditText.getText().toString();

        boolean isValid = true;
        Date parsedDate = null;
        if (StringUtils.isEmpty(expenseDate)) {
            expenseDateInputLayout.setError("Please select a date.");
            isValid = false;
        } else {
            parsedDate = DateUtility.parseDate(expenseDate);
        }

        if (expenseCategory.equalsIgnoreCase("Select")) {
            expenseCategoryInputLayout.setError("Please select a date.");
            isValid = false;
        }

        double expenseAmount = 0.0;
        if (StringUtils.isEmpty(strExpenseAmount)) {
            expenseAmountInputLayout.setError("Please enter expense amount.");
            isValid = false;
        } else {
            expenseAmount = Double.parseDouble(strExpenseAmount);
        }

        if (!isValid) {
            return;
        }
        final ExpenseRecord expenseRecord =
                ExpenseRecord.builder()
                        .expenseDate(parsedDate)
                        .category(expenseCategory)
                        .expenseAmount(expenseAmount)
                        .description(expenseDescription)
                        .id(generateRandomId())
                        .build();
        saveRecord(expenseRecord, this::onSaveSuccess);
    }

    private void saveRecord(final ExpenseRecord expenseRecord, final SaveRecordCallback callback) {
        progressBar.setVisibility(View.VISIBLE);
        final GenericRequest<JSONObject> genericRequest =
                GenericRequest.<JSONObject>builder()
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
        headers.put(KEY_AUTHORIZATION, sharedPreferencesUtil.getString(KEY_AUTHORIZATION, null));
        return headers;
    }

    @SneakyThrows
    private void onSaveSuccess(final JSONObject response) {
        progressBar.setVisibility(View.GONE);
        if (response == null) {
            ToastUtil.showToast("Failed to save record!!!");
            return;
        }
        ToastUtil.showToast("Record saved with id " + response.getString("id"));
    }

    private void onErrorResponse(final VolleyError error) {
        Log.e("Save Record: ", Arrays.toString(error.getStackTrace()));
        progressBar.setVisibility(View.GONE);
        ToastUtil.showToast("Failed to save record!!!");
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
            jsonBody.put(KEY_EXPENSE_DATE, DateUtility.getDateFormat().format(expenseDate));
            jsonBody.put(KEY_DESCRIPTION, expenseRecord.getDescription());
            jsonBody.put(KEY_CATEGORY, expenseRecord.getCategory());
            jsonBody.put(KEY_EXPENSE_AMOUNT, expenseRecord.getExpenseAmount());
            jsonBody.put(KEY_ID, expenseRecord.getId());
        } catch (final JSONException e) {
            Log.e("ADD_EXPENSE", e.toString());
        }
        return jsonBody;
    }

    private long generateRandomId() {
        return new Random().nextLong();
    }

    private void selectDate(final View view) {
        final CalendarConstraints.Builder calendarConstraintBuilder =
                new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
        // https://material.io/components/date-pickers/android#using-date-pickers
        final MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                        .setTheme(R.style.MaterialCalendar)
                        .setCalendarConstraints(calendarConstraintBuilder.build())
                        .build();
        datePicker.addOnPositiveButtonClickListener(this::onDatePickerPositiveButtonClick);
        datePicker.addOnNegativeButtonClickListener(view1 -> datePicker.dismiss());
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void onDatePickerPositiveButtonClick(final Long selection) {
        // https://stackoverflow.com/questions/58931051/materialdatepicker-get-selected-dates
        // user has selected a date
        // format the date and set the text of the input box to be the selected date
        // right now this format is hard-coded, this will change
        // Get the offset from our timezone and UTC.
        val timeZoneUTC = TimeZone.getDefault();
        // It will be negative, so that's the -1
        val offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
        // Create a date format, then a date object with our offset
        val simpleFormat =
                new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        val date = new Date(selection + offsetFromUTC);
        expenseDateEditText.setText(simpleFormat.format(date));
    }
}
