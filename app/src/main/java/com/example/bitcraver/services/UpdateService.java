package com.example.bitcraver.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;

public class UpdateService extends Service {


    private final IBinder mBinder;


    public UpdateService() {
        mBinder = new LocalBinder();
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        UpdateService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UpdateService.this;

        }
    }
}
