package com.bhavsar.vishal.app.expensedatacollector.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bhavsar.vishal.app.expensedatacollector.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAddNewExpense(final View view) {
        final Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }
}
