package com.example.bitcraver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.bitcraver.network.HTMLBuilder;
import com.example.bitcraver.network.HttpRequestHandler;
import com.example.bitcraver.network.JSONParser;
import com.example.bitcraver.network.ResponseCallback;
import com.izikode.izilib.veinview.VeinView;
import com.izikode.izilib.veinview.VeinViewClient;
import com.izikode.izilib.veinview.VeinViewInjector;

public class MainActivity extends AppCompatActivity{


    private HttpRequestHandler mRequestHandler;

    private VeinView mWebView;
    private Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.content_view);
        final SwipeRefreshLayout swipe_refresh_layout = findViewById(R.id.swipe_to_refresh);

        mRequestHandler = new HttpRequestHandler(this);

        getSupportActionBar().setTitle(R.string.action_bar_text);  // provide compatibility to all the versions


        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadContent();
                swipe_refresh_layout.setRefreshing(false);
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
        mWebView.setSoundEffectsEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");


        mWebView.loadData("",
                "text/html", "UTF-8");


        loadContent();

    }





    private void loadContent() {

        mRequestHandler.initiateRequest(new ResponseCallback() {
            @Override
            public void onSuccess(String data) {
                String content = JSONParser.getContentOfFirstPost(data);
                String html = HTMLBuilder.StringtoHTML(content);
                mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

            }

            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Toast.makeText(this, "Press Down", Toast.LENGTH_SHORT).show();
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress( int keyCode, KeyEvent event ) {
        if( keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
            //Handle what you want in long press.
            Toast.makeText(this, "Long press Down", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyLongPress( keyCode, event );
    }

}