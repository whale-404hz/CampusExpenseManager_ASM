package com.example.campusexpensemanager.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CurrencyHelper {

    private static final String TABLE_CURRENCY = "currency";
    private static final String COLUMN_CURRENCY_ID = "currency_id";
    private static final String COLUMN_CURRENCY_NAME = "currency_name";
    private static final String COLUMN_CURRENCY_CODE = "currency_code";
    private static final String COLUMN_EXCHANGE_RATE = "conversion_rate"; // Tỷ giá hối đoái

    private SQLiteOpenHelper dbHelper;

    public CurrencyHelper(Context context) {
        this.dbHelper = new DatabaseHelper(context); // Sử dụng DatabaseHelper chính
    }

    // Phương thức để lấy tỷ giá hối đoái dựa trên tên đơn vị tiền tệ
    public double getExchangeRate(String currencyName) {
        if (currencyName == null || currencyName.isEmpty()) {
            // Ghi log cảnh báo và trả về tỷ giá mặc định nếu tên tiền tệ là null hoặc rỗng
            Log.e("CurrencyHelper", "Currency name is null or empty. Unable to retrieve exchange rate.");
            return 1.0; // Trả về giá trị mặc định hoặc giá trị thích hợp
        }

        double exchangeRate = 1.0; // Giá trị mặc định nếu không tìm thấy đơn vị tiền tệ
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT " + COLUMN_EXCHANGE_RATE + " FROM " + TABLE_CURRENCY + " WHERE " + COLUMN_CURRENCY_NAME + " = ?", new String[]{currencyName});
            if (cursor.moveToFirst()) {
                exchangeRate = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXCHANGE_RATE));
            } else {
                Log.e("CurrencyHelper", "Không tìm thấy đơn vị tiền tệ trong cơ sở dữ liệu: " + currencyName);
            }
        } catch (Exception e) {
            Log.e("CurrencyHelper", "Lỗi khi truy xuất tỷ giá hối đoái cho " + currencyName, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return exchangeRate;
    }


    // Lấy tất cả tên đơn vị tiền tệ
    public List<String> getAllCurrencies() {
        List<String> currencies = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CURRENCY_NAME + " FROM " + TABLE_CURRENCY, null);

        if (cursor.moveToFirst()) {
            do {
                currencies.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENCY_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return currencies;
    }

    // Phương thức để lấy `currency_id` dựa trên tên đơn vị tiền tệ
    public int getCurrencyIdByName(String currencyName) {
        int currencyId = -1;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CURRENCY_ID + " FROM " + TABLE_CURRENCY + " WHERE " + COLUMN_CURRENCY_NAME + " = ?", new String[]{currencyName});

        if (cursor.moveToFirst()) {
            currencyId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENCY_ID));
        }
        cursor.close();
        db.close();

        return currencyId;
    }

    // Phương thức để lấy tên đơn vị tiền tệ dựa trên `currency_id`
    public String getCurrencyNameById(int currencyId) {
        String currencyName = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CURRENCY_NAME + " FROM " + TABLE_CURRENCY + " WHERE " + COLUMN_CURRENCY_ID + " = ?", new String[]{String.valueOf(currencyId)});

        if (cursor.moveToFirst()) {
            currencyName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENCY_NAME));
        }
        cursor.close();
        db.close();

        return currencyName;
    }

    // Phương thức cập nhật tỷ giá hối đoái vào cơ sở dữ liệu
    public void updateExchangeRates(JSONObject exchangeRates) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Iterator<String> keys = exchangeRates.keys();
            while (keys.hasNext()) {
                String currencyName = keys.next();
                double exchangeRate = exchangeRates.getDouble(currencyName);

                // Cập nhật tỷ giá cho mỗi đơn vị tiền tệ
                ContentValues values = new ContentValues();
                values.put(COLUMN_CURRENCY_NAME, currencyName);
                values.put(COLUMN_CURRENCY_CODE, currencyName); // Đảm bảo cột currency_code được cập nhật
                values.put(COLUMN_EXCHANGE_RATE, exchangeRate);

                // Thay vì xóa toàn bộ bảng, hãy thử cập nhật từng dòng
                int rowsAffected = db.update(TABLE_CURRENCY, values, COLUMN_CURRENCY_NAME + " = ?", new String[]{currencyName});

                // Nếu không tìm thấy currency_name trong database, thêm vào mới
                if (rowsAffected == 0) {
                    db.insert(TABLE_CURRENCY, null, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
