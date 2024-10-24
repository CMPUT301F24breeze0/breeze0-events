package com.example.breeze0events;
import java.util.List;
public class Facility {
    private String facilityId; // Unique ID for the facility
    private String location; // Location of the facility
    private String device; // Device information

    public Facility(String facilityId, String location, String device) {
        this.facilityId = facilityId;
        this.location = location;
        this.device = device;
    }

    // Getters and setters
    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "facilityId='" + facilityId + '\'' +
                ", location='" + location + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
