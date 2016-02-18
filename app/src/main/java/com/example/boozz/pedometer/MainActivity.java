package com.example.boozz.pedometer;

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
        Intent notificationIntent = new Intent(this, NotificationService.class);
        notificationIntent.putExtra("type", "");
        startService(notificationIntent);

        Intent stepCounter = new Intent(this, StepCounterService.class);
        startService(stepCounter);
    }
}
