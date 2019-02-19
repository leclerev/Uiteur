package com.structit.apiclient;

import android.media.MediaPlayer;
import android.view.View;

import com.structit.apiclient.data.access.DataHandler;

public class PlayOnClickListener implements View.OnClickListener {
    private MainActivity mActivity;
    private int mPlayId;

    public PlayOnClickListener(MainActivity activity, int playId) {
        this.mActivity = activity;
        this.mPlayId = playId;
    }

    @Override
    public void onClick(View view) {
        //this.mActivity.play(this.mPlayId);
    }
}
