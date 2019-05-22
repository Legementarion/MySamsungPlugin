package com.lego.mysamsungplugin.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.lego.mysamsungplugin.BuildConfig;
import com.lego.mysamsungplugin.R;
import com.lego.mysamsungplugin.receivers.ControlReceiver;
import com.lego.mysamsungplugin.receivers.ControlsCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.lego.mysamsungplugin.Constants.PLUGIN_REGISTRATION_PACKAGE_NAME_EXTRA;
import static com.lego.mysamsungplugin.Constants.REGISTRATION_ACTION;
import static com.lego.mysamsungplugin.Constants.REGISTRATION_PREF;
import static com.lego.mysamsungplugin.Constants.TASK_ACTION;
import static java.util.Calendar.SECOND;

public class MainActivity extends AppCompatActivity implements ControlsCallback {

    private int counter = 0;

    private ScheduledFuture currentTask;
    private ControlReceiver controlReceiver = new ControlReceiver();
    private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
    private Runnable sevenSecondsToast = new Runnable() {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, BuildConfig.PLUGIN_NAME + counter++, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

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
        controlReceiver.setupCallback(this);
        registerReceiver(controlReceiver, filter);
    }

    @Override
    public void onStartTask() {
        currentTask = scheduledPool.scheduleWithFixedDelay(sevenSecondsToast, SECOND, BuildConfig.PLUGIN_TIMER_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void onStopTask() {
        if (currentTask != null) {
            currentTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(controlReceiver);
        super.onDestroy();
    }
}
