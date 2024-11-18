package com.example.campusexpensemanager.Class;
import androidx.annotation.NonNull;
import java.util.Date;
import java.util.Objects;

public class Income {

    private int userId;
    private double amount;
    private String source;
    private Date incomeDate;
    private int transactionId;

    public Income(int incomeId, int userId, double amount, String source, Date incomeDate, int transactionId) {

        this.userId = userId;
        this.amount = amount;
        this.source = source;
        this.incomeDate = incomeDate;
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }





    @NonNull
    @Override
    public String toString() {
        return source + ": " + amount;
    }
}

