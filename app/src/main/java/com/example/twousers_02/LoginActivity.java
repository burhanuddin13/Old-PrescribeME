package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 786;
    private static final int REQUEST_CODE_SPEECH_INPUT = 110;

    TextView txtsignup, forgotPassword;
    EditText emailidip, passwordip;
    Button btnLogin, btnGoogle, btnFacebook, btnReset;
    String emailid, password;
    ImageButton email_mic;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realdb;
    DatabaseReference realref;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent oldaccint=getIntent();

        emailidip=(EditText)findViewById(R.id.EmailIDLogin);
        passwordip=(EditText)findViewById(R.id.PasswordLogin);

        mAuth=FirebaseAuth.getInstance();
        mLoadingBar=new ProgressDialog(this);

        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        txtsignup = findViewById(R.id.txtsignup);
        txtsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgotPassword=(TextView)findViewById(R.id.FgtPasswordtxt);
        btnReset=(Button)findViewById(R.id.btnResetPWD);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReset.setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.login_header)).setText("Reset Password");
                btnLogin.setVisibility(View.GONE);
                btnFacebook.setVisibility(View.GONE);
                btnGoogle.setVisibility(View.GONE);
                passwordip.setVisibility(View.GONE);
                forgotPassword.setVisibility(View.GONE);
                ((TextView)findViewById(R.id.txtnoac)).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.txtsignup)).setVisibility(View.GONE);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this, "This Feature is Temporarily Suspended",Toast.LENGTH_SHORT).show();
                emailid=emailidip.getText().toString();
                if (emailid.isEmpty() || !emailid.contains("@")){
                    emailidip.setError("Email ID Invalid!");
                    emailidip.requestFocus();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(emailid)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        Toast.makeText(LoginActivity.this, "Password Reset Link Sent Successful! Please Sign In Again", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(LoginActivity.this, "There was an Error Password Reset Message! Please Sign In Again", Toast.LENGTH_SHORT).show();
                                }
                            });
                    startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                }
            }
        });

        btnFacebook=(Button)findViewById(R.id.FBsignup);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "This Feature is Temporarily Suspended",Toast.LENGTH_SHORT).show();
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
                            .makeText(LoginActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
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
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "Name: "+user.getDisplayName()+"\nEmail: "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        realdb= FirebaseDatabase.getInstance();
        realref=realdb.getReference().child(user.getUid().toString());
        realref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=1;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    //Toast.makeText(LoginActivity.this, dataSnapshot.getKey()+dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                    if ((i++)==1)
                    {
                        if (dataSnapshot.getValue().toString().equals("false"))
                        {
                            Toast.makeText(LoginActivity.this, "Please Update Your Data First", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, UpdateProfile.class));
                        }
                        else
                        {
                            Intent  mainint=new Intent(LoginActivity.this, MainActivity.class);
                            mainint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainint);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

            mAuth.signInWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                        user=mAuth.getCurrentUser();
                        if(user.isEmailVerified())
                        {
                            /*Intent  mainint=new Intent(LoginActivity.this, MainActivity.class);
                            mainint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainint);*/
                            realdb= FirebaseDatabase.getInstance();
                            realref=realdb.getReference().child(user.getUid().toString());
                            realref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int i=1;
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                    {
                                        //Toast.makeText(LoginActivity.this, dataSnapshot.getKey()+dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                        if ((i++)==1)
                                        {
                                            if (dataSnapshot.getValue().toString().equals("false"))
                                            {
                                                Toast.makeText(LoginActivity.this, "Please Update Your Data First", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, UpdateProfile.class));
                                            }
                                            else
                                            {
                                                Intent  mainint=new Intent(LoginActivity.this, MainActivity.class);
                                                mainint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainint);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else
                        {
                            Intent  emverint=new Intent(LoginActivity.this, EmailVerification.class);
                            emverint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(emverint);
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Login Unsuccessful",Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                    }
                }
            });

        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                tv_Speech_to_text.setText(
                        Objects.requireNonNull(result).get(0));
            }
        }
    }*/

}