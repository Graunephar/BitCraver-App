package com.graunephar.bitcraver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bitcraver.R;
import com.graunephar.bitcraver.constants.AppConstants;
import com.graunephar.bitcraver.constants.UserConstant;
import com.graunephar.bitcraver.network.HTMLBuilder;
import com.graunephar.bitcraver.network.HttpRequestHandler;
import com.graunephar.bitcraver.network.JSONParser;
import com.graunephar.bitcraver.network.ResponseCallback;
import com.izikode.izilib.veinview.VeinView;
import com.izikode.izilib.veinview.VeinViewClient;
import com.izikode.izilib.veinview.VeinViewInjector;

public class MainActivity extends AppCompatActivity {


    private static final long REFRESH_DELAY = 5000;
    private static final String TAG = MainActivity.class.getSimpleName();
    private HttpRequestHandler mRequestHandler;

    private VeinView mWebView;
    private ImageView mLogo;
    private Context mContext = this;
    private String mLastLoadedData;

    private UsbAccessory accessory;
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private PreferenceSaver mPreferenceSaver;

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            Toast.makeText(mContext, "USB RECEIVED started", Toast.LENGTH_LONG).show();
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    Toast.makeText(mContext, "USB stuffz started", Toast.LENGTH_LONG).show();
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (accessory != null) {
                            //call method to set up accessory communication
                        }
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferenceSaver = new PreferenceSaver(this);

        mWebView = findViewById(R.id.content_view);
        final SwipeRefreshLayout swipe_refresh_layout = findViewById(R.id.swipe_to_refresh);

        mLogo = findViewById(R.id.bc_logo);
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
                getInjector().injectCSS(R.raw.bootstrap);
                getInjector().injectCSS(R.raw.style);
            }
        });

        initWebView();

        loadContent();
        tryReloadContent();
        registerUSBIntentsAndFields();

        checkIfHacked();
    }

    private void checkIfHacked() {
        if(mPreferenceSaver.hasAppBeenHacked()) {
            startCrashActivity();
            finish(); //im out
        }
    }


    private void testStartHackActivity() {
        Intent startIntent = new Intent(this, HackingActivity.class);
        startIntent.putExtra(AppConstants.USER_PARAM, AppConstants.CURRENT_USER);
        startActivity(startIntent);
    }

    private void startLoginActivity() {
        Intent startIntent = new Intent(this, LoginActivity.class);
        startIntent.putExtra(AppConstants.USER_PARAM, AppConstants.CURRENT_USER);
        startActivity(startIntent);
    }

    @Override
    public void onResume() {
        super.onResume();

        checkIfHacked();

        Intent intent = getIntent();
        Log.d(TAG, "intent: " + intent);
        String action = intent.getAction();



        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action) && !mPreferenceSaver.hasAppBeenHacked()) {
            Toast.makeText(this, "Tvinger app til at åbne", Toast.LENGTH_LONG).show();
            //do something
            startLoginActivity();
        } else if(mPreferenceSaver.hasAppBeenHacked()) {
            startCrashActivity();
        }
    }

        private void initWebView () {
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
        }

        private void registerUSBIntentsAndFields () {
            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);

        }

        private void tryReloadContent () {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfNewDataOnServerAndReload();
                    tryReloadContent();
                }
            }, REFRESH_DELAY);
        }

        private void checkIfNewDataOnServerAndReload () {
            mRequestHandler.initiateRequest(new ResponseCallback() {
                @Override
                public void onSuccess(String data) {
                    if (data == null) return;
                    if (!data.equals(mLastLoadedData))
                        loadDataToWebView(data); //if new data on server reload
                }
                @Override
                public void onError(String message) {
                    Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        private void loadContent () {

            mRequestHandler.initiateRequest(new ResponseCallback() {
                @Override
                public void onSuccess(String data) {
                    if (data == null) {
                        showNoDataSign();
                    } else {
                        loadDataToWebView(data);
                    }

                }

                @Override
                public void onError(String message) {
                    Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        private void showNoDataSign () {
            mLogo.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        }

        private void showDatViews () {
            mLogo.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }

        private void loadDataToWebView (String data){
            this.mLastLoadedData = data;
            String content = JSONParser.getContentOfFirstPost(data);
            if (content == null) {
                showNoDataSign();
                return; //fail fast
            }
            String html = HTMLBuilder.StringtoHTML(content);
            mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            showDatViews();
        }

        @Override
        public boolean onKeyDown ( int keyCode, KeyEvent event){
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                event.startTracking();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                event.startTracking();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyLongPress ( int keyCode, KeyEvent event){
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                startCrashActivity();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                //What goes here?             //What goes here?
                return true;
            }
            return super.onKeyLongPress(keyCode, event);
        }

    private void startCrashActivity() {
        Intent intent = new Intent(this, CrashingActivity.class);
        startActivity(intent);
    }

}