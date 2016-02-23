package com.example.boozz.pedometer;

/**
 * Created by dmitriy on 2/12/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StepCounterBootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean can = StepCounterOldService.deviceHasStepCounter(context.getPackageManager());
        Log.d("test", can ? "yes it c" : "no it cn");
        if(can) {
            Intent stepCounterServiceIntent = new Intent(context,StepCounterService.class);
            context.startService(stepCounterServiceIntent);
        } else {
            Intent stepCounterServiceOldIntent = new Intent(context, StepCounterOldService.class);
            context.startService(stepCounterServiceOldIntent);
        }

    }
}
