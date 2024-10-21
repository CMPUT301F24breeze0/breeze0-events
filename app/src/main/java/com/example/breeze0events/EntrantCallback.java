package com.example.breeze0events;
public interface EntrantCallback {
    void onSuccess(Entrant entrant);
    void onFailure(String errorMessage);

}
