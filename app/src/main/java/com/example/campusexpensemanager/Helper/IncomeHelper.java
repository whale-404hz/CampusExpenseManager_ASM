package com.example.campusexpensemanager.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IncomeHelper {
    private static final String TABLE_INCOME = "income";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_INCOME_DATE = "income_date";
    private static final String COLUMN_INCOME_SOURCE = "source"; // Nguồn thu nhập
    private static final String COLUMN_CURRENCY_ID = "currency_id"; // Thêm cột currency_id

    private SQLiteOpenHelper dbHelper;

    public IncomeHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context); // DatabaseHelper là lớp quản lý SQLite chính của bạn
    }

    // Phương thức để lấy tổng thu nhập của người dùng theo userId
    public float getTotalIncome(int userId) {
        float totalIncome = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Truy vấn để lấy tổng thu nhập của người dùng
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_INCOME + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            totalIncome = cursor.getFloat(0);  // Lấy tổng thu nhập từ kết quả truy vấn
        }

        cursor.close();
        db.close();

        return totalIncome;
    }

    // Phương thức để thêm thu nhập vào bảng income
    public long addIncome(int userId, double amount, String date, String source, int currencyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("amount", amount);
        values.put("income_date", date);
        values.put("source", source);
        values.put("currency_id", currencyId);

        long result = db.insert("income", null, values);
        db.close();
        return result;
    }


    // Phương thức để lấy tất cả các giao dịch thu nhập cho một người dùng cụ thể
    public Cursor getAllIncomeTransactions(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INCOME + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return cursor;
    }
}
