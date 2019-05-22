package com.lego.mysamsungplugin.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.lego.mysamsungplugin.Constants.START_TASK_CODE;
import static com.lego.mysamsungplugin.Constants.STOP_TASK_CODE;
import static com.lego.mysamsungplugin.Constants.TASK_CODE_EXTRA;

public class ControlReceiver extends BroadcastReceiver {

    private ControlsCallback callBack;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra(TASK_CODE_EXTRA, STOP_TASK_CODE)) {
            case STOP_TASK_CODE:
                callBack.onStopTask();
                break;
            case START_TASK_CODE:
                callBack.onStartTask();
                break;
        }

    }

    public void setupCallback(ControlsCallback callBack) {
        this.callBack = callBack;
    }
}
