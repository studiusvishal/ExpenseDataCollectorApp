package com.bhavsar.vishal.app.expensedatacollector.activities;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.bhavsar.vishal.app.expensedatacollector.R;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationBarListener implements NavigationBarView.OnItemSelectedListener {
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.page_1:
                Log.d("BOTTOM_NAV", "Selected page 1");
                break;
            default:
        }
        return false;
    }
}
