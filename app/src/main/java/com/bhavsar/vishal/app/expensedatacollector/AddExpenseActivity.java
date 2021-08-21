package com.bhavsar.vishal.app.expensedatacollector;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bhavsar.vishal.app.expensedatacollector.util.DateUtility;

import java.util.Arrays;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText editTextDate;
    private ImageButton selectDateButton;
    private static final List<String> itemList = Arrays.asList("Select",
            "Apartment Rent",
            "Credit card",
            "Fuel");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // https://stackoverflow.com/questions/4989817/set-the-textsize-to-a-text-in-spinner-in-android-programatically
        // https://stackoverflow.com/questions/9476665/how-to-change-spinner-text-size-and-text-color
        // TODO: Get from DB
        final Spinner categorySpinner = findViewById(R.id.spinnerExpenseCategory);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, itemList);
        categorySpinner.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        editTextDate = findViewById(R.id.editTextDate);
        selectDateButton = findViewById(R.id.selectDateButton);
        editTextDate.setText(DateUtility.getCurrentDate());
    }

    public void onClickSelectDateButton(final View view) {
        final DialogFragment datePickerFragment = new DatePickerFragment(editTextDate);
        datePickerFragment.show(getSupportFragmentManager(), "date picker");
    }
}