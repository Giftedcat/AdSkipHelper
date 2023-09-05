package com.giftedcat.adskiphelper.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.giftedcat.adskiphelper.event.ProcessActionEvent;

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
        if (serviceImpl != null) {
            serviceImpl = null;
        }
        serviceWeakReference = null;
        return super.onUnbind(intent);
    }

    public static boolean isServiceRunning() {
        final AdSkipService service = serviceWeakReference != null ? serviceWeakReference.get() : null;
        return service != null && service.serviceImpl != null;
    }
}
