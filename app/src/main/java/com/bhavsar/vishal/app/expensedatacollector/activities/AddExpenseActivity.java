package com.bhavsar.vishal.app.expensedatacollector.activities;

import static com.bhavsar.vishal.app.expensedatacollector.BudgetApp.getContext;
import static com.bhavsar.vishal.app.expensedatacollector.Constants.KEY_AUTHORIZATION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bhavsar.vishal.app.expensedatacollector.R;
import com.bhavsar.vishal.app.expensedatacollector.http.RetrofitHttpUtil;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRecord;
import com.bhavsar.vishal.app.expensedatacollector.model.ExpenseRequest;
import com.bhavsar.vishal.app.expensedatacollector.util.DateUtility;
import com.bhavsar.vishal.app.expensedatacollector.util.SharedPreferencesUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
        if (StringUtils.isEmpty(expenseDate)) {
            expenseDateInputLayout.setError("Please select a date.");
            isValid = false;
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
                        .expenseDate(expenseDate)
                        .category(expenseCategory)
                        .expenseAmount(expenseAmount)
                        .description(expenseDescription)
                        .id(generateRandomId())
                        .build();

        progressBar.setVisibility(View.VISIBLE);
        saveExpense(expenseRecord);
    }

    private void saveExpense(final ExpenseRecord expenseRecord) {
        val expenseRequest =
                ExpenseRequest.builder().requestBody(expenseRecord).headers(getHeaders()).build();
        RetrofitHttpUtil.saveExpenseRecord(expenseRequest);
    }

    private Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put(KEY_AUTHORIZATION, sharedPreferencesUtil.getString(KEY_AUTHORIZATION, null));
        return headers;
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
        val date = DateUtility.getDateFromCalendarSelection(selection);
        expenseDateEditText.setText(date);
    }
}
