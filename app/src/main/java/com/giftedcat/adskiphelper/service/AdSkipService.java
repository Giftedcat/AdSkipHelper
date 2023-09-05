package com.giftedcat.adskiphelper.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.giftedcat.adskiphelper.event.ProcessActionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * 广告跳过service
 */
public class AdSkipService extends AccessibilityService {

    private static WeakReference<AdSkipService> serviceWeakReference;
    private AdSkipServiceImpl serviceImpl;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        EventBus.getDefault().register(this);
        serviceWeakReference = new WeakReference<>(this);
        if (serviceImpl == null) {
            serviceImpl = new AdSkipServiceImpl(this);
        }
        serviceImpl.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (serviceImpl != null) {
            serviceImpl.onAccessibilityEvent(event);
        }
    }

    @Override
    public void onInterrupt() {
        if (serviceImpl != null) {
            serviceImpl.stopSkipAdProcess();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        EventBus.getDefault().unregister(this);
        if (serviceImpl != null) {
            serviceImpl = null;
        }
        serviceWeakReference = null;
        return super.onUnbind(intent);
    }

    /**
     * 收到指令
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ProcessActionEvent event) {
        AdSkipService service = serviceWeakReference.get();
        if (service != null) {
            service.serviceImpl.processActionHandler.sendEmptyMessage(event.getAction());
        }
    }

    public static boolean isServiceRunning() {
        final AdSkipService service = serviceWeakReference != null ? serviceWeakReference.get() : null;
        return service != null && service.serviceImpl != null;
    }
}
