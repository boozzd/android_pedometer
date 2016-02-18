package com.example.boozz.pedometer;

/**
 * Created by dmitriy on 2/18/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.util.Log;


public class NotificationServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "Start receiver boot");Intent notificationIntent = new Intent(context, NotificationService.class);
        notificationIntent.putExtra("type", "");
        context.startService(notificationIntent);
    }
}
