package com.example.breeze0events;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String eventId; // Event ID (previously organizerId)
    private String name; // Name of the event
    private String qrCode; // QR code for the event
    private String posterPhoto; // URL or path to the poster
    private String facility; // Facility where the event is held
    private String startDate; // Event start date
    private String endDate; // Event end date
    private List<String> entrants; // List of entrant IDs participating in the event
    private List<String> organizers; // List of organizer IDs managing the event


    public Event(){}

    public Event(
            String eventId, String name, String qrCode, String posterPhoto,
            String facility, String startDate, String endDate, List<String> entrants,
            List<String> organizers) {
        this.eventId = eventId != null ? eventId : "";;
        this.name = name != null ? name : "";
        this.qrCode = qrCode != null ? qrCode : "";
        this.posterPhoto = posterPhoto != null ? posterPhoto : "";
        this.facility = facility != null ? facility : "";
        this.startDate = startDate != null ? startDate : "";
        this.endDate = endDate != null ? endDate : "";
        this.entrants = entrants != null ? entrants : new ArrayList<>();
        this.organizers = organizers != null ? organizers : new ArrayList<>();
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

    public List<String> getEntrants() {
        return entrants;
    }

    public void setEntrants(List<String> entrants) {
        this.entrants = entrants;
    }

    public List<String> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(List<String> organizers) {
        this.organizers = organizers;
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
                ", entrants=" + entrants +
                ", organizers=" + organizers +
                '}';
    }
}
