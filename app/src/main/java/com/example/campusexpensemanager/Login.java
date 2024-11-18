package com.example.campusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager.Helper.Auth;
import com.example.campusexpensemanager.Helper.UserHelper;

public class Login extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private ImageView viewPass;
    private UserHelper userHelper;
    private Auth auth; // Sử dụng Auth để quản lý thông tin đăng nhập
    private Button btnLogin;
    private TextView register, changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Khởi tạo Auth và kiểm tra trạng thái đăng nhập
        auth = new Auth(this);
        if (auth.isLoggedIn()) {
            // Nếu đã đăng nhập, chuyển thẳng đến MainActivity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Khởi tạo các biến
        viewPass = findViewById(R.id.viewpass);
        btnLogin = findViewById(R.id.btnlogin);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        register = findViewById(R.id.register);
        changePassword = findViewById(R.id.changePassword);

        viewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra kiểu nhập hiện tại của EditText mật khẩu
                if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    // Hiển thị mật khẩu
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    viewPass.setImageResource(R.drawable.baseline_toggle_on_24);
                } else {
                    // Ẩn mật khẩu
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    viewPass.setImageResource(R.drawable.baseline_toggle_off_24);
                }
                // Đặt vị trí con trỏ ở cuối văn bản
                etPassword.setSelection(etPassword.getText().length());
            }
        });


        // Chuyển trang đổi mật khẩu
        changePassword.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, changepassword.class);
            startActivity(intent);
        });

        // Chuyển sang trang đăng ký
        register.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, register.class);
            startActivity(intent);
        });

        // Khởi tạo UserHelper
        userHelper = new UserHelper(this);

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            int userId = userHelper.validateUserLogin(username, password);
            if (userId != -1) {
                // Sử dụng Auth để lưu trạng thái đăng nhập
                auth.saveLogin(userId);

                Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
