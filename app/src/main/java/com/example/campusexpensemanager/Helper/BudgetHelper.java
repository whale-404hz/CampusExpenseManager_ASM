package com.example.campusexpensemanager.Helper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.campusexpensemanager.Class.Budget;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class BudgetHelper {
    private static final String TABLE_BUDGETS = "budgets";
    private static final String COLUMN_BUDGET_ID = "budget_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_BUDGET_AMOUNT = "budget_amount";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";
    private SQLiteOpenHelper dbHelper;
    private SimpleDateFormat dateFormat;
    public BudgetHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context); // Giả sử DatabaseHelper là lớp quản lý SQLite chính của bạn
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Định dạng ngày tháng
    }
    public List<Budget> getBudgetsByUserId(int userId) {
        List<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Truy vấn để lấy ngân sách cho người dùng cụ thể
        String query = "SELECT * FROM " + TABLE_BUDGETS + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Lấy giá trị từ con trỏ
                    int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_ID));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                    float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_AMOUNT));
                    String startDateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
                    String endDateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
                    // Chuyển đổi startDate và endDate từ String sang Date
                    Date startDate = null;
                    Date endDate = null;
                    try {
                        if (startDateString != null) startDate = dateFormat.parse(startDateString);
                        if (endDateString != null) endDate = dateFormat.parse(endDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Tạo đối tượng Budget và thêm vào danh sách
                    budgets.add(new Budget(budgetId, userId, categoryId, amount, startDate, endDate));
                } while (cursor.moveToNext());
            }
        }
        return budgets;
    }
    public boolean addBudget(int userId, int categoryId, float amount, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_CATEGORY_ID, categoryId);
        values.put(COLUMN_BUDGET_AMOUNT, amount);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);
        long result = db.insert(TABLE_BUDGETS, null, values);
        return result != -1; // Trả về true nếu thêm thành công
    }
    public boolean updateBudget(int budgetId, float newAmount, String newStartDate, String newEndDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUDGET_AMOUNT, newAmount);
        values.put(COLUMN_START_DATE, newStartDate);
        values.put(COLUMN_END_DATE, newEndDate);
        int rowsAffected = db.update(TABLE_BUDGETS, values, COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});
        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }
}
