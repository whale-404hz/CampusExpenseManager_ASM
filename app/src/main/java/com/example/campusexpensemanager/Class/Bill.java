package com.example.campusexpensemanager.Class;
import androidx.annotation.NonNull;
import java.util.Date;
import java.util.Objects;

public class Bill {
    private int billId;
    private int userId;
    private String billName;
    private double amount;
    private Date dueDate;
    private boolean paid;

    public Bill(int billId, int userId, String billName, double amount, Date dueDate, boolean paid) {
        this.billId = billId;
        this.userId = userId;
        this.billName = billName;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paid = paid;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return billId == bill.billId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId);
    }

    @NonNull
    @Override
    public String toString() {
        return billName + ": " + amount;
    }
}

