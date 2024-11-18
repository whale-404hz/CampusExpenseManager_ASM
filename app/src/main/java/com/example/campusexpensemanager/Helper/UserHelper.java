package com.example.campusexpensemanager.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.example.campusexpensemanager.Class.User;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserHelper {

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILE_IMAGE = "avatar"; // Avatar lưu dưới dạng blob
    private static final String PREFS_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private SQLiteOpenHelper dbHelper;

    public UserHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context); // DatabaseHelper là lớp quản lý SQLite chính
    }

    // Thêm người dùng mới
    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashPassword(password)); // Băm mật khẩu trước khi lưu

        long result = db.insert(TABLE_USERS, null, values);

        return result != -1; // Trả về true nếu thêm thành công
    }

    // Băm mật khẩu sử dụng SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi khi mã hóa mật khẩu", e);
        }
    }

    // Kiểm tra đăng nhập và trả về user_id nếu thành công
    public int validateUserLogin(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String hashedPassword = hashPassword(password); // Băm mật khẩu đã nhập

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, hashedPassword});
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            cursor.close();
        }

        return userId; // Trả về user_id nếu đăng nhập thành công, -1 nếu thất bại
    }

    // Lấy thông tin người dùng theo user_id
    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            byte[] profileImage = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE)); // Nếu cần lấy ảnh đại diện

            user = new User(userId, username, email, profileImage); // Giả định User có constructor phù hợp
            cursor.close();
        }

        return user;
    }
    // Hàm lấy user_id từ SharedPreferences
    public int getUserIdFromSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, -1); // Trả về -1 nếu không tìm thấy user_id
    }

    // Phương thức lưu user_id vào SharedPreferences khi người dùng đăng nhập
    public void saveUserIdToSession(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Phương thức xóa user_id khỏi SharedPreferences khi người dùng đăng xuất
    public void clearUserSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.apply();
    }
    // Phương thức để lấy mật khẩu băm của người dùng dựa trên username
    public String getPasswordForUser(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String hashedPassword = null;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));

        }

        return hashedPassword; // Trả về mật khẩu đã băm hoặc null nếu không tìm thấy
    }

    // Cập nhật mật khẩu người dùng dựa trên user_id
    public boolean updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, hashPassword(newPassword)); // Băm mật khẩu mới

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }

    // Cập nhật mật khẩu người dùng dựa trên username
    public boolean updatePasswordForUser(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, hashPassword(newPassword)); // Băm mật khẩu mới

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});

        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }

    // Cập nhật ảnh đại diện của người dùng dưới dạng Bitmap
    public void updateUserAvatar(int userId, Bitmap avatarBitmap) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        byte[] avatarBytes = bitmapToByteArray(avatarBitmap);
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_IMAGE, avatarBytes);

        db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

    }

    // Chuyển đổi Bitmap thành mảng byte
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // Nén thành PNG
        return byteArrayOutputStream.toByteArray();
    }

    // Lấy avatar của người dùng dưới dạng byte array
    public byte[] getUserAvatar(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PROFILE_IMAGE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        byte[] avatarBytes = null;
        if (cursor != null && cursor.moveToFirst()) {
            avatarBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE));

        }

        return avatarBytes;
    }
    // Phương thức để lấy username theo user_id
    public String getUsernameById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String username = null;

        // Truy vấn lấy tên người dùng từ bảng users theo user_id
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));

        }


        return username;
    }
}
