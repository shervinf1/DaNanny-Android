package com.example.dananny;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DeviceSummary extends AppCompatActivity implements Serializable {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();
    final DocumentReference userDoc = db.collection("Users").document(userID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_summary);


        Serializable devGpio = getIntent().getSerializableExtra("Equipment");
        Serializable devName = getIntent().getSerializableExtra("Name");
        Serializable devRoom = getIntent().getSerializableExtra("Room");


        System.out.println("Got Intent Gpio: " + devGpio);
        System.out.println("Got Intent Name: " + devName);
        System.out.println("Got Intent Room: " + devRoom);

        final CollectionReference documents = db.collection("Devices");
        documents.whereEqualTo("gpio", devGpio).whereEqualTo("userID", userDoc)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Device device = new Device();
                        System.out.println("Task: "+task);
                        System.out.println(doc.getData()) ;

                        Device document = doc.toObject(Device.class);
                        System.out.println("THe Name: "+document.getName());
                    }

                    System.out.println("Pulling Down");
                }
            }
        });

    }


}


