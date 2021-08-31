package com.bhavsar.vishal.app.expensedatacollector;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final TextInputLayout selectDateTextInputLayout = findViewById(R.id.testSelectDate);
        final EditText editText = selectDateTextInputLayout.getEditText();
        Objects.requireNonNull(editText);
        editText.setOnClickListener(view -> {
            final CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
            calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());

            // https://material.io/components/date-pickers/android#using-date-pickers
            final MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setTheme(R.style.MaterialCalendar)
                    .setCalendarConstraints(calendarConstraintBuilder.build())
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                // https://stackoverflow.com/questions/58931051/materialdatepicker-get-selected-dates
                // user has selected a date
                // format the date and set the text of the input box to be the selected date
                // right now this format is hard-coded, this will change
                // Get the offset from our timezone and UTC.
                final TimeZone timeZoneUTC = TimeZone.getDefault();
                // It will be negative, so that's the -1
                final int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;

                // Create a date format, then a date object with our offset
                final DateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                final Date date = new Date(selection + offsetFromUTC);

                editText.setText(simpleFormat.format(date));
            });
            datePicker.addOnNegativeButtonClickListener(view1 -> datePicker.dismiss());
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
    }
}