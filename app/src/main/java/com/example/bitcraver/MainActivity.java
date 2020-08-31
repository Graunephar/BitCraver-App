package com.example.bitcraver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitcraver.network.HTMLBuilder;
import com.example.bitcraver.network.HttpRequestHandler;
import com.example.bitcraver.network.JSONParser;
import com.example.bitcraver.network.ResponseCallback;
import com.izikode.izilib.veinview.VeinView;
import com.izikode.izilib.veinview.VeinViewClient;
import com.izikode.izilib.veinview.VeinViewInjector;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    private HttpRequestHandler mRequestHandler;

    private VeinView mWebView;
    private Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button updatebutton = findViewById(R.id.refresh_button);
        mWebView = findViewById(R.id.content_view);

        mRequestHandler = new HttpRequestHandler(this);


        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRequestHandler.initiateRequest(new ResponseCallback() {
                    @Override
                    public void onSuccess(String data) {
                        String content = JSONParser.getContentOfFirstPost(data);
                        HTMLBuilder.StringtoHTML(content);
                        mWebView.loadData(content,
                                "text/html", "UTF-8");
                    }

                    @Override
                    public void onError(String message) {

                        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

            }
        });

       mWebView.setVeinViewClient(new VeinViewClient() {
           @Override
           public void onReadyToInject(VeinViewInjector veinViewInjector, String s) {
               getInjector().injectCSS(R.raw.style);
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

}