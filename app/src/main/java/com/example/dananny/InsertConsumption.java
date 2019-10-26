package com.example.dananny;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

public class InsertConsumption extends Thread {
    private final Font.FontFamily FONT_TYPE = Font.FontFamily.HELVETICA;
    Document document;
    DocumentReference documentReference;
    String name;
    String value;
    AccessService accessService;

    InsertConsumption(Document doc, DocumentReference ref, String val, AccessService access){
        this.document = doc;
        this.documentReference = ref;
        this.value = val;
        this.accessService = access;
    }
    InsertConsumption(Document doc, String name, String val, AccessService access){
        this.document = doc;
        this.documentReference = null;
        this.value = val;
        this.accessService = access;
        this.name = name;
    }

    @Override
    public void run() {
//        super.run();
        this.accessService.requestAccess();
        if(this.documentReference == null){
            writeDeviceConsumption(this.document, this.name, this.value);
        }else{
            writeDeviceConsumption(this.document, this.documentReference, this.value);
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
                    if(task.getResult().toObject(Sources.class) != null){
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


                            accessService.releaseAccess();
                            accessService.decreaseCounter();

                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }else{
                        accessService.releaseAccess();
                        accessService.decreaseCounter();
                    }

                }else{
                    accessService.releaseAccess();
                    accessService.decreaseCounter();
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

            this.accessService.releaseAccess();
            accessService.decreaseCounter();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}
