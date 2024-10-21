package com.example.breeze0events;

public interface EventCallback {
    void onSuccess(Event event);
    void onFailure(String errorMessage);
}
