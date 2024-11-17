package com.example.breeze0events;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a class that defines an Entrant.
 */

public class Entrant {
    private String entrantId, name, email, phoneNumber, profilePhoto, device;
    private Map<String, String> eventsStatus; // Each Pair represents <eventId, Status>
    private Map<String, String> eventsName; // Each Pair represents <eventId, Name>
    private List<Pair<String, String>> notifications; // List of Pair<String, String> to store notifications
    private Map<String, GeoPoint> geoPointMap;

    public Entrant(String entrantId, String name, String email, String phoneNumber, String profilePhoto, String device,
                   Map<String, String> eventsName, Map<String, String> eventsStatus, List<Pair<String, String>> notifications,
                   Map<String, GeoPoint> geoPointMap) {
        this.entrantId = entrantId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePhoto = profilePhoto;
        this.device = device;
        this.eventsStatus = eventsStatus;
        this.eventsName = eventsName;
        this.notifications = notifications;
        this.geoPointMap = geoPointMap;
    }

    // Getters and setters for all fields


    public Map<String, GeoPoint> getGeoPointMap() {
        return geoPointMap;
    }
    public void addGeoPoint(String eventId, GeoPoint geoPoint) {
        this.geoPointMap.put(eventId, geoPoint);
    }
    public void setGeoPointMap(Map<String, GeoPoint> geoPointMap){
        this.geoPointMap = geoPointMap;
    }
    public void removeGeoPoint(String eventId){
        geoPointMap.remove(eventId);
    }

    public String getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
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

    public Map<String, String> getEventsName() {
        return eventsName;
    }

    public Map<String, String> getEventsStatus() {
        return eventsStatus;
    }

    public void setAllEvents(Map<String, String> eventsName, Map<String, String> eventsStatus) {
        this.eventsName = eventsName;
        this.eventsStatus = eventsStatus;
    }

    public String getStatus(String EventId) {
        String Status = eventsStatus.get(EventId);
        if (Status != null) {
            return Status;
        } else {
            return "-1";
        }
    }

    public boolean checkEventId(String EventId) {
        return eventsName.containsKey(EventId);
    }
    public String getName(){
        return name;
    }
    public String getName(String EventId) {
        String name = eventsName.get(EventId);
        if (name != null) {
            return name;
        } else {
            return null;
        }
    }

    public void UnjoinEvent(String EventId) {
        eventsName.remove(EventId);
        eventsStatus.remove(EventId);
    }

    public void set_add_Event(String eventId, String eventName, String Status) {
        eventsStatus.put(eventId, Status);
        eventsName.put(eventId, eventName);
    }

    public List<Pair<String, String>> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Pair<String, String>> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(String notificationId, String notificationContent) {
        notifications.add(new Pair<>(notificationId, notificationContent));
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
                ", events=" + eventsName +
                ", status=" + eventsStatus +
                ", notifications=" + notifications +
                '}';
    }
}
