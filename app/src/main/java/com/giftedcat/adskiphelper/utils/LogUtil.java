package com.giftedcat.adskiphelper.utils;

import android.util.Log;

/**
 * 管理Log的工具类：可通过设置mCurrentLevel，控制Log输出级别。
 * 项目上线时应将mCurrentLevel设置为LEVEL_NONE，禁止Log输出。
 */
public class LogUtil {

    //日志输出时的Tag
    private static String mTag = "LogUtil";
    //当前日志输出级别（是否允许输出log）

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void v(String msg) {
        Log.v(mTag, msg);
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void d(String msg) {
        Log.d(mTag, msg);
    }

    /**
     * 以级别为 i 的形式输出LOG
     */
    public static void i(String msg) {
        Log.i(mTag, msg);
    }

    /**
     * 以级别为 w 的形式输出LOG
     */
    public static void w(String msg) {
        Log.w(mTag, msg);
    }

    /**
     * 以级别为 w 的形式输出Throwable
     */
    public static void w(Throwable tr) {
        Log.w(mTag, "", tr);
    }

    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     */
    public static void w(String msg, Throwable tr) {
        Log.w(mTag, msg, tr);
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(String msg) {
        Log.e(mTag, msg);
    }

    /**
     * 以级别为 e 的形式输出Throwable
     */
    public static void e(Throwable tr) {
        Log.e(mTag, "", tr);
    }

    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */
    public static void e(String msg, Throwable tr) {
        Log.e(mTag, msg, tr);
    }

}
