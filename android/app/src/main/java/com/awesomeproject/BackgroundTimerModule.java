package com.awesomeproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.os.Handler;
import android.util.Log;

import java.util.logging.LogRecord;

public class BackgroundTimerModule extends ReactContextBaseJavaModule {

    private Handler handler;
    private ReactContext reactContext;
    private Runnable runnable;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    //Notification feature
    boolean notificationOn = false;

    int TIMER_DURATION;
    String CHANNEL_ID = "1";
    String name = "Savvy";
    int importance = 1;
    NotificationChannel channel;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    PendingIntent pendingIntent;
    private final LifecycleEventListener listener = new LifecycleEventListener(){
        @Override
        public void onHostResume() {}

        @Override
        public void onHostPause() {}

        @Override
        public void onHostDestroy() {
            if (wakeLock.isHeld()) wakeLock.release();
        }
    };
    @SuppressLint("InvalidWakeLockTag")
    public BackgroundTimerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.powerManager = (PowerManager) getReactApplicationContext().getSystemService(reactContext.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "rohit_bg_wakelock");
        reactContext.addLifecycleEventListener(listener);
    }

    @NonNull
    @Override
    public String getName() {
        return "RNBackgroundTimer";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @ReactMethod
    public void start(final int delay) {
        if (!wakeLock.isHeld()) wakeLock.acquire();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendEvent(reactContext, "backgroundTimer");
            }
        };

        handler.post(runnable);
    }

    @ReactMethod
    public void stop() {
        if (wakeLock.isHeld()) wakeLock.release();

        // avoid null pointer exceptio when stop is called without start
        if (handler != null) handler.removeCallbacks(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @ReactMethod
    public void notify(String time, String title){
//        if(Integer.valueOf(time) <= 0){
//            notificationManager.cancel(1);
//            return;
//        }
        if(notificationOn){
            Log.d("Notification ping", "pingggg" + time);
            builder.setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(title)
//                    .setContentText(Long.toString(millisUntilFinished/1000))
                    .setContentText(time)
                    .addAction(0, "", pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.notify(1, builder.build());
        }else{
            notificationOn = true;
            Intent intent = new Intent();
            intent.setAction("notificationToast");
            pendingIntent = PendingIntent.getBroadcast(reactContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager = reactContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(reactContext, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Timer")
                    .setContentText(Long.toString(30000/1000))
                    .addAction(0, "", pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();
        }
    }

    private void sendEvent(ReactContext reactContext, String eventName) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, null);
    }

    @ReactMethod
    public void setTimeout(final int id, final double timeout) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if (getReactApplicationContext().hasActiveCatalystInstance()) {
                    getReactApplicationContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("backgroundTimer.timeout", id);
                }
            }
        }, (long) timeout);
    }
}
