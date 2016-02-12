package com.example.boozz.pedometer;

/**
 * Created by boozz on 2/11/16.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;


import com.example.boozz.pedometer.util.Util;
import android.util.Log;

public class Pedometer extends Service implements SensorEventListener{

    public static int steps;
    private final static int MICROSECONDS_IN_ONE_MINUTE = 60000000;

    private static boolean WAIT_FOR_VALID_STEPS = false;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.values[0] > Integer.MAX_VALUE) {
            return;
        } else {
            steps = (int) event.values[0];
            if (WAIT_FOR_VALID_STEPS && steps > 0) {
                WAIT_FOR_VALID_STEPS = false;
                Database db = Database.getInstance(this);

                if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
                    db.insertNewDay(Util.getToday(), steps);
                    reRegisterSensor();
                }
                db.saveCurrentSteps(steps);
                db.close();
            }
        }
        Log.d("myLog", "Steps count from service: " + (int) event.values[0] );
        Database db = Database.getInstance(this);
        Log.d("myLog", "Steps count in service from db" + db.getSteps(Util.getToday()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        reRegisterSensor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null) {
            if (steps == 0) {
                Database db = Database.getInstance(this);
                steps = db.getCurrentSteps();
                db.close();
            }
            Database db = Database.getInstance(this);
            db.updateSteps(Util.getToday(), steps);
            db.close();
        }
        // restart service every hour to get the current step count
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                        PendingIntent.getService(getApplicationContext(), 2,
                                new Intent(this, Pedometer.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));

        WAIT_FOR_VALID_STEPS = true;

        return START_STICKY;
    }

    private void reRegisterSensor() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            , SensorManager.SENSOR_DELAY_NORMAL, 5 * MICROSECONDS_IN_ONE_MINUTE);
    }

}
