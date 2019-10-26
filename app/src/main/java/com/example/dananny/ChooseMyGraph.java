package com.example.dananny;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChooseMyGraph extends AppCompatActivity {
    LineChart lineChart;
    PieChart pieChart;
    Button BtnGeneration;
    Button BtnConsumption;
    Button BtnBoth;
    LinearLayout linearLayout;
    TextView dataText1;
    TextView dataText2;
    TextView valueText1;
    TextView valueText2;

    ArrayList<DCMicrogridMeasurements> allMeasures = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();
    final DocumentReference userDoc = db.collection("Users").document(userID);

    private static final String TAG = "ChooseMyGraph";
    private final int BY_HOUR = 1;
    private final int BY_DATE = 2;

    private final int DARKTONE = Color.rgb(61, 61, 63);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_my_graph);
        getSupportActionBar().hide();

        lineChart = findViewById(R.id.lineChart);

        BtnGeneration = findViewById(R.id.generation_button);
        BtnConsumption = findViewById(R.id.consumption_button);
        BtnBoth = findViewById(R.id.both_button);
        linearLayout = findViewById(R.id.linearLayout);

        BtnGeneration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_button));
                setAllButtonsDarkBackground();
                BtnGeneration.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.transparent_background));
                getGeneration();
                getGenerationRates();
            }
        });

        BtnConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.orange_button));
                setAllButtonsDarkBackground();
                BtnConsumption.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.transparent_background));
                getConsumption();
                getConsumptionRates();
            }
        });

        BtnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.graph_background));
                setAllButtonsDarkBackground();
                BtnBoth.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.transparent_background));
                getBoth();
                getBothRates();
            }
        });

        dataText1 = findViewById(R.id.data1Label);
        dataText2 = findViewById(R.id.data2Label);
        valueText1 = findViewById(R.id.value1Label);
        valueText2 = findViewById(R.id.value2Label);

        LineChartSetup();
    }

    private void setAllButtonsDarkBackground() {
        BtnGeneration.setBackgroundColor(DARKTONE);
        BtnBoth.setBackgroundColor(DARKTONE);
        BtnConsumption.setBackgroundColor(DARKTONE);
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

    private void getGeneration() {
        final List<Entry> values = new ArrayList<>();
        final List<Generation> generations = new ArrayList<>();
        final List<TimeManager> timeManagers = new ArrayList<>();

        db.collection("Generation")
                .whereEqualTo("userID", userDoc)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //Get data from Firestore
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                generations.add(documentSnapshot.toObject(Generation.class));
                                Log.d("Data", documentSnapshot.toString());
                            }

                            //Sort in Chronological Order
                            Collections.reverse(generations);

                            //Creates timemanagers for grouping
                            for (Generation data : generations) {
                                timeManagers.add(new TimeManager(data.getDate()));
                            }

                            //Group by Hour
                            List<Generation> list = generationGroupBy(generations, timeManagers, BY_HOUR);

                            //Creates timemanagers for grouping
                            int counter = 0;
                            for (Generation data : list) {
                                values.add(new Entry(counter, data.getWatts()));
                                counter++;
                            }

                            setData(values, timeManagers);
                        }
                    }
                });

    }

    private void getGenerationRates() {

        final int green = Color.rgb(32, 175, 37);

        //Get current time
        TimeManager currentDate = new TimeManager(new Date());
        final int day = currentDate.getDay();
        final int month = currentDate.getMonth();
        final int year = currentDate.getYear();

        //Check if leading zero is needed
        final String dayStr = (day < 10) ? "0" + (day) : String.valueOf(day);
        final String monthStr = (month < 10) ? "0" + (month) : String.valueOf(month);

        //Create to string date format
        String beginMonth = "01/" + monthStr + "/" + year;
        String beginDay = dayStr + "/" + monthStr + "/" + year;
        Long start_millis_month = 0l;
        Long start_millis_day = 0l;

        //Convert date to seconds
        try {
            start_millis_month = (new SimpleDateFormat("dd/MM/yyyy").parse(beginMonth).getTime()) / 1000;
            start_millis_day = ((new SimpleDateFormat("dd/MM/yyyy").parse(beginDay).getTime()) / 1000);

            System.out.println(start_millis_month);
            System.out.println(start_millis_day);

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

                            dataText1.setText("Monthly");
                            valueText1.setText((wattsTotal) + "W");
                            valueText1.setTextColor(green);
                        }
                    }
                });
        //Get Daily Generation
        db.collection("Generation")
                .whereEqualTo("userID", userDoc)
                .whereGreaterThan("date", start_millis_day)
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

                            dataText2.setText("Today");
                            valueText2.setText((wattsTotal) + "W");
                            valueText2.setTextColor(green);
                        }
                    }
                });

    }

    private void getConsumption() {
        final List<Entry> values = new ArrayList<>();
        final List<Measurements> measurements = new ArrayList<>();
        final List<TimeManager> timeManagers = new ArrayList<>();

        db.collection("Measurements")
                .whereEqualTo("userID", userDoc)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //Get data from Firestore
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
                            List<Measurements> list = measurementGroupBy(measurements, timeManagers, BY_HOUR);

                            //Creates timemanagers for grouping
                            int counter = 0;
                            for (Measurements data : list) {
                                values.add(new Entry(counter, data.getWatts()));
                                counter++;
                            }

                            setData(values, timeManagers);
                        }
                    }
                });
    }

    private void getConsumptionRates() {

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
        String beginDay = dayStr + "/" + monthStr + "/" + year;
        Long start_millis_month = 0l;
        Long start_millis_day = 0l;

        //Convert date to seconds
        try {
            start_millis_month = (new SimpleDateFormat("dd/MM/yyyy").parse(beginMonth).getTime()) / 1000;
            start_millis_day = ((new SimpleDateFormat("dd/MM/yyyy").parse(beginDay).getTime()) / 1000);

            System.out.println(start_millis_month);
            System.out.println(start_millis_day);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Get Monthly Consumption
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

                            dataText1.setText("Monthly");
                            valueText1.setText((wattsTotal) + "W");
                            valueText1.setTextColor(red);
                        }
                    }
                });

        //Get Daily Consumption
        db.collection("Measurements")
                .whereEqualTo("userID", userDoc)
                .whereGreaterThan("date", start_millis_day)
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

                            dataText2.setText("Today");
                            valueText2.setText((wattsTotal) + "W");
                            valueText2.setTextColor(red);
                        }
                    }
                });

    }

    private void getBoth() {
        final List<Entry> values = new ArrayList<>();
        final List<Entry> values2 = new ArrayList<>();
        final List<Generation> generations = new ArrayList<>();
        final List<TimeManager> generationTime = new ArrayList<>();
        final List<Measurements> measurements = new ArrayList<>();
        final List<TimeManager> measurementTime = new ArrayList<>();


        System.out.println("Querying...");

        final Task<QuerySnapshot> measureQuery = db.collection("Measurements")
                .whereEqualTo("userID", userDoc)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(15)
                .get();

        final Task<QuerySnapshot> genrateQuery = db.collection("Generation")
                .whereEqualTo("userID", userDoc)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(15)
                .get();

        measureQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                genrateQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (measureQuery.isSuccessful() && genrateQuery.isSuccessful()) {
                            //---MEASUREMENT SECTION---
                            //Get data from Firestore
                            for (QueryDocumentSnapshot documentSnapshot : measureQuery.getResult()) {
                                measurements.add(documentSnapshot.toObject(Measurements.class));
                                Log.d("Data", documentSnapshot.toString());
                            }

                            //Sort in Chronological Order
                            Collections.reverse(measurements);

                            //Creates timemanagers for grouping
                            for (Measurements data : measurements) {
                                measurementTime.add(new TimeManager(data.getDate()));
                            }

                            //Group by Hour
                            List<Measurements> list = measurementGroupBy(measurements, measurementTime, BY_HOUR);

                            //Creates timemanagers for grouping
                            int counter = 0;
                            for (Measurements data : list) {
                                values.add(new Entry(counter, data.getWatts()));
                                counter++;
                            }


                            //---GENERATION SECTION---
                            //Get data from Firestore
                            for (QueryDocumentSnapshot documentSnapshot : genrateQuery.getResult()) {
                                generations.add(documentSnapshot.toObject(Generation.class));
                                Log.d("Data", documentSnapshot.toString());
                            }

                            //Sort in Chronological Order
                            Collections.reverse(generations);

                            //Creates timemanagers for grouping
                            for (Generation data : generations) {
                                generationTime.add(new TimeManager(data.getDate()));
                            }

                            //Group by Hour
                            List<Generation> list2 = generationGroupBy(generations, generationTime, BY_HOUR);

                            //Creates timemanagers for grouping
                            int counter2 = 0;
                            for (Generation data : list2) {
                                values2.add(new Entry(counter2, data.getWatts()));
                                counter2++;
                            }

                            System.out.println("Plotting");
                            setData(values, values2, measurementTime, generationTime);
                        }

                    }
                });
            }
        });


    }

    private void getBothRates(){

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

                            dataText1.setText("Generated");
                            valueText1.setText((wattsTotal) + "W");
                            valueText1.setTextColor(green);
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

                            dataText2.setText("Consumed");
                            valueText2.setText((wattsTotal) + "W");
                            valueText2.setTextColor(red);
                        }
                    }
                });

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

    private List<Generation> generationGroupBy(List<Generation> generationList, List<TimeManager> timeManagerList, int groupCode) {
        List<Generation> generationDummy = new ArrayList<>();
        List<TimeManager> timeDummy = new ArrayList<>();

        TimeManager timeBefore = timeManagerList.get(0);
        Generation dummy = generationList.get(0);
        generationDummy.add(dummy);

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
                                dummy.setCurrent(dummy.getCurrent() + generationList.get(i).getCurrent());
                                dummy.setWatts(dummy.getWatts() + generationList.get(i).getWatts());

                            } else {
                                //Not same Hour
                                System.out.println("Not same hour" + i);
                                generationDummy.add(generationList.get(i));
                                timeDummy.add(timeManagerList.get(i));
                                dummy = generationList.get(i);
                            }
                        } else {
                            //Not same Day
                            System.out.println("Not same day" + i);
                            generationDummy.add(generationList.get(i));
                            timeDummy.add(timeManagerList.get(i));
                            dummy = generationList.get(i);
                        }
                    } else {
                        //Not same Month
                        System.out.println("Not same month" + i);
                        generationDummy.add(generationList.get(i));
                        timeDummy.add(timeManagerList.get(i));
                        dummy = generationList.get(i);
                    }
                } else {
                    //Not same Year
                    System.out.println("Not same year" + i);
                    generationDummy.add(generationList.get(i));
                    timeDummy.add(timeManagerList.get(i));
                    dummy = generationList.get(i);
                }

                timeBefore = timeManagerList.get(i);

            }
            timeManagerList = timeDummy;
            System.out.println("List size" + generationDummy.size());
            return generationDummy;

        } else if (groupCode == BY_DATE) {

            for (int i = 1; i < timeManagerList.size(); i++) {

                //Check if same year
                if (timeBefore.getYear() == timeManagerList.get(i).getYear()) {
                    //Check if same month
                    if (timeBefore.getMonth() == timeManagerList.get(i).getMonth()) {
                        //Check if same day of the month
                        if (timeBefore.getDay() == timeManagerList.get(i).getDay()) {

                            //Sum consumption
                            dummy.setCurrent(dummy.getCurrent() + generationList.get(i).getCurrent());
                            dummy.setWatts(dummy.getWatts() + generationList.get(i).getWatts());
                        } else {
                            //Not same Day
                            System.out.println("Not same day" + i);
                            generationDummy.add(generationList.get(i));
                            timeDummy.add(timeManagerList.get(i));
                            dummy = generationList.get(i);
                        }
                    } else {
                        //Not same Month
                        System.out.println("Not same month" + i);
                        generationDummy.add(generationList.get(i));
                        timeDummy.add(timeManagerList.get(i));
                        dummy = generationList.get(i);
                    }
                } else {
                    //Not same Year
                    System.out.println("Not same year" + i);
                    generationDummy.add(generationList.get(i));
                    timeDummy.add(timeManagerList.get(i));
                    dummy = generationList.get(i);
                }

                timeBefore = timeManagerList.get(i);

            }

            return generationDummy;

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

    private void setData(final List<Entry> consumption, final List<Entry> genration, final List<TimeManager> consumptionTime, final List<TimeManager> generationTime) {

        LineDataSet set1, set2;

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return consumptionTime.get((int) value).getHourString();
            }
        });

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 1) {

            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(consumption);

            set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
            set2.setValues(genration);

            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {

            lineChart.clear();

            set1 = new LineDataSet(consumption, "Data Set1");
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

            set2 = new LineDataSet(genration, "Data Set2");
            set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setColor(Color.WHITE);
            set2.setDrawCircles(true);
            set2.setCircleColor(Color.WHITE);
            set2.setLineWidth(2);
            set2.setFillAlpha(255);
            set2.setCircleRadius(4f);
            set2.setDrawCircleHole(false);
            set2.setHighlightEnabled(false);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return 0;
                }
            });

            // set the filled area
            set2.setDrawFilled(true);
            set2.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return 0;
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.orange_button);
                Drawable drawable2 = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
                set2.setFillDrawable(drawable2);
            } else {
                set1.setFillColor(Color.BLACK);
                set2.setFillColor(Color.BLACK);
            }
        }
        LineData data = new LineData(set1, set2);
        data.setDrawValues(false);

        lineChart.setData(data);
        lineChart.invalidate();
    }

}