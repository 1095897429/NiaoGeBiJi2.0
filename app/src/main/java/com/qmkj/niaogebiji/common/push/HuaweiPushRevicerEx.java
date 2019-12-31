package com.qmkj.niaogebiji.common.push;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.socks.library.KLog;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-30
 * 描述:实现接收通知栏或通知栏上按钮点击事件的onEvent回调。
 *
 * public static enum Event
 * {
 *     NOTIFICATION_OPENED, //通知栏中的通知被点击打开
 *     NOTIFICATION_CLICK_BTN, //通知栏中通知上的按钮被点击
 * }
 */
public class HuaweiPushRevicerEx extends PushReceiver {

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        super.onEvent(context, event, extras);
        //开发者自行实现相应处理逻辑

        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            KLog.e("tag", "收到通知栏消息点击事件,notifyId:" + notifyId);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
        }
        String message = extras.getString(BOUND_KEY.pushMsgKey);
        KLog.e("tag",message);
        super.onEvent(context, event, extras);
    }


}
