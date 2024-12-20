package com.example.campusexpensemanager.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.campusexpensemanager.Helper.CurrencyHelper;
import com.example.campusexpensemanager.Helper.CurrencyService;
import com.example.campusexpensemanager.Helper.ExpenseHelper;
import com.example.campusexpensemanager.Helper.IncomeHelper;
import com.example.campusexpensemanager.Helper.UserHelper;
import com.example.campusexpensemanager.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private UserHelper userHelper;
    private IncomeHelper incomeHelper;
    private ExpenseHelper expenseHelper;
    private CurrencyHelper currencyHelper;

    private TextView totalIncomeView, totalExpenseView, balanceView;
    private BarChart barChart;
    private LottieAnimationView loadingAnimation;

    private int userId = -1;
    private String selectedCurrency = "USD";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize context and helpers
        Context context = requireContext();
        userHelper = new UserHelper(context);
        incomeHelper = new IncomeHelper(context);
        expenseHelper = new ExpenseHelper(context);
        currencyHelper = new CurrencyHelper(context);

        // Map views from XML
        totalIncomeView = view.findViewById(R.id.textViewTotalIncome);
        totalExpenseView = view.findViewById(R.id.textViewTotalExpense);

        barChart = view.findViewById(R.id.barChart);
        loadingAnimation = view.findViewById(R.id.lottieLoading); // Add Lottie view in XML

        // Get user ID from SharedPreferences
        userId = getUserIdFromSession();

        if (userId != -1) {
            selectedCurrency = getSelectedCurrency();
            fetchAndUpdateExchangeRates();
        } else {
            Toast.makeText(context, "User ID not found!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedCurrency = getSelectedCurrency();
        if (userId != -1) {
            fetchAndUpdateExchangeRates();
        }
    }

    private String getSelectedCurrency() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        return prefs.getString("default_currency", "USD");
    }

    private void fetchAndUpdateExchangeRates() {
        // Show loading animation while data is being fetched
        loadingAnimation.setVisibility(View.VISIBLE);
        loadingAnimation.playAnimation();

        Context context = requireContext();
        CurrencyService.fetchExchangeRates(context, new CurrencyService.ExchangeRateListener() {
            @Override
            public void onExchangeRateReceived(JSONObject exchangeRates) {
                currencyHelper.updateExchangeRates(exchangeRates);
                updateStatistics();

                // Hide loading animation when data is loaded
                loadingAnimation.setVisibility(View.GONE);
                loadingAnimation.cancelAnimation();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Failed to fetch exchange rates: " + error, Toast.LENGTH_SHORT).show();

                // Hide loading animation if there’s an error
                loadingAnimation.setVisibility(View.GONE);
                loadingAnimation.cancelAnimation();
            }
        });
    }

    private void updateStatistics() {
        double totalIncome = incomeHelper.getTotalIncome(userId);
        double totalExpense = expenseHelper.getTotalExpense(userId);
        double balance = totalIncome - totalExpense;

        double exchangeRate = currencyHelper.getExchangeRate(selectedCurrency);
        if (exchangeRate <= 0) {
            Toast.makeText(requireContext(), "Exchange rate unavailable for " + selectedCurrency, Toast.LENGTH_SHORT).show();
            return;
        }

        double incomeInSelectedCurrency = totalIncome * exchangeRate;
        double expenseInSelectedCurrency = totalExpense * exchangeRate;

        Locale locale = getLocaleForCurrency(selectedCurrency);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance(selectedCurrency));

        totalIncomeView.setText(String.format("Total Income: %s", currencyFormat.format(incomeInSelectedCurrency)));
        totalExpenseView.setText(String.format("Total Expense: %s", currencyFormat.format(expenseInSelectedCurrency)));
        balanceView.setText(String.format("Balance: %s", currencyFormat.format(balance)));

        if (incomeInSelectedCurrency > 0 || expenseInSelectedCurrency > 0) {
            updateBarChart(incomeInSelectedCurrency, expenseInSelectedCurrency);
        } else {
            barChart.clear();
            barChart.setNoDataText("No data available");
            barChart.invalidate();
        }
    }


    private Locale getLocaleForCurrency(String currencyCode) {
        switch (currencyCode) {
            case "VND":
                return new Locale("vi", "VN");
            case "USD":
                return Locale.US;
            case "EUR":
                return Locale.GERMANY;
            case "JPY":
                return Locale.JAPAN;
            case "GBP":
                return Locale.UK;
            default:
                return Locale.getDefault();
        }
    }

    private void updateBarChart(double income, double expense) {
        // Prepare BarChart entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) income)); // Income at X=0
        entries.add(new BarEntry(1f, (float) expense)); // Expense at X=1

        // Create dataset
        BarDataSet dataSet = new BarDataSet(entries, "Financial Overview");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set colors
        dataSet.setValueTextSize(12f); // Set text size for values

        // Attach dataset to BarData
        BarData data = new BarData(dataSet);
        barChart.setData(data);

        // Customize BarChart
        barChart.getDescription().setEnabled(false); // Remove description text
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Income", "Expense"})); // Set labels
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Labels at the bottom
        barChart.getXAxis().setGranularity(1f); // Ensure 1 unit between bars
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getAxisLeft().setAxisMinimum(0f); // Ensure y-axis starts at 0
        barChart.getAxisRight().setEnabled(false); // Disable right y-axis
        barChart.getLegend().setEnabled(true); // Display legend

        // Refresh the chart
        barChart.invalidate();
    }


    private int getUserIdFromSession() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userHelper = null;
        incomeHelper = null;
        expenseHelper = null;
        currencyHelper = null;
    }
}
