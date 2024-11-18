package com.example.campusexpensemanager.Class;

public class Transaction {
    private int transactionId;
    private int userId;
    private int accountId;
    private double amount;
    private String date;
    private String type;
    private int categoryId;
    private String description;
    private int currencyId; // Thêm currencyId

    public Transaction(int transactionId, int userId, int accountId, double amount, String date, String type, int categoryId, String description, int currencyId) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.accountId = accountId;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.categoryId = categoryId;
        this.description = description;
        this.currencyId = currencyId;
    }

    // Thêm getter và setter cho currencyId
    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    // Các getter và setter khác
}
