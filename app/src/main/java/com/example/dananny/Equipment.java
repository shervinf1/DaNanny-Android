package com.example.dananny;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Equipment extends AppCompatActivity implements Serializable {

    LinearLayout listView;
    Button button;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();
    final DocumentReference userDoc = db.collection("Users").document(userID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_equipment);
        getSupportActionBar().hide();

        button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Equipment.this, NewDevice.class);
                startActivity(intent);
            }
        });


        listView = findViewById(R.id.listMode);

        //Real Time Listener->updates the list everything the db changes
        db.collection("Devices").whereEqualTo("userID", userDoc)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        ArrayList<Device> devices = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Device d = doc.toObject(Device.class);

                            devices.add(d);
                        }
                        addDataToList(devices);
                    }
                });
    }

    private void addDataToList(final ArrayList<Device> devices) {
        listView.removeAllViews();
        for (Device d : devices) {
            //Defines the spaces between the elements
            LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 2f);

            LinearLayout.LayoutParams oneSpaceParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f);


            //Text parameters for the layout
            TextView textView = new TextView(getApplicationContext());
            textView.setText(d.getName());
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setTextSize(25);
            textView.setAllCaps(true);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(oneSpaceParam);


            //Creates the on/off Button
            final Switch state = new Switch(getApplicationContext());
            state.setId(d.getGpio());
            state.setChecked(d.getStatus().equals("ON"));
            state.setLayoutParams(oneSpaceParam);
            state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                    final CollectionReference documents = db.collection("Devices");
                    documents.whereEqualTo("gpio", state.getId()).whereEqualTo("userID", userDoc)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<Object, String> map = new HashMap<>();
                                    map.put("status", (isChecked) ? "ON" : "OFF");
                                    documents.document(document.getId()).set(map, SetOptions.merge());
                                }
                            }
                        }
                    });

                }
            });

            //Creates the delete button
            Button deleteButton = new Button(getApplicationContext());
            deleteButton.setText("Delete");
            deleteButton.setTextSize(20);
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setTextColor(Color.RED);
            deleteButton.setId(d.getGpio());
            deleteButton.setLayoutParams(oneSpaceParam);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db.collection("Devices").whereEqualTo("userID", userDoc)
                            .whereEqualTo("gpio", state.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                            }
                        }
                    });
                }
            });

//            private  void getGeneration(){
//                final List<Entry> values = new ArrayList<>();
//
//                db.collection("Devices")
//                        .whereEqualTo("userID", userDoc)
//                        .orderBy("name", Query.Direction.ASCENDING)
//                        .limit(15)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                                if (task.isSuccessful()) {
//                                    timeManagers.clear();
//                                    //Get data from Firestore
//                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                        measurements.add(documentSnapshot.toObject(Measurements.class));
//                                        Log.d("Data", documentSnapshot.toString());
//                                    }
//
//                                    //Sort in Chronological Order
//                                    Collections.reverse(measurements);
//
//                                    //Creates timemanagers for grouping
//                                    for (Measurements data : measurements) {
//                                        timeManagers.add(new TimeManager(data.getDate()));
//                                    }
//
//                                    //Group by Hour
//                                    List<Measurements> list = groupBy(measurements, timeManagers, BY_HOUR);
//
//                                    //Creates timemanagers for grouping
//                                    int counter = 0;
//                                    for (Measurements data : list) {
//                                        values.add(new Entry(counter, data.getWatts()));
//                                        counter++;
//                                    }
//
//                                    setData(values, timeManagers);
//                                }
//                            }
//                        });
//
//            }

            //Creates the device Profile
            final int deviceGpio = d.getGpio();
            final String deviceName = d.getName();
            final String deviceRoom = d.getRoom();
            final String deviceStatus = d.getStatus();
            final float deviceThreshold = d.getThreshold();
            final float deviceConsumption = d.getConsumption();
            final DocumentReference deviceUserId = d.getUserID();


//          final Device deviceProfile = new Device(deviceName,deviceGpio,deviceRoom,deviceStatus,deviceThreshold,deviceConsumption,deviceUserId);

            //Creates the Card view layout
            CardView cardView = new CardView(getApplicationContext());
            cardView.setUseCompatPadding(true);
            cardView.setContentPadding(30, 30, 30, 0);
            cardView.setPreventCornerOverlap(true);
            cardView.setCardBackgroundColor(Color.parseColor("#0b9914"));
            cardView.setCardElevation(100.0f);
            cardView.setRadius(10);
            cardView.setMaxCardElevation(3f);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send the devices to the navigation route
                    Intent i = new Intent(Equipment.this, DeviceSummary.class);
                    System.out.println("Sending Intent Gpio: " + i.putExtra("Equipment", deviceGpio));
                    System.out.println("Sending Intent Name: " + i.putExtra("Name", deviceName));
                    System.out.println("Sending Intent Room: " + i.putExtra("Room", deviceRoom));
                    System.out.println("Sending Intent Status: " + i.putExtra("Status", deviceStatus));
                    System.out.println("Sending Intent Threshold: " + i.putExtra("Threshold", deviceThreshold));
                    System.out.println("Sending Intent Consumption: " + i.putExtra("Consumption", deviceConsumption));
                    System.out.println("Status" + deviceStatus);
                    startActivity(i);

                }
            });


            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            //layout.setWeightSum(5f);

            layout.addView(textView);
//            layout.addView(state);
//            layout.addView(deleteButton);
            cardView.addView(layout);

            listView.addView(cardView);

        }
    }
}
