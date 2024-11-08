package com.example.breeze0events;
import java.util.List;

/**
 * This is a class that defines the Admin.
 */

public class Admin {
    private String adminId; // Unique ID for the admin
    private String device; // Device information

    public Admin(String adminId, String device) {
        this.adminId = adminId;
        this.device = device;
    }

    // Getters and setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
