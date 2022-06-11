package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

//import id.zelory.compressor.Compressor;

public class UpdateSignature extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 786;
    protected Uri imageUri, uri1$2, uri2$2;
    protected String UserID, signName;
    protected Uri signUri;
    protected Bitmap bitmap1$2, bitmap2$2;

    TextView[] error = new TextView[4];
    Button select, upload;
    ImageView imgSelect, imgCurrent;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference signRef;
    FirebaseDatabase realDB;
    DatabaseReference realRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_signature);

        Intent sign_int=getIntent();

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        UserID=user.getUid();
        signName=UserID.trim()+".jpg";
        storage=FirebaseStorage.getInstance();
        signRef=storage.getReference().child("Signatures").child(signName);
        realDB=FirebaseDatabase.getInstance();
        realRef=realDB.getReference().child(UserID);

        error[0]=(TextView) findViewById(R.id.error1);
        error[1]=(TextView) findViewById(R.id.error2);
        error[2]=(TextView) findViewById(R.id.error3);
        error[3]=(TextView) findViewById(R.id.error4);

        imgCurrent=(ImageView) findViewById(R.id.imgCurrentSign);
        error[0].setText(signName);
        error[3].setText("Image Load Started");
        File file=new File("content://Pictures/"+signName);
        error[2].setText(file.getAbsolutePath());
        if (file.exists())
        {
            error[1].setText("File Fetched");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgCurrent.setImageBitmap(bitmap);
        }
        else
        {
            error[1].setText("Couldn't fetch File");
            signRef.getBytes(6*1024*1024).addOnSuccessListener(bytes -> {
                error[3].setText("Image Loaded Successfully");
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgCurrent.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> error[3].setText("Image Loading Error: "+e));
        }

        imgSelect=(ImageView) findViewById(R.id.imgSelectSign);
        select=(Button) findViewById(R.id.btnSignSelect);
        select.setOnClickListener(v -> openImage());

        upload=(Button) findViewById(R.id.btnSignUpload);
        upload.setOnClickListener(v -> compressImage());
    }

    private void openImage() {
        Intent imageIntent=new Intent();
        imageIntent.setType("image/jpeg");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageIntent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    imageUri=data.getData();
                    error[1].setText(imageUri.toString());
                    imgSelect.setImageURI(imageUri);
                    try {
                        bitmap1$2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (Exception e) {
                        error[2].setText("Bitmap Error: "+e);
                    }
                }
                break;
        }
    }

    private void uploadImage(Uri uri) {
        if (uri!=null)
        {
            error[2].setText("Image Accessed");
            signRef.putFile(uri).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    error[2].setText("Image Uploaded Successfully");
                    realRef.child("Sign Uploaded").setValue(true);
                    //realref.child("Sign URL").setValue(imageUri);
                    Toast.makeText(UpdateSignature.this, "Signature Upload Successful", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UpdateSignature.this, UpdateSignature.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(UpdateSignature.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else Toast.makeText(UpdateSignature.this, "Uri is Empty", Toast.LENGTH_SHORT).show();
    }

    private void compressImage() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap1$2.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String path1$2 = MediaStore.Images.Media.insertImage(UpdateSignature.this.getContentResolver(),bitmap1$2,signName,null);
        uri1$2 = Uri.parse(path1$2);
        if (uri1$2 != null)
            error[2].setText("Compression 1/2 Completed");
        try {
            bitmap2$2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri1$2);
        } catch (Exception e) {
            error[2].setText("Bitmap Error: "+e);
        }
        bitmap1$2.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String path2$2 = MediaStore.Images.Media.insertImage(UpdateSignature.this.getContentResolver(),bitmap2$2,signName,null);
        uri2$2 = Uri.parse(path2$2);
        if (uri2$2 != null)
            error[2].setText("Compression 2/2 Completed");
        //error[2].setText(path2$2);
        uploadImage(uri2$2);
    }

    /*private void compressImage() {
        Bitmap bitmap = null;
        try {
            bitmap = new Compressor(this)
                    .setMaxHeight(200) //Set height and width
                    .setMaxWidth(200)
                    .setQuality(100) // Set Quality
                    .compressToBitmap(bitmap1$2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] bytes = baos.toByteArray();
    }*/
}