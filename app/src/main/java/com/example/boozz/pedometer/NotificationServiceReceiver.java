package com.example.boozz.pedometer;

/**
 * Created by dmitriy on 2/18/16.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;


public class NotificationServiceReceiver extends BroadcastReceiver {

//    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "Start receiver boot");
//        String action = intent.getAction();
//        Log.i("Receiver", " " + action);
        String type = intent.getStringExtra("type");
        Intent notificationIntent = new Intent(context, NotificationService.class);
        notificationIntent.putExtra("type", type);
        context.startService(notificationIntent);
    }
}
