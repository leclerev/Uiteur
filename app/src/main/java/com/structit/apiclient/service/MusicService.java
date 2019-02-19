package com.structit.apiclient.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.structit.apiclient.R;
import com.structit.apiclient.data.access.DataHandler;

import java.io.File;
import java.io.IOException;

public class MusicService extends Service  implements MediaPlayer.OnCompletionListener{
    private MediaPlayer myMediaPlayer ;
    private DataHandler mDataHandler;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onCreate(){
        //create services
        super.onCreate();

        //create music player
        myMediaPlayer = new MediaPlayer();
        mDataHandler = new DataHandler(this);

        //A modifier
        //myMediaPlayer = MediaPlayer.create(this, R.raw.music2);
        myMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId){
        //  MusicListener myMusicListener = new MusicListener(this);
        /*if (!myMediaPlayer.isPlaying()) {
            try {

                myMediaPlayer.prepare();
                myMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        this.mDataHandler.open();
        String filename = this.mDataHandler.getPlayFile( intent.getIntExtra("playId", 0));
        this.mDataHandler.close();

        if(filename.length() > 0) {
            try {
                File file = new File(getFilesDir(), filename);
                this.myMediaPlayer.setDataSource(getApplicationContext(),
                        Uri.fromFile(file));
                this.myMediaPlayer.prepare();
                this.myMediaPlayer.start();
            } catch (Exception ex) {
                //Log.e(LOG_TAG, "Unable to play sound");
            }
        } else {
            //Log.d(LOG_TAG, "No file found for play: " + playId);
        }

        return START_STICKY;
    }

    public void onDestroy() {
        if (myMediaPlayer.isPlaying()) {
            myMediaPlayer.stop();
        }
        myMediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }
}



