package com.awesomeproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;

public class NotificationBroadcast extends BroadcastReceiver {
    private static final String TAG = "Native";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "showToast function Received");
        Toast.makeText(context, "Savvy's Intent Detected.", Toast.LENGTH_LONG).show();
    }
}

