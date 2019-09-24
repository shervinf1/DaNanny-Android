package com.example.dananny;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;

import java.util.Calendar;

public class reportList extends AppCompatActivity {

    Button startDate_button;
    Button endDate_button;
    EditText startDate_editText;
    EditText endDate_editText;
    GridLayout calendarStartLayout;
    GridLayout calendarEndLayout;
    DatePicker startDatePicker;
    DatePicker endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        getSupportActionBar().hide();

        //Initialize elements
        startDate_button = findViewById(R.id.startButton);
        endDate_button = findViewById(R.id.endButton);

        startDate_editText = findViewById(R.id.startText);
        endDate_editText = findViewById(R.id.endText);

        calendarStartLayout = findViewById(R.id.calendarStartView);
        calendarEndLayout = findViewById(R.id.calendarEndView);

        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);


        //Add listeners
        startDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarStartLayout.setVisibility(View.VISIBLE);
            }
        });
        endDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarEndLayout.setVisibility(View.VISIBLE);
            }
        });


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        startDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                startDate_editText.setText(dayOfMonth + "/" + month + "/" + year);
                calendarStartLayout.setVisibility(View.GONE);

            }
        });
        endDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                endDate_editText.setText(dayOfMonth + "/" + month + "/" + year);
                calendarEndLayout.setVisibility(View.GONE);
            }
        });
    }
}
