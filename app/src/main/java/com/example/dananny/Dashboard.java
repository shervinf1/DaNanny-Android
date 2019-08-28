package com.example.dananny;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        ButtonConfiguration();
        setBatteryLevelGraph();
    }

    public void ButtonConfiguration(){
        int theme = Color.rgb(32,175,36);
        int textColor = Color.WHITE;

        btnGraph.setBackgroundColor(theme);
        btnGraph.setTextColor(textColor);
        btnGraph.setText("Graph");


        btnEquipment.setBackgroundColor(theme);
        btnEquipment.setTextColor(textColor);
        btnEquipment.setText("Equipment");

        btnThresholds.setBackgroundColor(theme);
        btnThresholds.setTextColor(textColor);
        btnThresholds.setText("Thresholds");

        btnReports.setBackgroundColor(theme);
        btnReports.setTextColor(textColor);
        btnReports.setText("Reports");

        btnSettings.setBackgroundColor(theme);
        btnSettings.setTextColor(textColor);
        btnSettings.setText("Settings");

        btnLogout.setBackgroundColor(theme);
        btnLogout.setTextColor(textColor);
        btnLogout.setText("Log Out");
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

        setChargePercent();
    }

    private void setChargePercent(){
        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(7,"Available"));
        values.add(new PieEntry(3,""));

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
}
