package com.example.dananny;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.dananny.notification.CHANNEL_1_ID;

public class DatabaseService extends IntentService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userID;
    DocumentReference userDoc;

    private NotificationManagerCompat notificationManager;


    public DatabaseService() {
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        System.out.println("DatabaseService Starting...");
        userID = intent.getStringExtra("user_id");

        System.out.println("userID = " + userID);

        userDoc = db.collection("Users").document(userID);

        System.out.println("userDoc = " + userDoc.toString());

        notificationManager = NotificationManagerCompat.from(this);

        //method to perform long running tasks.
        final List<Measurements> measurements = new ArrayList<>();

        db.collection("Measurements")
                .whereEqualTo("userID", userDoc)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            System.out.println("DatabaseService: " + "Something went wrong");
                            e.printStackTrace();
                            return;
                        }

                        //Get data from Firestore
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            measurements.add(documentSnapshot.toObject(Measurements.class));
                            System.out.println("DatabaseService: "+  documentSnapshot.toString());
                        }

                        // Check Threshold in Device Table
                        for(final Measurements measurements1:measurements){
                            DocumentReference documentReference = measurements1.getDeviceID();

                            documentReference.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                try{
                                                    if(task.getResult() != null){
                                                        Device device = task.getResult().toObject(Device.class);

                                                        if(device != null){
                                                            if(device.getThreshold() <= measurements1.getWatts()){
                                                                thresholdNotification(device.getName(), device.getGpio());
                                                            }
                                                        }else{
                                                            System.out.println("Device is NULL");
                                                        }


                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    });
                        }

                    }
                });


    }

    public void thresholdNotification(String deviceName, int deviceGpio) {
        //Creates the direction where the notification navigates to
        Intent intent = new Intent(DatabaseService.this, Equipment.class);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Notification body build
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Device Limit Warning")
                .setContentText("Your device " + deviceName + " is consuming more than normal")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.BLUE)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultIntent)
                .addAction(R.mipmap.ic_launcher, "Ok", resultIntent)
                .build();

        //Displays the notification through channel 1
        notificationManager.notify(deviceGpio, notification);

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
