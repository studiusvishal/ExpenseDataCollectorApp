package com.bhavsar.vishal.app.expensedatacollector.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bhavsar.vishal.app.expensedatacollector.BudgetApp;
import com.bhavsar.vishal.app.expensedatacollector.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private boolean clicked = false;
    private FloatingActionButton addBtn;
    private FloatingActionButton addExpenseBtn;
    private FloatingActionButton addTransferBtn;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        addBtn = findViewById(R.id.addFab);
        addExpenseBtn = findViewById(R.id.addExpenseFab);
        addTransferBtn = findViewById(R.id.addMoneyTransferFab);

        addBtn.setOnClickListener(view -> onAddBtnClicked());

        addExpenseBtn.setOnClickListener(view -> {
            Toast.makeText(BudgetApp.getContext())
        });

        addTransferBtn.setOnClickListener(view -> {});
    }

    private void onAddBtnClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
    }

    private void setAnimation(final boolean clicked) {
        if (!clicked) {
            addExpenseBtn.startAnimation(fromBottom);
            addTransferBtn.startAnimation(fromBottom);
            addBtn.startAnimation(rotateOpen);
        } else {
            addExpenseBtn.startAnimation(toBottom);
            addTransferBtn.startAnimation(toBottom);
            addBtn.startAnimation(rotateClose);
        }
    }

    private void setVisibility(final boolean clicked) {
        if (!clicked) {
            addExpenseBtn.setVisibility(View.VISIBLE);
            addTransferBtn.setVisibility(View.VISIBLE);
        } else {
            addExpenseBtn.setVisibility(View.INVISIBLE);
            addTransferBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void onClickAddNewExpense(final View view) {
        final Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }

    public void setClickable(final boolean clicked) {
        if (!clicked) {
            addExpenseBtn.setClickable(true);
            addTransferBtn.setClickable(true);
        } else {
            addExpenseBtn.setClickable(false);
            addTransferBtn.setClickable(false);
        }
    }
}
