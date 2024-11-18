package com.example.campusexpensemanager.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager.Helper.BudgetHelper;
import com.example.campusexpensemanager.Helper.CategoryHelper;
import com.example.campusexpensemanager.Helper.UserHelper;
import com.example.campusexpensemanager.Class.Budget;
import com.example.campusexpensemanager.Class.Category;
import com.example.campusexpensemanager.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BuggetFragment extends Fragment {
    private CategoryHelper categoryHelper;
    private BudgetHelper budgetHelper;
    private UserHelper userHelper;
    private Spinner spinnerCategory;
    private EditText etBudgetAmount, etStartDate, etEndDate;
    private Button btnAddBudget, btnSaveBudget;
    private LineChart lineChart;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bugget, container, false);

        // Khởi tạo các view
        lineChart = view.findViewById(R.id.lineChart);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etBudgetAmount = view.findViewById(R.id.etBudgetAmount);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        btnAddBudget = view.findViewById(R.id.btnAddBudget);
        btnSaveBudget = view.findViewById(R.id.btnSaveBudget);

        // Khởi tạo các helper
        categoryHelper = new CategoryHelper(getContext());
        budgetHelper = new BudgetHelper(getContext());
        userHelper = new UserHelper(getContext());

        // Lấy user ID từ SharedPreferences qua UserHelper
        userId = userHelper.getUserIdFromSession(getContext());

        // Cài đặt bộ chọn ngày cho các trường ngày bắt đầu và ngày kết thúc
        setupDatePickers();

        // Tải danh mục từ cơ sở dữ liệu và thiết lập spinner
        loadCategories();

        // Tải dữ liệu ngân sách hiện có và cài đặt biểu đồ
        loadBudgetData();
        setupAddBudgetButton();

        return view;
    }

    private void setupDatePickers() {
        // Thiết lập DatePickerDialog cho ngày bắt đầu
        etStartDate.setOnClickListener(v -> showDatePickerDialog((date) -> etStartDate.setText(date)));

        // Thiết lập DatePickerDialog cho ngày kết thúc
        etEndDate.setOnClickListener(v -> showDatePickerDialog((date) -> etEndDate.setText(date)));
    }

    // Phương thức hiển thị DatePickerDialog và trả về ngày đã chọn
    private void showDatePickerDialog(OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year1, month1, dayOfMonth) -> {
                    // Định dạng ngày và trả về giá trị cho listener
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    listener.onDateSet(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Giao diện cho việc xử lý ngày được chọn
    private interface OnDateSetListener {
        void onDateSet(String date);
    }

    private void loadCategories() {
        // Lấy danh mục từ CategoryHelper
        List<Category> categories = categoryHelper.getAllCategories();

        // Tạo ArrayAdapter để hiển thị danh mục trong Spinner
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupAddBudgetButton() {
        btnAddBudget.setOnClickListener(v -> {
            // Hiển thị các trường khi nhấn vào "Add Budget"
            spinnerCategory.setVisibility(View.VISIBLE);
            etBudgetAmount.setVisibility(View.VISIBLE);
            etStartDate.setVisibility(View.VISIBLE);
            etEndDate.setVisibility(View.VISIBLE);
            btnSaveBudget.setVisibility(View.VISIBLE);
        });

        btnSaveBudget.setOnClickListener(v -> {
            // Lưu ngân sách mới
            Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
            int categoryId = selectedCategory.getId(); // Lấy category_id
            String amountStr = etBudgetAmount.getText().toString();
            String startDate = etStartDate.getText().toString();
            String endDate = etEndDate.getText().toString();

            if (!amountStr.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty()) {
                float amount = Float.parseFloat(amountStr);

                // Lưu vào cơ sở dữ liệu với category_id qua BudgetHelper
                boolean isAdded = budgetHelper.addBudget(userId, categoryId, amount, startDate, endDate);
                if (isAdded) {
                    Toast.makeText(getActivity(), "Thêm ngân sách thành công", Toast.LENGTH_SHORT).show();

                    // Xóa các trường và ẩn chúng
                    clearAndHideFields();

                    // Tải lại dữ liệu ngân sách và làm mới biểu đồ
                    loadBudgetData();
                } else {
                    Toast.makeText(getActivity(), "Không thể thêm ngân sách", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAndHideFields() {
        // Xóa tất cả các trường
        etBudgetAmount.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
        spinnerCategory.setSelection(0); // Đặt lại spinner về giá trị mặc định

        // Ẩn các trường
        spinnerCategory.setVisibility(View.GONE);
        etBudgetAmount.setVisibility(View.GONE);
        etStartDate.setVisibility(View.GONE);
        etEndDate.setVisibility(View.GONE);
        btnSaveBudget.setVisibility(View.GONE);
    }

    private void loadBudgetData() {
        // Lấy dữ liệu ngân sách từ BudgetHelper và cập nhật biểu đồ
        List<Budget> budgets = budgetHelper.getBudgetsByUserId(userId);

        // Cập nhật biểu đồ với dữ liệu ngân sách mới nhất
        updateLineChart(budgets);
    }

    private void updateLineChart(List<Budget> budgets) {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()); // Định dạng ngày

        for (Budget budget : budgets) {
            String startDateStr = dateFormat.format(budget.getStartDate()); // Chuyển ngày sang chuỗi định dạng "yyyyMMdd"
            float startDateFloat = Float.parseFloat(startDateStr); // Chuyển thành float

            entries.add(new Entry(startDateFloat, budget.getAmount())); // Sử dụng getAmount() sau khi đã kiểm tra
        }

        LineDataSet dataSet = new LineDataSet(entries, "Ngân sách");
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();  // Làm mới biểu đồ
    }
}
