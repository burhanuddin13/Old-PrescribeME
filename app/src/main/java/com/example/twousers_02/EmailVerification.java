package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;

    TextView useremail, userpassword;
    Button btnSignOut, btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        Intent emverint=getIntent();

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        useremail=(TextView)findViewById(R.id.eidfgt);
        userpassword=(TextView)findViewById(R.id.Passworduser);
        useremail.setText(user.getEmail());

        btnSignOut=(Button)findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(EmailVerification.this, "Logout Successful",Toast.LENGTH_SHORT).show();
                Intent loginint=new Intent(EmailVerification.this, LoginActivity.class);
                loginint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginint);
            }
        });
        btnVerify=(Button)findViewById(R.id.btnReset);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification()
                        .addOnCompleteListener(EmailVerification.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(EmailVerification.this, "Email Verification Sent Successful!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(EmailVerification.this, "There was an Error Sending Verification Message! Please Sign In Again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Toast.makeText(EmailVerification.this, "Logout Successful!  Please Sign In Again",Toast.LENGTH_SHORT).show();
                Intent loginint=new Intent(EmailVerification.this, LoginActivity.class);
                loginint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginint);

            }
        });
    }
}