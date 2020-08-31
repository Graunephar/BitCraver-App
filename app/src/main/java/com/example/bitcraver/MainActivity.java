package com.example.bitcraver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.bitcraver.network.HTMLBuilder;
import com.example.bitcraver.network.HttpRequestHandler;
import com.example.bitcraver.network.JSONParser;
import com.example.bitcraver.network.ResponseCallback;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    private HttpRequestHandler mRequestHandler;

    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button updatebutton = findViewById(R.id.refresh_button);
        final TextView testView = findViewById(R.id.test_textview);
        mWebView = findViewById(R.id.content_view);

        mRequestHandler = new HttpRequestHandler(this);


        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRequestHandler.initiateRequest(new ResponseCallback() {
                    @Override
                    public void onSuccess(String data) {
                        String test = JSONParser.getContentOfFirstPost(data);
                        HTMLBuilder.StringtoHTML(test);

                        testView.setText(test);

                        mWebView.loadData(test,
                                "text/html", "UTF-8");

                    }

                    @Override
                    public void onError(String message) {

                        testView.setText(message);
                    }
                });

            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {



            }
        });


        // Initiate webview
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);


        mWebView.requestFocus();
        mWebView.getSettings().setLightTouchEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.setSoundEffectsEnabled(true);

        mWebView.loadData("",
                "text/html", "UTF-8");

        mWebView.setWebChromeClient(new WebChromeClient() {
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