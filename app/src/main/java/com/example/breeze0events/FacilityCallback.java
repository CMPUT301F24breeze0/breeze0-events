package com.example.breeze0events;

public interface FacilityCallback {
    void onSuccess(Facility facility);
    void onFailure(String errorMessage);
}
