package com.example.campusexpensemanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.campusexpensemanager.Helper.CurrencyHelper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private Spinner spinnerCurrency, spinnerLanguage;
    private TextView tvSelectedDate;
    private CurrencyHelper currencyHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ các View từ XML
        ImageButton btnBack = findViewById(R.id.btnback);
        Button btnSelectDate = findViewById(R.id.btnSelectDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);

        // Helper
        currencyHelper = new CurrencyHelper(this);

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút chọn ngày
        setupDatePicker(btnSelectDate);

        // Dark Mode Switch
        setupDarkModeSwitch();

        // Currency Spinner
        initializeCurrencySpinner();

        // Language Spinner
        setupLanguageSpinner();
    }

    private void setupDatePicker(Button btnSelectDate) {
        btnSelectDate.setOnClickListener(v -> {
            // Lấy ngày hiện tại
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Hiển thị DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Định dạng ngày tháng
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(selectedDate.getTime());

                        // Hiển thị ngày đã chọn
                        tvSelectedDate.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void setupDarkModeSwitch() {
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isDarkModeEnabled = preferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkModeEnabled);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });
    }

    private void initializeCurrencySpinner() {
        List<String> currencies = currencyHelper.getAllCurrencies();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        String currentCurrency = preferences.getString("default_currency", "USD");
        spinnerCurrency.setSelection(currencies.indexOf(currentCurrency));

        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCurrency = parent.getItemAtPosition(position).toString();
                preferences.edit().putString("default_currency", selectedCurrency).apply();
                Toast.makeText(SettingsActivity.this, "Currency updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupLanguageSpinner() {
        SharedPreferences preferences = getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("language", "en");

        String[] languages = {"English", "Vietnamese", "Chinese"};
        String[] languageCodes = {"en", "vi", "zh"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        int position = Arrays.asList(languageCodes).indexOf(languageCode);
        spinnerLanguage.setSelection(position);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageCode = languageCodes[position];
                preferences.edit().putString("language", selectedLanguageCode).apply();
                setLocale(selectedLanguageCode);
                Toast.makeText(SettingsActivity.this, "Language changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
