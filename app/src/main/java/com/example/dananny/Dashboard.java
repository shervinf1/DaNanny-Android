package com.example.dananny;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    PieChart pieChart;
    Button btnGraph;
    Button btnEquipment;
    Button btnThresholds;
    Button btnReports;
    Button btnSettings;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pieChart = findViewById(R.id.BatteryLevel);
        btnGraph = findViewById(R.id.BtnGraph);
        btnEquipment = findViewById(R.id.BtnEquipments);
        btnThresholds = findViewById(R.id.BtnThreshold);
        btnReports = findViewById(R.id.BtnReport);
        btnSettings = findViewById(R.id.BtnSettings);
        btnLogout = findViewById(R.id.BtnLogout);

        setBatteryLevelGraph();
    }

    public void setBatteryLevelGraph(){
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
        pieChart.setCenterText("Battery Level");
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.setMaxAngle(180);
        pieChart.setRotationAngle(180f);
        pieChart.setClickable(false);
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        setChargePercent((float) 80);
    }

    private void setChargePercent(float avialable){
        ArrayList<PieEntry> values = new ArrayList<>();
        int[] colorArray;

        values.add(new PieEntry(avialable,""));
        values.add(new PieEntry(100-avialable,""));

        PieDataSet dataSet = new PieDataSet(values, "Measurements");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(7f);


       //When avialable is bigger than a certain number


        if(avialable>70) {
            colorArray = new int[]{Color.rgb(32, 175, 36), Color.TRANSPARENT}; //High Charge
        } else if(avialable<30) {
            colorArray = new int[]{Color.rgb(209, 13, 49), Color.TRANSPARENT}; //Medium Charge
        } else{
            colorArray=new int[]{Color.rgb(255,255,51),Color.TRANSPARENT};  //Low Charge
        }
        dataSet.setColors(colorArray);

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
}
