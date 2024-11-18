package com.example.campusexpensemanager.Class;
import androidx.annotation.NonNull;
import java.util.Objects;

public class Account {
    private int accountId;
    private int userId;
    private String accountName;
    private double accountBalance;

    public Account(int accountId, int userId, String accountName, double accountBalance) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountName = accountName;
        this.accountBalance = accountBalance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

    @NonNull
    @Override
    public String toString() {
        return accountName + ": " + accountBalance;
    }
}
