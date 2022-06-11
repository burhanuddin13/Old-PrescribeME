package com.example.twousers_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realdb;
    DatabaseReference realref;

    TextView username, drname;
    Button btnviewprof, btnprescribe, btnSignOut, btnviewsign;
    ImageView ProfComplete, SignComplete, menu;
    CardView mainMenuCV;
    LinearLayout DarkLL, LightLL, AboutLL, PrivacyLL, WebsiteLL, BackLL, Paper1LL, Paper2LL, FeedbackLL, YouTubeLL, SignOutLL;

    Drawable Complete, Pending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        Intent backint = getIntent();

        Complete=getResources().getDrawable(R.drawable.ic_complete);
        Pending=getResources().getDrawable(R.drawable.ic_pending);

        checkInternet();
        checkComplete();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        drname = (TextView) findViewById(R.id.drnametxt);
        drname.setText("Dr. " + user.getDisplayName());

        ProfComplete=(ImageView) findViewById(R.id.ProfComplete);
        SignComplete=(ImageView) findViewById(R.id.SignComplete);

        btnviewprof = (Button) findViewById(R.id.btnViewProf);
        btnviewprof.setOnClickListener(view -> {
            Intent viewint = new Intent(MainActivity.this, ViewProfile.class);
            startActivity(viewint);
        });

        btnviewsign = (Button) findViewById(R.id.btnViewSign);
        btnviewsign.setOnClickListener(view -> {
            Intent sign_int = new Intent(MainActivity.this, UpdateSignature.class);
            startActivity(sign_int);
        });

        btnprescribe = (Button) findViewById(R.id.btnprescribe);
        btnprescribe.setOnClickListener(view -> {
            Intent presint = new Intent(MainActivity.this, PatientInfo.class);
            startActivity(presint);
        });

        btnSignOut = (Button) findViewById(R.id.btnSignOut2);
        btnSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
            Intent loginint = new Intent(MainActivity.this, LoginActivity.class);
            loginint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginint);
        });

        mainMenuCV = (CardView) findViewById(R.id.MainMenuCV);
        menu = (ImageView) findViewById(R.id.imgMenu);
        menu.setOnClickListener(v -> mainMenuCV.setVisibility(View.VISIBLE));

        DarkLL=(LinearLayout) findViewById(R.id.DarkLL);
        LightLL=(LinearLayout) findViewById(R.id.LightLL);
        AboutLL=(LinearLayout) findViewById(R.id.AboutLL);
        PrivacyLL=(LinearLayout) findViewById(R.id.PrivacyLL);
        WebsiteLL=(LinearLayout) findViewById(R.id.WebsiteLL);
        YouTubeLL=(LinearLayout) findViewById(R.id.YouTubeLL);
        BackLL=(LinearLayout) findViewById(R.id.BackMenuLL);
        FeedbackLL=(LinearLayout) findViewById(R.id.FeedBackLL);
        Paper1LL=(LinearLayout) findViewById(R.id.Paper1LL);
        Paper2LL=(LinearLayout) findViewById(R.id.Paper2LL);
        SignOutLL=(LinearLayout) findViewById(R.id.SignOutLL);
        if(isDarkModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            DarkLL.setVisibility(View.GONE);
            LightLL.setVisibility(View.VISIBLE);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            DarkLL.setVisibility(View.VISIBLE);
            LightLL.setVisibility(View.GONE);
        }
        DarkLL.setOnClickListener(v11 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean("isDarkModeOn", true);
            editor.apply();
        });
        LightLL.setOnClickListener(v12 -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("isDarkModeOn", false);
            editor.apply();
        });
        AboutLL.setOnClickListener(v13 -> {
            Intent extInt = new Intent(MainActivity.this, ExternalLink.class);
            extInt.putExtra("URL", getString(R.string.ABOUT));
            startActivity(extInt);
        });
        PrivacyLL.setOnClickListener(v14 -> {
            Intent extInt = new Intent(MainActivity.this, ExternalLink.class);
            extInt.putExtra("URL", getString(R.string.PRIVACY));
            startActivity(extInt);
        });
        WebsiteLL.setOnClickListener(v15 -> Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show());
        YouTubeLL.setOnClickListener(v16 -> {
            /*Intent extInt = new Intent(MainActivity.this, ExternalLink.class);
            extInt.putExtra("URL", getString(R.string.YOUTUBE));
            startActivity(extInt);*/
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.YOUTUBE))));
        });
        Paper1LL.setOnClickListener(v17 -> {
            Intent extInt = new Intent(MainActivity.this, ExternalLink.class);
            extInt.putExtra("URL", getString(R.string.PAPER1));
            startActivity(extInt);
        });
        Paper2LL.setOnClickListener(v18 -> Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show());
        FeedbackLL.setOnClickListener(v19 -> {
            Intent extInt = new Intent(MainActivity.this, ExternalLink.class);
            extInt.putExtra("URL", getString(R.string.FEEDBACK));
            startActivity(extInt);
        });
        SignOutLL.setOnClickListener(v20 -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
            Intent loginint = new Intent(MainActivity.this, LoginActivity.class);
            loginint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginint);
        });
        BackLL.setOnClickListener(v21 -> mainMenuCV.setVisibility(View.GONE));

    }

    private void checkInternet() {
        if (!haveNetworkConnection())
        {
//            Toast.makeText(MainActivity.this, "Not Connected to Internet", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Internet Detected!")
                    .setMessage("Please Turn On Internet to use this App")
                    .setCancelable(false)
                    .setIcon(R.drawable.no_internet)
                    .setPositiveButton("WiFi Settings", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                    .setNegativeButton("Mobile Data Settings", (dialog, which) -> startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)))
                    .setNeutralButton("Exit App", (dialog, id) -> MainActivity.this.finish());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
            Toast.makeText(MainActivity.this, "Connected to Internet", Toast.LENGTH_SHORT).show();
    }

    private void checkComplete() {
        DatabaseReference realref= FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        realref.addValueEventListener(new ValueEventListener() {
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
                        case "Completed":
                            if (snap_value.equals("false")){
                                ProfComplete.setImageDrawable(Pending);
                                Toast.makeText(MainActivity.this, "Please Update Profile Info Before Continuing", Toast.LENGTH_LONG).show();
                                Intent updint = new Intent(MainActivity.this, UpdateProfile.class);
                                startActivity(updint);
                            }
                            else
                                ProfComplete.setImageDrawable(Complete);
                            break;
                        case "Profile Info":
                        case "Sign Extension":
                            break;
                        case "Sign Uploaded":
                            if (snap_value.equals("false")){
                                SignComplete.setImageDrawable(Pending);
                                Toast.makeText(MainActivity.this, "Please Upload Signature Before Continuing", Toast.LENGTH_LONG).show();
                                Intent sign_int = new Intent(MainActivity.this, UpdateSignature.class);
                                startActivity(sign_int);
                            }
                            else
                                SignComplete.setImageDrawable(Complete);
                            break;
                        default:
                            Toast.makeText(MainActivity.this, (j++)+": "+snap_value,Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}