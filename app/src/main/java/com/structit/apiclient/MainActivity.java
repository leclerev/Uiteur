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

    private DataHandler mDataHandler;
    public MediaPlayer mMediaPlayer;


    private String mPlayListName;
    private static int mPlayListId = -1;
    private int lastPlayedID = 10;

    private Map<Integer, TextView> playIndicators = new HashMap<>();

    //private SensorManager sensorManager;

    //public Button mPlayButton = null;
    public Button mStopButton = null;
    public Intent musicIntent;
    private int playId;

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
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayListName = intent.getStringExtra("playlist");
        mPlayListId = intent.getIntExtra("playlistid", -1);
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Starting...");


        Intent locationService = new Intent(this, SensorService.class);
        startService(locationService);

        LocalBroadcastManager.getInstance(this).registerReceiver(new MyBroadcastReceiver(this), new IntentFilter(SensorService.ACTION_LOCATION));

        LocalBroadcastManager.getInstance(this).registerReceiver(new MyBroadcastReceiver(this), new IntentFilter(SensorService.ACTION_LOCALISATION));
        Log.i(LOG_TAG, "localBroadcastManager initialisés...");
        //mis dans SensorService
        /*sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> allAccSensors = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

        if(allAccSensors.size() > 0) {
            Log.d(LOG_TAG, "onStart: Accelerometer found! (" + allAccSensors.size() + ")");
        } else {
            Log.d(LOG_TAG, "onStart: Accelerometer not found!");
        }*/

      //  sensorManager.registerListener(new LocationSensorListener(), allAccSensors.get(0), 1000000);

        super.onStart();

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

                    //musicIntent = new Intent(this, MusicService.class);
                }

                LinearLayout buttons = (LinearLayout) findViewById(R.id.id_control_buttons);
               // mPlayButton= (Button) buttons.findViewById(R.id.id_play_button);
                mStopButton = (Button) buttons.findViewById(R.id.id_stop_button);

               /* mPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playId = lastPlayedID;
                        musicIntent.putExtra("playId", playId);
                        startService(musicIntent);
                        //finish();
                    }
                });*/

                mStopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // stopService(musicIntent);
                    }
                });
            } // Else do nothing
        } else {
            Intent intent = new Intent(this, ApiService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

       // Intent intent = new Intent(this, ApiService.class);
       // stopService(intent);
    }

    public void play(int playId) {
        this.mDataHandler.open();
        String filename = this.mDataHandler.getPlayFile(playId);
        this.mDataHandler.close();

        if(filename.length() > 0) {
            try {
                File file = new File(getFilesDir(), filename);

                this.mMediaPlayer.setDataSource(getApplicationContext(),
                        Uri.fromFile(file));
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.start();
                playIndicators.get(playId).setText("Play");
                this.playId = playId;
                this.lastPlayedID = playId;
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Unable to play sound");
            }
        } else {
            Log.d(LOG_TAG, "No file found for play: " + playId);
        }
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

    public void stop(int playId) {
        if(this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.reset();

            playIndicators.get(lastPlayedID).setText("Pause");
        }

        if(playId != this.lastPlayedID)
            this.play(playId);
    }
}
