package com.example.breeze0events;

import java.util.List;

public class Organizer {
    private String organizerId;
    private String device;
    private List<String> events; // List of event IDs organized by the organizer

    public Organizer(String organizerId, String device, List<String> events) {
        this.organizerId = organizerId;
        this.device = device;
        this.events = events;
    }

    // Getters and setters
    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "organizerId='" + organizerId + '\'' +
                ", device='" + device + '\'' +
                ", events=" + events +
                '}';
    }
}
