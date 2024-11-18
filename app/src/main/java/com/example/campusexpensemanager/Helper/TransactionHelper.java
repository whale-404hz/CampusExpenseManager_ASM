package com.example.campusexpensemanager.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusexpensemanager.Class.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionHelper {
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_ACCOUNT_ID = "account_id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TRANSACTION_DATE = "transaction_date";
    private static final String COLUMN_TYPE = "type"; // Loại giao dịch (income hoặc expense)
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CURRENCY_ID = "currency_id"; // Thêm cột currency_id

    private SQLiteOpenHelper dbHelper;
    private CurrencyHelper currencyHelper;

    public TransactionHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.currencyHelper = new CurrencyHelper(context); // Khởi tạo CurrencyHelper
    }

    // Thêm giao dịch mới bao gồm `currency_id`
    public boolean addTransaction(int userId, double amount, String date, String type, int categoryId, String description, int currencyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_TRANSACTION_DATE, date);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_CATEGORY_ID, categoryId);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CURRENCY_ID, currencyId); // Thêm currency_id vào ContentValues

        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return result != -1;
    }

    // Lấy tất cả giao dịch của người dùng bao gồm `currency_id`
    public List<Transaction> getAllTransactions(int userId) {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                int transactionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID));
                int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                int currencyId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENCY_ID)); // Lấy `currency_id`

                Transaction transaction = new Transaction(transactionId, userId, accountId, amount, date, type, categoryId, description, currencyId);
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactionList;
    }

    // Tính tổng số tiền theo loại giao dịch và đơn vị tiền tệ
    public double getTotalAmountByTypeAndCurrency(int userId, String type, int currencyId) {
        double totalAmount = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_TYPE + " = ? AND " + COLUMN_CURRENCY_ID + " = ?", new String[]{String.valueOf(userId), type, String.valueOf(currencyId)});
        if (cursor.moveToFirst()) {
            totalAmount = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return totalAmount;
    }

    // Tìm kiếm giao dịch với các điều kiện bao gồm `currency_id`
    public List<Transaction> searchTransactions(int userId, String type, String dateRangeStart, String dateRangeEnd, double amount, int currencyId) {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_USER_ID + " = ?";
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(String.valueOf(userId));

        if (type != null && !type.isEmpty()) {
            query += " AND " + COLUMN_TYPE + " = ?";
            selectionArgs.add(type);
        }

        if (dateRangeStart != null && !dateRangeStart.isEmpty() && dateRangeEnd != null && !dateRangeEnd.isEmpty()) {
            query += " AND " + COLUMN_TRANSACTION_DATE + " BETWEEN ? AND ?";
            selectionArgs.add(dateRangeStart);
            selectionArgs.add(dateRangeEnd);
        }

        if (amount > 0) {
            query += " AND " + COLUMN_AMOUNT + " = ?";
            selectionArgs.add(String.valueOf(amount));
        }

        if (currencyId > 0) {
            query += " AND " + COLUMN_CURRENCY_ID + " = ?";
            selectionArgs.add(String.valueOf(currencyId));
        }

        Cursor cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));
        if (cursor.moveToFirst()) {
            do {
                int transactionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID));
                int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID));
                double amountValue = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE));
                String transactionType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                int currencyIdValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENCY_ID)); // Lấy `currency_id`

                Transaction transaction = new Transaction(transactionId, userId, accountId, amountValue, date, transactionType, categoryId, description, currencyIdValue);
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactionList;
    }

    // Phương thức chuyển đổi đơn vị tiền tệ cho tất cả giao dịch
    public void updateTransactionsToCurrency(String newCurrency) {
        // Lấy tỷ giá chuyển đổi mới
        double newExchangeRate = currencyHelper.getExchangeRate(newCurrency);

        // Bắt đầu truy cập cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Lấy danh sách tất cả các giao dịch hiện có
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TRANSACTION_ID + ", " + COLUMN_AMOUNT + ", " + COLUMN_CURRENCY_ID + " FROM " + TABLE_TRANSACTIONS, null);

        // Lặp qua từng giao dịch để cập nhật số tiền dựa trên tỷ giá mới
        if (cursor.moveToFirst()) {
            do {
                int transactionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID));
                double oldAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                int currentCurrencyId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENCY_ID));

                // Lấy tỷ giá của đơn vị tiền tệ hiện tại
                String currentCurrency = currencyHelper.getCurrencyNameById(currentCurrencyId);
                double currentExchangeRate = currencyHelper.getExchangeRate(currentCurrency);

                // Chuyển đổi số tiền sang đơn vị tiền tệ mới
                double convertedAmount = (oldAmount / currentExchangeRate) * newExchangeRate;

                // Cập nhật giao dịch với số tiền mới và đơn vị tiền tệ mới
                ContentValues values = new ContentValues();
                values.put(COLUMN_AMOUNT, convertedAmount);
                values.put(COLUMN_CURRENCY_ID, currencyHelper.getCurrencyIdByName(newCurrency));

                db.update(TABLE_TRANSACTIONS, values, COLUMN_TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
    public boolean addTransaction(int userId, double amount, String date, String type, int categoryId, int currencyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("amount", amount);
        values.put("transaction_date", date);
        values.put("type", type);
        values.put("category_id", categoryId);
        values.put("currency_id", currencyId);

        long result = db.insert("transactions", null, values);
        db.close();
        return result != -1;
    }


}
