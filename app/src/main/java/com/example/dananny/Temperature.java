package com.example.dananny;

import com.google.firebase.firestore.DocumentReference;

public class Temperature {

    private long date;
    private int temperature;
    private DocumentReference userID;

    Temperature(){
        date = 0;
        temperature = 0;
        userID = null;
    }

    Temperature(long date, int temperature, DocumentReference userID){
        this.date = date;
        this.temperature = temperature;
        this.userID = userID;
    }

    public DocumentReference getUserID() {
        return userID;
    }

    public void setUserID(DocumentReference userID) {
        this.userID = userID;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
