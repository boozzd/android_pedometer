package com.example.boozz.pedometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.app.Notification;
import android.app.NotificationManager;

import android.util.Log;
import com.example.boozz.pedometer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startService(new Intent(this, StepCounterService.class));

        SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//        SharedPreferences sharedPref = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        if(sharedPref.contains("pedometerData")){
            String pDataString = sharedPref.getString("pedometerData", "{}");

            Date currentDate = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateString = dateFormatter.format(currentDate);

            JSONObject pData = new JSONObject();
            JSONObject dayData = new JSONObject();
            Integer daySteps = -1;
            try{
                pData = new JSONObject(pDataString);
                Log.d(TAG," got json shared prefs "+pData.toString());
            }catch (JSONException err){
                Log.d(TAG," Exception while parsing json string : "+pDataString);
            }

            if(pData.has(currentDateString)){
                try {
                    dayData = pData.getJSONObject(currentDateString);
                    daySteps = dayData.getInt("steps");
                }catch(JSONException err){
                    Log.e(TAG,"Exception while getting Object from JSON for "+currentDateString);
                }
            }

            Log.i(TAG, "Getting steps for today: " + daySteps);
        }

        Boolean can = StepCounterOldService.deviceHasStepCounter(this.getPackageManager());

        if(can) {
            Intent stepCounter = new Intent(this, StepCounterService.class);
            startService(stepCounter);
        } else {
            Intent stepCounterOld = new Intent(this, StepCounterOldService.class);
            startService(stepCounterOld);
        }
    }

    private void setAlarm() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, 15);
        cal1.set(Calendar.MINUTE, 35);
        cal1.set(Calendar.SECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, 15);
        cal2.set(Calendar.MINUTE, 36);
        cal2.set(Calendar.SECOND, 0);

        Calendar now = Calendar.getInstance();
        if(now.after(cal1)) {
            cal1.add(Calendar.HOUR_OF_DAY, 24);
        }
        if(now.after(cal2)) {
            cal2.add(Calendar.HOUR_OF_DAY, 24);
        }

        Intent intent = new Intent(this, NotificationServiceReceiver.class);
        intent.putExtra("type", "Morning");
        PendingIntent morningAlarm = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        intent.putExtra("type", "Evening");
        PendingIntent eveningAlarm = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, morningAlarm);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, eveningAlarm);
    }
}
