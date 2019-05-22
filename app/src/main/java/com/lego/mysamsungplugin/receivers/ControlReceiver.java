package com.lego.mysamsungplugin.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.lego.mysamsungplugin.TimerWorker;

import static com.lego.mysamsungplugin.Constants.START_TASK_CODE;
import static com.lego.mysamsungplugin.Constants.STOP_TASK_CODE;
import static com.lego.mysamsungplugin.Constants.TASK_CODE_EXTRA;

public class ControlReceiver extends BroadcastReceiver {

    OneTimeWorkRequest timerWorkRequest = new OneTimeWorkRequest.Builder(TimerWorker.class).build();

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra(TASK_CODE_EXTRA, STOP_TASK_CODE)) {
            case STOP_TASK_CODE:
                WorkManager.getInstance().cancelWorkById(timerWorkRequest.getId());
                break;
            case START_TASK_CODE:
                WorkManager.getInstance().enqueue(timerWorkRequest);
                break;
        }

    }

}
