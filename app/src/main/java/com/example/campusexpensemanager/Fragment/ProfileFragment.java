package com.example.campusexpensemanager.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager.Helper.Auth;
import com.example.campusexpensemanager.Helper.UserHelper;
import com.example.campusexpensemanager.Login;
import com.example.campusexpensemanager.R;
import com.example.campusexpensemanager.SettingsActivity;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView ivAvatar;
    private TextView tvUsername;
    private Button btnChangeAvatar, btnSettings, btnLogout;



    private UserHelper userHelper;
    private Auth auth;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Initialize helpers
        userHelper = new UserHelper(requireActivity());
        auth = new Auth(requireActivity());
        userId = auth.getUserId();

        // Load user data
        loadUserInfo();

        // Setup button click listeners
        btnChangeAvatar.setOnClickListener(v -> openImagePicker());
        btnLogout.setOnClickListener(v -> logout());
        btnSettings.setOnClickListener(v -> openSettings());


        return view;
    }
    private void openSettings() {
        Intent intent = new Intent(requireContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void loadUserInfo() {
        String username = userHelper.getUsernameById(userId);
        if (username != null) {
            tvUsername.setText(username);
        } else {
            Toast.makeText(requireActivity(), "User not found!", Toast.LENGTH_SHORT).show();
        }

        byte[] avatarBytes = userHelper.getUserAvatar(userId);
        if (avatarBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            ivAvatar.setImageBitmap(bitmap);
        } else {ivAvatar.setImageResource(R.mipmap.avatar_foreground);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                ivAvatar.setImageBitmap(bitmap);
                userHelper.updateUserAvatar(userId, bitmap);
                Toast.makeText(requireActivity(), "Avatar updated successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), "Error while selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        auth.logout();
        Intent intent = new Intent(requireContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}