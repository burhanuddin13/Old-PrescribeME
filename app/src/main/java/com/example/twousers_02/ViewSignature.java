package com.example.twousers_02;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

public class ViewSignature extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realdb;
    DatabaseReference realref;
    FirebaseStorage storage;//Make Changes to realdb.getReference().child(UserID)
    ProgressDialog mLoadingBar;
    StorageReference signref;

    WebView webView;
    ImageView signView;
    String UserID, imgExt;
    TextView signExt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_signature);

        webView=new WebView(this);
        signView=(ImageView)findViewById(R.id.signView);
        signExt=(TextView) findViewById(R.id.signExt);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        UserID=user.getUid();

        realdb=FirebaseDatabase.getInstance();
        realref=realdb.getReference().child(UserID);
        storage=FirebaseStorage.getInstance();
        signref= storage.getReference().child("Signatures");

        realref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(ViewSignature.this, "Please Wait while we load your signature", Toast.LENGTH_LONG).show();
                int i = 0, j = 1;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String snap_value = snapshot1.getValue().toString();
                    String snap_name = snapshot1.getKey();

                    //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
                    switch (snap_name) {
                        case "Completed":
                        case "Profile Info":
                            break;
                        case "Sign Extension":
                            Toast.makeText(ViewSignature.this, snap_value, Toast.LENGTH_LONG).show();
                            //StorageReference storageReference = signref.child(UserID + "." + snap_value);
                            signExt.setText(snap_value);
                            StorageReference storageReference = signref.child("Caduceus.svg");
                            /*storageReference.getBytes(2*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    signView.setImageBitmap(bitmap);
                                }
                            });*/
                            try {
                                File localFile = File.createTempFile("Caduceus", "svg", ViewSignature.this.getFilesDir());
                                storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                    Bitmap bitmap=BitmapFactory.decodeFile("Caduceus.svg");
                                    signView.setImageBitmap(bitmap);
                                    // Local temp file has been created
                                }).addOnFailureListener(exception -> {
                                    //TODO Handle any errors
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            /*try{
                                Glide.with(ViewSignature.this).load(storageReference).into(signView);
                            }
                            catch (Exception e){
                                Toast.makeText(ViewSignature.this, e.toString(), Toast.LENGTH_LONG).show();
                            }*/
                            //String downloadUrl=storageReference.getDownloadUrl().toString();
                            //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/testing-modules-27d9b.appspot.com/o/Signatures%2FQtgexRN3ylX2Yo0PvmYCFOiAukP2.jpg?alt=media&token=ecf60fc9-a5e0-4c7b-be30-9013334e647e").into(signView);
                            /*storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful())
                                    {
                                        String downloadUrl =
                                        Toast.makeText(ViewSignature.this, "URL: "+downloadUrl, Toast.LENGTH_LONG).show();
                                    }
                                    else Toast.makeText(ViewSignature.this, "Error: "+task.getException().toString(), Toast.LENGTH_LONG).show();
                                }
                            });*/
                            //Toast.makeText(ViewSignature.this, "URL: "+downloadUrl, Toast.LENGTH_LONG).show();
                            //String firebaseURL="https://firebasestorage.googleapis.com/v0/b/testing-modules-27d9b.appspot.com/o/Signatures%2FQtgexRN3ylX2Yo0PvmYCFOiAukP2.jpg?alt=media&token=ecf60fc9-a5e0-4c7b-be30-9013334e647e";
                            //String imageHTML="<img src=\""+firebaseURL+"\"><br>";
                            //String nameHTMl="<div style=\"font-family: \'Brush Script MT\', cursive;\">"+user.getDisplayName()+"</div>";
                            //String urlHTML="<bR><div style=\"font-family: \'Courier New\', monospace;\""+downloadUrl+"</div>";
                            //String signHTML="<html><body style=\"margin-top:100px;text-align:center;\">"+imageHTML+nameHTMl+"</body></html>";
                            //webView.loadData(signHTML, "text/html", "UTF-8");
                            //setContentView(webView);
                            break;
                        case "Sign Uploaded":
                            if (snap_value.equals("false"))
                                Toast.makeText(ViewSignature.this, "Signature hasn't been uploaded", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewSignature.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}