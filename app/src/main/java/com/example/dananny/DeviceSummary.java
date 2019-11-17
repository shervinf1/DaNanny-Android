package com.example.dananny;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceSummary extends AppCompatActivity implements Serializable {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();
    final DocumentReference userDoc = db.collection("Users").document(userID);


    private static final String TAG = "DeviceSummary";
    private final int BY_HOUR = 1;
    private final int BY_DATE = 2;
    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_summary);
        getSupportActionBar().hide();

        lineChart = findViewById(R.id.lineChart);

        final Serializable devGpio = getIntent().getSerializableExtra("Equipment");
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



                        getDeviceID(devGpio);

                    }

                    System.out.println("Pulling Down");
                }

            }
        });

        LineChartSetup();

    }

    public void getDeviceID(Serializable pin){
        db.collection("Devices")
                .whereEqualTo("gpio", pin)
                .whereEqualTo("userID", userDoc)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        getConsumptionByID(doc.getId());

                    }
                }
            }
        });
    }

    public void getConsumptionByID(String deviceID){
        final List<Entry> values = new ArrayList<>();
        final List<Measurements> measurements = new ArrayList<>();
        final List<TimeManager> timeManagers = new ArrayList<>();
        final DocumentReference document = db.collection("Devices").document(deviceID);

        db.collection("Measurements")
                .whereEqualTo("deviceID", document)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                measurements.add(documentSnapshot.toObject(Measurements.class));
                                Log.d("Data", documentSnapshot.toString());
                            }

                            //Sort in Chronological Order
                            Collections.reverse(measurements);

                            //Creates timemanagers for grouping
                            for (Measurements data : measurements) {
                                timeManagers.add(new TimeManager(data.getDate()));
                            }

                            //Group by Hour
                            //List<Measurements> list = measurementGroupBy(measurements, timeManagers, BY_HOUR);

                            //Creates timemanagers for grouping
                            int counter = 0;
                            for (Measurements data : measurements) {
                                values.add(new Entry(counter, data.getWatts()));
                                counter++;
                            }

                            setData(values, timeManagers);
                        }
                    }
                });
    }


    private void LineChartSetup() {
        lineChart.setGridBackgroundColor(Color.TRANSPARENT);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setMinimumHeight(600);
        lineChart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));

        //Graph X-Axis Settings
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(true);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGridLineWidth(1.3f);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getXAxis().setGridColor(Color.WHITE);

        //Graph Y-Axis Settings
        lineChart.getAxisLeft().setDrawAxisLine(true);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawZeroLine(true);
        lineChart.getAxisLeft().setZeroLineWidth(4);
        lineChart.getAxisLeft().setGridLineWidth(1.3f);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setGridColor(Color.WHITE);
    }


    private List<Measurements> measurementGroupBy(List<Measurements> measurementsList, List<TimeManager> timeManagerList, int groupCode) {
        List<Measurements> measureDummy = new ArrayList<>();
        List<TimeManager> timeDummy = new ArrayList<>();

        TimeManager timeBefore = timeManagerList.get(0);
        Measurements dummy = measurementsList.get(0);
        measureDummy.add(dummy);

        if (groupCode == BY_HOUR) {


            for (int i = 1; i < timeManagerList.size(); i++) {
                System.out.println("Entered the for loop: " + i);
                System.out.println("dummy: " + dummy.getDate());

                //Check if same year
                if (timeBefore.getYear() == timeManagerList.get(i).getYear()) {
                    //Check if same month
                    if (timeBefore.getMonth() == timeManagerList.get(i).getMonth()) {
                        //Check if same day of the month
                        if (timeBefore.getDay() == timeManagerList.get(i).getDay()) {
                            //Check if same hour
                            if (timeBefore.getHour() == timeManagerList.get(i).getHour()) {

                                //Sum consumption
                                System.out.println("Summing" + i);
                                dummy.setCurrent(dummy.getCurrent() + measurementsList.get(i).getCurrent());
                                dummy.setVoltage(dummy.getVoltage() + measurementsList.get(i).getVoltage());
                                dummy.setWatts(dummy.getWatts() + measurementsList.get(i).getWatts());

                            } else {
                                //Not same Hour
                                System.out.println("Not same hour" + i);
                                measureDummy.add(measurementsList.get(i));
                                timeDummy.add(timeManagerList.get(i));
                                dummy = measurementsList.get(i);
                            }
                        } else {
                            //Not same Day
                            System.out.println("Not same day" + i);
                            measureDummy.add(measurementsList.get(i));
                            timeDummy.add(timeManagerList.get(i));
                            dummy = measurementsList.get(i);
                        }
                    } else {
                        //Not same Month
                        System.out.println("Not same month" + i);
                        measureDummy.add(measurementsList.get(i));
                        timeDummy.add(timeManagerList.get(i));
                        dummy = measurementsList.get(i);
                    }
                } else {
                    //Not same Year
                    System.out.println("Not same year" + i);
                    measureDummy.add(measurementsList.get(i));
                    timeDummy.add(timeManagerList.get(i));
                    dummy = measurementsList.get(i);
                }

                timeBefore = timeManagerList.get(i);

            }
            timeManagerList = timeDummy;
            System.out.println("List size" + measureDummy.size());
            return measureDummy;

        } else if (groupCode == BY_DATE) {

            for (int i = 1; i < timeManagerList.size(); i++) {

                //Check if same year
                if (timeBefore.getYear() == timeManagerList.get(i).getYear()) {
                    //Check if same month
                    if (timeBefore.getMonth() == timeManagerList.get(i).getMonth()) {
                        //Check if same day of the month
                        if (timeBefore.getDay() == timeManagerList.get(i).getDay()) {

                            //Sum consumption
                            dummy.setCurrent(dummy.getCurrent() + measurementsList.get(i).getCurrent());
                            dummy.setVoltage(dummy.getVoltage() + measurementsList.get(i).getVoltage());
                            dummy.setWatts(dummy.getWatts() + measurementsList.get(i).getWatts());
                        } else {
                            //Not same Day
                            System.out.println("Not same day" + i);
                            measureDummy.add(measurementsList.get(i));
                            timeDummy.add(timeManagerList.get(i));
                            dummy = measurementsList.get(i);
                        }
                    } else {
                        //Not same Month
                        System.out.println("Not same month" + i);
                        measureDummy.add(measurementsList.get(i));
                        timeDummy.add(timeManagerList.get(i));
                        dummy = measurementsList.get(i);
                    }
                } else {
                    //Not same Year
                    System.out.println("Not same year" + i);
                    measureDummy.add(measurementsList.get(i));
                    timeDummy.add(timeManagerList.get(i));
                    dummy = measurementsList.get(i);
                }

                timeBefore = timeManagerList.get(i);

            }

            return measureDummy;

        } else {
            return null;
        }
    }


    private void setData(final List<Entry> values, final List<TimeManager> times) {

        LineDataSet set1;

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return times.get((int) value).getHourString();
            }
        });

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            set1.setValues(values);
            set1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Data Set1");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.WHITE);
            set1.setDrawCircles(true);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2);
            set1.setFillAlpha(255);
            set1.setCircleRadius(4f);
            set1.setDrawCircleHole(false);
            set1.setHighlightEnabled(false);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return 0;
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
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        lineChart.setData(data);
        lineChart.invalidate();
    }


}


