package com.example.campusexpensemanager.Adapter;

import android.content.Context;
import com.example.campusexpensemanager.Helper.CurrencyHelper;

public class CurrencyConverter {
    private CurrencyHelper currencyHelper;

    public CurrencyConverter(Context context) {
        this.currencyHelper = new CurrencyHelper(context);
    }

    public double convertToDefaultCurrency(double amount, String fromCurrency) {
        double rate = currencyHelper.getExchangeRate(fromCurrency);
        if (rate == 0) {
            throw new IllegalArgumentException("Exchange rate cannot be zero");
        }
        return amount / rate;
    }

    public double convertFromDefaultCurrency(double amount, String toCurrency) {
        double rate = currencyHelper.getExchangeRate(toCurrency);
        if (rate == 0) {
            throw new IllegalArgumentException("Exchange rate cannot be zero");
        }
        return amount * rate;
    }

    public double convertBetweenCurrencies(double amount, String fromCurrency, String toCurrency) {
        double fromRate = currencyHelper.getExchangeRate(fromCurrency);
        double toRate = currencyHelper.getExchangeRate(toCurrency);

        if (fromRate == 0 || toRate == 0) {
            throw new IllegalArgumentException("Exchange rates cannot be zero");
        }

        // Chuyển đổi sang đơn vị tiền tệ mặc định trước, sau đó sang đơn vị đích
        double amountInDefaultCurrency = amount / fromRate;
        return amountInDefaultCurrency * toRate;
    }
}
