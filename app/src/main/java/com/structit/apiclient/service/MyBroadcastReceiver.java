package com.structit.apiclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.structit.apiclient.MainActivity;
import com.structit.apiclient.service.sensors.LocationSensorListener;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = MyBroadcastReceiver.class.getSimpleName();

    private MainActivity mActivity;

    public MyBroadcastReceiver(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == LocationSensorListener.ACTION_LOCATION) {
            Log.i(LOG_TAG, "Intent received : " + intent.getAction());

            this.mActivity.switchMusic(intent.getIntExtra("gyroZ", 0));
        } else {
            Log.w(LOG_TAG, "Unknown intent received : " + intent.getAction());
        }
    }
}