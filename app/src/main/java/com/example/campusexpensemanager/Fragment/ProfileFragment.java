package com.example.campusexpensemanager.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager.Helper.Auth;
import com.example.campusexpensemanager.Helper.CurrencyHelper;
import com.example.campusexpensemanager.Helper.TransactionHelper;
import com.example.campusexpensemanager.Helper.UserHelper;
import com.example.campusexpensemanager.Login;
import com.example.campusexpensemanager.R;
import com.example.campusexpensemanager.SettingsActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView ivAvatar;
    private TextView tvUsername;
    private Button btnChangeAvatar, btnLogout, btnSettings;
    private Switch switchDarkMode;
    private Spinner spinnerCurrency, spinnerLanguage;

    private UserHelper userHelper;
    private Auth auth;
    private CurrencyHelper currencyHelper;
    private TransactionHelper transactionHelper;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        initUI(view);

        // Initialize helpers
        initHelpers();

        // Load user data and setup views
        loadUserInfo();
        initializeCurrencySpinner();
        setupLanguageSpinner();
        setupDarkModeSwitch();

        // Setup button click listeners
        setupListeners();

        return view;
    }

    private void initUI(View view) {
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnSettings = view.findViewById(R.id.btn_settings);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
    }

    private void initHelpers() {
        userHelper = new UserHelper(requireActivity());
        auth = new Auth(requireActivity());
        currencyHelper = new CurrencyHelper(requireActivity());
        transactionHelper = new TransactionHelper(requireActivity());
        userId = auth.getUserId();
    }

    private void setupListeners() {
        btnChangeAvatar.setOnClickListener(v -> openImagePicker());
        btnLogout.setOnClickListener(v -> logout());
        btnSettings.setOnClickListener(v -> openSettings());
    }
    private void openSettings() {
        Intent intent = new Intent(requireContext(), SettingsActivity.class);
        startActivity(intent);
    }


    private void loadUserInfo() {
        String username = userHelper.getUsernameById(userId);
        if (username != null) {
            tvUsername.setText(username);
        } else {
            Toast.makeText(requireActivity(), "User not found!", Toast.LENGTH_SHORT).show();
        }

        byte[] avatarBytes = userHelper.getUserAvatar(userId);
        if (avatarBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            ivAvatar.setImageBitmap(bitmap);
        } else {
            ivAvatar.setImageResource(R.mipmap.avatar_foreground);
        }
    }

    private void initializeCurrencySpinner() {
        List<String> currencies = currencyHelper.getAllCurrencies();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);

        SharedPreferences preferences = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        String currentCurrency = preferences.getString("default_currency", "USD");
        spinnerCurrency.setSelection(currencies.indexOf(currentCurrency));

        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCurrency = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("default_currency", selectedCurrency);
                editor.apply();
                updateTransactionAmounts(selectedCurrency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDarkModeSwitch() {
        boolean isDarkModeEnabled = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        switchDarkMode.setChecked(isDarkModeEnabled);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });
    }

    private void setupLanguageSpinner() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("language", "en");

        String[] languages = {"English", "Vietnamese", "Chinese"};
        String[] languageCodes = {"en", "vi", "zh"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        int position = Arrays.asList(languageCodes).indexOf(languageCode);
        spinnerLanguage.setSelection(position);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageCode = languageCodes[position];
                if (!languageCode.equals(selectedLanguageCode)) {
                    saveLanguagePreference(selectedLanguageCode);
                    setLocale(selectedLanguageCode);
                    Toast.makeText(requireContext(), "Language changed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE).edit();
        editor.putString("language", languageCode);
        editor.apply();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                ivAvatar.setImageBitmap(bitmap);
                userHelper.updateUserAvatar(userId, bitmap);
                Toast.makeText(requireActivity(), "Avatar updated successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), "Error while selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        auth.logout();
        Intent intent = new Intent(requireContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void updateTransactionAmounts(String newCurrency) {
        transactionHelper.updateTransactionsToCurrency(newCurrency);
    }
}
