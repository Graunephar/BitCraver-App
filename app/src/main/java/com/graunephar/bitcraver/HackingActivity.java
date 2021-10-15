package com.graunephar.bitcraver;

import static com.graunephar.bitcraver.constants.AppConstants.HACKING_ACTIVITY_SHOWING_TIME_DELAY;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.VideoView;

import com.example.bitcraver.R;
import com.graunephar.bitcraver.constants.AppConstants;
import com.graunephar.bitcraver.constants.UserConstant;
import com.graunephar.bitcraver.relay.RelayStrategy;
import com.graunephar.bitcraver.relay.YoctoRelayStrategyImpl;

import java.util.Random;

public class HackingActivity extends AppCompatActivity {

    private MediaPlayer mMediaplayer;
    private RelayStrategy mRelayStrategy;
    private Handler mHandler;
    private Activity mActivity;

    private Runnable mRunnableRelay = new Runnable() {
        @Override
        public void run() {
            mRelayStrategy.activateRelayShort();
            makeHandlerDoRelayClickAfterDelayInMSUntilStopped(mActivity);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_hacking);

        mActivity = this;
        mRelayStrategy = new YoctoRelayStrategyImpl(50, mActivity);


        PreferenceSaver preferenceSaver = new PreferenceSaver(this);
        if(preferenceSaver.hasAppBeenHacked()) finish();

        VideoView videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hack);
        videoview.setVideoURI(uri);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int resId;
        if(AppConstants.CURRENT_USER == UserConstant.DANIEL) {
            resId = R.raw.daniel_hack;
        } else if(AppConstants.CURRENT_USER == UserConstant.TOBIAS) {
            resId = R.raw.tobias_hack;
        } else {
            resId = R.raw.sara_hack;
        }

        mMediaplayer = MediaPlayer.create(this, resId);

        videoview.start();
        mMediaplayer.start();

        mHandler = new Handler();
        makeHandlerDoRelayClickAfterDelayInMSUntilStopped(this);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.removeCallbacks(mRunnableRelay);
                saveHackedInPreferences();
                launchErrorActivity();
            }
        }, HACKING_ACTIVITY_SHOWING_TIME_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaplayer.stop();  //>>> stop myMediaPlayer
        mMediaplayer.release(); //>>> free myMediaPlayer
    }

    private void makeHandlerDoRelayClickAfterDelayInMSUntilStopped(final Activity activity) {
        mHandler.postDelayed(mRunnableRelay, getRandom());
    }

    private int getRandom() {
        int min = 1;
        int max = 500;
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }


    private void saveHackedInPreferences() {
        PreferenceSaver prefSaver = new PreferenceSaver(this);
        prefSaver.setAppHackedStatus(true);
    }

    private void launchErrorActivity() {
        Intent startIntent = new Intent(this, CrashingActivity.class);
        startIntent.putExtra(AppConstants.USER_PARAM, AppConstants.CURRENT_USER);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}