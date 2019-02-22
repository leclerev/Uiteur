package com.structit.apiclient.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.structit.apiclient.service.sensors.LocationSensorListener;

import java.util.List;


public class SensorService extends Service {

    private static final String LOG_TAG = SensorService.class.getSimpleName();
    //
    public static final String ACTION_LOCATION = "com.example.digital.androidlocation.LOCATION";
    public static final String  ACTION_LOCALISATION = "ACTION_LOCALISATION";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Localisation
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationSensorListener sensorListener  = new LocationSensorListener(this) ;
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, sensorListener);
            Log.e(LOG_TAG, "GPS Provider found");
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "onStart exception: " + e.getMessage());
        }

        // capteur type Gyroscope
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allAccSensors = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        if(allAccSensors.size() > 0) {
            Log.d(LOG_TAG, "onStart: Accelerometer found! (" + allAccSensors.size() + ")");
        } else {
            Log.d(LOG_TAG, "onStart: Accelerometer not found!");
        }

        return START_STICKY;
    }

    public void notifyLocation(Location location){
        Intent intent = new Intent(ACTION_LOCALISATION);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude" , location.getLongitude());
        intent.putExtra("altitude", location.getAltitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void notifySensor (SensorEvent event){
        if (event.values[2] != 0) {
            Intent intent = new Intent(ACTION_LOCATION);
            intent.putExtra("gyroZ", event.values[2] > 0 ? 10 : -10);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
