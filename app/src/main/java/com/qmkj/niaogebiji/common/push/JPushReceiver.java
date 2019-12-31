package com.qmkj.niaogebiji.common.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.CommentDetailActivity;
import com.qmkj.niaogebiji.module.activity.HomeActivity;
import com.qmkj.niaogebiji.module.activity.NewsDetailActivity;
import com.qmkj.niaogebiji.module.activity.UserInfoActivity;
import com.qmkj.niaogebiji.module.activity.WebViewActivityWithLayout;
import com.qmkj.niaogebiji.module.activity.WebViewActivityWithStep;
import com.qmkj.niaogebiji.module.activity.WebViewAllActivity;
import com.qmkj.niaogebiji.module.bean.JPushBean;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-30
 * 描述:极光接收器
 */
public class JPushReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";


    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageOpened] "+message);
        String json = message.notificationExtras;
        KLog.e("tag","json = " + json);
        if(!TextUtils.isEmpty(json)){
            JPushBean javaBean = JSON.parseObject(json, JPushBean.class);
            //判断app是否启动，没有启动就启动
            if(StringUtil.isAppAlive(context, AppUtils.getAppPackageName())){
                KLog.e("tag"," app已经打开");
                toDiffer(javaBean,context);
            }else{
                KLog.e("tag"," 跳转到主界面 ");
                //跳转到主界面
                sendBroadToHome(javaBean);
            }
        }
    }

    private void sendBroadToHome(JPushBean javaBean) {
        Intent msgIntent = new Intent(HomeActivity.MESSAGE_RECEIVED_ACTION);
        Bundle bundle = new Bundle();
        bundle.putSerializable("jpushbean",javaBean);
        msgIntent.putExtras(bundle);
        msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        BaseApp.getApplication().startActivity(msgIntent);
    }

    private void toDiffer(JPushBean javaBean, Context context) {
        String jump_type = javaBean.getJump_type();
        String jump_info = javaBean.getJump_info();
        Intent i  = null;
        try{
            if("1".equals(jump_type)){
                //个人中心 - ok
                i =  new Intent(context, UserInfoActivity.class);
            }else if("20".equals(jump_type)){
                //文章 ok
                i =  new Intent(context, NewsDetailActivity.class);
                i.putExtra("newsId",jump_info);
            }else if("31".equals(jump_type)){
                i = new Intent(context,HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type",HomeActivity.JPUSH_TO_FLASH);
                i.putExtras(bundle);
            }else if("50".equals(jump_type)){
                //动态 id - 58 - ok
                i = new Intent(context, CommentDetailActivity.class);
                Bundle  bundle = new Bundle();
                bundle.putString("blog_id",jump_info);
                i.putExtras(bundle);
            }else if("60".equals(jump_type)){
                //wiki  1 ok
                String link = StringUtil.getLink("wikilist/" + jump_info);
                i = new Intent(context, WebViewActivityWithStep.class);
                i.putExtra("link",link);
            }else if("70".equals(jump_type)){
                //外 link - ok
                String link = jump_info;
                i = new Intent(context, WebViewActivityWithLayout.class);
                i.putExtra("link",link);
                i.putExtra("fromWhere","");
            }

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            context.startActivity(i);
        }catch (Throwable throwable){

        }


    }




    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        KLog.e(TAG, "[onMessage] " + customMessage);
        processCustomMessage(context, customMessage);
    }


    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if(nActionExtra==null){
            Log.d(TAG,"ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageArrived] "+message);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageDismiss] "+message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Log.e(TAG,"[onRegister] "+registrationId);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Log.e(TAG,"[onConnected] "+isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Log.e(TAG,"[onCommandResult] "+cmdMessage);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, CustomMessage customMessage) {
        if (HomeActivity.isForeground) {
            String message = customMessage.message;
            String extras = customMessage.extra;
            Intent msgIntent = new Intent(HomeActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(HomeActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(HomeActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        }
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        Log.e(TAG,"[onNotificationSettingsCheck] isOn:"+isOn+",source:"+source);
    }


}