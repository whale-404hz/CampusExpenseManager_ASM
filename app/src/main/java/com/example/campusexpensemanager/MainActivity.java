package com.example.campusexpensemanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager.Fragment.AddFragment;
import com.example.campusexpensemanager.Fragment.BuggetFragment;
import com.example.campusexpensemanager.Fragment.HomeFragment;
import com.example.campusexpensemanager.Fragment.ProfileFragment;
import com.example.campusexpensemanager.Fragment.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private boolean isChatOpen = false; // Trạng thái hiển thị chatbox

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Đặt HomeFragment làm mặc định
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new HomeFragment())
                    .commit();
        }

        // Xử lý chuyển đổi giữa các Fragment qua BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_transaction) {
                selectedFragment = new TransactionFragment();
            } else if (item.getItemId() == R.id.nav_add) {
                selectedFragment = new AddFragment();
            } else if (item.getItemId() == R.id.nav_budget) {
                selectedFragment = new BuggetFragment();
            } else if (item.getItemId() == R.id.nav_account) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}
