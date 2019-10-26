package com.example.dananny;

import com.google.firebase.firestore.DocumentReference;

public class Sources {

    private String name;
    private DocumentReference userID;

    Sources(){
        name = "";
        userID = null;
    }

    Sources(String n, DocumentReference doc){
        name = n;
        userID = doc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserID(DocumentReference userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public DocumentReference getUserID() {
        return userID;
    }
}
