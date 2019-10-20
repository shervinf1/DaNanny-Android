package com.example.dananny;

import com.google.firebase.firestore.DocumentReference;

public class Generation {

    private float current;
    private float watts;
    private long date;
    private DocumentReference sourceID;
    private DocumentReference userID;

    Generation(){
        current = 0;
        watts = 0;
        date = 0;
        sourceID = null;
        userID = null;
    }

    Generation(float c, float w, long d, DocumentReference did, DocumentReference uid){
        current = c;
        watts = w;
        date = d;
        sourceID = did;
        userID = uid;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setWatts(float watts) {
        this.watts = watts;
    }

    public void setSourceID(DocumentReference sourceID) {
        this.sourceID = sourceID;
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

    public float getWatts() {
        return watts;
    }

    public DocumentReference getSourceID() {
        return sourceID;
    }

    public DocumentReference getUserID() {
        return userID;
    }
}
