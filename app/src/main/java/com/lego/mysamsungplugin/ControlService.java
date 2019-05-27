package com.lego.mysamsungplugin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.lego.mysamsungplugin.Constants.PLUGIN_CHANNEL_NAME;
import static com.lego.mysamsungplugin.Constants.START_TASK_CODE;
import static com.lego.mysamsungplugin.Constants.TASK_CODE_EXTRA;

public class ControlService extends JobIntentService {

    OneTimeWorkRequest timerWorkRequest;
    Notification notification;
    NotificationManager manager;
    WorkManager workManager;
    int notificationId = 1;

    private int counter = 0;
    private ScheduledFuture currentTask;
    private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
    private Runnable TimedToastTask = new Runnable() {
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


    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(notificationId, new Notification());

        timerWorkRequest = new OneTimeWorkRequest.Builder(TimerWorker.class).build();
        workManager = WorkManager.getInstance();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {       //dont need in this case
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getIntExtra(TASK_CODE_EXTRA, 0) == START_TASK_CODE) {
                currentTask = scheduledPool.scheduleWithFixedDelay(TimedToastTask, 1, BuildConfig.PLUGIN_TIMER_DELAY, TimeUnit.SECONDS);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        currentTask.cancel(true);
        super.onDestroy();
    }

    private void startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = getApplicationContext().getPackageName();
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, PLUGIN_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(getString(R.string.plugin_notification_message))
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(notificationId, notification);

        }
    }

}
