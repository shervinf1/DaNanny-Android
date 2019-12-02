package com.example.dananny;

import com.google.firebase.firestore.DocumentReference;

public class Battery {

    private float batteryStatus;
    private long date;
    private DocumentReference userID;

    Battery(){
        this.batteryStatus = 0;
        this.date = 0;
        this.userID = null;
    }

    Battery(float batteryStatus, long date, DocumentReference userID){
        this.batteryStatus = batteryStatus;
        this.date = date;
        this.userID = userID;
    }

    public float getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(float batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public DocumentReference getUserID() {
        return userID;
    }

    public void setUserID(DocumentReference userID) {
        this.userID = userID;
    }
}
