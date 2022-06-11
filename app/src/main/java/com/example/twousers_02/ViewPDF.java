package com.example.twousers_02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.widget.Toast;

import java.io.File;

public class ViewPDF extends AppCompatActivity {

    String HTML, PrescriptionNo, Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        Intent viewPDF=getIntent();
        HTML=viewPDF.getExtras().getString("HTML","");
        PrescriptionNo=viewPDF.getExtras().getString("Prescription No","");
        Date=viewPDF.getExtras().getString("Date","");

        String fileName="Prescription #"+PrescriptionNo;

        /*File file = new File(ViewPDF.this.getFilesDir(), fileName + ".pdf");
        HtmlToPdfConverter.fromHTMLString(ViewPDF.this, HTML)
                // Configure title for the created document.
                .title("Prescription #"+PrescriptionNo+"-"+Date)
                // Perform the conversion.
                .convertToPdfAsync(file)
                // Subscribe to the conversion result.
                .subscribe(() -> {
                    // Open and process the converted document.
                    PdfDocument document = PdfDocumentLoader.openDocument(ViewPDF.this, Uri.fromFile(file));
                }, throwable -> {
                    // Handle the error.
                });
        printPDF(file);*/
    }

    private void printPDF(File file) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(ViewPDF.this, file.getPath(), file.getName());
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        }
        catch (Exception e)
        {
            Toast.makeText(ViewPDF.this, "Error#2: "+e, Toast.LENGTH_LONG).show();
        }
    }
}