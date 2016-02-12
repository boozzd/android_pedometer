package com.example.boozz.pedometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.util.Log;
import com.example.boozz.pedometer.util.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, Pedometer.class));

        Database db = Database.getInstance(this);
        int steps = db.getSteps(Util.getToday());
        Log.d("myLog","" + steps);
    }
}
