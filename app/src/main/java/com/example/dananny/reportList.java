package com.example.dananny;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    Button report_button;

    private final String TAG = "REPORT";


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

        report_button = findViewById(R.id.createReportBtn);


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
        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Creating PDF...");
                if(isStoragePermissionGranted()){
                    createReport();
                }else{
                    StoragePermission();
                }

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
                //createReport();
            }
        });


    }

    private void createReport() {

        System.out.println("Printing");
/**
 * Creating Document
 */
        Document document = new Document();
        try {
            //java.io.FileNotFoundException: /storage/emulated/0/DaNanny/EnergyCoreReport.pdf (No such file or directory)

            isStoragePermissionGranted();
            //String filepath = "/sdcard/EnergyCoreReport.pdf";
            String filepath = getAppPath(getApplicationContext()) + "EnergyCoreReport.pdf";

            if (new File(filepath).exists()) {
                new File(filepath).delete();
            }

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(filepath));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.LETTER);
            document.addCreationDate();
            document.addAuthor("Energy Core");
            document.addCreator("Energy Core");

            writeTitle(document, "Energy Core Report");
            insertSeparatorLine(document);
            writeTimePeriod(document, startDate_editText.getText().toString(), endDate_editText.getText().toString());
            insertSeparatorLine(document);
            writeSubTitle(document, "Generation");
            insertSeparatorLine(document);
            writeSourceGeneration(document, "Solar Panels", "150kW");
            writeSourceGeneration(document, "Wind Turbines", "150kW");
            writeSourceGeneration(document, "Total Generated", "300kW");
            insertSeparatorLine(document);
            writeSubTitle(document, "Consumption");
            insertSeparatorLine(document);
            writeDeviceConsumption(document, "Television", "60kW");
            writeDeviceConsumption(document, "Air Conditioner", "120kW");
            writeDeviceConsumption(document, "Total Consumed", "180kW");
            insertSeparatorLine(document);
            writeSourceGeneration(document, "Overall", "+180kW");

            document.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void writeTitle(Document document, String title) {
        try {
            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 36.0f, Font.NORMAL, BaseColor.BLACK);
            // Creating Chunk
            Chunk mTitleChunk = new Chunk(title, mOrderDetailsTitleFont);
            // Creating Paragraph to add...
            Paragraph mTitleParagraph = new Paragraph(mTitleChunk);
            // Setting Alignment for Heading
            mTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            // Finally Adding that Chunk
            document.add(mTitleParagraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeSubTitle(Document document, String subTitle) {
        try {
            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLACK);
            // Creating Chunk
            Chunk mTitleChunk = new Chunk(subTitle, mOrderDetailsTitleFont);
            // Creating Paragraph to add...
            Paragraph mTitleParagraph = new Paragraph(mTitleChunk);
            // Setting Alignment for Heading
            mTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            // Finally Adding that Chunk
            document.add(mTitleParagraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void insertSeparatorLine(Document document) {

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        try {
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeTimePeriod(Document document, String startDate, String endDate){
        /***
         * Variables for further use....
         */
        BaseColor mColorAccent = new BaseColor(52, 73, 94, 255);
        float mHeadingFontSize = 20.0f;

        try {
            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(Font.FontFamily.COURIER, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk("From: " + startDate, mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderDateValueFont = new Font(Font.FontFamily.COURIER, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk("To: " + endDate, mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeSourceGeneration(Document document, String sourceName, String generation) {

        /***
         * Variables for further use....
         */
        BaseColor mColorAccent = new BaseColor(32, 175, 36, 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;

        try {
            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(Font.FontFamily.COURIER, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk(sourceName, mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderDateValueFont = new Font(Font.FontFamily.COURIER, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk(generation, mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeDeviceConsumption(Document document, String deviceName, String consumption) {

        /***
         * Variables for further use....
         */
        BaseColor mColorAccent = new BaseColor(255, 87, 51 , 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;

        try {
            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(Font.FontFamily.COURIER, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk(deviceName, mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);


            Font mOrderDateValueFont = new Font(Font.FontFamily.COURIER, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk(consumption, mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Path of App which contains Files
     *
     * @return path of root dir
     */
    public static String getAppPath(Context context) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + context.getResources().getString(R.string.app_name)
                + File.separator);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getPath() + File.separator;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void StoragePermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    createReport();
                }else{
                    System.out.println("Dismissed");
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    createReport();
                }else{
                    System.out.println("Dismissed");
                }
                break;
        }
    }

}
