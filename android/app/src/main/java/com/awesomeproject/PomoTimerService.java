package com.awesomeproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PomoTimerService extends Service {
    int TIMER_DURATION;
    String CHANNEL_ID = "1";
    String name = "Savvy";
    int importance = 1;
    NotificationChannel channel;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    CountDownTimer pomoCountDownTimer;

    Context context = this;
    public PomoTimerService(ReactContext reactContext) {
//        this.reactContext = this;
    }
    public static final String textContent = "Hello dude, Savvy's calling open the app.";
    public static final String textTitle = "Savvy's Pomo";

    @Override
    public void onDestroy() {
        pomoCountDownTimer.cancel();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PomoTimerService", "onStartCommand -> PomoTimerService");
        TIMER_DURATION = intent.getIntExtra("TimerDuration", 25);

        intent.setAction("notificationToast");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(textTitle)
                        .setContentText(Long.toString(30000/1000))
                        .addAction(0, "", pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        notificationManager.notify(0, builder.build());
//        builder.build();
//        Notification notification =
//                new Notification.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.notification_icon)
//                        .setContentTitle(textTitle)
//                        .setContentText(textContent)
//                        .setContentIntent(pendingIntent)
//                        .build();

        startForeground(1, builder.build());

        pomoCountDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {

                builder.setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(textTitle)
                        .setContentText(Long.toString(millisUntilFinished/1000))
                        .addAction(0, "", pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(1, builder.build());


                //push pomo timer

            }

            public void onFinish() {
                notificationManager.cancel(1);
            }
        }.start();



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showPomoTimerNotification(String CHANNEL_ID, String name, int importance){
        Intent intent = new Intent();
        intent.setAction("notificationToast");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .addAction(0, "", pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(0, builder.build());
    }
}