package com.structit.apiclient;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.structit.apiclient.data.access.DataHandler;
import com.structit.apiclient.service.MusicService;

public class PlayOnClickListener implements View.OnClickListener {
    private MainActivity mActivity;
    private int mPlayId;

    public PlayOnClickListener(MainActivity activity, int playId) {
        this.mActivity = activity;
        this.mPlayId = playId;
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
            case R.id.pauseButton: this.mActivity.pause(this.mPlayId);
                                    break;
            case R.id.playButton:  this.mActivity.play(this.mPlayId);
                                   break;
        }


    }
}
