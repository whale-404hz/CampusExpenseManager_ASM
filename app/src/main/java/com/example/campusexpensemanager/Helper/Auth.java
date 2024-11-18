package com.example.campusexpensemanager.Helper;

import android.content.Context;
import android.content.SharedPreferences;
public class Auth {
        private static final String PREFS_NAME = "user_session";
        private static final String KEY_IS_LOGGED_IN = "is_logged_in";
        private static final String KEY_USER_ID = "user_id";
        private SharedPreferences sharedPreferences;
        public Auth(Context context) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        // Lưu trạng thái đăng nhập
        public void saveLogin(int userId) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putInt(KEY_USER_ID, userId);
            editor.apply();
        }
        // Kiểm tra xem người dùng có đang đăng nhập không
        public boolean isLoggedIn() {
            return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        }
        // Lấy ID người dùng đã đăng nhập
        public int getUserId() {
            return sharedPreferences.getInt(KEY_USER_ID, -1);
        }
        // Xóa thông tin đăng nhập (Đăng xuất)
        public void logout() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

