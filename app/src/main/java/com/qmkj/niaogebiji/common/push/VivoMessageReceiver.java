package com.qmkj.niaogebiji.common.push;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


// regid = 15780379920011670484994
//imei 866354033770128
public class VivoMessageReceiver extends OpenClientPushMessageReceiver {
    /**
     * TAG to Log
     */
    public static final String TAG = VivoMessageReceiver.class.getSimpleName();

    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        String customContentString = msg.getSkipContent();
        String notifyString = "通知点击 msgId " + msg.getMsgId() + " ;customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(notifyString);
    }

    @Override
    public void onReceiveRegId(Context context, String regId) {
        String responseString = "" + regId;
        Log.d("tag", "vivo的id " + responseString);
        updateContent(responseString);
    }



    public static void updateContent(String content) {
        Log.d(TAG, "updateContent");
        String logText = "";

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss", Locale.CHINA);
        logText += sDateFormat.format(new Date()) + ": ";
        logText += content;

        Log.i(TAG, "updateContent : " + logText);

    }




}
