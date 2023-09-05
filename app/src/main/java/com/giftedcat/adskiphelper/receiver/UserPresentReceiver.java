package com.giftedcat.adskiphelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.giftedcat.adskiphelper.config.ProcessAction;
import com.giftedcat.adskiphelper.event.ProcessActionEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 屏幕唤醒receiver
 * */
public class UserPresentReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_USER_PRESENT)) {
            //发送开始扫描的指令
            EventBus.getDefault().post(new ProcessActionEvent(ProcessAction.ACTION_START_SKIP_PROCESS));
        }
    }
}
