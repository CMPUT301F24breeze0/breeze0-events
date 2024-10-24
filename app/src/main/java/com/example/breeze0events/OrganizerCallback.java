package com.example.breeze0events;

public interface OrganizerCallback {
    void onSuccess(Organizer organizer);
    void onFailure(String errorMessage);
}
