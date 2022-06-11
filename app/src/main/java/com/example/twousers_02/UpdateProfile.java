package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

public class UpdateProfile extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT_1 = 13391;
    private static final int REQUEST_CODE_SPEECH_INPUT_2 = 13392;
    private static final int REQUEST_CODE_SPEECH_INPUT_3 = 13393;
    private static final int REQUEST_CODE_SPEECH_INPUT_4 = 13394;
    private static final int REQUEST_CODE_SPEECH_INPUT_5 = 13395;
    private static final int REQUEST_CODE_SPEECH_INPUT_6 = 13396;
    private static final int IMAGE_REQUEST_CODE = 420;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realdb;
    DatabaseReference realref;
    FirebaseStorage storage;//Make Changes to realdb.getReference().child(UserID)
    ProgressDialog mLoadingBar;
    StorageReference signref;

    EditText firstname, lastname, aadhar, qualif, registration, clinic;
    ImageButton mic_1, mic_2, mic_3, mic_4, mic_5, mic_6;
    Button update;
    String Pending="", Name, Name1, Name2, Aadhar, Qualif, RegNo, Clinic, UserID;

    protected String URL;
    protected Uri imageUri;

    //ArrayList<String> values=new ArrayList<>(6);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Intent updint=getIntent();

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        UserID=user.getUid();
        realdb=FirebaseDatabase.getInstance();
        realref=realdb.getReference().child(UserID);
        storage=FirebaseStorage.getInstance();
        signref=storage.getReference().child("Signatures");

        firstname=(EditText)findViewById(R.id.userfirstname);
        lastname=(EditText)findViewById(R.id.userlastname);
        aadhar=(EditText)findViewById(R.id.useraadharno);
        qualif=(EditText)findViewById(R.id.userqualification);
        registration=(EditText)findViewById(R.id.useregistrationno);
        clinic=(EditText)findViewById(R.id.userclinic);

        mic_1=(ImageButton)findViewById(R.id.mic_1name);
        mic_2=(ImageButton)findViewById(R.id.mic_lname);
        mic_3=(ImageButton)findViewById(R.id.mic_aadhar);
        mic_4=(ImageButton)findViewById(R.id.mic_qualif);
        mic_5=(ImageButton)findViewById(R.id.mic_regno);
        mic_6=(ImageButton)findViewById(R.id.mic_clinic);

        //displayUserProfile();
        getUserInfo();

        update=(Button)findViewById(R.id.btnupdateprof);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

        /*signURL=(TextView)findViewById(R.id.signlink);
        signURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!signURL.getText().toString().isEmpty())
                    startActivity(new Intent(UpdateProfile.this, ViewSignature.class));
            }
        });

        signUpload=(ImageButton)findViewById(R.id.signupload);
        signUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });*/

        {
            mic_1=(ImageButton)findViewById(R.id.mic_1name);
            mic_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent
                            = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                    try {
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_1);
                    }
                    catch (Exception e) {
                        Toast
                                .makeText(UpdateProfile.this, " " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
            mic_2=(ImageButton)findViewById(R.id.mic_lname);
            mic_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent
                            = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                    try {
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_2);
                    }
                    catch (Exception e) {
                        Toast
                                .makeText(UpdateProfile.this, " " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
            mic_3=(ImageButton)findViewById(R.id.mic_aadhar);
            mic_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent
                            = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                    try {
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_3);
                    }
                    catch (Exception e) {
                        Toast
                                .makeText(UpdateProfile.this, " " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
            mic_4=(ImageButton)findViewById(R.id.mic_qualif);
            mic_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent
                            = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                    try {
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_4);
                    }
                    catch (Exception e) {
                        Toast
                                .makeText(UpdateProfile.this, " " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
            mic_5=(ImageButton)findViewById(R.id.mic_regno);
            mic_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent
                            = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                    try {
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_5);
                    }
                    catch (Exception e) {
                        Toast
                                .makeText(UpdateProfile.this, " " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
            mic_6=(ImageButton)findViewById(R.id.mic_clinic);
            mic_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent
                            = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                    try {
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT_6);
                    }
                    catch (Exception e) {
                        Toast
                                .makeText(UpdateProfile.this, " " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    }

    private void openImage() {
        Intent imageIntent=new Intent();
        imageIntent.setType("image/");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageIntent, IMAGE_REQUEST_CODE);
    }

    private void updateUserProfile() {
        if (checkCredentials()){
                    /*Updating.setTitle("Updating Profile");
                    Updating.setMessage("Please Wait while we update your profile!");
                    Updating.setCanceledOnTouchOutside(false);
                    Updating.show();*/

            Name=Name1+" "+Name2;
            UserProfileChangeRequest profileUpdates;
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(Name)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Updating.dismiss();
                                Toast.makeText(UpdateProfile.this, "(1/2) User Profile Update Successful!",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(UpdateProfile.this, "(1/2) User Profile Update Unsuccessful!",Toast.LENGTH_SHORT).show();
                                //Updating.dismiss();
                            }
                        }
                    });

            HashMap<String, Object> Profile=new HashMap<>();
            Profile.put("First Name", Name1);
            Profile.put("Last Name", Name2);
            Profile.put("Aadhar No", Aadhar);
            Profile.put("Registration No", RegNo);
            Profile.put("Qualifications", Qualif);
            Profile.put("Clinic", Clinic);
            //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
            realref.child("Profile Info").updateChildren(Profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                realref.child("Completed").setValue(true)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(UpdateProfile.this, "Database Updation Successful!", Toast.LENGTH_LONG).show();
                                                        Toast.makeText(UpdateProfile.this, "Loading View Profile Activity", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(UpdateProfile.this, ViewProfile.class));
                                                    //uploadImage();
                                                }
                                                else Toast.makeText(UpdateProfile.this, "Database Updation Unsuccessful!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            else Toast.makeText(UpdateProfile.this, "Database Updation Unsuccessful!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void uploadImage() {
        /*ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading Signature/Stamp");
        progressDialog.show();*/
        //Toast.makeText(UpdateProfile.this, "Inside Function", Toast.LENGTH_SHORT).show();
        if (imageUri!=null)
        {
            StorageReference storageReference= signref.child(UserID+"."+getFileExtension(imageUri));
            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        realref.child("Sign Uploaded").setValue(true);
                        realref.child("Sign URL").setValue(imageUri);
                        Toast.makeText(UpdateProfile.this, "Signature Upload Successful", Toast.LENGTH_SHORT).show();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(UpdateProfile.this, "Signature URL accessed", Toast.LENGTH_SHORT).show();
                                String url=uri.toString();
                                Toast.makeText(UpdateProfile.this, url, Toast.LENGTH_SHORT).show();
                                Log.d("Download URL: ",url);
                                //progressDialog.dismiss();
                                Toast.makeText(UpdateProfile.this, "Loading View Profile Activity", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(UpdateProfile.this, ViewProfile.class));
                            }
                        });
                    }
                    else{
                        Toast.makeText(UpdateProfile.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else Toast.makeText(UpdateProfile.this, "Uri is Empty", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        //Toast.makeText(UpdateProfile.this, "Retrieving File Extension", Toast.LENGTH_SHORT).show();
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        String type=mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        realref.child("Sign Extension").setValue(type);
        return type;
    }

    private boolean checkCredentials() {
        Name1=firstname.getText().toString();
        Name2=lastname.getText().toString();
        Aadhar=aadhar.getText().toString();
        Qualif=qualif.getText().toString();
        RegNo=registration.getText().toString();
        Clinic=clinic.getText().toString();
//        String imguri=signURL.getText().toString();
        if (Name1.isEmpty()){
            firstname.setError("Please Enter First Name");
            firstname.requestFocus();
            return false;
        }
        else if (Name2.isEmpty()){
            lastname.setError("Please Enter Last Name");
            lastname.requestFocus();
            return false;
        }
        else if (Aadhar.isEmpty() || Aadhar.length()<11 || Aadhar.length()>15){
            aadhar.setError("Enter Valid Aadhar No");
            aadhar.requestFocus();
            return false;
        }
        if (Qualif.isEmpty()){
            qualif.setError("Please Enter Qualification");
            qualif.requestFocus();
            return false;
        }
        else if (RegNo.isEmpty()){
            registration.setError("Please Enter Registration No.");
            registration.requestFocus();
            return false;
        }
        else if (Clinic.isEmpty()){
            clinic.setError("Enter Atleast 1 Clinic/Hospital");
            clinic.requestFocus();
            return false;
        }
        /*else if (imguri.isEmpty()){
            signURL.setError("Please Upload Signature");
            signURL.requestFocus();
            return false;
        }*/
        return true;
    }

    private void getUserInfo() {
        DatabaseReference realref= FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        realref.child("Profile Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(UpdateProfile.this, "Please Wait while we load your data", Toast.LENGTH_LONG).show();
                int i=0, j=1;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String snap_value=snapshot1.getValue().toString();
                    //values.add(snap_value);

                    //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
                    switch((i++)){
                        case 0:
                            if(snap_value!="false")
                            aadhar.setText(snap_value);
                            j++;
                            break;
                        case 1:
                            if(snap_value!="false")
                            clinic.setText(snap_value);
                            j++;
                            break;
                        case 2:
                            if(snap_value!="false")
                            firstname.setText(snap_value);
                            j++;
                            break;
                        case 3:
                            if(snap_value!="false")
                            lastname.setText(snap_value);
                            j++;
                            break;
                        case 4:
                            if(snap_value!="false")
                            qualif.setText(snap_value);
                            j++;
                            break;
                        case 5:
                            if(snap_value!="false")
                            registration.setText(snap_value);
                            j++;
                            break;
                        default:
                            Toast.makeText(UpdateProfile.this, (j++)+": "+snap_value,Toast.LENGTH_SHORT).show();
                    }
                }
                DatabaseReference realref=FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                realref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Toast.makeText(UpdateProfile.this, "Please Wait while we load your data", Toast.LENGTH_LONG).show();
                        int i = 0, j = 1;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String snap_value = snapshot1.getValue().toString();
                            //values.add(snap_value);

                            //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
                            switch ((i++)) {
                                case 0:
                                case 1:
                                    break;
                                case 2:
                                    StorageReference storageReference = signref.child(UserID + "." + snap_value);
                                    String downloadUrl = storageReference.getDownloadUrl().toString();
                                    //signURL.setText(downloadUrl);
                                    break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(UpdateProfile.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(UpdateProfile.this, "You may now proceed to enter your details", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfile.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT_1:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    firstname.setText(
                            Objects.requireNonNull(result).get(0));
                }
                break;
            case REQUEST_CODE_SPEECH_INPUT_2:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    lastname.setText(
                            Objects.requireNonNull(result).get(0));
                }
                break;
            case REQUEST_CODE_SPEECH_INPUT_3:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    aadhar.setText(
                            Objects.requireNonNull(result).get(0));
                }
                break;
            case REQUEST_CODE_SPEECH_INPUT_4:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    qualif.setText(
                            Objects.requireNonNull(result).get(0));
                }
                break;
            case REQUEST_CODE_SPEECH_INPUT_5:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    registration.setText(
                            Objects.requireNonNull(result).get(0));
                }
                break;
            case REQUEST_CODE_SPEECH_INPUT_6:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    clinic.setText(
                            Objects.requireNonNull(result).get(0));
                }
                break;
            case IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    imageUri=data.getData();
                    //signURL.setText(imageUri.toString());
                    uploadImage();
                }
                break;
        }
    }
}