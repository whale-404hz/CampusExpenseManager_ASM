package com.example.campusexpensemanager.Helper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.campusexpensemanager.Class.Category;
import java.util.ArrayList;
import java.util.List;
public class CategoryHelper {
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private SQLiteOpenHelper dbHelper;
    public CategoryHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context); // Giả sử DatabaseHelper là lớp quản lý SQLite chính của bạn
    }
    public int addNewCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        long result = db.insert(TABLE_CATEGORIES, null, values);
        return result != -1 ? (int) result : -1; // Trả về ID của danh mục mới nếu thêm thành công, -1 nếu không
    }
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));

                categories.add(new Category(categoryId, categoryName));
            } while (cursor.moveToNext());
        }
        return categories;
    }
    public List<String> getAllCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CATEGORY_NAME + " FROM " + TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categoryNames.add(cursor.getString(0)); // Lấy tên danh mục từ kết quả truy vấn
            } while (cursor.moveToNext());
        }
        return categoryNames;
    }
    public int getCategoryIdByName(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CATEGORY_ID + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME + " = ?", new String[]{categoryName});

        int categoryId = -1;
        if (cursor.moveToFirst()) {
            categoryId = cursor.getInt(0); // Lấy ID của danh mục
        }
        return categoryId;
    }
}
