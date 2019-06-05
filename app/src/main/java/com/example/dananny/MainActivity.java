package com.example.dananny;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

LineChart lineChart;
int fillColor = Color.argb(150,38, 166, 154);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.lineChart);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(true);
        lineChart.getLegend().setEnabled(false);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setAxisMaximum(26);
        lineChart.getAxisLeft().setAxisMinimum(0);

        setData(10,25);
    }

    private void setData(int count, float range){

        ArrayList<Entry> yVals = new ArrayList<>();

        for(int i=0;i<count;i++){
            float val = (float) (Math.random()*range);
            yVals.add(new Entry(i, val));
        }


        LineDataSet set1, set2;

        set1 = new LineDataSet(yVals, "Data Set1");
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

        LineData data = new LineData(set1);
        data.setDrawValues(false);

        lineChart.setData(data);
    }
}
