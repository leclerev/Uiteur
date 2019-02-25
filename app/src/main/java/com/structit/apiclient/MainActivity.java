package com.structit.apiclient;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.structit.apiclient.data.PlayItem;
import com.structit.apiclient.data.access.DataHandler;
import com.structit.apiclient.service.ApiService;
import com.structit.apiclient.service.MainActivityReceiver;
import com.structit.apiclient.service.MyBroadcastReceiver;
import com.structit.apiclient.service.SensorService;
import com.structit.apiclient.service.sensors.LocationSensorListener;
import com.structit.apiclient.service.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String  ACTION_PLAY = "ACTION_PLAY" , ACTION_STOP = "ACTION_STOP" , ACTION_PAUSE = "ACTION_PAUSE";
    public static final String  ACTION_CURRENT_STATUS = "ACTION_CURRENT_STATUS";
    private DataHandler mDataHandler;


    private String mPlayListName;
    private static int mPlayListId = -1;
    private int lastPlayedID = 10;
    private Map<Integer, TextView> playIndicators = new HashMap<>();

    private int playId;
    private static MusicService.PlayerStates currentMPState ;
    private MainActivityReceiver mainActivityReceiver;
    private Button pauseButton;
    private Button playButton;
    public Button mStopButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Creating...");

        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String url = intent.getStringExtra("url");

        TextView textviewApiId = (TextView)findViewById(R.id.api_id);
        textviewApiId.setText(id);

        TextView textviewApiUrl = (TextView)findViewById(R.id.api_url);
        textviewApiUrl.setText(url);

        mDataHandler = new DataHandler(this);



        mPlayListName = intent.getStringExtra("playlist");
        mPlayListId = intent.getIntExtra("playlistid", -1);
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Starting...");
        super.onStart();

       Intent musicIntent = new Intent(this, MusicService.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(musicIntent);
        } else {
            startService(musicIntent);
        }

        if(mPlayListId > -1) {
            mDataHandler.open();
            ArrayList<PlayItem> playList = mDataHandler.getPlayList();
            mDataHandler.close();

            LinearLayout playListLayout = (LinearLayout) findViewById(R.id.playListLinearLayout);
            playListLayout.removeAllViews();

            if (!playList.isEmpty()) {
                LayoutInflater inflater = this.getLayoutInflater();

                for(PlayItem item:playList) {
                    // ActivityItem Layout
                    RelativeLayout playItemLayout =
                            (RelativeLayout) inflater.inflate(R.layout.play_item, null);
                    playListLayout.addView(playItemLayout);

                    TextView playNameTextView =
                            (TextView) playItemLayout.findViewById(R.id.playName);
                    playNameTextView.setText(item.getName());

                    TextView playAuthorTextView =
                            (TextView) playItemLayout.findViewById(R.id.playAuthor);
                    playAuthorTextView.setText(item.getAuthor());

                    TextView playRecordTextView =
                            (TextView) playItemLayout.findViewById(R.id.playRecord);
                    playRecordTextView.setText(item.getRecord());

                    playIndicators.put(item.getId(), (TextView) playItemLayout.findViewById(R.id.isPlayed));

                    PlayOnClickListener listener = new PlayOnClickListener(this, item.getId());
                    Log.i(LOG_TAG, "onStart: ItemID: " + item.getId());
                    playItemLayout.setOnClickListener(listener);

                    playButton = (Button) findViewById(R.id.playButton);
                    pauseButton = (Button) findViewById(R.id.pauseButton);
                    pauseButton.setClickable(true);
                    playButton.setClickable(true);
                    playButton.setOnClickListener(listener);
                    pauseButton.setOnClickListener(listener);
                }

            } // Else do nothing
        } else {
            Intent intent = new Intent(this, ApiService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }

        Intent locationService = new Intent(this, SensorService.class);
        startService(locationService);

        LocalBroadcastManager.getInstance(this).registerReceiver(new MyBroadcastReceiver(this), new IntentFilter(SensorService.ACTION_LOCATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(new MyBroadcastReceiver(this), new IntentFilter(SensorService.ACTION_LOCALISATION));
        this.mainActivityReceiver = new MainActivityReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mainActivityReceiver,new IntentFilter(MainActivity.ACTION_CURRENT_STATUS));
        Log.i(LOG_TAG, "localBroadcastManager initialisés...");

    }


    private int minInMap() {
        Set<Integer> allInts = playIndicators.keySet();
        int minInt = (int) allInts.toArray()[0];
        for(int i = 0; i < allInts.toArray().length; i++) {
            if((int)allInts.toArray()[i] < minInt) {
                minInt = (int) allInts.toArray()[i];
                Log.d(LOG_TAG, "minInt = " + minInt);
            }
        }
        return minInt;
    }

    private int maxInMap() {
        Set<Integer> allInts = playIndicators.keySet();
        int maxInt = (int) allInts.toArray()[0];
        for(int i = 0; i > allInts.toArray().length; i++) {
            if((int)allInts.toArray()[i] < maxInt) {
                maxInt = (int) allInts.toArray()[i];
                Log.d(LOG_TAG, "minInt = " + maxInt);
            }
        }
        return maxInt;
    }

    public void switchMusic(int incValue) {
        int min = minInMap(), max = maxInMap();

        Log.i(LOG_TAG, "switchMusic: " + incValue);

        if(playId + incValue < min){
            playId = max;
        } else if(playId + incValue > max) {
            playId = min;
        } else {
            this.stop(playId + incValue);
            return;
        }
        this.stop(playId);
    }

  public void musicPapeteries( double latitude, double longitude){
        double latitude_papeteries = 45.907973;
        double longitude_papeteries = 6.102794 ;
        Log.i(LOG_TAG, " Localisation : LATITUDE =" + latitude + "LONGITUDE =" + longitude);
        if ((latitude_papeteries == latitude) && (longitude_papeteries == longitude)){
            Log.i(LOG_TAG, " Localisation : Papeteries " );
        }
  }


    public void play(int playId) {
        playIndicators.get(playId).setText("Play");
        Intent playIntent = new Intent(MusicService.ACTION_PLAY);
        playIntent.putExtra("playId" ,playId );
        LocalBroadcastManager.getInstance(this).sendBroadcast(playIntent);

        Log.d(" PLAY =","Id envoyé");
    }
    public void stop(int playId) {
        Intent stopIntent = new Intent(MusicService.ACTION_STOP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(stopIntent);
        Log.d("STOP = ","Id envoyé");

      /* if(this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.reset();

            playIndicators.get(lastPlayedID).setText("Pause");
        }

        if(playId != this.lastPlayedID)
            this.play(playId);*/
    }
    public void pause(int playId) {

        Intent pauseIntent = new Intent(MusicService.ACTION_PAUSE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pauseIntent);
        Log.d("PAUSE = ","Id envoyé");
        playIndicators.get(lastPlayedID).setText("Pause");
        if(playId != this.lastPlayedID)
            this.play(playId);
    }


    public void updateMPState(MusicService.PlayerStates currentState) {
        this.currentMPState = currentState;
        Log.i("STATUT du MP" , "" +this.currentMPState );

        if (this.currentMPState == MusicService.PlayerStates.PLAYING) {
            pauseButton.setClickable(true);
            playButton.setClickable(false);
        }
        if (this.currentMPState == MusicService.PlayerStates.PAUSED) {
            pauseButton.setClickable(false);
            playButton.setClickable(true);
        }
    }
}
