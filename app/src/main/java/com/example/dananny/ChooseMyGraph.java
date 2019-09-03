package com.example.dananny;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ChooseMyGraph extends AppCompatActivity {
    LineChart lineChart;
    PieChart pieChart;
    Button button;
    ArrayList<Measurements> allMeasures = new ArrayList<>();
    ArrayList<TimeManager> timeManagers = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ChooseMyGraph";
    private int option;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_my_graph);
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.halfPie);

        LineChartSetup();
        PieChartSetup();
    }

    private void LineChartSetup(){
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(true);
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
        lineChart.getXAxis().setTextColor(Color.rgb(0, 143, 204));
        lineChart.getXAxis().setGridColor(Color.rgb(0, 143, 204));

        //Graph Y-Axis Settings
        lineChart.getAxisLeft().setDrawAxisLine(true);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawZeroLine(true);
        lineChart.getAxisLeft().setZeroLineWidth(4);
        lineChart.getAxisLeft().setTextColor(Color.rgb(0, 143, 204));
        lineChart.getAxisLeft().setGridColor(Color.rgb(0, 143, 204));
    }
    private void PieChartSetup(){
        pieChart.setBackgroundColor(Color.TRANSPARENT);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHorizontalScrollBarEnabled(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(false);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setMaxAngle(360);
        pieChart.setRotationAngle(180f);
        pieChart.setClickable(true);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(allMeasures.size()!=0){
                    final ArrayList<Entry> values = new ArrayList<>();
                    int counter = 0;
                    switch ((int)h.getX()){
                        case 0:
                            //MWT
                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getCURRMWT()));
                                counter++;
                            }
                            break;
                        case 1:
                            //Battery: V
                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getVBATT()));
                                counter++;
                            }
                            break;
                        case 2:
                            //Battery: A
                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getCURRBATT()));
                                counter++;
                            }
                            break;
                        case 3:
                            //Average: V
                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getVAVG()));
                                counter++;
                            }
                            break;
                        case 4:
                            //Average: W
                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getWAVG()));
                                counter++;
                            }
                            break;
                        case 5:
                            //DC Load
                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getDCLOAD()));
                                counter++;
                            }
                            break;
                        case 6:
                            //DC Power
                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getDCPOWER()));
                                counter++;
                            }
                            break;
                    }

                    setData(values, timeManagers);
                }
                else {
                    switch ((int)h.getX()){
                        case 0:
                            //MWT
                            getMWTValues();
                            break;
                        case 1:
                            //Battery: V
                            getBattVoltValues();
                            break;
                        case 2:
                            //Battery: A
                            getBattCurrentValues();
                            break;
                        case 3:
                            //Average: V
                            getAverageVoltageValues();
                            break;
                        case 4:
                            //Average: W
                            getAverageWattsValues();
                            break;
                        case 5:
                            //DC Load
                            getDCLoadValues();
                            break;
                        case 6:
                            //DC Power
                            getDCPowerValues();
                            break;
                    }
                }

            }

            @Override
            public void onNothingSelected() {

            }
        });

        GraphList();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }
    private void GraphList(){
        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(1,"MWT"));
        values.add(new PieEntry(1,"Battery: V"));
        values.add(new PieEntry(1,"Battery: A"));
        values.add(new PieEntry(1,"Average: V"));
        values.add(new PieEntry(1,"Average: W"));
        values.add(new PieEntry(1,"DC Load"));
        values.add(new PieEntry(1,"DC Power"));

        PieDataSet dataSet = new PieDataSet(values, "Measurements");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(7f);

        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        pieChart.setData(data);

        pieChart.invalidate();
    }

    private void getMWTValues(){
        final ArrayList<Entry> values = new ArrayList<>();
        db.collection("values").document("User X")
                .collection("test").orderBy("date", Query.Direction.ASCENDING).limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            allMeasures.clear();
                            timeManagers.clear();
                            allMeasures = new ArrayList<>();
                            timeManagers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }

                            Collections.reverse(allMeasures);
                            int counter = 0;

                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getCURRMWT()));
                                timeManagers.add(measurements.getDate());
                                counter++;
                            }
                            setData(values, timeManagers);

                        } else {
                            Toast.makeText(ChooseMyGraph.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void getBattVoltValues(){
        final ArrayList<Entry> values = new ArrayList<>();
        db.collection("values").document("User X")
                .collection("test").orderBy("date", Query.Direction.ASCENDING).limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Measurements> allMeasures = new ArrayList<>();
                            ArrayList<TimeManager> timeManagers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }

                            Collections.reverse(allMeasures);
                            int counter = 0;

                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getVBATT()));
                                timeManagers.add(measurements.getDate());
                                counter++;
                            }
                            setData(values, timeManagers);

                        } else {
                            Toast.makeText(ChooseMyGraph.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void getBattCurrentValues(){
        final ArrayList<Entry> values = new ArrayList<>();
        db.collection("values").document("User X")
                .collection("test").orderBy("date", Query.Direction.ASCENDING).limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Measurements> allMeasures = new ArrayList<>();
                            ArrayList<TimeManager> timeManagers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }

                            Collections.reverse(allMeasures);
                            int counter = 0;

                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getCURRBATT()));
                                timeManagers.add(measurements.getDate());
                                counter++;
                            }
                            setData(values, timeManagers);

                        } else {
                            Toast.makeText(ChooseMyGraph.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void getAverageVoltageValues(){
        final ArrayList<Entry> values = new ArrayList<>();
        db.collection("values").document("User X")
                .collection("test").orderBy("date", Query.Direction.ASCENDING).limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Measurements> allMeasures = new ArrayList<>();
                            ArrayList<TimeManager> timeManagers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }

                            Collections.reverse(allMeasures);
                            int counter = 0;

                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getVAVG()));
                                timeManagers.add(measurements.getDate());
                                counter++;
                            }
                            setData(values, timeManagers);

                        } else {
                            Toast.makeText(ChooseMyGraph.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void getAverageWattsValues(){
        final ArrayList<Entry> values = new ArrayList<>();
        db.collection("values").document("User X")
                .collection("test").orderBy("date", Query.Direction.ASCENDING).limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Measurements> allMeasures = new ArrayList<>();
                            ArrayList<TimeManager> timeManagers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }

                            Collections.reverse(allMeasures);
                            int counter = 0;

                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getWAVG()));
                                timeManagers.add(measurements.getDate());
                                counter++;
                            }
                            setData(values, timeManagers);

                        } else {
                            Toast.makeText(ChooseMyGraph.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void getDCLoadValues(){
        final ArrayList<Entry> values = new ArrayList<>();
        db.collection("values").document("User X")
                .collection("test").orderBy("date", Query.Direction.ASCENDING).limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Measurements> allMeasures = new ArrayList<>();
                            ArrayList<TimeManager> timeManagers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }

                            Collections.reverse(allMeasures);
                            int counter = 0;

                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getDCLOAD()));
                                timeManagers.add(measurements.getDate());
                                counter++;
                            }
                            setData(values, timeManagers);

                        } else {
                            Toast.makeText(ChooseMyGraph.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void getDCPowerValues(){
        final ArrayList<Entry> values = new ArrayList<>();
        db.collection("values").document("User X")
                .collection("test").orderBy("date", Query.Direction.ASCENDING).limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Measurements> allMeasures = new ArrayList<>();
                            ArrayList<TimeManager> timeManagers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }

                            Collections.reverse(allMeasures);
                            int counter = 0;

                            for (Measurements measurements:allMeasures) {
                                values.add(new Entry(counter, measurements.getDCPOWER()));
                                timeManagers.add(measurements.getDate());
                                counter++;
                            }
                            setData(values, timeManagers);

                        } else {
                            Toast.makeText(ChooseMyGraph.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setData(ArrayList<Entry> values, final ArrayList<TimeManager> times){

        LineDataSet set1;

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return times.get((int)value).getTime();
            }
        });

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
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