package com.example.campusexpensemanager.Class;
// Lớp để lưu trữ dữ liệu thu nhập
class IncomeData {
    private String date;
    private float amount;

    public IncomeData(String date, float amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }
}
