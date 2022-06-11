package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.HttpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.logging.type.HttpRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    
    private static final int RC_SIGN_IN = 786;
    private static final int REQUEST_CODE_SPEECH_INPUT = 110;

    EditText emailidip, passwordip;
    Button btnRegister, btnGoogle, btnFacebook;
    String emailid, password, UserID;
    ImageButton email_mic;
    TextView txtsignin;

    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase realdb;
    DatabaseReference realref; //Make Changes to realdb.getReference().child(UserID)

    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailidip=(EditText)findViewById(R.id.EmailIDRegister);
        passwordip=(EditText)findViewById(R.id.PasswordRegister);
        passwordip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAuth= FirebaseAuth.getInstance();
        mLoadingBar=new ProgressDialog(this);

        txtsignin = findViewById(R.id.txtsignin);
        txtsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister=(Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> checkCredentials());

        btnFacebook=(Button)findViewById(R.id.FBsignup);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "This Feature is Temporarily Suspended",Toast.LENGTH_SHORT).show();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle=(Button)findViewById(R.id.Gsignup);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        email_mic=(ImageButton)findViewById(R.id.mic_email);
        email_mic.setOnClickListener(new View.OnClickListener() {
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
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e) {
                    Toast
                            .makeText(RegisterActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void checkCredentials() {
        emailid=emailidip.getText().toString();
        password=passwordip.getText().toString();
        if (emailid.isEmpty() || !emailid.contains("@")){
            emailidip.setError("Email ID Invalid!");
            emailidip.requestFocus();
        }
        else if (password.isEmpty() || password.length()<8){
            passwordip.setError("Password should be greater than 8 characters");
            passwordip.requestFocus();
        }
        else{
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please Wait while we log you in");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                        user=mAuth.getCurrentUser();

                        UserID=user.getUid();
                        realdb=FirebaseDatabase.getInstance();
                        realref=realdb.getReference().child(UserID);
                        HashMap<String, Object> Profile=new HashMap<>();
                        Profile.put("First Name", false);
                        Profile.put("Last Name", false);
                        Profile.put("Aadhar No", false);
                        Profile.put("Registration No", false);
                        Profile.put("Qualifications", false);
                        Profile.put("Clinic", false);
                        //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
                        realref.child("Profile Info").updateChildren(Profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "User Created Successfully", Toast.LENGTH_LONG).show();
                                    realref.child("Completed").setValue(false);
                                    realref.child("Sign Extension").setValue(false);
                                    realref.child("Sign Uploaded").setValue(false);
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "User Creation Unsuccessful", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        if(user.isEmailVerified())
                        {
                            Intent mainint=new Intent(RegisterActivity.this, UpdateProfile.class);
                            mainint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainint);
                        }
                        else
                        {
                            Intent  emverint=new Intent(RegisterActivity.this, EmailVerification.class);
                            emverint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(emverint);
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Registration Unsuccessful",Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                        startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
                    }
                }
            });

        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    //Log.w(TAG, "Google sign in failed", e);
                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    emailidip.setText(
                            Objects.requireNonNull(result).get(0));
                }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Name: "+user.getDisplayName()+"\nEmail: "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        UserID=user.getUid();
        realdb=FirebaseDatabase.getInstance();
        realref=realdb.getReference().child(UserID);
        HashMap<String, Object> Profile=new HashMap<>();
        Profile.put("First Name", false);
        Profile.put("Last Name", false);
        Profile.put("Aadhar No", false);
        Profile.put("Registration No", false);
        Profile.put("Qualifications", false);
        Profile.put("Clinic", false);
        //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
        realref.child("Profile Info").updateChildren(Profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User Created Successfully", Toast.LENGTH_LONG).show();
                    realref.child("Completed").setValue(false);
                    realref.child("Sign Extension").setValue(false);
                    realref.child("Sign Uploaded").setValue(false);
                }
                else{
                    Toast.makeText(RegisterActivity.this, "User Creation Unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        });
        Intent  mainint=new Intent(RegisterActivity.this, UpdateProfile.class);
        mainint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainint);
    }
}