package com.structit.apiclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusicBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = ApiService.class.getSimpleName();
    private MusicService myMusicService ;
    int receivedId;

    public MusicBroadcastReceiver(MusicService musicService)
    {
        this.myMusicService = musicService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Intent received : " + intent.getAction());

        switch (intent.getAction()) {
            case (MusicService.ACTION_PLAY):
                Log.d("LOG_TAG","Id REçu");
                receivedId = intent.getExtras().getInt("playId");
                this.myMusicService.play(receivedId);
                Log.d("LOG_TAG","Id  =" + receivedId);
                break;

            case (MusicService.ACTION_STOP):
                this.myMusicService.stop();
                break;

            case (MusicService.ACTION_PAUSE):
                this.myMusicService.pause();
                break;
        }
    }
}
