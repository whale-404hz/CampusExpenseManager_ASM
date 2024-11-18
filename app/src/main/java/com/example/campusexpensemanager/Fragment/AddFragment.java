package com.example.campusexpensemanager.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager.Helper.CategoryHelper;
import com.example.campusexpensemanager.Helper.TransactionHelper;
import com.example.campusexpensemanager.Helper.IncomeHelper;
import com.example.campusexpensemanager.Helper.ExpenseHelper;
import com.example.campusexpensemanager.Helper.CurrencyHelper;
import com.example.campusexpensemanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddFragment extends Fragment {
    private CategoryHelper categoryHelper;
    private TransactionHelper transactionHelper;
    private IncomeHelper incomeHelper;
    private ExpenseHelper expenseHelper;
    private CurrencyHelper currencyHelper;
    private EditText etAmount, etDescription, etNewCategory;
    private Spinner spinnerCategory, spinnerType, spinnerSource, spinnerCurrency;
    private TextView tvDate;
    private Button btnAddTransaction;
    private int selectedYear, selectedMonth, selectedDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        categoryHelper = new CategoryHelper(getContext());
        transactionHelper = new TransactionHelper(getContext());
        incomeHelper = new IncomeHelper(getContext());
        expenseHelper = new ExpenseHelper(getContext());
        currencyHelper = new CurrencyHelper(getContext());

        initializeViews(view);
        setupEventListeners();

        return view;
    }

    private void initializeViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        spinnerType = view.findViewById(R.id.spinnerType);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerSource = view.findViewById(R.id.spinnerSource);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency);
        etDescription = view.findViewById(R.id.etDescription);
        etNewCategory = view.findViewById(R.id.etNewCategory);
        tvDate = view.findViewById(R.id.tvDate);
        btnAddTransaction = view.findViewById(R.id.btnSaveTransaction);

        loadCategoriesIntoSpinner();
        loadCurrenciesIntoSpinner();
        setupTypeSpinner();
    }

    private void setupEventListeners() {
        tvDate.setOnClickListener(v -> showDatePickerDialog());
        btnAddTransaction.setOnClickListener(v -> addTransaction());

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleTransactionTypeSelection(parent.getItemAtPosition(position).toString().toLowerCase());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Other (Add New)")) {
                    etNewCategory.setVisibility(View.VISIBLE);
                } else {
                    etNewCategory.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etNewCategory.setVisibility(View.GONE);
            }
        });
    }

    private void handleTransactionTypeSelection(String selectedType) {
        if (selectedType.equals("income")) {
            setupSourceSpinner();
            spinnerSource.setVisibility(View.VISIBLE);
            etDescription.setVisibility(View.GONE);
        } else if (selectedType.equals("expense")) {
            spinnerSource.setVisibility(View.GONE);
            etDescription.setVisibility(View.VISIBLE);
        }
    }

    private void setupTypeSpinner() {
        String[] types = {"income", "expense"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void setupSourceSpinner() {
        String[] sources = {"Salary", "Freelance"};
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sources);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(sourceAdapter);
    }

    private void loadCurrenciesIntoSpinner() {
        List<String> currencies = currencyHelper.getAllCurrencies();
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);
    }

    private void addTransaction() {
        String amountStr = etAmount.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String date = tvDate.getText().toString();
        String type = spinnerType.getSelectedItem().toString().toLowerCase();
        String selectedCurrency = spinnerCurrency.getSelectedItem().toString();

        if (validateInputs(amountStr, category, date)) {
            double amount = Double.parseDouble(amountStr);
            int userId = getUserIdFromSession();
            int categoryId = category.equals("Other (Add New)") ? categoryHelper.addNewCategory(etNewCategory.getText().toString()) : categoryHelper.getCategoryIdByName(category);
            int currencyId = currencyHelper.getCurrencyIdByName(selectedCurrency);

            if (type.equals("income")) {
                String source = spinnerSource.getSelectedItem().toString();
                incomeHelper.addIncome(userId, amount, date, source, currencyId); // Add to income table
            } else if (type.equals("expense")) {
                String description = etDescription.getText().toString();
                expenseHelper.addExpense(userId, categoryId, amount, date, description, currencyId); // Add to expense table
            }

            boolean isInserted = transactionHelper.addTransaction(userId, amount, date, type, categoryId, currencyId);

            if (isInserted) {
                Toast.makeText(getContext(), "Transaction added successfully!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(getContext(), "Failed to add transaction", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean validateInputs(String amountStr, String category, String date) {
        if (amountStr.isEmpty() || category.isEmpty() || date.equals("Select Date")) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (category.equals("Other (Add New)") && etNewCategory.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter the new category", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int getUserIdFromSession() {
        SharedPreferences prefs = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            tvDate.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void loadCategoriesIntoSpinner() {
        List<String> categories = new ArrayList<>(categoryHelper.getAllCategoryNames());
        categories.add("Other (Add New)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void clearFields() {
        etAmount.setText("");
        etDescription.setText("");
        etNewCategory.setText("");
        tvDate.setText("Select Date");
        spinnerCategory.setSelection(0);
        spinnerType.setSelection(0);
        spinnerCurrency.setSelection(0);
        etNewCategory.setVisibility(View.GONE);
        spinnerSource.setVisibility(View.GONE);
        etDescription.setVisibility(View.GONE);
    }
}
