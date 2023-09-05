# 1.前言
当代移动应用广告的过度侵扰问题已经引起了广大用户的关注和不满。而芒果TV平台运营中心的副总经理陈超推出了一项名为"摇一摇开屏广告"的新策略↓


![ezgif.com-resize.gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/358545027a4d45298b829779726a790d~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=200&h=432&s=701049&e=gif&f=53&b=fcfcfc)

引发了更多对于用户体验的担忧下↓


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/923bbc8b42144b389da678987fbbfc50~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=300&h=487&s=204510&e=png&b=857569)

在这种策略下，用户在不经意间被强制打开广告，这对用户来说无疑是一种糟糕的体验。当人处于运动的状态下，打开某些APP。

而“李跳跳”APP通过利用Android的无障碍模式，"李跳跳"成功帮助用户自动跳过这些令人困扰的开屏广告，从而有效地减轻了用户的不便。随之而来的不正当竞争指控引发了对于这类应用的法律和道德讨论。

我决定仿“李跳跳”写一个广告跳过助手，以呼吁对于这种过度侵扰性广告的关注，同时也为广大Android开发者们分享运用的技术原理。
# 2.效果图

![ezgif-2-147d9e39be.gif](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/06ee9ff7159045ab9797a33de6de58e9~tplv-k3u1fbpfcp-jj-mark:0:0:0:0:q75.image#?w=200&h=445&s=539245&e=gif&f=28&b=066d63)

# 3.无障碍模式
当我们深入探讨"李跳跳"及其仿制应用的功能实现时，了解Android的无障碍模式和AccessibilityService以及onAccessibilityEvent函数的详细内容至关重要。这些技术是这些应用背后的核心，让我们更深入地了解它们：
## 3.1Android的无障碍模式
无障碍模式是Android操作系统的一个功能，旨在提高设备的可用性和可访问性，特别是为了帮助那些有视觉、听觉或运动障碍的用户。通过无障碍模式，应用可以获取有关用户界面和用户操作的信息，以便在需要时提供更好的支持。
## 3.2 onServiceConnected函数
这是AccessibilityService的回调函数之一，当服务被绑定到系统时会被调用。在这个函数中，可以进行初始化操作，如设置服务的配置、注册事件监听等。
```java
@Override
public void onServiceConnected() {
    // 在这里进行服务的初始化操作
    // 注册需要监听的事件类型
}
```
## 3.3 onAccessibilityEvent函数

这是AccessibilityService的核心函数，用于处理发生的可访问性事件。在这个函数中，可以检查事件类型、获取事件源信息以及采取相应的操作。
本次功能主要用到的就是这个函数
```java
@Override 
public void onAccessibilityEvent(AccessibilityEvent event) {
    // 处理可访问性事件 
    // 获取事件类型、源信息，执行相应操作 
}
```
## 3.4 onInterrupt函数

这个函数在服务被中断时会被调用，例如，用户关闭了无障碍服务或系统资源不足。可以在这里进行一些清理工作或记录日志以跟踪服务的中断情况。
```java
@Override
public void onInterrupt() {
    // 服务中断时执行清理或记录日志操作
}
```
## 3.5 onUnbind函数
当服务被解绑时，这个函数会被调用。可以在这里进行资源的释放和清理工作。
```java
@Override
public boolean onUnbind(Intent intent) {
    // 解绑时执行资源释放和清理操作
    return super.onUnbind(intent);
}
```
## 3.6 onKeyEvent函数（未用到）
这个函数用于处理键盘事件。通过监听键盘事件，可以实现自定义的按键处理逻辑。例如，可以捕获特定按键的按下和释放事件，并执行相应操作。
```java
@Override
public boolean onKeyEvent(KeyEvent event) {
    // 处理键盘事件，执行自定义逻辑
    return super.onKeyEvent(event);
}

```
## 3.7 onGesture函数（未用到）
onGesture()函数允许处理手势事件。这些事件可以包括触摸屏幕上的手势，例如滑动、缩放、旋转等。通过监听手势事件，可以实现各种手势相关的应用功能。
```java
@Override
public boolean onGesture(int gestureId) {
    // 处理手势事件，执行自定义逻辑
    return super.onGesture(gestureId);
}

```
# 4.功能实现
## 4.1无障碍服务的启用和注册
-   创建AccessibilityService的类。
```java
public class AdSkipService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
```
-   在AndroidManifest.xml文件中声明AccessibilityService。
```xml
<service android:name=".service.AdSkipService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```
## 4.2 onAccessibilityEvent函数的实现
-   在onAccessibilityEvent函数中获取当前界面的控件，并在异步遍历所有子控件
```java
@Override
public void onAccessibilityEvent(AccessibilityEvent event) {
    // 获取当前界面的控件
    AccessibilityNodeInfo nodeInfo = event.getSource();
    
    taskExecutorService.execute(new Runnable() {
        @Override
        public void run() {
            //遍历节点函数，查找所有控件
            iterateNodesToSkipAd(nodeInfo);
        }
    });
}

```
-   判断控件的文本是否带有“跳过”二字
```java
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
```
-   触发控件的点击事件
```java
/**
 * 点击跳过按钮
 * @param node 节点
 * @return 是否点击成功
 * */
private boolean clickSkipNode(AccessibilityNodeInfo node){
    //尝试点击
    boolean clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    //打印点击按钮的结果
    LogUtil.e("clicked result = " + clicked);
    return clicked;
}
```

**注：本篇章为了读者方便理解，对代码进行了简化，删去了繁琐的逻辑判断。具体实现详见源码**
# 5.结语
我们通过AccessibilityService和无障碍模式，提供了一种改善用户体验的方法，帮助用户摆脱令人不快的广告干扰。通过了解如何开发这样的应用，我们可以更好地理解无障碍技术的潜力，并在保护用户权益的前提下改善应用环境。

如果对你有所帮助，请记得帮我点一个star
