package com.giftedcat.adskiphelper.event;

import com.giftedcat.adskiphelper.config.ProcessAction;

public class ProcessActionEvent {

    private int action;

    /**
     * @param action 动作指令{@link ProcessAction}
     * */
    public ProcessActionEvent(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
