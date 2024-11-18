package com.example.campusexpensemanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager.Fragment.AddFragment;
import com.example.campusexpensemanager.Fragment.BuggetFragment;
import com.example.campusexpensemanager.Fragment.HomeFragment;
import com.example.campusexpensemanager.Fragment.ProfileFragment;
import com.example.campusexpensemanager.Fragment.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        if (savedInstanceState == null) {
            getSupportFragmentManager(). beginTransaction()
                    . replace (R.id.fragmentContainerView,
                            new HomeFragment()).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener (item -> {
            Fragment selectedFragment = null;
            if (item. getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_transaction) {
                selectedFragment = new TransactionFragment() ;
            }else if (item.getItemId() == R.id.nav_add) {
                selectedFragment = new AddFragment();
            }else if (item.getItemId() == R.id.nav_budget) {
                selectedFragment = new BuggetFragment() ;
            }else if (item.getItemId() == R.id.nav_account) {
                selectedFragment = new ProfileFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView,
                                selectedFragment).commit();
            }
            return true;
        });
    }

}