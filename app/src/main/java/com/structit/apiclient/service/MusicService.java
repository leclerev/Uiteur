package com.structit.apiclient.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.structit.apiclient.MainActivity;
import com.structit.apiclient.R;
import com.structit.apiclient.data.access.DataHandler;

import java.io.File;
import java.io.IOException;

public class MusicService extends Service {
    private static final String LOG_TAG = MusicService.class.getSimpleName();

    public static final String  ACTION_PLAY = "ACTION_PLAY";
    public static final String  ACTION_STOP = "ACTION_STOP";
    public static final String  ACTION_PAUSE = "ACTION_PAUSE";

    public enum PlayerStates{PAUSED,STOPPED,PLAYING};
    public PlayerStates currentState  = PlayerStates.STOPPED;

    private MediaPlayer myMediaPlayer ;
    private DataHandler mDataHandler;
    MusicBroadcastReceiver messageReceiver;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //create music player
        myMediaPlayer = new MediaPlayer();
        myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mDataHandler = new DataHandler(this);
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId){


        messageReceiver = new MusicBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,new IntentFilter(ACTION_PLAY)) ;
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,new IntentFilter(ACTION_STOP)) ;
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,new IntentFilter(ACTION_PAUSE));
        return START_STICKY;
    }



    public void play(int playId){

        Log.d(LOG_TAG,"play()");
        Log.d(LOG_TAG,"prepare()");
        this.mDataHandler.open();
        String filename = this.mDataHandler.getPlayFile(playId);
        this.mDataHandler.close();


        if(filename.length() > 0) {
            try {
                File file = new File(getFilesDir(), filename);
                this.myMediaPlayer.setDataSource(getApplicationContext(), Uri.fromFile(file));
                this.myMediaPlayer.prepare();
                this.myMediaPlayer.start();
                this.currentState=PlayerStates.PLAYING;
                notifyMainActivity();
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Unable to play sound");
            }
        } else {
            Log.d(LOG_TAG, "No file found for play: " + playId);
        }
    }

    public void pause() {
        if (myMediaPlayer.isPlaying()) {
            this.myMediaPlayer.pause();
            this.currentState=PlayerStates.PAUSED;
        }
        else
        {
            this.myMediaPlayer.start();
            this.currentState=PlayerStates.PLAYING;
        }
        notifyMainActivity();
    }

    public void stop() {
       if (myMediaPlayer.isPlaying()) {
            Log.d(LOG_TAG, "stop()");
            this.myMediaPlayer.reset();
            this.currentState=PlayerStates.STOPPED;
       }
    }
    private void notifyMainActivity(){
        Intent intent = new Intent(MainActivity.ACTION_CURRENT_STATUS);
        intent.putExtra("STATUS",this.currentState);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(LOG_TAG,"send broadcast to Main Activity");

    }

    public void onDestroy() {
        if (myMediaPlayer != null) {
            myMediaPlayer.release();
            myMediaPlayer = null ;
        }


    }



}



