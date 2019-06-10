package com.example.dananny;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends Activity {

    LineChart lineChart;
    private static final String TAG = "MainActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Unlimited Cache Size for Downloaded Values
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

        //Graph Settings
        lineChart = findViewById(R.id.lineChart);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setGridBackgroundColor(Color.TRANSPARENT);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.getLegend().setEnabled(false);

        //Graph Axis Settings
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setAxisMaximum(26);
        lineChart.getAxisLeft().setAxisMinimum(0);

        //A Firebase Listener: This code will execute as soon as
        final CollectionReference docRef = db.collection("values").document("User X")
                .collection("messages");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    getValues();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    /*
    private void UploadData()
    An example code that shows how to create a new document
    and insert values inside that document
     */
    /*private void UploadData(){
        CollectionReference cities = db.collection("users");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Ben 10");
        data1.put("phone", "787-010-1000");
        data1.put("email", "ben10@omniverse.glx");
        cities.document("User X").set(data1);
    }*/

    /*
    private void DownloadData()
    An example code that shows how to download data
    from a specific document and assigned to a class
     */
    /*private void DownloadData(){
        DocumentReference docRef = db.collection("users").document("User X");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users user = documentSnapshot.toObject(Users.class);

            }
        });
    }*/

    private void getValues(){

        Toast.makeText(MainActivity.this,"Connecting to Database",Toast.LENGTH_LONG).show();

        final ArrayList<Entry> values = new ArrayList<>();

        final CollectionReference docRef = db
                .collection("values").document("User X")
                .collection("messages");

        db.collection("values").document("User X")
                .collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Downloading New Data",Toast.LENGTH_LONG).show();
                            int counter = 0; //Counter
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Measurements measurements = document.toObject(Measurements.class);
                                //Date received as: Year-Month-Day Hour:Minutes:Seconds
                                TimeManager timeManager = new TimeManager(measurements.getDate());
                                values.add(new Entry(counter, 12 + Float.parseFloat(measurements.getVolts())));
                                counter++;
                            }
                            setData(values);
                        } else {
                            Toast.makeText(MainActivity.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //private void setData(int count, float range){
    private void setData(ArrayList<Entry> values){

        LineDataSet set1;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
            Log.d(TAG, "Entered If Statement");
        } else {

            Log.d(TAG, "Entered Else If Statement");

            set1 = new LineDataSet(values, "Data Set1");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.rgb(0, 188, 212));
            set1.setDrawCircles(true);
            set1.setCircleColor(Color.rgb(40, 180, 99));
            set1.setLineWidth(4);
            set1.setFillAlpha(255);
            set1.setCircleRadius(4f);
            set1.setDrawCircleHole(false);
            set1.setHighlightEnabled(false);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }
        }
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        lineChart.setData(data);
        lineChart.invalidate();
    }
}
