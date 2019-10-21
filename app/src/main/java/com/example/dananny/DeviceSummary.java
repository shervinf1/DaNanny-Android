package com.example.dananny;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

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
        final Serializable devName = getIntent().getSerializableExtra("Name");
        Serializable devRoom = getIntent().getSerializableExtra("Room");


        System.out.println("Got Intent Gpio: " + devGpio);
        System.out.println("Got Intent Name: " + devName);
        System.out.println("Got Intent Room: " + devRoom);
        System.out.println("To String Method" + devName.toString());

        final CollectionReference documents = db.collection("Devices");
        documents.whereEqualTo("gpio", devGpio).whereEqualTo("userID", userDoc)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Device device = new Device();
                        System.out.println("Task: " + task);
                        System.out.println(doc.getData());

                        Device document = doc.toObject(Device.class);
                        TextView deviceTextView = (TextView) findViewById(R.id.textDevice);
                        TextView roomText = (TextView) findViewById(R.id.roomText);
                        TextView consumptionText = (TextView) findViewById(R.id.consumptionText);

                        deviceTextView.setText(document.getName());
                        roomText.setText(document.getRoom());
                        consumptionText.setText(Float.toString(document.getConsumption()));


                    }

                    System.out.println("Pulling Down");
//                    TextView textView = new TextView(getApplicationContext()).findViewById("@+tex");
//                    textView.setText("testing");


                }

            }
        });

    }


}


