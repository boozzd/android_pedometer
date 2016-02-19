package com.example.boozz.pedometer;

/**
 * Created by dmitriy on 2/18/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import android.os.Binder;
import android.os.IBinder;

public class NotificationService extends Service {

    private IBinder mBinder = null;
    private final String TAG = "NotificationService";

    public String type;

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new NotificationServiceBinder();
        Log.i(TAG, "onBind" + intent);
        return mBinder;
    }

    public class NotificationServiceBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        type = intent.getStringExtra("type");

        Log.i(TAG, type);
        if(!type.isEmpty()) {
            sendNotification();
        }
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public boolean stopService(Intent intent) {
        Log.i(TAG, "= Received stop: " + intent);
        return super.stopService(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
    }

    private void sendNotification () {
        Intent intentA = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentA, 0);

        Notification noti = new Notification.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("Good " + type)
                .setContentText("Touch to start the app and specify your status")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        Log.i(TAG, "send notification");

        notificationManager.notify(0, noti);
    }
}
