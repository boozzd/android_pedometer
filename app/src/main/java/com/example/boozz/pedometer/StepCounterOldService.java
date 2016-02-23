package com.example.boozz.pedometer;

/**
 * Created by boozz on 2/22/16.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Build;
import android.util.Log;
import android.os.Binder;
import android.content.pm.PackageManager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Date;
import java.text.SimpleDateFormat;
import org.json.JSONException;
import org.json.JSONObject;

public class StepCounterOldService extends Service implements SensorEventListener{
    private final String TAG = "stepCounterServiceOld";
    private IBinder mBinder = null;
    private static boolean isRunning = false;

    private SensorManager mSensorManager;
    private Sensor mStepSensor;

    /**
     * Variables for counter
     */
    private float   mLimit = 10;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;


    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new StepCounterOldServiceBinder();
        Log.i(TAG, "onBind" + intent);
        return mBinder;
    }

    public class StepCounterOldServiceBinder extends Binder {
        StepCounterOldService getService() {
            return StepCounterOldService.this;
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

        if (isRunning) {
            Log.i(TAG, "not initialising sensors");
            return Service.START_STICKY;
        }

        Log.i(TAG, "Initialising sensors");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mStepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        isRunning = true;
        return Service.START_STICKY;
    }

    @Override
    public boolean stopService(Intent intent) {
        Log.i(TAG, "Receiver stop:" + intent);
        if (isRunning) {
            mSensorManager.unregisterListener(this);
        }
        return super.stopService(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() { Log.i(TAG, "onDestroy"); }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i(TAG, "onAccuracyChanged: " + sensor);
        Log.i(TAG, "Accuracy: " + i);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
            }
            else {
                int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                if (j == 1) {
                    float vSum = 0;
                    for (int i=0 ; i<3 ; i++) {
                        final float v = mYOffset + event.values[i] * mScale[j];
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == - mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        if (diff > mLimit) {

                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                Log.i(TAG, "On step");
                                mLastMatch = extType;
                                onStep();
                            }
                            else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                }
            }
        }
    }

    public StepCounterOldService() {
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    private void onStep() {
        Integer daySteps = 0;
        Date currentDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        String currentDateString = dateFormatter.format(currentDate);
        SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        JSONObject pData = new JSONObject();
        JSONObject dayData = new JSONObject();
        if(sharedPref.contains("pedometerData")){
            String pDataString = sharedPref.getString("pedometerData","{}");
            try{
                pData = new JSONObject(pDataString);
                Log.d(TAG," got json shared prefs "+pData.toString());
            }catch (JSONException err){
                Log.d(TAG," Exception while parsing json string : "+pDataString);
            }
        }

        if (pData.has(currentDateString)) {
            try {
                dayData = pData.getJSONObject(currentDateString);
                daySteps = dayData.getInt("steps");
            } catch (JSONException err) {
                Log.e(TAG, "Exception while getting Object from JSON for " + currentDateString);
            }
        }

        daySteps++;

        Log.i(TAG, "** daySteps :"+ daySteps+" ** ");

        try{
            dayData.put("steps",daySteps);
            dayData.put("offset",0);
            pData.put(currentDateString,dayData);
        }catch (JSONException err){
            Log.e(TAG,"Exception while setting int in JSON for "+currentDateString);
        }
        editor.putString("pedometerData",pData.toString());
        editor.commit();
    }

    public static boolean deviceHasStepCounter(PackageManager pm) {
        // Require at least Android KitKat
        int currentApiVersion = Build.VERSION.SDK_INT;

        // Check that the device supports the step counter and detector sensors
        return currentApiVersion >= 19
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
    }
}
