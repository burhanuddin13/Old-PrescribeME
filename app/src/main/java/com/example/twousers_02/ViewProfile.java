package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewProfile extends AppCompatActivity {

    TextView firstname, lastname, qualif, regno, aadhar, clinic;
    Button btnupdate, home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Intent viewint=getIntent();

        firstname=(TextView)findViewById(R.id.viewfirstname);
        lastname=(TextView)findViewById(R.id.viewlastname);
        qualif=(TextView)findViewById(R.id.viewqulaif);
        regno=(TextView)findViewById(R.id.viewregno);
        aadhar=(TextView)findViewById(R.id.viewaadhar);
        clinic=(TextView)findViewById(R.id.viewclinic);

        getUserInfo();

        btnupdate=(Button)findViewById(R.id.btnupdate);
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updint=new Intent(ViewProfile.this, UpdateProfile.class);
                startActivity(updint);
            }
        });

        home=(Button)findViewById(R.id.btnHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backint=new Intent(ViewProfile.this,MainActivity.class);
                startActivity(backint);
            }
        });
    }

    private void getUserInfo() {
        DatabaseReference realref= FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        realref.child("Profile Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0, j=1;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String snap_value=snapshot1.getValue().toString();
                    //values.add(snap_value);

                    //Alphabetical Order: Aadhar No, Clinic, First Name, Last Name, Qualifications, Registration No
                    switch((i++)){
                        case 0:
                            aadhar.setText(snap_value);
                            j++;
                            break;
                        case 1:
                            clinic.setText(snap_value);
                            j++;
                            break;
                        case 2:
                            firstname.setText(snap_value);
                            j++;
                            break;
                        case 3:
                            lastname.setText(snap_value);
                            j++;
                            break;
                        case 4:
                            qualif.setText(snap_value);
                            j++;
                            break;
                        case 5:
                            regno.setText(snap_value);
                            j++;
                            break;
                        default:
                            Toast.makeText(ViewProfile.this, (j++)+": "+snap_value,Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProfile.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}