package com.example.twousers_02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Build;
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
import java.util.Vector;

public class Prescribe extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;
    private final String URL="https://prescribe-me.herokuapp.com/predict";

    EditText diagpres, presmed, infopres, dummy;
    ImageButton mic_diagpres, mic_presmed, mic_infopres;
    TextView presmore, slot_name, slot_value;
    Button btnhtml;

    String Name, Age, Gender, prescription, Diagnosis, Information, PresHTML="", PrescriptionNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescribe);

        Intent pres_int=getIntent();
        Name=pres_int.getExtras().getString("Name");
        Age=pres_int.getExtras().getString("Age");
        Gender=pres_int.getExtras().getString("Gender");
        PrescriptionNo=pres_int.getExtras().getString("Prescription No");

        PresHTML=PrescriptionHTML.getPresHead();

        btnhtml=(Button)findViewById(R.id.btn_gen_presc_html);
        btnhtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Diagnosis=diagpres.getText().toString();
                Information=infopres.getText().toString();
                prescription=presmed.getText().toString();
                Intent conf_int=new Intent(Prescribe.this,ConfirmProfile.class);
                if(!prescription.isEmpty()){
                    Toast.makeText(Prescribe.this, "Note: \""+prescription+"\" will not be considered", Toast.LENGTH_SHORT).show();
                    // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(Prescribe.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Note: \""+prescription+"\" is left to Prescribe")
                            .setTitle("Pending Prescription")
                            .setCancelable(false)
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(Prescribe.this, "Text Ignored", Toast.LENGTH_SHORT).show();// User clicked OK button
                                    conf_int.putExtra("PresHTML", PresHTML+"</table>");
                                    conf_int.putExtra("Name", Name);
                                    conf_int.putExtra("Age", Age);
                                    conf_int.putExtra("Gender",Gender);
                                    conf_int.putExtra("Diagnosis", Diagnosis);
                                    conf_int.putExtra("Information", Information);
                                    conf_int.putExtra("Prescription No", PrescriptionNo);
                                    startActivity(conf_int);
                                }
                            })
                            .setNegativeButton("Prescribe", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PresHTML=callingAPI();// User cancelled the dialog
                                }
                            });

                    // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    conf_int.putExtra("PresHTML", PresHTML+"</table>");
                    conf_int.putExtra("Name", Name);
                    conf_int.putExtra("Age", Age);
                    conf_int.putExtra("Gender",Gender);
                    conf_int.putExtra("Diagnosis", Diagnosis);
                    conf_int.putExtra("Information", Information);
                    conf_int.putExtra("Prescription No", PrescriptionNo);
                    startActivity(conf_int);
                }
            }
        });

        slot_name=(TextView)findViewById(R.id.slot_name);
        slot_value=(TextView)findViewById(R.id.slot_value);

        diagpres=(EditText)findViewById(R.id.DiagPres);
        mic_diagpres=(ImageButton)findViewById(R.id.mic_diagpres);
        mic_diagpres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                micCalling(diagpres);
            }
        });

        presmed=(EditText)findViewById(R.id.PresMed);
        mic_presmed=(ImageButton)findViewById(R.id.mic_presmed);
        mic_presmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                micCalling(presmed);
            }
        });

        infopres=(EditText)findViewById(R.id.InfoPres);
        mic_infopres=(ImageButton)findViewById(R.id.mic_infopres);
        mic_infopres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                micCalling(infopres);
            }
        });

        presmore=(TextView)findViewById(R.id.PresMoretxt);
        presmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prescription=presmed.getText().toString();
                presmed.setText("");
                slot_name.setText("Slot Name: ");
                slot_value.setText("Slot Value: ");
                if(prescription.isEmpty())
                {
                    presmed.setError("Please Enter Prescription");
                    presmed.requestFocus();
                }
                else{
                    PresHTML=callingAPI();
                }
            }
        });
    }

    private boolean PendingPrescription() {


        return true;
    }

    private String callingAPI() {
        Toast.makeText(Prescribe.this, "Calling API", Toast.LENGTH_SHORT).show();
        // hit the API -> Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("non-prescriptive")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray slots_JSONArray = jsonObject.getJSONArray("response");
                                JSONObject json_array[] = new JSONObject[slots_JSONArray.length()];
                                for (int i = 0, l = slots_JSONArray.length(); i < l; i++)
                                    json_array[i] = new JSONObject(slots_JSONArray.getString(i));
                                String slots_name="Intent: ", slots_value="Prescriptive";
                                for (int j = 0; j < json_array.length; j++){
                                    slots_name=slots_name+"\n"+json_array[j].getString("slot")+": ";
                                    slots_value=slots_value+"\n"+json_array[j].getString("value");
                                }
                                slot_name.setText(slots_name.trim());
                                slot_value.setText(slots_value.trim());
                                PresHTML=generate_prescription(json_array);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Prescribe.this, "JSON Error: "+e, Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            slot_name.setText("Intent: ");
                            slot_value.setText("Non Prescriptive");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Prescribe.this, "API Error: "+error, Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map getParams(){
                Map params = new HashMap();
                params.put("prescription",prescription);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(Prescribe.this);
        queue.add(stringRequest);
        return PresHTML;
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
            Toast.makeText(Prescribe.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    dummy.setText(Objects.requireNonNull(result).get(0));
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generate_prescription(JSONObject[] jsonObjects) throws JSONException {
        String Drug = "", INN = "", Freq = "", CMA = "", Dur = "", Gap = "", Qty = "", ROA = "", Cond = "", Fast = "", cma = "", fasting = "", roa = "", Rhy="", Max="";
        Vector<String> inn=new Vector<>(1), rhy=new Vector<>(1), freq=new Vector<>(1), dur_val=new Vector<>(1), dos_val=new Vector<>(1), cond=new Vector<>(1);
        String[] drug = {"","","",""}, dur = {"",""}, qty = {"",""}, gap = {"",""}, max = {"",""};
        String slot, value;
        for(JSONObject object: jsonObjects) {
            slot = object.getString("slot");
            value = object.getString("value");

            switch (slot) {
                case "INN":
                    inn.add(value.trim());
                    break;
                case "Drug":
                    drug[1] = value.trim();
                    break;
                case "d-dos-form":
                    drug[0] = value.trim();
                    break;
                case "d-dos-val":
                    drug[2] = value.trim();
                    break;
                case "d-dos-UP":
                    drug[3] = value.trim();
                    break;
                case "rhythm-TDTE":
                case "rhythm-rec-ut":
                    rhy.add(value.trim());
                    break;
                case "rhythm-hour":
                    rhy.add("at " + value.trim());
                    break;
                case "rhythm-perday":
                    rhy.add(value.trim() + " daily");
                    break;
                case "rhythm-rec-val":
                    rhy.add("every " + value.trim());
                    break;
                case "freq-days":
                case "freq-count":
                case "freq-count-ut":
                    freq.add(value.trim());
                    break;
                case "dur-val":
                    dur_val.add(value.trim());
                    break;
                case "dur-UT":
                    dur[1] = value.trim();
                    break;
                case "cma-event":
                    CMA = value.trim();
                    break;
                case "dos-val":
                    dos_val.add(value.trim());
                    break;
                case "dos-UF":
                    qty[1] = value.trim();
                    break;
                case "dos-cond":
                    cond.add(value.trim());
                    break;
                case "fasting":
                    Fast = value.trim();
                    break;
                case "ROA":
                    ROA = value.trim();
                    break;
                case "min-gap-val":
                    gap[0] = value.trim();
                    break;
                case "min-gap-ut":
                    gap[1] = value.trim();
                    break;
                case "max-unit-val":
                    max[0] = value.trim();
                    break;
                case "max-unit-uf":
                    max[1] = value.trim();
                    break;
            }
        }
        INN = (String.join(" + ", inn)).trim();
        if (drug[1].equals(""))
            drug[1] = inn.get(0);

        Drug = (String.join(" ",drug)).trim();
        Rhy = (String.join(", ",rhy)).trim();
        Freq = (String.join(", ",freq)).trim();
        dur[0] = (String.join(", ",dur_val)).trim();
        Gap = (String.join(" ",gap)).trim();
        Dur = (String.join(" ",dur)).trim();
        qty[0] = (String.join(", ",dos_val)).trim();
        Qty = (String.join(" ",qty)).trim();
        Cond = (String.join(", ",cond)).trim();
        Max = (String.join(" ",max)).trim();

        if (Rhy.equals("") && !Freq.equals(""))
            Freq = Freq;
        else if (Freq.equals("") && !Rhy.equals(""))
            Freq = Rhy;
        else if (!Freq.equals("") && !Rhy.equals("")){
            Freq = Rhy + " & " + Freq;
            Freq = Freq.trim();}
        if (Max.equals("") && !Qty.equals(""))
            Qty = Qty;
        else if (Qty.equals("") && !Max.equals(""))
            Qty = "Max: " + Max;
        else if (!Qty.equals("") && !Max.equals("")){
            Qty = Qty + " & Max: " + Max;
            Qty = Qty.trim();}
        if (!Cond.equals(""))
            Cond = "if " + Cond;
        if (!Gap.equals(""))
            Gap = "Gap: " + Gap;
        if (Freq.equals(""))
            Freq = "-";
        if (CMA.equals(""))
            CMA = "-";
        if (Dur.equals(""))
            Dur = "-";
        if (Gap.equals(""))
            Gap = "-";
        if (Qty.equals(""))
            Qty = "-";
        if (ROA.equals(""))
            ROA = "-";
        if (Cond.equals(""))
            Cond = "-";
        if (Fast.equals(""))
            Fast = "-";
        String[] prescribe = {Drug, INN, Freq, CMA, Dur, Gap, Qty, ROA, Cond, Fast};
        PresHTML=PrescriptionHTML.getPresHTML(PresHTML, prescribe);
        return PresHTML;
    }
}