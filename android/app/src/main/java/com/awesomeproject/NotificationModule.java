package com.awesomeproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class NotificationModule extends ReactContextBaseJavaModule {

    private static final String TAG = "Native";
    private static ReactApplicationContext reactContext;
    //song drive link
    String SONG = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
    Boolean musicPlaying = false;
    Intent serviceIntent;
    Intent pomoTimerServiceIntent;
    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationModule(ReactApplicationContext reactApplicationContext){
        super(reactApplicationContext);
        Log.d("Notification module", "Notification COnstructor");
        reactContext = reactApplicationContext;
        serviceIntent = new Intent(reactContext, MyPlayService.class);
        pomoTimerServiceIntent = new Intent(reactContext, PomoTimerService.class);
//        reactContext.startForegroundService(pomoTimerServiceIntent);
    }


    public static final String CHANNEL_ID = "channel1";
    public static final String textContent = "Hello dude, Savvy's calling open the app.";
    public static final String textTitle = "Savvy's Pomo";
    public static Notification notification;
    Boolean pomoTimerRunning = false;


    //click trigger method to play and pause music for background service
    @ReactMethod
    public void bangNotification(){
        Log.d("bang", "Bannnngg");
//        if(!musicPlaying){
//            playAudio();
//            musicPlaying = true;
//        }else{
//            stopAudio();
//            musicPlaying = false;
//        }

        if(!pomoTimerRunning){
            startPomoTimer();
            pomoTimerRunning = true;
        }else{
            stopPomoTimer();
            pomoTimerRunning = false;
        }
    }

    private void stopAudio() {

        try{
            reactContext.stopService(serviceIntent);
        }catch (SecurityException e){
            Toast.makeText(reactContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void playAudio() {

        serviceIntent.putExtra("AudioLink", SONG);

        try{
            reactContext.startService(serviceIntent);
        }catch (SecurityException e){
            Toast.makeText(reactContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    //Pomo Timer Functions
    private void startPomoTimer(){
     pomoTimerServiceIntent.putExtra("TimerDuration", 5);
     try{
         reactContext.startService(pomoTimerServiceIntent);
     }catch (Exception e){
         Toast.makeText(reactContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
     }
    }
    private void stopPomoTimer(){
        try{
            reactContext.stopService(pomoTimerServiceIntent);
        }catch (SecurityException e){
            Toast.makeText(reactContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        String channelID = "ch1";

        Intent intent = new Intent();
        intent.setAction("notificationToast");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(reactContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        reactContext.sendBroadcast(intent);
        NotificationBroadcast notificationBroadcast = new NotificationBroadcast();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notif";
            String description = "Savvy's Invitation";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = reactContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(reactContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .addAction(0, "", pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(0, builder.build());
        }

    }


    public void pomoTimerEvent(){
        Log.d("pomo", "PomoTimer value pushed");
        WritableMap map = Arguments.createMap();
        map.putInt("event", 1);
        try{
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("textColor", map);
        } catch (Exception e){
            Log.d("ReactNative", "Caught Exceptioin:" + e.getMessage());
        }
    }




    @NonNull
    @Override
    public String getName() {
        return "NotificationModule";
    }
}
