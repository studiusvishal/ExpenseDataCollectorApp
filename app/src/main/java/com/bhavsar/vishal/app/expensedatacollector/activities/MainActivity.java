package com.bhavsar.vishal.app.expensedatacollector.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bhavsar.vishal.app.expensedatacollector.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final Button addExpenseButton = findViewById(R.id.buttonAddExpense);
//        addExpenseButton.setOnClickListener(this::onClickAddNewExpense);

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
    }

    public void onClickAddNewExpense(final View view) {
        final Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }
}
