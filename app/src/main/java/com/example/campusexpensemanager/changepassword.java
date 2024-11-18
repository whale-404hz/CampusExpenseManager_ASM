package com.example.campusexpensemanager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager.Helper.UserHelper;
import com.example.campusexpensemanager.Helper.SecurityHelper;

public class changepassword extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private UserHelper userHelper; // Sử dụng UserHelper để xử lý người dùng
    private SecurityHelper securityHelper; // Sử dụng SecurityHelper để mã hóa mật khẩu
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_changepassword);

        etOldPassword = findViewById(R.id.edtOldpass);
        etNewPassword = findViewById(R.id.edtNewpass);
        etConfirmPassword = findViewById(R.id.edtComfirm);
        btnChangePassword = findViewById(R.id.btnConfirm);

        // Khởi tạo UserHelper và SecurityHelper
        userHelper = new UserHelper(this);
        securityHelper = new SecurityHelper();

        // Khởi tạo SharedPreferences để lấy thông tin người dùng hiện tại
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        // Xử lý sự kiện khi bấm nút "Đổi mật khẩu"
        btnChangePassword.setOnClickListener(view -> changePassword());
    }

    // Phương thức xử lý đổi mật khẩu
    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Lấy username từ SharedPreferences
        String username = sharedPreferences.getString("username", null);
        if (username == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra các trường có trống hay không
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter complete information", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp không
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Confirmation password does not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy mật khẩu hiện tại từ cơ sở dữ liệu và kiểm tra mật khẩu cũ
        String currentHashedPassword = userHelper.getPasswordForUser(username);
        if (currentHashedPassword == null) {
            Toast.makeText(this, "Username does not exist!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sử dụng SecurityHelper để băm mật khẩu cũ và so sánh
        String hashedOldPassword = securityHelper.hashPassword(oldPassword);
        if (!hashedOldPassword.equals(currentHashedPassword)) {
            Toast.makeText(this, "Old password is incorrect!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu mới
        boolean updateSuccess = userHelper.updatePasswordForUser(username, newPassword);
        if (updateSuccess) {
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity sau khi đổi mật khẩu thành công
        } else {
            Toast.makeText(this, "Error changing password", Toast.LENGTH_SHORT).show();
        }
    }
}
