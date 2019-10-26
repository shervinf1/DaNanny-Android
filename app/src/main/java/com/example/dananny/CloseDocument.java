package com.example.dananny;

import com.itextpdf.text.Document;

public class CloseDocument extends Thread {

    Document document;
    AccessService accessService;

    CloseDocument(Document document, AccessService accessService){
        this.document = document;
        this.accessService = accessService;
    }

    @Override
    public void run() {
//        super.run();
        this.closePDF();
    }

    private void closePDF(){
        this.accessService.freeDocument();
        this.document.close();
    }
}
