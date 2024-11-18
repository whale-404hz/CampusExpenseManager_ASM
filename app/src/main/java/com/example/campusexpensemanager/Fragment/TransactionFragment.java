package com.example.campusexpensemanager.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusexpensemanager.Adapter.TransactionAdapter;
import com.example.campusexpensemanager.Adapter.Transaction_Notification;
import com.example.campusexpensemanager.Helper.IncomeHelper;
import com.example.campusexpensemanager.Helper.ExpenseHelper;
import com.example.campusexpensemanager.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private IncomeHelper incomeHelper;
    private ExpenseHelper expenseHelper;
    private List<Transaction_Notification.Transaction> transactionList;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        // Khởi tạo các view và helper
        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        incomeHelper = new IncomeHelper(getContext());
        expenseHelper = new ExpenseHelper(getContext());
        transactionList = new ArrayList<>();

        // Lấy user_id đã đăng nhập từ SharedPreferences
        userId = getUserIdFromSession();

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(transactionAdapter);

        // Lấy danh sách giao dịch cho user đã đăng nhập
        fetchTransactions(userId);

        return view;
    }

    private int getUserIdFromSession() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1); // Trả về -1 nếu không tìm thấy user_id
    }

    private void fetchTransactions(int userId) {
        transactionList.clear();

        // Lấy danh sách giao dịch thu nhập của người dùng qua IncomeHelper
        Cursor incomeCursor = incomeHelper.getAllIncomeTransactions(userId);
        if (incomeCursor.moveToFirst()) {
            do {
                int transactionId = incomeCursor.getInt(incomeCursor.getColumnIndexOrThrow("transaction_id"));
                double amount = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow("amount"));
                String source = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("source"));
                String date = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("income_date"));

                Transaction_Notification.Transaction incomeTransaction = new Transaction_Notification.Transaction(
                        transactionId, userId, -1, amount, date, "income", source);
                transactionList.add(incomeTransaction);
            } while (incomeCursor.moveToNext());
        }
        incomeCursor.close();

        // Lấy danh sách giao dịch chi tiêu của người dùng qua ExpenseHelper
        Cursor expenseCursor = expenseHelper.getAllExpenseTransactions(userId);
        if (expenseCursor.moveToFirst()) {
            do {
                int transactionId = expenseCursor.getInt(expenseCursor.getColumnIndexOrThrow("expense_id"));
                double amount = expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow("amount"));
                String description = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("description"));
                String date = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("expense_date"));

                Transaction_Notification.Transaction expenseTransaction = new Transaction_Notification.Transaction(
                        transactionId, userId, -1, amount, date, "expense", description);
                transactionList.add(expenseTransaction);
            } while (expenseCursor.moveToNext());
        }
        expenseCursor.close();

        // Cập nhật RecyclerView khi có thay đổi
        transactionAdapter.notifyDataSetChanged();
    }
}
