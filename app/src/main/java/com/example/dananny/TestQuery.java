package com.example.dananny;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class TestQuery extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();

    final DocumentReference userDoc = db.collection("Test").document("Query").
            collection("Users").document(userID);

    final static String TAG = "TestQuery";
    Button BtnUser;
    Button BtnNewDevice;
    Button BtnAllDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_query);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BtnUser = findViewById(R.id.createBtn);
        BtnNewDevice = findViewById(R.id.newDeviceBtn);
        BtnAllDevice = findViewById(R.id.allDeviceBtn);

        BtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUserID();
            }
        });

        BtnNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    insertNewDevice();
            }
        });

        BtnAllDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    downloadAllDevices();
            }
        });

    Log.d("Test User: ", userID);


    }

    public void uploadUserID(){
        //Values
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Juan del Pueblo");
        user.put("email", "juan@email.com");


        db.collection("Test").document("Query").
                collection("Users").document(userID).
                set(user, SetOptions.merge());
    }

    public void insertNewDevice(){
        Map<String, Object> device = new HashMap<>();
        device.put("consumption", 120);
        device.put("gpio", 23);
        device.put("room", "Sala");
        device.put("status", true);
        device.put("threshold", 300);
        device.put("userID", userDoc);

        db.collection("Test").document("Query").
                collection("Device").document().set(device, SetOptions.merge());

    }

    public void downloadAllDevices(){
        db.collection("Test").document("Query").
                collection("Device").whereEqualTo("userID", userDoc).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc: task.getResult()) {
                            Log.d("From Device", doc.getId() + " => " + doc.getData());
                        }
                    }
                });

    }

    public void insertMeasurement(){

    }

}
