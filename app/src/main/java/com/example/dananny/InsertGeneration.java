package com.example.dananny;

import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

public class InsertGeneration extends Thread{

    private final Font.FontFamily FONT_TYPE = Font.FontFamily.HELVETICA;
    LinearLayout document;
    DocumentReference documentReference;
    String value;
    String name;
    android.content.Context context;
    AccessService accessService;

    InsertGeneration(LinearLayout doc, DocumentReference ref, String val, android.content.Context context, AccessService access){
        this.document = doc;
        this.documentReference = ref;
        this.value = val;
        this.context = context;
        this.accessService = access;
    }

    InsertGeneration(LinearLayout doc, String name, String val, android.content.Context context, AccessService access){
        this.document = doc;
        this.documentReference = null;
        this.name = name;
        this.value = val;
        this.context = context;
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

    private void writeSourceGeneration(final LinearLayout document, DocumentReference documentReference, final String generation) {

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

//                        try {
//                            // Fields of Order Details...
//                            // Adding Chunks for Title and value
//                            Font mOrderIdFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, mColorAccent);
//                            Chunk mOrderIdChunk = new Chunk(sourceName, mOrderIdFont);
//                            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
//                            document.add(mOrderIdParagraph);
//
//                            Font mOrderDateValueFont = new Font(FONT_TYPE, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
//                            Chunk mOrderDateValueChunk = new Chunk(generation, mOrderDateValueFont);
//                            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
//                            document.add(mOrderDateValueParagraph);
//
//                            accessService.releaseAccess();
//                            accessService.decreaseCounter();
//
//                        } catch (DocumentException e) {
//                            e.printStackTrace();
//                            System.out.println("ERROR" + e.toString());
//                        }

                        TextView textView1 = new TextView(context);
                        textView1.setText(sourceName);
                        textView1.setTextSize(20f);

                        TextView textView2 = new TextView(context);
                        textView2.setText(generation);
                        textView2.setTextSize(26f);

                        document.addView(textView1);
                        document.addView(textView2);

                        accessService.releaseAccess();
                        accessService.decreaseCounter();

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


    private void writeSourceGeneration(final LinearLayout document, String sourceName, final String generation) {

        /***
         * Variables for further use....
         */
        final BaseColor mColorAccent = new BaseColor(32, 175, 36, 255);
        final float mHeadingFontSize = 20.0f;
        final float mValueFontSize = 26.0f;

//        try {
//            // Fields of Order Details...
//            // Adding Chunks for Title and value
//            Font mOrderIdFont = new Font(FONT_TYPE, mHeadingFontSize, Font.NORMAL, mColorAccent);
//            Chunk mOrderIdChunk = new Chunk(sourceName, mOrderIdFont);
//            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
//            document.add(mOrderIdParagraph);
//
//            Font mOrderDateValueFont = new Font(FONT_TYPE, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
//            Chunk mOrderDateValueChunk = new Chunk(generation, mOrderDateValueFont);
//            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
//            document.add(mOrderDateValueParagraph);
//
//            accessService.releaseAccess();
//            accessService.decreaseCounter();
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }

        TextView textView1 = new TextView(context);
        textView1.setText(sourceName);
        textView1.setTextSize(20f);

        TextView textView2 = new TextView(context);
        textView2.setText(generation);
        textView2.setTextSize(26f);

        document.addView(textView1);
        document.addView(textView2);

        accessService.releaseAccess();
        accessService.decreaseCounter();
    }


}
