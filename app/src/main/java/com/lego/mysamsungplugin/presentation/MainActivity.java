package com.lego.mysamsungplugin.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.lego.mysamsungplugin.R;
import com.lego.mysamsungplugin.receivers.ControlReceiver;

import static com.lego.mysamsungplugin.Constants.PLUGIN_REGISTRATION_PACKAGE_NAME_EXTRA;
import static com.lego.mysamsungplugin.Constants.REGISTRATION_ACTION;
import static com.lego.mysamsungplugin.Constants.REGISTRATION_PREF;
import static com.lego.mysamsungplugin.Constants.TASK_ACTION;

public class MainActivity extends AppCompatActivity {

    private ControlReceiver controlReceiver = new ControlReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = getSharedPreferences(getApplication().getPackageName(), 0);

        if (sharedPref.getBoolean(REGISTRATION_PREF, true)) {
            Intent intent = new Intent(REGISTRATION_ACTION)
                    .putExtra(PLUGIN_REGISTRATION_PACKAGE_NAME_EXTRA, getApplication().getPackageName());
            sendBroadcast(intent);
            sharedPref.edit().putBoolean(REGISTRATION_PREF, false).apply();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(getApplication().getPackageName() + "_" + TASK_ACTION);
        registerReceiver(controlReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(controlReceiver);
        super.onDestroy();
    }
}
