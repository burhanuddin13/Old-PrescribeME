package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.nvest.html_to_pdf.HtmlToPdfConvertor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ConfirmProfile extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference signRef;

    String PatName, PatAge, PatGender, Diagnosis, Information;
    String DocName, DocQualif, DocRegNo, DocClinic;
    String PresHTML="", PatHTML="", DocHTML="", Head="", Footer="", HTML="", DiagHTML="", InfoHTML="";
    String Date, PrescriptionNo, signURL, UserID;
    String[] pat_info =new String[3];
    String[] doc_info =new String[4];

    File PDF;

    TextView txtDocName, txtDocQualif, txtDocRegNo, txtDocClinic, txtPatName, txtPatAge, txtPatGender, txtSignURL;
    Button btnHTML, btnPDF, btnReset, btnShare;
    ImageView ProfComplete, SignComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_profile);

        Intent conf_int=getIntent();
        pat_info[0]=PatName=conf_int.getExtras().getString("Name");
        pat_info[1]=PatAge=conf_int.getExtras().getString("Age");
        pat_info[2]=PatGender=conf_int.getExtras().getString("Gender");
        Diagnosis=conf_int.getExtras().getString("Diagnosis");
        Information=conf_int.getExtras().getString("Information");
        PresHTML=conf_int.getExtras().getString("PresHTML");
        PrescriptionNo=conf_int.getExtras().getString("Prescription No");

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        UserID=user.getUid();
        storage=FirebaseStorage.getInstance();
        signRef=storage.getReference().child("Signatures").child(UserID.trim()+".jpg");

        signRef.getDownloadUrl().addOnSuccessListener(uri -> txtSignURL.setText(uri.toString()));

        txtDocName=(TextView) findViewById(R.id.viewdocname);
        txtDocQualif=(TextView) findViewById(R.id.viewdocqualif);
        txtDocRegNo=(TextView) findViewById(R.id.viewdocregno);
        txtDocClinic=(TextView) findViewById(R.id.viewdocclinic);

        txtSignURL=(TextView) findViewById(R.id.txtSignatureURL);

        txtDocName.setText(user.getDisplayName());
        displayDocInfo();

        txtPatName=(TextView) findViewById(R.id.viewpatname);
        txtPatAge=(TextView) findViewById(R.id.viewpatage);
        txtPatGender=(TextView) findViewById(R.id.viewpatgender);
        txtPatName.setText(PatName);
        txtPatAge.setText(PatAge);
        txtPatGender.setText(PatGender);

        ProfComplete = (ImageView) findViewById(R.id.ProfComplete);
        SignComplete = (ImageView) findViewById(R.id.SignComplete);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String[] splitDate=formattedDate.split("-");
        Date=splitDate[0]+" "+splitDate[1]+" "+splitDate[2];


        if(!Diagnosis.trim().isEmpty())
            DiagHTML=PrescriptionHTML.getDiagInfo("Diagnosis", Diagnosis);
        if(!Information.trim().isEmpty())
            InfoHTML=PrescriptionHTML.getDiagInfo("Additional Information", Information);

        btnHTML=(Button) findViewById(R.id.btnHTML);
        btnHTML.setOnClickListener(v -> {
            doc_info[0]=DocName=txtDocName.getText().toString();
            doc_info[1]=DocQualif=txtDocQualif.getText().toString();
            doc_info[2]=DocRegNo=txtDocRegNo.getText().toString();
            doc_info[3]=DocClinic=txtDocClinic.getText().toString();
            signURL=txtSignURL.getText().toString();
            PatHTML=PrescriptionHTML.getPatInfo(pat_info, Date, PrescriptionNo);
            DocHTML=PrescriptionHTML.getDocInfo(doc_info);
            Head=PrescriptionHTML.getHeadHTML();
            Footer=PrescriptionHTML.getFooterHTML(DocName, signURL);
            HTML=Head+DocHTML+PatHTML+DiagHTML+PresHTML+InfoHTML+Footer;
            Intent view_html=new Intent(ConfirmProfile.this, ViewHTML.class);
            view_html.putExtra("HTML", HTML);
            view_html.putExtra("Prescription No", PrescriptionNo);
            view_html.putExtra("Date", Date);
            startActivity(view_html);
        });

        btnShare=(Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> {
            //Check if Permission for Location is not Granted
            if (ContextCompat.checkSelfPermission(ConfirmProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                //Request Permission for Location
                ActivityCompat.requestPermissions(ConfirmProfile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            else
//                    btnFetch.setVisibility(View.VISIBLE);
            Toast.makeText(ConfirmProfile.this, "Granted!", Toast.LENGTH_SHORT).show();
            Intent shareIntent=new Intent(Intent.ACTION_SEND);
            shareIntent.setType("*/*");
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM, PDF.toURI());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share " + PDF.getName()));
            } catch (Exception e)
            {
                Toast.makeText(ConfirmProfile.this, "Error: "+e, Toast.LENGTH_SHORT).show();
            }
        });

        btnPDF=(Button) findViewById(R.id.btnPDF);
        btnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doc_info[0]=DocName=txtDocName.getText().toString();
                doc_info[1]=DocQualif=txtDocQualif.getText().toString();
                doc_info[2]=DocRegNo=txtDocRegNo.getText().toString();
                doc_info[3]=DocClinic=txtDocClinic.getText().toString();
                signURL=txtSignURL.getText().toString();
                PatHTML=PrescriptionHTML.getPatInfo(pat_info, Date, PrescriptionNo);
                DocHTML=PrescriptionHTML.getDocInfo(doc_info);
                Head=PrescriptionHTML.getHeadHTML();
                Footer=PrescriptionHTML.getFooterHTML(DocName, signURL);
                HTML=Head+DocHTML+PatHTML+DiagHTML+PresHTML+InfoHTML+Footer;
                createPDF();
                btnShare.setVisibility(View.VISIBLE);
            }
        });

        btnReset=(Button) findViewById(R.id.btnBack);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pat_int=new Intent(ConfirmProfile.this, PatientInfo.class);
                pat_int.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pat_int.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(pat_int);
            }
        });
    }


    private void displayDocInfo() {
        DatabaseReference realref= FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        realref.child("Profile Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int j=1;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String snap_value=snapshot1.getValue().toString();
                    String snap_name= snapshot1.getKey();
                    //values.add(snap_value);

                    //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
                    switch (snap_name)
                    {
                        case "Qualifications":
                            txtDocQualif.setText(snap_value);
                            break;
                        case "Registration No":
                            txtDocRegNo.setText(snap_value);
                            break;
                        case "Clinic":
                            txtDocClinic.setText(snap_value);
                        case "First Name":
                        case "Last Name":
                        case "Aadhar No":
                            break;
                        default:
                            Toast.makeText(ConfirmProfile.this, (j++)+": "+snap_value,Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmProfile.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPDF(){
        String fileName = "Prescription-" + PrescriptionNo;
        PDF = new File(ConfirmProfile.this.getExternalFilesDir(null), fileName + ".pdf");
        HtmlToPdfConvertor htmlToPdfConvertor = new HtmlToPdfConvertor(ConfirmProfile.this);
        htmlToPdfConvertor.convert(PDF, HTML, e -> {
            e.printStackTrace();
            Toast.makeText(ConfirmProfile.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
            return null; }, file -> {
            printPDF(file, fileName);
        return null;});
    }

    private void printPDF(File file, String name) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(ConfirmProfile.this, file.getPath(), name);
            printManager.print(name, printDocumentAdapter, new PrintAttributes.Builder().build());
        }
        catch (Exception e)
        {
            Toast.makeText(ConfirmProfile.this, "Error#2: "+e, Toast.LENGTH_LONG).show();
        }
    }

    //Function to Request Permission for Location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //If Permission is Granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(ConfirmProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ConfirmProfile.this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                    btnFetch.setVisibility(View.VISIBLE); //Making "btnFetch" Visible
                }
            } else {
                Toast.makeText(ConfirmProfile.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}