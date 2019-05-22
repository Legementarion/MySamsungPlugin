package com.lego.mysamsungplugin;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerWorker extends Worker {

    private int counter = 0;
    private ScheduledFuture currentTask;
    private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
    private Runnable sevenSecondsToast = new Runnable() {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), BuildConfig.PLUGIN_NAME + " " + counter++, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    public TimerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        currentTask = scheduledPool.scheduleWithFixedDelay(sevenSecondsToast, 1, BuildConfig.PLUGIN_TIMER_DELAY, TimeUnit.SECONDS);
        return null;
    }

    @Override
    public void onStopped() {
        currentTask.cancel(true);
        super.onStopped();
    }
}
