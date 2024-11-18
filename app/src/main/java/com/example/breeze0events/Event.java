package com.example.breeze0events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that defines an Event.
 */

public class Event implements Serializable {
    private String eventId; // Event ID
    private String name; // Name of the event
    private String qrCode; // QR code for the event
    private String posterPhoto; // URL or path to the poster
    private String facility; // Facility where the event is held
    private String startDate; // Event start date
    private String endDate; // Event end date
    private String limitedNumber; // Limit for the number of entrants
    private List<String> entrants; // List of entrant IDs participating in the event
    private List<String> organizers; // List of organizer IDs managing the event
    private String geolocation; // Geolocation of the event

    // Default constructor
    public Event() {
        this.entrants = new ArrayList<>();
        this.organizers = new ArrayList<>();
    }

    // Parameterized constructor
    public Event(String eventId, String name, String qrCode, String posterPhoto, String facility,
                 String startDate, String endDate, String limitedNumber,String geolocation, List<String> entrants,
                 List<String> organizers) {
        this.eventId = eventId;
        this.name = name;
        this.qrCode = qrCode;
        this.posterPhoto = posterPhoto;
        this.facility = facility;
        this.startDate = startDate;
        this.endDate = endDate;
        this.limitedNumber = limitedNumber;
        this.entrants = entrants == null ? new ArrayList<>() : entrants;
        this.organizers = organizers == null ? new ArrayList<>() : organizers;
        this.geolocation = geolocation;
    }

    // Getters and setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getPosterPhoto() {
        return posterPhoto;
    }

    public void setPosterPhoto(String posterPhoto) {
        this.posterPhoto = posterPhoto;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLimitedNumber() {
        return limitedNumber;
    }

    public void setLimitedNumber(String limitedNumber) {
        this.limitedNumber = limitedNumber;
    }

    public List<String> getEntrants() {
        return entrants;
    }

    public void setEntrants(List<String> entrants) {
        this.entrants = entrants == null ? new ArrayList<>() : entrants;
    }

    public void addEntrants(String entrantId) {
        if (this.entrants == null) {
            this.entrants = new ArrayList<>();
        }
        this.entrants.add(entrantId);
    }

    public void removeEntrant(String entrantId) {
        if (this.entrants != null) {
            this.entrants.remove(entrantId);
        }
    }

    public List<String> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(List<String> organizers) {
        this.organizers = organizers == null ? new ArrayList<>() : organizers;
    }

    public void addOrganizer(String organizerId) {
        if (this.organizers == null) {
            this.organizers = new ArrayList<>();
        }
        this.organizers.add(organizerId);
    }

    public void removeOrganizer(String organizerId) {
        if (this.organizers != null) {
            this.organizers.remove(organizerId);
        }
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", posterPhoto='" + posterPhoto + '\'' +
                ", facility='" + facility + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", limitedNumber='" + limitedNumber + '\'' +
                ", entrants=" + entrants +
                ", organizers=" + organizers +
                ", geolocation='" + geolocation + '\'' +
                '}';
    }
}
