package com.example.campusexpensemanager.Class;
import androidx.annotation.NonNull;
import java.sql.Timestamp;
import java.util.Objects;

public class BackupRestore {
    private int backupId;
    private int userId;
    private Timestamp backupTime;
    private String backupFile;

    public BackupRestore(int backupId, int userId, Timestamp backupTime, String backupFile) {
        this.backupId = backupId;
        this.userId = userId;
        this.backupTime = backupTime;
        this.backupFile = backupFile;
    }

    public int getBackupId() {
        return backupId;
    }

    public void setBackupId(int backupId) {
        this.backupId = backupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(Timestamp backupTime) {
        this.backupTime = backupTime;
    }

    public String getBackupFile() {
        return backupFile;
    }

    public void setBackupFile(String backupFile) {
        this.backupFile = backupFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackupRestore that = (BackupRestore) o;
        return backupId == that.backupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(backupId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Backup file: " + backupFile;
    }
}

