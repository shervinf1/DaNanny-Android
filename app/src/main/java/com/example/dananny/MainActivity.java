package com.example.dananny;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.LinearLayout;
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

    LineChart MicroWindTurbine_Chart;
    LineChart CurrentBattery_Chart;
    LineChart VoltageBattery_Chart;
    LineChart VoltageAverage_Chart;
    LineChart WattsAverage_Chart;
    LineChart DCLoad_Chart;
    LineChart Power_Chart;
    LinearLayout list_item;

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

        list_item = findViewById(R.id.list_item);

        SetMicroWindTurbineChart();
        SetCurrentBatteryChart();
        SetVoltageBatteryChart();
        SetVoltageAverageChart();
        SetWattAverageChart();
        SetDCLoadChart();
        SetPowerChart();

        //A Firebase Listener: This code will execute as soon as
        final CollectionReference docRef = db.collection("values").document("User X")
                .collection("test");
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

    private void SetMicroWindTurbineChart(){
        //Graph Settings
        MicroWindTurbine_Chart = new LineChart(getApplicationContext());
        MicroWindTurbine_Chart.setBackgroundColor(Color.TRANSPARENT);
        MicroWindTurbine_Chart.setGridBackgroundColor(Color.TRANSPARENT);
        MicroWindTurbine_Chart.setDrawGridBackground(false);
        MicroWindTurbine_Chart.setDrawBorders(false);
        MicroWindTurbine_Chart.getDescription().setEnabled(false);
        MicroWindTurbine_Chart.setPinchZoom(true);
        MicroWindTurbine_Chart.setHorizontalScrollBarEnabled(true);
        MicroWindTurbine_Chart.getLegend().setEnabled(false);
        MicroWindTurbine_Chart.setDragEnabled(true);
        MicroWindTurbine_Chart.setMinimumHeight(600);
        MicroWindTurbine_Chart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));


        //Graph X-Axis Settings
        MicroWindTurbine_Chart.getAxisRight().setEnabled(false);
        MicroWindTurbine_Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        MicroWindTurbine_Chart.getXAxis().setDrawAxisLine(false);
        MicroWindTurbine_Chart.getXAxis().setDrawGridLines(true);
        MicroWindTurbine_Chart.getXAxis().setGranularityEnabled(true);

        //Graph Y-Axis Settings
        MicroWindTurbine_Chart.getAxisLeft().setDrawAxisLine(true);
        MicroWindTurbine_Chart.getAxisLeft().setDrawGridLines(true);
        MicroWindTurbine_Chart.getAxisLeft().setDrawZeroLine(true);
        MicroWindTurbine_Chart.getAxisLeft().setZeroLineWidth(4);
    }
    private void SetCurrentBatteryChart(){
        //Graph Settings
        CurrentBattery_Chart = new LineChart(getApplicationContext());
        CurrentBattery_Chart.setBackgroundColor(Color.TRANSPARENT);
        CurrentBattery_Chart.setGridBackgroundColor(Color.TRANSPARENT);
        CurrentBattery_Chart.setDrawGridBackground(false);
        CurrentBattery_Chart.setDrawBorders(false);
        CurrentBattery_Chart.getDescription().setEnabled(false);
        CurrentBattery_Chart.setPinchZoom(true);
        CurrentBattery_Chart.setHorizontalScrollBarEnabled(true);
        CurrentBattery_Chart.getLegend().setEnabled(false);
        CurrentBattery_Chart.setDragEnabled(true);
        CurrentBattery_Chart.setMinimumHeight(600);
        CurrentBattery_Chart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));

        //Graph X-Axis Settings
        CurrentBattery_Chart.getAxisRight().setEnabled(false);
        CurrentBattery_Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        CurrentBattery_Chart.getXAxis().setDrawAxisLine(false);
        CurrentBattery_Chart.getXAxis().setDrawGridLines(true);
        CurrentBattery_Chart.getXAxis().setGranularityEnabled(true);

        //Graph Y-Axis Settings
        CurrentBattery_Chart.getAxisLeft().setDrawAxisLine(true);
        CurrentBattery_Chart.getAxisLeft().setDrawGridLines(true);
        CurrentBattery_Chart.getAxisLeft().setDrawZeroLine(true);
        CurrentBattery_Chart.getAxisLeft().setZeroLineWidth(4);
    }
    private void SetVoltageBatteryChart(){
        //Graph Settings
        VoltageBattery_Chart = new LineChart(getApplicationContext());
        VoltageBattery_Chart.setBackgroundColor(Color.TRANSPARENT);
        VoltageBattery_Chart.setGridBackgroundColor(Color.TRANSPARENT);
        VoltageBattery_Chart.setDrawGridBackground(false);
        VoltageBattery_Chart.setDrawBorders(false);
        VoltageBattery_Chart.getDescription().setEnabled(false);
        VoltageBattery_Chart.setPinchZoom(true);
        VoltageBattery_Chart.setHorizontalScrollBarEnabled(true);
        VoltageBattery_Chart.getLegend().setEnabled(false);
        VoltageBattery_Chart.setDragEnabled(true);
        VoltageBattery_Chart.setMinimumHeight(600);
        VoltageBattery_Chart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));

        //Graph X-Axis Settings
        VoltageBattery_Chart.getAxisRight().setEnabled(false);
        VoltageBattery_Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        VoltageBattery_Chart.getXAxis().setDrawAxisLine(false);
        VoltageBattery_Chart.getXAxis().setDrawGridLines(true);
        VoltageBattery_Chart.getXAxis().setGranularityEnabled(true);

        //Graph Y-Axis Settings
        VoltageBattery_Chart.getAxisLeft().setDrawAxisLine(true);
        VoltageBattery_Chart.getAxisLeft().setDrawGridLines(true);
        VoltageBattery_Chart.getAxisLeft().setDrawZeroLine(true);
        VoltageBattery_Chart.getAxisLeft().setZeroLineWidth(4);
    }
    private void SetVoltageAverageChart(){
        //Graph Settings
        VoltageAverage_Chart = new LineChart(getApplicationContext());
        VoltageAverage_Chart.setBackgroundColor(Color.TRANSPARENT);
        VoltageAverage_Chart.setGridBackgroundColor(Color.TRANSPARENT);
        VoltageAverage_Chart.setDrawGridBackground(false);
        VoltageAverage_Chart.setDrawBorders(false);
        VoltageAverage_Chart.getDescription().setEnabled(false);
        VoltageAverage_Chart.setPinchZoom(true);
        VoltageAverage_Chart.setHorizontalScrollBarEnabled(true);
        VoltageAverage_Chart.getLegend().setEnabled(false);
        VoltageAverage_Chart.setDragEnabled(true);
        VoltageAverage_Chart.setMinimumHeight(600);
        VoltageAverage_Chart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));

        //Graph X-Axis Settings
        VoltageAverage_Chart.getAxisRight().setEnabled(false);
        VoltageAverage_Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        VoltageAverage_Chart.getXAxis().setDrawAxisLine(false);
        VoltageAverage_Chart.getXAxis().setDrawGridLines(true);
        VoltageAverage_Chart.getXAxis().setGranularityEnabled(true);

        //Graph Y-Axis Settings
        VoltageAverage_Chart.getAxisLeft().setDrawAxisLine(true);
        VoltageAverage_Chart.getAxisLeft().setDrawGridLines(true);
        VoltageAverage_Chart.getAxisLeft().setDrawZeroLine(true);
        VoltageAverage_Chart.getAxisLeft().setZeroLineWidth(4);

    }
    private void SetWattAverageChart(){
        //Graph Settings
        WattsAverage_Chart = new LineChart(getApplicationContext());
        WattsAverage_Chart.setBackgroundColor(Color.TRANSPARENT);
        WattsAverage_Chart.setGridBackgroundColor(Color.TRANSPARENT);
        WattsAverage_Chart.setDrawGridBackground(false);
        WattsAverage_Chart.setDrawBorders(false);
        WattsAverage_Chart.getDescription().setEnabled(false);
        WattsAverage_Chart.setPinchZoom(true);
        WattsAverage_Chart.setHorizontalScrollBarEnabled(true);
        WattsAverage_Chart.getLegend().setEnabled(false);
        WattsAverage_Chart.setDragEnabled(true);
        WattsAverage_Chart.setMinimumHeight(600);
        WattsAverage_Chart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));

        //Graph X-Axis Settings
        WattsAverage_Chart.getAxisRight().setEnabled(false);
        WattsAverage_Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        WattsAverage_Chart.getXAxis().setDrawAxisLine(false);
        WattsAverage_Chart.getXAxis().setDrawGridLines(true);
        WattsAverage_Chart.getXAxis().setGranularityEnabled(true);

        //Graph Y-Axis Settings
        WattsAverage_Chart.getAxisLeft().setDrawAxisLine(true);
        WattsAverage_Chart.getAxisLeft().setDrawGridLines(true);
        WattsAverage_Chart.getAxisLeft().setDrawZeroLine(true);
        WattsAverage_Chart.getAxisLeft().setZeroLineWidth(4);
    }
    private void SetDCLoadChart(){
        //Graph Settings
        DCLoad_Chart = new LineChart(getApplicationContext());
        DCLoad_Chart.setBackgroundColor(Color.TRANSPARENT);
        DCLoad_Chart.setGridBackgroundColor(Color.TRANSPARENT);
        DCLoad_Chart.setDrawGridBackground(false);
        DCLoad_Chart.setDrawBorders(false);
        DCLoad_Chart.getDescription().setEnabled(false);
        DCLoad_Chart.setPinchZoom(true);
        DCLoad_Chart.setHorizontalScrollBarEnabled(true);
        DCLoad_Chart.getLegend().setEnabled(false);
        DCLoad_Chart.setDragEnabled(true);
        DCLoad_Chart.setMinimumHeight(600);
        DCLoad_Chart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));

        //Graph X-Axis Settings
        DCLoad_Chart.getAxisRight().setEnabled(false);
        DCLoad_Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        DCLoad_Chart.getXAxis().setDrawAxisLine(false);
        DCLoad_Chart.getXAxis().setDrawGridLines(true);
        DCLoad_Chart.getXAxis().setGranularityEnabled(true);

        //Graph Y-Axis Settings
        DCLoad_Chart.getAxisLeft().setDrawAxisLine(true);
        DCLoad_Chart.getAxisLeft().setDrawGridLines(true);
        DCLoad_Chart.getAxisLeft().setDrawZeroLine(true);
        DCLoad_Chart.getAxisLeft().setZeroLineWidth(4);
    }
    private void SetPowerChart(){
        //Graph Settings
        Power_Chart = new LineChart(getApplicationContext());
        Power_Chart.setBackgroundColor(Color.TRANSPARENT);
        Power_Chart.setGridBackgroundColor(Color.TRANSPARENT);
        Power_Chart.setDrawGridBackground(false);
        Power_Chart.setDrawBorders(false);
        Power_Chart.getDescription().setEnabled(false);
        Power_Chart.setPinchZoom(true);
        Power_Chart.setHorizontalScrollBarEnabled(true);
        Power_Chart.getLegend().setEnabled(false);
        Power_Chart.setDragEnabled(true);
        Power_Chart.setMinimumHeight(600);
        Power_Chart.invalidateDrawable(ContextCompat.getDrawable(this, R.drawable.fade_red));

        //Graph X-Axis Settings
        Power_Chart.getAxisRight().setEnabled(false);
        Power_Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Power_Chart.getXAxis().setDrawAxisLine(false);
        Power_Chart.getXAxis().setDrawGridLines(true);
        Power_Chart.getXAxis().setGranularityEnabled(true);

        //Graph Y-Axis Settings
        Power_Chart.getAxisLeft().setDrawAxisLine(true);
        Power_Chart.getAxisLeft().setDrawGridLines(true);
        Power_Chart.getAxisLeft().setDrawZeroLine(true);
        Power_Chart.getAxisLeft().setZeroLineWidth(4);
    }


    private void GetMicroWindTurbineChartData(ArrayList<Entry> values){
        LineDataSet set1;

        if (MicroWindTurbine_Chart.getData() != null &&
                MicroWindTurbine_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) MicroWindTurbine_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            MicroWindTurbine_Chart.getData().notifyDataChanged();
            MicroWindTurbine_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        MicroWindTurbine_Chart.setData(data);
        MicroWindTurbine_Chart.invalidate();
    }
    private void GetCurrentBatteryChartData(ArrayList<Entry> values){
        LineDataSet set1;

        if (CurrentBattery_Chart.getData() != null &&
                CurrentBattery_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) CurrentBattery_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            CurrentBattery_Chart.getData().notifyDataChanged();
            CurrentBattery_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        CurrentBattery_Chart.setData(data);
        CurrentBattery_Chart.invalidate();
    }
    private void GetVoltageBatteryChartData(ArrayList<Entry> values){
        LineDataSet set1;

        if (VoltageBattery_Chart.getData() != null &&
                VoltageBattery_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) VoltageBattery_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            VoltageBattery_Chart.getData().notifyDataChanged();
            VoltageBattery_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        VoltageBattery_Chart.setData(data);
        VoltageBattery_Chart.invalidate();
    }
    private void GetVoltageAverageChartData(ArrayList<Entry> values){
        LineDataSet set1;

        if (VoltageAverage_Chart.getData() != null &&
                VoltageAverage_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) VoltageAverage_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            VoltageAverage_Chart.getData().notifyDataChanged();
            VoltageAverage_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        VoltageAverage_Chart.setData(data);
        VoltageAverage_Chart.invalidate();
    }
    private void GetWattAverageChartData(ArrayList<Entry> values){
        LineDataSet set1;

        if (WattsAverage_Chart.getData() != null &&
                WattsAverage_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) WattsAverage_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            WattsAverage_Chart.getData().notifyDataChanged();
            WattsAverage_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        WattsAverage_Chart.setData(data);
        WattsAverage_Chart.invalidate();
    }
    private void GetDCLoadChartData(ArrayList<Entry> values){
        LineDataSet set1;

        if (DCLoad_Chart.getData() != null &&
                DCLoad_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) DCLoad_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            DCLoad_Chart.getData().notifyDataChanged();
            DCLoad_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        DCLoad_Chart.setData(data);
        DCLoad_Chart.invalidate();
    }
    private void GetPowerChartData(ArrayList<Entry> values){
        LineDataSet set1;

        if (Power_Chart.getData() != null &&
                Power_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) Power_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            Power_Chart.getData().notifyDataChanged();
            Power_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        Power_Chart.setData(data);
        Power_Chart.invalidate();
    }

    private void getValues(){

        Toast.makeText(MainActivity.this,"Connecting to Database",Toast.LENGTH_LONG).show();

        final ArrayList<Entry> mwt_values = new ArrayList<>();
        final ArrayList<Entry> iBatt_values = new ArrayList<>();
        final ArrayList<Entry> vBatt_values = new ArrayList<>();
        final ArrayList<Entry> vAvg_values = new ArrayList<>();
        final ArrayList<Entry> wAvg_values = new ArrayList<>();
        final ArrayList<Entry> DC_values = new ArrayList<>();
        final ArrayList<Entry> Pow_values = new ArrayList<>();

        final CollectionReference docRef = db
                .collection("values").document("User X")
                .collection("test");

        db.collection("values").document("User X")
                .collection("test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Downloading New Data",Toast.LENGTH_LONG).show();
                            ArrayList<Measurements> allMeasures = new ArrayList<>();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                allMeasures.add(document.toObject(Measurements.class));
                            }
                            allMeasures = TimeManager.OrderValuesByTime(allMeasures);

                            int counter = 0;
                            for (Measurements measurements:allMeasures) {
                                mwt_values.add(new Entry(counter, measurements.getCURRMWT()));
                                iBatt_values.add(new Entry(counter, measurements.getCURRBATT()));
                                vBatt_values.add(new Entry(counter, measurements.getVBATT()));
                                vAvg_values.add(new Entry(counter, measurements.getVAVG()));
                                wAvg_values.add(new Entry(counter, measurements.getWAVG()));
                                DC_values.add(new Entry(counter, measurements.getDCLOAD()));
                                Pow_values.add(new Entry(counter, measurements.getDCPOWER()));

                                Log.d(TAG, String.valueOf(measurements.getCURRMWT()));
                                Log.d(TAG, String.valueOf(measurements.getCURRBATT()));
                                Log.d(TAG, String.valueOf(measurements.getVBATT()));
                                Log.d(TAG, String.valueOf(measurements.getCURRMWT()));
                                Log.d(TAG, String.valueOf(measurements.getVAVG()));
                                Log.d(TAG, String.valueOf(measurements.getDCLOAD()));
                                Log.d(TAG, String.valueOf(measurements.getDCPOWER()));
                                counter++;
                            }

                            GetMicroWindTurbineChartData(mwt_values);
                            GetCurrentBatteryChartData(iBatt_values);
                            GetVoltageBatteryChartData(vBatt_values);
                            GetVoltageAverageChartData(vAvg_values);
                            GetWattAverageChartData(wAvg_values);
                            GetDCLoadChartData(DC_values);
                            GetPowerChartData(Pow_values);

                            list_item.removeAllViews();
                            list_item.addView(MicroWindTurbine_Chart);
                            list_item.addView(CurrentBattery_Chart);
                            list_item.addView(VoltageBattery_Chart);
                            list_item.addView(VoltageAverage_Chart);
                            list_item.addView(WattsAverage_Chart);
                            list_item.addView(DCLoad_Chart);
                            list_item.addView(Power_Chart);

                        } else {
                            Toast.makeText(MainActivity.this,"Couldn't Download Data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //private void setData(int count, float range){
    private void setData(ArrayList<Entry> values){

        LineDataSet set1;

        if (MicroWindTurbine_Chart.getData() != null &&
                MicroWindTurbine_Chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) MicroWindTurbine_Chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            MicroWindTurbine_Chart.getData().notifyDataChanged();
            MicroWindTurbine_Chart.notifyDataSetChanged();
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
        Log.d(TAG, "Plotting Graph");
        LineData data = new LineData(set1);
        data.setDrawValues(false);

        MicroWindTurbine_Chart.setData(data);
        MicroWindTurbine_Chart.invalidate();
    }
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