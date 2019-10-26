package com.example.dananny;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class InsertSection extends Thread {

    private String section;
    private AccessService accessService;
    private Document document;
    private final Font.FontFamily FONT_TYPE = Font.FontFamily.HELVETICA;

    InsertSection(Document document, String section, AccessService access){
        this.section = section;
        this.accessService = access;
        this.document = document;
    }

    @Override
    public void run() {
//        super.run();
        accessService.requestAccess();
        this.writeSection();
    }

    private void writeSection(){
        insertSeparatorLine(document);
        writeSubTitle(document, "Consumption");
        insertSeparatorLine(document);
        accessService.releaseAccess();
        accessService.decreaseCounter();
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


}
