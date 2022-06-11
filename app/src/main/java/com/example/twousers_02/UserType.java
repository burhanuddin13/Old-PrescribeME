package com.example.twousers_02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserType extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;

    Button newacc, oldacc, doc, pat;
    TextView txt1, txt2;
    LinearLayout lin1, lin2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        if(user!=null){
            Intent main_int=new Intent(UserType.this,MainActivity.class);
        }

        txt1=(TextView)findViewById(R.id.txt_choose_type);
        txt2=(TextView)findViewById(R.id.txt_back);

        lin1=(LinearLayout)findViewById(R.id.choose_account);
        lin2=(LinearLayout)findViewById(R.id.choose_type);

        newacc=(Button)findViewById(R.id.btn_new_account);
        newacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt1.setText("Choose User Type");
                lin1.setVisibility(View.GONE);
                lin2.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);
            }
        });

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt2.setVisibility(View.INVISIBLE);
                txt1.setText("Choose Account Type");
                lin1.setVisibility(View.VISIBLE);
                lin2.setVisibility(View.GONE);
            }
        });

        oldacc=(Button)findViewById(R.id.btn_existing_account);
        oldacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oldaccint=new Intent(UserType.this, LoginActivity.class);
                startActivity(oldaccint);
            }
        });
    }
}