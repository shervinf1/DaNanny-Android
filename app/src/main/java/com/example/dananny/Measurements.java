package com.example.dananny;

import com.google.firebase.firestore.DocumentReference;

public class Measurements {

    private float voltage;
    private float current;
    private float watts;
    private long date;
    private DocumentReference deviceID;
    private DocumentReference userID;

    Measurements(){
        voltage = 0;
        current = 0;
        watts = 0;
        date = 0;
        deviceID = null;
        userID = null;
    }

    Measurements(float v, float c, float w, long d, DocumentReference did, DocumentReference uid){
        voltage = v;
        current = c;
        watts = w;
        date = d;
        deviceID = did;
        userID = uid;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public void setWatts(float watts) {
        this.watts = watts;
    }

    public void setDeviceID(DocumentReference deviceID) {
        this.deviceID = deviceID;
    }

    public void setUserID(DocumentReference userID) {
        this.userID = userID;
    }

    public long getDate() {
        return date;
    }

    public float getCurrent() {
        return current;
    }

    public float getVoltage() {
        return voltage;
    }

    public float getWatts() {
        return watts;
    }

    public DocumentReference getDeviceID() {
        return deviceID;
    }

    public DocumentReference getUserID() {
        return userID;
    }
}

