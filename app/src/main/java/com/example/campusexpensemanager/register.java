package com.example.campusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager.Helper.UserHelper;

public class register extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail;
    private Button btRegister;
    private UserHelper userHelper; // Sử dụng UserHelper để thêm người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo các view và UserHelper
        etUsername = findViewById(R.id.edtUser);
        etPassword = findViewById(R.id.edtPass);
        etEmail = findViewById(R.id.edtGmail);
        btRegister = findViewById(R.id.btnRegister);
        userHelper = new UserHelper(this);

        // Lắng nghe sự kiện khi nút đăng ký được nhấn
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Kiểm tra hợp lệ của các trường nhập liệu
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(register.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(register.this, "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
                } else {
                    // Thêm người dùng vào cơ sở dữ liệu thông qua UserHelper
                    boolean success = userHelper.addUser(username, email, password);
                    if (success) {
                        Toast.makeText(register.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(register.this, Login.class);
                        startActivity(intent);
                        finish(); // Kết thúc Activity đăng ký
                    } else {
                        Toast.makeText(register.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
