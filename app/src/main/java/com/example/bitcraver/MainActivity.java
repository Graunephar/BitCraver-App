package com.example.bitcraver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.example.bitcraver.network.HttpRequestHandler;
import com.example.bitcraver.network.ResponseCallback;

public class MainActivity extends AppCompatActivity {


    private HttpRequestHandler mRequestHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button updatebutton = findViewById(R.id.refresh_button);
        final TextView testView = findViewById(R.id.test_textview);

        mRequestHandler = new HttpRequestHandler(this);


        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRequestHandler.initiateRequest(new ResponseCallback() {
                    @Override
                    public void onSuccess(String data) {
                        testView.setText(data);

                    }

                    @Override
                    public void onError(String message) {

                        testView.setText(message);
                    }
                });

            }
        });

        // Initiate webview
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);

        WebView web_view = findViewById(R.id.content_view);

        web_view.requestFocus();
        web_view.getSettings().setLightTouchEnabled(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setGeolocationEnabled(true);
        web_view.setSoundEffectsEnabled(true);

        web_view.loadData("",
                "text/html", "UTF-8");

        web_view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });


    }
}