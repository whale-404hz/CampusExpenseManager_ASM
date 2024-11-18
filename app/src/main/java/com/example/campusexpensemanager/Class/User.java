package com.example.campusexpensemanager.Class;

public class User {
    private int userId;
    private String username;
    private String email;
    private byte[] profileImage; // Thay đổi kiểu dữ liệu của profileImage thành byte[]

    // Constructor mới hỗ trợ byte[] cho profileImage
    public User(int userId, String username, String email, byte[] profileImage) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
    }

    // Các phương thức getter và setter cho các thuộc tính
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}
