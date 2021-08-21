package com.bhavsar.vishal.app.expensedatacollector;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bhavsar.vishal.app.expensedatacollector.util.DateUtility;

import java.util.Arrays;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private static final List<String> itemList = Arrays.asList("Select",
            "Apartment Rent",
            "Credit card",
            "Fuel");
    private EditText expenseDateEditText;
    private ImageButton selectDateButton;
    private Spinner categorySpinner;
    private EditText expenseAmountEditText;
    private EditText expenseDescriptionEditText;
    private Button resetButton, saveButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // https://stackoverflow.com/questions/4989817/set-the-textsize-to-a-text-in-spinner-in-android-programatically
        // https://stackoverflow.com/questions/9476665/how-to-change-spinner-text-size-and-text-color
        // TODO: Get from DB
        categorySpinner = findViewById(R.id.spinnerExpenseCategory);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, itemList);
        categorySpinner.setAdapter(arrayAdapter);

        expenseDateEditText = findViewById(R.id.editTextDate);
        selectDateButton = findViewById(R.id.selectDateButton);
        expenseDateEditText.setText(DateUtility.getCurrentDate());
        expenseAmountEditText = findViewById(R.id.editTextExpenseAmount);
        expenseDescriptionEditText = findViewById(R.id.editTextDescription);
        resetButton = findViewById(R.id.buttonReset);
        saveButton = findViewById(R.id.buttonOk);
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
}