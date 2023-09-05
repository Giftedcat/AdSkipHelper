package com.giftedcat.adskiphelper.config;

/**
 * 广告跳过服务的指令
 * */
public class ProcessAction {

    /**
     * 应用变动Action
     * */
    public final static int ACTION_REFRESH_PACKAGE = 0x08;

    /**
     * 服务停止
     * */
    public final static int ACTION_STOP_SERVICE = 0x09;

    /**
     * 开启广告跳过的扫描
     * */
    public final static int ACTION_START_SKIP_PROCESS = 0x01;

    /**
     * 关闭广告跳过的扫描
     * */
    public final static int ACTION_STOP_SKIP_PROCESS = 0x02;

}
