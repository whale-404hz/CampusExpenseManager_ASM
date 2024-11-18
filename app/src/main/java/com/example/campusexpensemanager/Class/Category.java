package com.example.campusexpensemanager.Class;

public class Category {
    private int id;
    private String name;
    private int someOtherField; // Nếu có thuộc tính này

    // Constructor hai tham số
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Constructor ba tham số
    public Category(int id, String name, int someOtherField) {
        this.id = id;
        this.name = name;
        this.someOtherField = someOtherField;
    }

    // Getters và Setters cho các thuộc tính
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSomeOtherField() {
        return someOtherField;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSomeOtherField(int someOtherField) {
        this.someOtherField = someOtherField;
    }

    @Override
    public String toString() {
        return name; // Hiển thị tên danh mục trong Spinner hoặc ListView
    }
}
