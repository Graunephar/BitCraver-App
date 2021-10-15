package com.graunephar.bitcraver;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.graunephar.bitcraver.constants.AppConstants;

public class PreferenceSaver {

    private final Activity mActivity;

    public PreferenceSaver(Activity activity) {
        this.mActivity = activity;

    }

    public void setAppHackedStatus(boolean enable) {


        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(AppConstants.PREFERENCE_KEY_HAS_BEEN_HACKED, MODE_PRIVATE);

        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // Storing the key and its value as the data fetched from edittext
        myEdit.putBoolean(AppConstants.PREFERENCE_KEY_HAS_BEEN_HACKED_STATUS_KEY, enable);

        // Once the changes have been made,
        // we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.commit();

    }

    public boolean hasAppBeenHacked(){

        // Retrieving the value using its keys the file name
        // must be same in both saving and retrieving the data
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(AppConstants.PREFERENCE_KEY_HAS_BEEN_HACKED, MODE_PRIVATE);

        // The value will be default as empty string because for
        // the very first time when the app is opened, there is nothing to show
        boolean res = sharedPreferences.getBoolean(AppConstants.PREFERENCE_KEY_HAS_BEEN_HACKED_STATUS_KEY, false);
        return res;
    }




}
