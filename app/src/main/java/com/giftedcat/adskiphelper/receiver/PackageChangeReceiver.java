package com.giftedcat.adskiphelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.giftedcat.adskiphelper.config.ProcessAction;
import com.giftedcat.adskiphelper.event.ProcessActionEvent;
import com.giftedcat.adskiphelper.service.AdSkipService;

import org.greenrobot.eventbus.EventBus;

/**
 * 应用安装、卸载receiver
 * */
public class PackageChangeReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            //发送应用变动的指令
            EventBus.getDefault().post(new ProcessActionEvent(ProcessAction.ACTION_REFRESH_PACKAGE));
        }
    }
}
