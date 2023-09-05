package com.giftedcat.adskiphelper.utils;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.giftedcat.adskiphelper.config.Constant;

import java.util.Set;

public class SkipUtil {

    /**
     * 判断节点内容是否是关键字(默认为”跳过“二字 )
     * @param node 节点
     * @param keyWords 关键字
     * @return 是否包含
     * */
    public static boolean isKeywords(AccessibilityNodeInfo node, String keyWords){
        CharSequence text = node.getText();
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        //查询是否包含"跳过"二字
        return text.toString().contains(keyWords);
    }

    /**
     * 生成节点的描述，防止重复点击
     * @param nodeInfo
     * */
    public static String generateNodeDescribe(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return "null";
        }

        StringBuilder nodeDesc = new StringBuilder();

        //描述中加入类名
        nodeDesc.append("class =")
                .append(nodeInfo.getClassName().toString());

        //描述中加入按钮位置
        final Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        String nodeRect = String.format(" Position=[%d, %d, %d, %d]",
                rect.left,
                rect.right,
                rect.top,
                rect.bottom);
        nodeDesc.append(nodeRect);


        //描述中加入控件的ID
        CharSequence id = nodeInfo.getViewIdResourceName();
        if (id != null) {
            nodeDesc.append(" ResourceId=")
                    .append(id);
        }

        return nodeDesc.toString();
    }

}
