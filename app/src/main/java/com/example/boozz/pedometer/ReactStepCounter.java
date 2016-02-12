//package com.example.boozz.pedometer;

//class Api {
//    private final static String TAG = "api";
//
//    public final void getDaySteps() {
//
//    }
//}
//
///**
// * Created by dmitriy on 2/12/16.
// */
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.IBinder;
//import android.util.Log;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class ReactStepCounter {
//
//    private final String TAG = "CordovaStepCounter";
//
//    //private final String ACTION_CONFIGURE        = "configure";
//    private final String ACTION_START            = "start";
//    private final String ACTION_STOP             = "stop";
//    private final String ACTION_GET_STEPS        = "get_step_count";
//    private final String ACTION_GET_TODAY_STEPS  = "get_today_step_count";
//    private final String ACTION_CAN_COUNT_STEPS  = "can_count_steps";
//    private final String ACTION_GET_HISTORY      = "get_history";
//
//
//    private Intent  stepCounterIntent;
//    private Boolean isEnabled    = false;
//
//    private StepCounterService stepCounterService;
//    private boolean bound = false;
//
//    private Integer beginningOffset = 0;
//
//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            StepCounterService.StepCounterServiceBinder binder = (StepCounterService.StepCounterServiceBinder) service;
//            stepCounterService = binder.getService();
//            bound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            bound = false;
//        }
//    };
//
//    @Override
//    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
//        LOG.i(TAG, "execute()");
//        Boolean result = true;
//
//        Activity activity = this.cordova.getActivity();
//        stepCounterIntent = new Intent(activity, StepCounterService.class);
//
//        if (ACTION_CAN_COUNT_STEPS.equals(action)) {
//            Boolean can = deviceHasStepCounter(activity.getPackageManager());
//            Log.i(TAG, "Checking if device has step counter APIS: "+ can);
//            callbackContext.success( can ? 1 : 0 );
//        }
//        else if (ACTION_START.equals(action)) {
//            beginningOffset = data.getInt(0);
//
//            Log.i(TAG, "Starting StepCounterService");
//            isEnabled = true;
//            //stepCounterIntent.putExtra("beginningOffset", beginningOffset);
//            activity.startService(stepCounterIntent);
//            activity.bindService(stepCounterIntent, mConnection, Context.BIND_AUTO_CREATE);
//        }
//        else if (ACTION_STOP.equals(action)) {
//            Log.i(TAG, "Stopping StepCounterService");
//            if (isEnabled && bound) {
//                stepCounterService.stopTracking();
//                activity.unbindService(mConnection);
//                bound = false;
//            } else {
//                Log.i(TAG, "Unable to manually stop step counter");
//            }
//
//            isEnabled = false;
//            activity.stopService(stepCounterIntent);
//        }
//        else if (ACTION_GET_STEPS.equals(action)) {
//            if (isEnabled && bound) {
//                Integer steps = stepCounterService.getStepsCounted();
//                Log.i(TAG, "Geting steps counted from stepCounterService: " + steps);
//                callbackContext.success(steps);
//            } else {
//                Log.i(TAG, "Can't get steps from stepCounterService as we're not enabled / bound - returning 0");
//                callbackContext.success(0);
//            }
//        }
//        else if (ACTION_GET_TODAY_STEPS.equals(action)) {
//            SharedPreferences sharedPref = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
//            if(sharedPref.contains("pedometerData")){
//                String pDataString = sharedPref.getString("pedometerData", "{}");
//
//                Date currentDate = new Date();
//                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//                String currentDateString = dateFormatter.format(currentDate);
//
//                JSONObject pData = new JSONObject();
//                JSONObject dayData = new JSONObject();
//                Integer daySteps = -1;
//                try{
//                    pData = new JSONObject(pDataString);
//                    Log.d(TAG," got json shared prefs "+pData.toString());
//                }catch (JSONException err){
//                    Log.d(TAG," Exception while parsing json string : "+pDataString);
//                }
//
//                if(pData.has(currentDateString)){
//                    try {
//                        dayData = pData.getJSONObject(currentDateString);
//                        daySteps = dayData.getInt("steps");
//                    }catch(JSONException err){
//                        Log.e(TAG,"Exception while getting Object from JSON for "+currentDateString);
//                    }
//                }
//
//                Log.i(TAG, "Getting steps for today: " + daySteps);
//                callbackContext.success(daySteps);
//            }else{
//                Log.i(TAG, "No steps history found in stepCounterService !");
//                callbackContext.success(-1);
//            }
//        }
//        else if(ACTION_GET_HISTORY.equals(action)){
//            SharedPreferences sharedPref = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
//            if(sharedPref.contains("pedometerData")){
//                String pDataString = sharedPref.getString("pedometerData","{}");
//                Log.i(TAG, "Getting steps history from stepCounterService: " + pDataString);
//                callbackContext.success(pDataString);
//            }else{
//                Log.i(TAG, "No steps history found in stepCounterService !");
//                callbackContext.success("{}");
//            }
//        }
//        else {
//            Log.e(TAG, "Invalid action called on class " + TAG + ", " + action);
//            callbackContext.error("Invalid action called on class " + TAG + ", " + action);
//        }
//
//        return result;
//    }
//
//    public static boolean deviceHasStepCounter(PackageManager pm) {
//        // Require at least Android KitKat
//        int currentApiVersion = Build.VERSION.SDK_INT;
//
//        // Check that the device supports the step counter and detector sensors
//        return currentApiVersion >= 19
//                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
//                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
//    }
//
////    @Override
////    public void onDestroy() {
////        if(bound){
////            Activity activity = this.cordova.getActivity();
////            activity.unbindService(mConnection);
////            bound = false;
////        }
////        super.onDestroy();
////    }
//}
