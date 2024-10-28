package com.example.breeze0events;
import java.util.List;

public class Entrant {
    private String entrantId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePhoto;
    private String device;
    private List<Pair<String, String>> events; // Each Pair represents <eventId, location>

    public Entrant(String entrantId, String name, String email, String phoneNumber, String profilePhoto, String device, List<Pair<String, String>> events) {
        this.entrantId = entrantId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePhoto = profilePhoto;
        this.device = device;
        this.events = events;
    }

    // Getters and setters for all fields

    public String getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public List<Pair<String, String>> getEvents() {
        return events;
    }

    public void setEvents(List<Pair<String, String>> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Entrant{" +
                "entrantId='" + entrantId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", device='" + device + '\'' +
                ", events=" + events +
                '}';
    }
}
