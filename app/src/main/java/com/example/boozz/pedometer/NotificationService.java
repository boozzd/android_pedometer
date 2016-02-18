package com.example.boozz.pedometer;

/**
 * Created by dmitriy on 2/18/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.AlarmManager;
import android.content.Intent;
import android.util.Log;
import android.content.Context;

import java.util.Calendar;

import android.os.Binder;
import android.os.IBinder;

public class NotificationService extends Service {

    private IBinder mBinder = null;
    private final String TAG = "NotificationService";

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

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
        if(type.isEmpty()) {
            setAlarm();
        } else {
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
        Log.i(TAG, type);
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

    private void setAlarm () {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("type", "Morning");
        PendingIntent pi = PendingIntent.getService(this, 0,
                intent ,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 17); // For 1 PM or 2 PM
        calendar2.set(Calendar.MINUTE, 31);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        Intent intent1 = new Intent(this, NotificationService.class);
        intent1.putExtra("type", "Evening");
        PendingIntent pi1 = PendingIntent.getService(this, 1,
                intent1,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am1 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am1.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi1);
    }
}
