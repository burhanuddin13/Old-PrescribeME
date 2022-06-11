package com.example.twousers_02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class ViewHTML extends AppCompatActivity {

    String HTML="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_html);

        Intent view_html=getIntent();
        HTML=view_html.getExtras().getString("HTML");

        EditText txtHTML=(EditText) findViewById(R.id.txtHTML);
        txtHTML.setText(HTML);
        WebView webView=new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadDataWithBaseURL("", HTML, "text/html", "UTF-8", "");
        setContentView(webView);
    }
}