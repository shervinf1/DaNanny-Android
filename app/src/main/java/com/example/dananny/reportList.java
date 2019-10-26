package com.example.dananny;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ProgressBar progressBar;

    Timestamp start;
    Timestamp end;

    RelativeLayout relativeLayout;
    RelativeLayout layout;

    Display mDisplay;
    String imagesUri;
    String filepath;
    Bitmap b;
    int totalHeight;
    int totalWidth;
    public static final int READ_PHONE = 110;
    String file_name = "Screenshot";
    File myPath;

    final List<Generation> generations = new ArrayList<>();
    final List<Measurements> measurements = new ArrayList<>();

    private final String TAG = "REPORT";
    private final Font.FontFamily FONT_TYPE = Font.FontFamily.HELVETICA;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getCurrentUser().getUid();
    final DocumentReference userDoc = db.collection("Users").document(userID);
    private FirebaseAuth mAuth;

    AccessService accessService = new AccessService();
    public static final Object OBJECT = new Object();

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

        progressBar = findViewById(R.id.progress_circular);

        //relativeLayout = findViewById(R.id.reportLayoutView);


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
                progressBar.setVisibility(View.VISIBLE);
                System.out.println("Creating PDF...");
                if (isStoragePermissionGranted()) {
                    Toast.makeText(getApplicationContext(), "Creating PDF", Toast.LENGTH_SHORT).show();
                    createReport();
                } else {
                    StoragePermission();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        startDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                String temp = "";
                month = month + 1;
                temp += (dayOfMonth < 10) ? ("0" + dayOfMonth + "/") : (dayOfMonth + "/");
                temp += (month < 10) ? ("0" + month + "/") : (month + "/");
                temp += year;
                startDate_editText.setText(temp);
                calendarStartLayout.setVisibility(View.GONE);

            }
        });
        endDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                String temp = "";
                month = month + 1;
                temp += (dayOfMonth < 10) ? ("0" + dayOfMonth + "/") : (dayOfMonth + "/");
                temp += (month < 10) ? ("0" + month + "/") : (month + "/");
                temp += year;
                endDate_editText.setText(temp);
                calendarEndLayout.setVisibility(View.GONE);
            }
        });


        mAuth = FirebaseAuth.getInstance();

    }

    private void createReport() {

        System.out.println("Printing");
/**
 * Creating Document
 */
            String filename = "EnergyCoreReport" + new TimeManager(new Date()).getFullTimestamp() + ".pdf";
            String filepath = getAppPath(getApplicationContext()) + filename;

            if (new File(filepath).exists()) {
                new File(filepath).delete();
            }

            // Location to save

            accessService.lockDocument();


            accessService.addToCounter(6);
            writeTitle("Energy Core Report");
            accessService.decreaseCounter();
            //insertSeparatorLine();
            accessService.decreaseCounter();

            //writeTimePeriod(startDate_editText.getText().toString(), endDate_editText.getText().toString());
            accessService.decreaseCounter();
            //insertSeparatorLine();
            accessService.decreaseCounter();

            //writeSubTitle("Generation");
            accessService.decreaseCounter();
            //insertSeparatorLine();
            accessService.decreaseCounter();

            Toast.makeText(getApplicationContext(), "Getting Generation", Toast.LENGTH_SHORT).show();
            //getSourcesGeneration();



    }

    /**
     * Write Title to document
     *
     * @param document - document to write
     * @param title    - Title to insert
     */
    private void writeTitle(String title) {

        TextView textView = new TextView(getApplicationContext());
        textView.setText(title);
        textView.setTextSize(36f);

        relativeLayout.addView(textView);

    }

    /**
     * Write time  to document
     *
     * @param document  - document to write
     * @param startDate - start date to insert
     * @param endDate   - end date to insert
     */
    private void writeTimePeriod(Document document, String startDate, String endDate) {

        float mHeadingFontSize = 20.0f;

        try {
            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdChunk = new Chunk("From: " + startDate, mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderDateValueFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk("To: " + endDate, mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeSubTitle(Document document, String subTitle) {
        try {
            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(FONT_TYPE, 26.0f, Font.NORMAL, BaseColor.BLACK);
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

//        LineSeparator lineSeparator = new LineSeparator();
//        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
//        try {
//            document.add(new Paragraph(""));
//            document.add(new Chunk(lineSeparator));
//            document.add(new Paragraph(""));
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        TextView textView = new TextView(getApplicationContext());
        textView.setWidth((int)dpWidth);
        textView.setHeight(1);

        relativeLayout.addView(textView);
    }

    private void writeSourceGeneration(final Document document, DocumentReference documentReference, final String generation) {

        /***
         * Variables for further use....
         */
        final BaseColor mColorAccent = new BaseColor(32, 175, 36, 255);
        final float mHeadingFontSize = 20.0f;
        final float mValueFontSize = 26.0f;

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String sourceName = task.getResult().toObject(Sources.class).getName();

                    try {
                        // Fields of Order Details...
                        // Adding Chunks for Title and value
                        Font mOrderIdFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, mColorAccent);
                        Chunk mOrderIdChunk = new Chunk(sourceName, mOrderIdFont);
                        Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
                        document.add(mOrderIdParagraph);

                        Font mOrderDateValueFont = new Font(FONT_TYPE, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderDateValueChunk = new Chunk(generation, mOrderDateValueFont);
                        Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
                        document.add(mOrderDateValueParagraph);

                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void writeSourceGeneration(final Document document, String sourceName, final String generation) {

        /***
         * Variables for further use....
         */
        final BaseColor mColorAccent = new BaseColor(32, 175, 36, 255);
        final float mHeadingFontSize = 20.0f;
        final float mValueFontSize = 26.0f;

        try {
            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk(sourceName, mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderDateValueFont = new Font(FONT_TYPE, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk(generation, mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void writeDeviceConsumption(final Document document, DocumentReference documentReference, final String consumption) {

        /***
         * Variables for further use....
         */
        final BaseColor mColorAccent = new BaseColor(255, 87, 51, 255);
        final float mHeadingFontSize = 20.0f;
        final float mValueFontSize = 26.0f;

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String sourceName = task.getResult().toObject(Sources.class).getName();

                    try {
                        // Fields of Order Details...
                        // Adding Chunks for Title and value
                        Font mOrderIdFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, mColorAccent);
                        Chunk mOrderIdChunk = new Chunk(sourceName, mOrderIdFont);
                        Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
                        document.add(mOrderIdParagraph);

                        Font mOrderDateValueFont = new Font(FONT_TYPE, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderDateValueChunk = new Chunk(consumption, mOrderDateValueFont);
                        Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
                        document.add(mOrderDateValueParagraph);

                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void writeDeviceConsumption(Document document, String deviceName, String consumption) {

        /***
         * Variables for further use....
         */
        BaseColor mColorAccent = new BaseColor(255, 87, 51, 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;

        try {
            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk(deviceName, mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);


            Font mOrderDateValueFont = new Font(FONT_TYPE, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk(consumption, mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private Boolean getSourcesGeneration(final Document document) {

        String beginDate = startDate_editText.getText().toString().trim();
        String finalDate = endDate_editText.getText().toString().trim();
        Long start_millis = 0l;
        Long end_millis = 0l;
        final int seconds_in_days = 86400;

        try {
            start_millis = (new SimpleDateFormat("dd/MM/yyyy").parse(beginDate).getTime()) / 1000;
            end_millis = ((new SimpleDateFormat("dd/MM/yyyy").parse(finalDate).getTime()) / 1000) + seconds_in_days - 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        db.collection("Generation")
                .orderBy("date")
                .orderBy("sourceID")
                .whereEqualTo("userID", userDoc)
                .whereGreaterThanOrEqualTo("date", start_millis)
                .whereLessThanOrEqualTo("date", end_millis)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            generations.clear();

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                generations.add(doc.toObject(Generation.class));
                            }

                            Log.d("Before Generation", "Size: " + generations.size());
                            List<Generation> temp = new ArrayList<>();
                            temp = groupBySourceID(generations);
                            Log.d("After Generation", "Size: " + temp.size());
                            accessService.addToCounter(temp.size());

                            int total = 0;
                            for (Generation generation : temp) {
                                total += generation.getWatts();
                                Log.d("Writing Generation", "watts: " + generation.getWatts());
                                //writeSourceGeneration(document, generation.getSourceID(), String.valueOf(generation.getWatts()));
                                //accessService.increaseCounter();
                                System.out.println("Writing Generation..." + total);
                                new InsertGeneration(document, generation.getSourceID(), String.valueOf(generation.getWatts()), accessService).start();
                            }

                            accessService.increaseCounter();
                            System.out.println("Waiting Generation Total: " + total);
                            //writeSourceGeneration(document, "Total Generated", String.valueOf(total));
                            new InsertGeneration(document, "Total Generated", String.valueOf(total), accessService).start();

                            accessService.increaseCounter();
                            new InsertSection(document, "Consumption", accessService).start();



                            Toast.makeText(getApplicationContext(), "Getting Consumption", Toast.LENGTH_SHORT).show();

                            getDeviceConsumption(document);
                        }
                    }
                });
        return true;
    }

    private Boolean getDeviceConsumption(final Document document) {

        String beginDate = startDate_editText.getText().toString().trim();
        String finalDate = endDate_editText.getText().toString().trim();
        Long start_millis = 0l;
        Long end_millis = 0l;
        final int seconds_in_days = 86400;

        try {
            start_millis = (new SimpleDateFormat("dd/MM/yyyy").parse(beginDate).getTime()) / 1000;
            end_millis = ((new SimpleDateFormat("dd/MM/yyyy").parse(finalDate).getTime()) / 1000) + seconds_in_days - 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        db.collection("Measurements")
                .orderBy("date")
                .orderBy("deviceID")
                .whereEqualTo("userID", userDoc)
                .whereGreaterThanOrEqualTo("date", start_millis)
                .whereLessThanOrEqualTo("date", end_millis)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            measurements.clear();

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                measurements.add(doc.toObject(Measurements.class));
                            }

                            Log.d("Before Consumption", "Size: " + measurements.size());
                            List<Measurements> temp = new ArrayList<>();
                            temp = groupByDeviceID(measurements);
                            Log.d("After Consumption", "Size: " + temp.size());
                            accessService.addToCounter(temp.size());

                            int total = 0;
                            for (Measurements measurement : temp) {
                                total += measurement.getWatts();
                                Log.d("Writing Consumption", "watts: " + measurement.getWatts());
//                                    writeDeviceConsumption(document, measurement.getDeviceID(), String.valueOf(measurement.getWatts()));
                                System.out.println("Writing Consumption..." + total);
                                new InsertConsumption(document, measurement.getDeviceID(), String.valueOf(measurement.getWatts()), accessService).start();
                            }


                            accessService.increaseCounter();
                            System.out.println("Writing Consumption Total: " + total);
                            //writeDeviceConsumption(document, "Total Consumed", String.valueOf(total));
                            new InsertConsumption(document, "Total Consumed", String.valueOf(total), accessService).start();


                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "PDF Created Successfully", Toast.LENGTH_SHORT).show();


                            new CloseDocument(document, accessService).start();
                        }
                    }
                });
        return true;
    }

    private List<Generation> groupBySourceID(List<Generation> generationList) {

        List<Generation> generationDummy = new ArrayList<>();

        if (generationList.get(0) != null) {

            Generation dummy = generationList.get(0);
            generationDummy.add(dummy);

            for (int i = 1; i < generationList.size(); i++) {

                System.out.println("Comparing dummy: " + dummy.getSourceID()
                + " with element " + i + " :" + generationList.get(i).getSourceID());

                if (dummy.getSourceID().toString().equals(generationList.get(i).getSourceID().toString())) {
                    dummy.setCurrent(dummy.getCurrent() + generationList.get(i).getCurrent());
                    dummy.setWatts(dummy.getWatts() + generationList.get(i).getWatts());
                } else {
                    dummy = generationList.get(i);
                    generationDummy.add(dummy);
                }
            }
        }

        //generationList = generationDummy;
        generationList.clear();
        for (Generation generation : generationDummy) {
            generationList.add(generation);
        }

        return generationList;
    }

    private List<Measurements> groupByDeviceID(List<Measurements> measurementsList) {

        List<Measurements> measurementDummy = new ArrayList<>();

        if (measurementsList.get(0) != null) {

            Measurements dummy = measurementsList.get(0);
            measurementDummy.add(dummy);

            for (int i = 1; i < measurementsList.size(); i++) {

                System.out.println("Comparing dummy: " + dummy.getDeviceID()
                        + " with element " + i + " :" + measurementsList.get(i).getDeviceID());

                if (dummy.getDeviceID().toString().equals(measurementsList.get(i).getDeviceID().toString())) {
                    dummy.setCurrent(dummy.getCurrent() + measurementsList.get(i).getCurrent());
                    dummy.setWatts(dummy.getWatts() + measurementsList.get(i).getWatts());
                } else {
                    dummy = measurementsList.get(i);
                    measurementDummy.add(dummy);
                }
            }
        }

//        measurementsList = measurementDummy;
        measurementsList.clear();
        for (Measurements measurement : measurementDummy) {
            measurementsList.add(measurement);
        }

        return measurementsList;
    }

    private void submitReportData(String filename, String generation, String consumption) {

        Map<String, Object> report = new HashMap<>();
        report.put("name", filename);
        report.put("generation", generation);
        report.put("consumption", consumption);
        report.put("date", new Timestamp(new Date()));
        report.put("userID", userDoc);

        db.collection("Report").document()
                .set(report, SetOptions.merge());
    }


    private void takeScreenShot() {

        String filename = "EnergyCoreReport" + new TimeManager(new Date()).getFullTimestamp() + ".pdf";
        filepath = getAppPath(getApplicationContext()) + filename;

        if (new File(filepath).exists()) {
            new File(filepath).delete();
        }

        // List<Integer> hights = new ArrayList<>();
        View u = findViewById(R.id.scroll);

        NestedScrollView z = (NestedScrollView) findViewById(R.id.scroll);
        totalHeight = z.getChildAt(0).getHeight();
        totalWidth = z.getChildAt(0).getWidth();

        Log.e("totalHeight--", "" + totalHeight);
        Log.e("totalWidth--", "" + totalWidth);

        //Save bitmap
        String extr = Environment.getExternalStorageDirectory() + "/ScreenShot/";
        File file = new File(extr);
        if (!file.exists())
            file.mkdir();
        String fileName = file_name + ".jpg";
        myPath = new File(extr, fileName);
        imagesUri = myPath.getPath();
        FileOutputStream fos = null;
        b = getBitmapFromView(u, totalHeight, totalWidth);

        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        createPdf();

    }

    private void createPdf() {

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(b.getWidth(), b.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);


        Bitmap bitmap = Bitmap.createScaledBitmap(b, b.getWidth(), b.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        File filePath = new File(filepath);
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();


        if (myPath.exists())
            myPath.delete();

        //openPdf(filepath);
    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
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

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    public void StoragePermission() {
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    createReport();
                } else {
                    System.out.println("Dismissed");
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    createReport();
                } else {
                    System.out.println("Dismissed");
                }
                break;
        }
    }

}
