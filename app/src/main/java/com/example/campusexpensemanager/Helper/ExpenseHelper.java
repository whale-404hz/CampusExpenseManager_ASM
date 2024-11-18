package com.example.campusexpensemanager.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusexpensemanager.Class.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseHelper {
    private static final String TABLE_EXPENSES = "expenses";
    private static final String COLUMN_EXPENSE_ID = "expense_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_EXPENSE_DATE = "expense_date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CURRENCY_ID = "currency_id"; // Thêm cột currency_id

    private SQLiteOpenHelper dbHelper;

    public ExpenseHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context); // DatabaseHelper là lớp quản lý SQLite chính của bạn
    }

    // Thêm một chi tiêu mới cho người dùng với đơn vị tiền tệ
    public long addExpense(int userId, int categoryId, double amount, String date, String description, int currencyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("expense_date", date);
        values.put("description", description);
        values.put("currency_id", currencyId);

        long result = db.insert("expenses", null, values);
        db.close();
        return result;
    }


    // Lấy danh sách chi tiêu của một người dùng theo userId
    public List<Expense> getExpensesByUserId(int userId) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                int currencyId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENCY_ID)); // Lấy currency_id

                // Tạo đối tượng Expense và thêm vào danh sách
                expenseList.add(new Expense(expenseId, userId, categoryId, amount, date, description, currencyId));
            } while (cursor.moveToNext());
        }
        return expenseList;
    }

    // Tính tổng chi tiêu của người dùng theo userId
    public double getTotalExpense(int userId) {
        double totalExpense = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);  // Lấy tổng chi tiêu từ kết quả truy vấn
        }
        return totalExpense;
    }

    // Lấy tất cả các giao dịch chi tiêu cho một người dùng cụ thể
    public Cursor getAllExpenseTransactions(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

}
