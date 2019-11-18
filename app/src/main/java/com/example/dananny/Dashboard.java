package com.example.dananny;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.dananny.notification.CHANNEL_1_ID;

public class Dashboard extends AppCompatActivity {

    PieChart pieChart;
    Button btnGraph;
    Button btnEquipment;
    Button btnReports;

    TextView generation;
    TextView consumption;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();
    final DocumentReference userDoc = db.collection("Users").document(userID);
    //private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        pieChart = findViewById(R.id.BatteryLevel);
        btnGraph = findViewById(R.id.BtnGraph);
        btnEquipment = findViewById(R.id.BtnEquipments);
        btnReports = findViewById(R.id.BtnReport);

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, ChooseMyGraph.class);
                startActivity(intent);
            }
        });

        btnEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Equipment.class);
                startActivity(intent);
            }
        });

        btnReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, reportList.class);
                startActivity(intent);
            }
        });

        generation = findViewById(R.id.generatedValue);
        consumption = findViewById(R.id.consumedValue);

        //notificationManager = NotificationManagerCompat.from(this);
        setBatteryLevelGraph();
        getBothRates();

        //deviceConsumptionComparison();
        //thresholdNotification("Lamp 2", 2);

        Intent dbIntent =  new Intent();
        dbIntent.setClass(this, DatabaseService.class);
        dbIntent.putExtra("user_id", userID);
        startService(dbIntent);
    }


    public void setBatteryLevelGraph() {
        pieChart.setBackgroundColor(Color.TRANSPARENT);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHorizontalScrollBarEnabled(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.setMaxAngle(180);
        pieChart.setRotationAngle(180f);
        pieChart.setClickable(false);
        pieChart.setCenterTextSize(26);
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        setChargePercent((float) 100);
    }

    private void setChargePercent(float available) {

        ArrayList<PieEntry> values = new ArrayList<>();
        int[] colorArray;

        values.add(new PieEntry(available, ""));
        values.add(new PieEntry(100 - available, ""));

        PieDataSet dataSet = new PieDataSet(values, "DCMicrogridMeasurements");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(7f);


        //When available is bigger than a certain number


        if (available > 70) {
            colorArray = new int[]{Color.rgb(32, 175, 36), Color.TRANSPARENT}; //High Charge
        } else if (available > 30) {
            colorArray = new int[]{Color.rgb(255, 255, 51), Color.TRANSPARENT}; //Medium Charge
        } else {
            colorArray = new int[]{Color.rgb(209, 13, 49), Color.TRANSPARENT};  //Low Charge
        }
        dataSet.setColors(colorArray);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        pieChart.setCenterTextColor(colorArray[0]);
        pieChart.setCenterText((int) available + "%");
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void getBothRates() {


        final int green = Color.rgb(32, 175, 37);
        final int red = Color.rgb(255, 86, 34);

        //Get current time
        TimeManager currentDate = new TimeManager(new Date());
        final int day = currentDate.getDay();
        final int month = currentDate.getMonth();
        final int year = currentDate.getYear();

        //Check if leading zero is needed
        final String dayStr = (day < 10) ? "0" + day : String.valueOf(day);
        final String monthStr = (month < 10) ? "0" + month : String.valueOf(month);

        //Create to string date format
        String beginMonth = "01/" + monthStr + "/" + year;
        Long start_millis_month = 0l;

        //Convert date to seconds
        try {
            start_millis_month = (new SimpleDateFormat("dd/MM/yyyy").parse(beginMonth).getTime()) / 1000;


            System.out.println(start_millis_month);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Get Montly Generation
        db.collection("Generation")
                .whereEqualTo("userID", userDoc)
                .whereGreaterThan("date", start_millis_month)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            float wattsTotal = 0;

                            //Get data from Firestore
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                wattsTotal += documentSnapshot.toObject(Generation.class).getWatts();
                            }

                            generation.setText((wattsTotal) + "W");
                        }
                    }
                });

        //Get Daily Consumption
        db.collection("Measurements")
                .whereEqualTo("userID", userDoc)
                .whereGreaterThan("date", start_millis_month)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            float wattsTotal = 0;

                            //Get data from Firestore
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                wattsTotal += documentSnapshot.toObject(Measurements.class).getWatts();
                            }

                            consumption.setText((wattsTotal) + "W");
                        }
                    }
                });

    }

    public void thresholdNotification(String deviceName, int deviceGpio) {
//        Intent activityIntent = new Intent(this,Equipment.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);
//        Intent broadcastIntent = new Intent(this,NotificationReceiver.class);

        //Creates the direction where the notification navigates to
        Intent intent = new Intent(Dashboard.this, Equipment.class);
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
       // notificationManager.notify(deviceGpio, notification);

        //deviceConsumptionComparison();


    }


    //        new java.util.Timer().schedule(new java.util.TimerTask(){@Override public void run(){}},5000)
    public void deviceConsumptionComparison() {
        /**
         * 1.Query a devices consumption
         * 2.Gets the devices threshold value
         * 3.Compare if consumption > threshold Value
         * 4.if greater show thresholdNotification(string deviceName)
         * 5.else keep checking value
         */
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
                        System.out.println("Devices" + devices);
                        for (Device d : devices) {
                            final int deviceGpio = d.getGpio();
                            final String deviceName = d.getName();
                            final String deviceRoom = d.getRoom();
                            final String deviceStatus = d.getStatus();
                            final float deviceThreshold = d.getThreshold();
                            final float deviceConsumption = d.getConsumption();
                            final DocumentReference deviceUserId = d.getUserID();

                            System.out.println("Device Consumption: " + deviceConsumption);
                            System.out.println("Device Threshold: " + deviceThreshold);

                            if (deviceConsumption >= deviceThreshold) {
                                thresholdNotification(deviceName, deviceGpio);
                            }

                        }

                    }

                });

    }
}


