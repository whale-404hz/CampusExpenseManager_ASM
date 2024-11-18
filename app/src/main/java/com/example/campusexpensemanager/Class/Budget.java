package com.example.campusexpensemanager.Class;
import androidx.annotation.NonNull;
import java.util.Date;
import java.util.Objects;

public class Budget {
    private int budgetId;
    private int userId;
    private int categoryId;
    private double budgetAmount;
    private Date startDate;
    private Date endDate;
    private float amount;
    public Budget(int budgetId, int userId, int categoryId, double budgetAmount, Date startDate, Date endDate) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.budgetAmount = budgetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
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

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }
    public float getAmount() {
        return amount;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return budgetId == budget.budgetId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Budget for category " + categoryId + ": " + budgetAmount;
    }
}

