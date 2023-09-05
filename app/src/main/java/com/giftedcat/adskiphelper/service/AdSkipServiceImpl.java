package com.giftedcat.adskiphelper.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.giftedcat.adskiphelper.config.Constant;
import com.giftedcat.adskiphelper.config.ProcessAction;
import com.giftedcat.adskiphelper.receiver.PackageChangeReceiver;
import com.giftedcat.adskiphelper.receiver.UserPresentReceiver;
import com.giftedcat.adskiphelper.utils.LogUtil;
import com.giftedcat.adskiphelper.utils.SkipUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 广告跳过具体实现类
 * */
public class AdSkipServiceImpl {

    private final AccessibilityService service;

    /**
     * 广告跳过动作
     */
    public Handler processActionHandler;

    private ScheduledExecutorService taskExecutorService;

    /**
     * 是否已开启跳过进程
     * */
    private volatile boolean skipProcessRunning;
    /**
     * 当前打开的英勇的package和Activity
     * */
    private String currentPackageName;
    /**
     * 用户安装的应用的包名
     * */
    private Set<String> installPackages;
    /**
     * 节点描述集合
     * */
    private Set<String> nodeDescribes;

    public AdSkipServiceImpl(AccessibilityService service) {
        this.service = service;
    }

    public void onServiceConnected() {
        try {
            currentPackageName = "PackageName";

            installPackages = getInstallPackages();

            nodeDescribes = new HashSet<>();

            initActionHandler();

            taskExecutorService = Executors.newSingleThreadScheduledExecutor();
        } catch (Throwable e) {
            LogUtil.e(e.getMessage());
        }
    }

    /**
     * 初始化指令接收handler
     * */
    private void initActionHandler() {
        processActionHandler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case ProcessAction.ACTION_REFRESH_PACKAGE:
                    installPackages = getInstallPackages();
                    break;
                case ProcessAction.ACTION_STOP_SERVICE:
                    service.disableSelf();
                    break;
                case ProcessAction.ACTION_START_SKIP_PROCESS:
                    startSkipAdProcess();
                    break;
                case ProcessAction.ACTION_STOP_SKIP_PROCESS:
                    stopSkipAdProcess();
                    break;
            }
            return true;
        });
    }

    /**
     * 处理手机界面变动的事件
     * */
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null || event.getClassName() == null){
            return;
        }
        String actionPackageName = event.getPackageName().toString();
        String actionClassname = event.getClassName().toString();
        try {
            switch (event.getEventType()) {
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    boolean isActivity = !actionClassname.startsWith("android.")
                            && !actionClassname.startsWith("androidx.");
                    if (!currentPackageName.equals(actionPackageName)) {//打开的是一个新应用
                        if (isActivity) {
                            currentPackageName = actionPackageName;
                            stopSkipAdProcess();
                            if (installPackages.contains(actionPackageName)) {
                                startSkipAdProcess();
                            }
                        }
                    }
                    executeSkipTask(service.getRootInActiveWindow());
                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    if (!installPackages.contains(actionPackageName)) {
                        break;
                    }
                    executeSkipTask(event.getSource());
                    break;
            }
        } catch (Throwable e) {
            LogUtil.e(e.getMessage());
        }
    }

    /**
     * 执行跳过任务
     * */
    private void executeSkipTask(final AccessibilityNodeInfo nodeInfo){
        if (!skipProcessRunning) {
            return;
        }
        taskExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                iterateNodesToSkipAd(nodeInfo);
            }
        });
    }

    /**
     * 遍历节点跳过广告
     * @param rootNodeInfo 根节点信息
     */
    private void iterateNodesToSkipAd(AccessibilityNodeInfo rootNodeInfo) {
        List<AccessibilityNodeInfo> parentNodes = new ArrayList<>();
        List<AccessibilityNodeInfo> childNodes = new ArrayList<>();
        parentNodes.add(rootNodeInfo);

        int total = parentNodes.size();
        int index = 0;
        while (index < total && skipProcessRunning) {
            AccessibilityNodeInfo nodeInfo = parentNodes.get(index++);
            if (nodeInfo != null) {
                boolean clicked = clickSkipNode(nodeInfo);
                if (clicked) {
                    nodeInfo.recycle();
                    break;
                }
                for (int n = 0; n < nodeInfo.getChildCount(); n++) {
                    childNodes.add(nodeInfo.getChild(n));
                }
                nodeInfo.recycle();
            }
            if (index == total) {
                //父节点处理完毕，处理子节点
                parentNodes.clear();
                parentNodes.addAll(childNodes);
                childNodes.clear();
                index = 0;
                total = parentNodes.size();
            }
        }
        // 未处理的节点进行遍历回收
        while (index < total) {
            AccessibilityNodeInfo node = parentNodes.get(index++);
            if (node != null) node.recycle();
        }
        index = 0;
        total = childNodes.size();
        while (index < total) {
            AccessibilityNodeInfo node = childNodes.get(index++);
            if (node != null) node.recycle();
        }
    }

    /**
     * 点击跳过按钮
     * @param node 节点
     * @return 是否点击成功
     * */
    private boolean clickSkipNode(AccessibilityNodeInfo node){
        //如果不包含"跳过",则不进行点击
        if (!SkipUtil.isKeywords(node, Constant.SCAN_KEYWORD)){
            return false;
        }
        String nodeDesc = SkipUtil.generateNodeDescribe(node);
        //保证不重复点击
        if (!nodeDescribes.contains(nodeDesc)) {
            nodeDescribes.add(nodeDesc);

            //尝试点击
            boolean clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            //打印点击按钮的描述
            LogUtil.d("try to click " + nodeDesc);
            //打印点击按钮的结果
            LogUtil.e("clicked result = " + clicked);
            return true;
        }
        return false;
    }

    /**
     * 开始扫描进程
     */
    public void startSkipAdProcess() {
        skipProcessRunning = true;
        nodeDescribes.clear();

        //定时结束进程(默认4秒)
        processActionHandler.removeMessages(ProcessAction.ACTION_STOP_SKIP_PROCESS);
        processActionHandler.sendEmptyMessageDelayed(ProcessAction.ACTION_STOP_SKIP_PROCESS, Constant.SCAN_TIME);
    }

    /**
     * 结束扫描进程
     */
    public void stopSkipAdProcess() {
        skipProcessRunning = false;
        processActionHandler.removeMessages(ProcessAction.ACTION_STOP_SKIP_PROCESS);
    }

    /**
     * 获取用户安装所有应用的报名
     * @return 安装的应用包的集合
     */
    private Set<String> getInstallPackages() {
        PackageManager packageManager = service.getPackageManager();
        Set<String> installPackages = new HashSet<>();
        Set<String> homeAppPackages = new HashSet<>();

        // 获取所有应用的包名
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        for (ResolveInfo e : resolveInfoList) {
            installPackages.add(e.activityInfo.packageName);
        }
        // 获取桌面应用的包名
        intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        for (ResolveInfo e : resolveInfoList) {
            homeAppPackages.add(e.activityInfo.packageName);
        }

        //加入当前应用的包名和设置的包名
        homeAppPackages.add(service.getPackageName());
        homeAppPackages.add("com.android.settings");

        installPackages.removeAll(homeAppPackages);
        return installPackages;
    }
}
