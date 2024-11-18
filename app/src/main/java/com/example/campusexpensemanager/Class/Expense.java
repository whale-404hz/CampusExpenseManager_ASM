package com.example.campusexpensemanager.Class;

public class Expense {
    private int expenseId;
    private int userId;
    private int categoryId;
    private double amount;
    private String date;
    private String description;
    private int currencyId; // Thêm currencyId

    // Constructor bao gồm currencyId
    public Expense(int expenseId, int userId, int categoryId, double amount, String date, String description, int currencyId) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.currencyId = currencyId; // Gán giá trị currencyId
    }

    // Các getter và setter cho currencyId
    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    // Các getter và setter khác
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
