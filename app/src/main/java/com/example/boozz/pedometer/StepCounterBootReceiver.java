package com.example.boozz.pedometer;

/**
 * Created by dmitriy on 2/12/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StepCounterBootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent stepCounterServiceIntent = new Intent(context,StepCounterService.class);
        context.startService(stepCounterServiceIntent);
    }
}
