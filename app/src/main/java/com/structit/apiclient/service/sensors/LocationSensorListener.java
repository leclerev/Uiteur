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
import android.util.Log;

public class LocationSensorListener extends Service implements LocationListener, SensorEventListener {
    private static final String LOG_TAG = LocationSensorListener.class.getSimpleName();

    public static final String ACTION_LOCATION = "com.example.digital.androidlocation.LOCATION";

    /*private LocationService myLocationService;

    public LocationSensorListener() {
        myLocationService = null;
    }

    public LocationSensorListener(LocationService locationService) {
        myLocationService = locationService;
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "onStart: " + e.getMessage());
        }

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "onLocationChanged: Actual location: " + location);
        //this.myLocationService.notifyLocation(location);
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
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x, y, z;
            float gravity[] = new float[]{0, 0, 0};
            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            x = event.values[0] - gravity[0];
            y = event.values[1] - gravity[1];
            z = event.values[2] - gravity[2];

            /*Log.i(LOG_TAG, "onSensorChanged: GyroX: " + x);
            Log.i(LOG_TAG, "onSensorChanged: GyroY: " + y);
            Log.i(LOG_TAG, "onSensorChanged: GyroZ: " + z);
            Log.i(LOG_TAG, "onSensorChanged: ---------------------------------------------------");*/
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}