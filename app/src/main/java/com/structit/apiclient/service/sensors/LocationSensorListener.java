package com.structit.apiclient.service.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.structit.apiclient.MainActivity;
import com.structit.apiclient.service.MyBroadcastReceiver;
import com.structit.apiclient.service.SensorService;

public class LocationSensorListener implements LocationListener, SensorEventListener {
    private SensorService myService;
    private static final String LOG_TAG = LocationSensorListener.class.getSimpleName();

    //public static final String ACTION_LOCATION = "com.example.digital.androidlocation.LOCATION";

    public LocationSensorListener( SensorService service) {
        this.myService = service;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "onLocationChanged: Actual location: " + location);
        Log.d("location", "provider "+location.getProvider());
        Log.d("location", "latitude "+location.getLatitude());
        Log.d("location", "longitude"+location.getLongitude());
        this.myService .notifyLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Log.d(LOG_TAG, "onStatusChanged: Provider: " + provider + "/Status: " + status + "/Extras: " + extras.toString());
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG, "onProviderEnabled: Provider " + provider + " Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(LOG_TAG, "onProviderDisabled: Provider " + provider + " Disabled");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            this.myService.notifySensor(event);
            if (event.values[2] != 0) {
                Log.i(LOG_TAG, "onSensorChanged: GyroX: " + event.values[0]);
                Log.i(LOG_TAG, "onSensorChanged: GyroY: " + event.values[1]);
                Log.i(LOG_TAG, "onSensorChanged: GyroZ: " + event.values[2]);
                Log.i(LOG_TAG, "onSensorChanged: ---------------------------------------------------");

                /*Intent mainActivityIntent = new Intent(ACTION_LOCATION);
                mainActivityIntent.putExtra("gyroZ", event.values[2] > 0 ? 10 : -10);
                LocalBroadcastManager.getInstance(this).sendBroadcast(mainActivityIntent);*/
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}