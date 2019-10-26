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

public class InsertGeneration extends Thread{

    private final Font.FontFamily FONT_TYPE = Font.FontFamily.HELVETICA;
    Document document;
    DocumentReference documentReference;
    String value;
    String name;
    AccessService accessService;

    InsertGeneration(Document doc, DocumentReference ref, String val, AccessService access){
        this.document = doc;
        this.documentReference = ref;
        this.value = val;
        this.accessService = access;
    }

    InsertGeneration(Document doc, String name, String val, AccessService access){
        this.document = doc;
        this.documentReference = null;
        this.name = name;
        this.value = val;
        this.accessService = access;
    }

    @Override
    public void run() {
//        super.run();
        this.accessService.requestAccess();
        if(this.documentReference == null){
            writeSourceGeneration(this.document, this.name, this.value);
        }else{
            writeSourceGeneration(this.document, this.documentReference, this.value);
        }
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
                if(task.isSuccessful()){
                    if(task.getResult().toObject(Sources.class)!=null){
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

                            accessService.releaseAccess();
                            accessService.decreaseCounter();

                        } catch (DocumentException e) {
                            e.printStackTrace();
                            System.out.println("ERROR" + e.toString());
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

            accessService.releaseAccess();
            accessService.decreaseCounter();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}
