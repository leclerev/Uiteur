package com.structit.apiclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.structit.apiclient.MainActivity;

public class MainActivityReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;
    private MusicService.PlayerStates currentState;

    public MainActivityReceiver(MainActivity mainActivity) {this.mainActivity = mainActivity;}
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == MainActivity.ACTION_CURRENT_STATUS) {
            this.currentState = ( MusicService.PlayerStates )intent.getExtras().get("STATUS");
            this.mainActivity.updateMPState(this.currentState );
        }
    }
}
