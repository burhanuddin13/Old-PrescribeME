package com.example.twousers_02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PatientInfo extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1301;
    private final String URL="https://prescribe-me.herokuapp.com/predict";

    EditText pat1name, patlname, patage, patgender, dummy;
    TextView txtPrescriptionNo;
    ImageButton mic_pat1name, mic_patlname, mic_patage, mic_patgender;
    Button btnProceed;

    String Name1, Name2, Age, Gender, PrescriptionNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        pat1name=(EditText)findViewById(R.id.Pat1Name);
        mic_pat1name=(ImageButton)findViewById(R.id.mic_pat1name);
        mic_pat1name.setOnClickListener(v -> micCalling(pat1name));

        patlname=(EditText)findViewById(R.id.PatLName);
        mic_patlname=(ImageButton)findViewById(R.id.mic_patlname);
        mic_patlname.setOnClickListener(v -> micCalling(patlname));

        patage=(EditText)findViewById(R.id.PatAge);
        mic_patage=(ImageButton)findViewById(R.id.mic_patage);
        mic_patage.setOnClickListener(v -> micCalling(patage));

        patgender=(EditText)findViewById(R.id.PatGender);
        mic_patgender=(ImageButton)findViewById(R.id.mic_patgender);
        mic_patgender.setOnClickListener(v -> micCalling(patgender));

        PrescriptionNo=PrescriptionHTML.generatePrescriptionNo();
        txtPrescriptionNo=(TextView) findViewById(R.id.txtPrescriptionNo);
        txtPrescriptionNo.setText("Prescription #"+PrescriptionNo);

        btnProceed=(Button) findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCredentials()){
                    checkAPI();
                    Intent pres_int=new Intent(PatientInfo.this, Prescribe.class);
                    pres_int.putExtra("Name", Name1+" "+Name2);
                    pres_int.putExtra("Age", Age);
                    pres_int.putExtra("Gender", Gender);
                    pres_int.putExtra("Prescription No", PrescriptionNo);
                    startActivity(pres_int);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    dummy.setText(
                            Objects.requireNonNull(result).get(0));
                }
        }
    }

    public void micCalling(EditText editText)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
        try {
            dummy=editText;
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e) {
            Toast.makeText(PatientInfo.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkCredentials() {
        Name1=pat1name.getText().toString();
        Name2=patlname.getText().toString();
        Age=patage.getText().toString();
        Gender=patgender.getText().toString();
        if (Name1.isEmpty()){
            pat1name.setError("Please Enter First Name");
            pat1name.requestFocus();
            return false;
        }
        else if (Name2.isEmpty()){
            patlname.setError("Please Enter Last Name");
            patlname.requestFocus();
            return false;
        }
        else if (Age.isEmpty() || !Age.matches("[0-9]+")) {
            patage.setError("Enter Valid Age");
            patage.requestFocus();
            return false;
        }
        else if (Gender.isEmpty()){
            patgender.setError("Please Enter Gender");
            patgender.requestFocus();
            return false;
        }
        return true;
    }

    private void checkAPI() {
        for(int j=0; j<2; j++)
        {
            // hit the API -> Volley
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("non-prescriptive")) {
                                Toast.makeText(PatientInfo.this, "API Connection Successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(PatientInfo.this, "Error #1 in connecting API", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(PatientInfo.this, "Error #2 in connecting API", Toast.LENGTH_SHORT).show();
                        }
                    }){
                @Override
                protected Map getParams(){
                    Map params = new HashMap();
                    params.put("prescription","hello how are you");
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(PatientInfo.this);
            queue.add(stringRequest);
        }
    }
}