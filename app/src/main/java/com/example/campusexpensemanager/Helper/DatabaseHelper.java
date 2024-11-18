package com.example.campusexpensemanager.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Thông tin cơ bản về cơ sở dữ liệu
    public static final String DATABASE_NAME = "qlct.db"; // Tên tệp cơ sở dữ liệu trong thư mục assets
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PATH = "/data/data/com.example.campusexpensemanager/databases/"; // Đường dẫn đến thư mục lưu trữ nội bộ

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // Kiểm tra nếu cơ sở dữ liệu chưa tồn tại trong bộ nhớ nội bộ thì sao chép từ assets
        if (!checkDatabase()) {
            copyDatabaseFromAssets();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không cần tạo bảng ở đây vì cơ sở dữ liệu đã có sẵn trong assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp nếu cần thiết
    }

    // Kiểm tra xem cơ sở dữ liệu đã có trong bộ nhớ nội bộ chưa
    private boolean checkDatabase() {
        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    // Sao chép cơ sở dữ liệu từ assets vào bộ nhớ nội bộ
    private void copyDatabaseFromAssets() {
        try {
            // Mở tệp cơ sở dữ liệu trong assets
            InputStream input = context.getAssets().open(DATABASE_NAME);

            // Đường dẫn đến tệp đích trong bộ nhớ nội bộ
            String outFileName = DATABASE_PATH + DATABASE_NAME;
            File databaseFolder = new File(DATABASE_PATH);
            if (!databaseFolder.exists()) {
                databaseFolder.mkdirs(); // Tạo thư mục nếu chưa tồn tại
            }

            // Tạo output stream để sao chép dữ liệu
            OutputStream output = new FileOutputStream(outFileName);

            // Sao chép dữ liệu từ input stream sang output stream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Đóng các stream
            output.flush();
            output.close();
            input.close();

            Log.d("DatabaseHelper", "Cơ sở dữ liệu đã được sao chép thành công từ assets!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DatabaseHelper", "Lỗi khi sao chép cơ sở dữ liệu từ assets: " + e.getMessage());
        }
    }

    // Phương thức để mở cơ sở dữ liệu trong chế độ ghi
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // Phương thức để mở cơ sở dữ liệu trong chế độ chỉ đọc
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
    }
}
