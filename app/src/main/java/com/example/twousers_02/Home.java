package com.example.twousers_02;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends Application {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realdb;
    DatabaseReference realref;

    @Override
    public void onCreate(){
        super.onCreate();

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        if (user!=null)
        {
            /*realdb= FirebaseDatabase.getInstance();
            realref=realdb.getReference().child(user.getUid());
            realref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i=1;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String snap_value=dataSnapshot.getValue().toString();
                        String snap_name= dataSnapshot.getKey();
                        //Toast.makeText(LoginActivity.this, dataSnapshot.getKey()+dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                        switch (snap_name){
                            case "Completed":
                                if (snap_value.equals("false"))
                                {
                                    Intent intent;
                                    Toast.makeText(Home.this, "Please Update Your Data First", Toast.LENGTH_SHORT).show();
                                    intent=new Intent(Home.this, UpdateProfile.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                                break;
                            case "Sign Uploaded":
                                if (snap_value.equals("false"))
                                {
                                    Intent intent;
                                    Toast.makeText(Home.this, "Please Update Your Sign First", Toast.LENGTH_SHORT).show();
                                    intent=new Intent(Home.this, UpdateProfile.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                                else
                                {

                                }
                                break;
                            case "Profile Info":
                            case "Sign Extension":
                                break;
                            default:
                                Toast.makeText(Home.this, (i++)+": "+snap_value,Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/
            Intent intent;
            intent=new Intent(Home.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //intent = new Intent(Home.this, MainActivity.class);
        }
        else
        {
            Intent intent;
            intent = new Intent(Home.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

}
