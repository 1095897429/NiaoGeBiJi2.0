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
import com.qmkj.niaogebiji.module.activity.HomeActivityV2;
import com.qmkj.niaogebiji.module.activity.NewsDetailActivity;
import com.qmkj.niaogebiji.module.activity.SplashActivity;
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
 * 1.如果通过极光接入厂商，那么都会走这里； -- 统一了(华为的走这里 小米走这里)【前提是不需要在xml中注册单独的receiver】
 */
public class JPushReceiver extends JPushMessageReceiver {
    private static final String TAG = "tag";


    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        String json = message.notificationExtras;
        KLog.e("tag","json = " + json);
        if(!TextUtils.isEmpty(json)){
            JPushBean javaBean = JSON.parseObject(json, JPushBean.class);
            //判断app是否启动，没有启动就启动 -- 经测试都走第一条
            if(StringUtil.isAppAlive(context, AppUtils.getAppPackageName())){
                //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
                //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收，如果直接启动
                //DetailActivity,再按Back键就不会返回MainActivity了。所以在启动
                //DetailActivity前，要先启动MainActivity。
                Log.e("tag", "      the app process is alive     ");

                Intent mainIntent;
                if(StringUtil.isExsitMianActivity(context, HomeActivityV2.class)){
                    mainIntent= new Intent(context, HomeActivityV2.class);
                }else{
                    mainIntent = new Intent(context, SplashActivity.class);
                }
                //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
                //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
                //如果Task栈不存在MainActivity实例，则在栈顶创建

                Bundle bundle = new Bundle();
                bundle.putSerializable("jpushbean",javaBean);
                mainIntent.putExtras(bundle);

                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                context.startActivity(mainIntent);

            }else{
                KLog.e("tag", "      the app process is dead      ");
                //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
                //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入
                Intent mainIntent;
                mainIntent = new Intent(context, SplashActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("jpushbean",javaBean);
                mainIntent.putExtras(bundle);

                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                context.startActivity(mainIntent);
            }

        }
    }

    private void sendBroadToHome(JPushBean javaBean) {
        Intent msgIntent = new Intent(HomeActivityV2.MESSAGE_RECEIVED_ACTION);
        Bundle bundle = new Bundle();
        bundle.putSerializable("jpushbean",javaBean);
        msgIntent.putExtras(bundle);
        msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        BaseApp.getApplication().startActivity(msgIntent);
    }

    private void toDiffer(JPushBean javaBean, Context context) {
        String jump_type = javaBean.getJump_type();
        String jump_info = javaBean.getJump_info();
        Intent i ;
        try{
            if("1".equals(jump_type)){
                KLog.e("tag"," 个人中心");
                //个人中心 - ok
                i =  new Intent(context, HomeActivityV2.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type",HomeActivityV2.JPUSH_TO_My);
                i.putExtras(bundle);
            }else if("20".equals(jump_type)){
                KLog.e("tag"," 文章");
                //文章 ok
                i =  new Intent(context, NewsDetailActivity.class);
                i.putExtra("newsId",jump_info);
            }else if("31".equals(jump_type)){
                KLog.e("tag"," 快讯");
                i = new Intent(context,HomeActivityV2.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type",HomeActivityV2.JPUSH_TO_FLASH);
                i.putExtras(bundle);
            }else if("50".equals(jump_type)){
                KLog.e("tag"," 动态");
                //动态 id - 58 - ok
                i = new Intent(context, CommentDetailActivity.class);
                Bundle  bundle = new Bundle();
                bundle.putString("blog_id",jump_info);
                i.putExtras(bundle);
            }else if("60".equals(jump_type)){
                KLog.e("tag"," wiki");
                //wiki  1 ok
                String link = StringUtil.getLink("wikilist/" + jump_info);
                i = new Intent(context, WebViewActivityWithStep.class);
                i.putExtra("link",link);
            }else if("70".equals(jump_type)){
                KLog.e("tag"," 外链");
                //外 link - ok
                String link = jump_info;
                i = new Intent(context, WebViewActivityWithLayout.class);
                i.putExtra("link",link);
                i.putExtra("fromWhere","");
            }else{
                //没有额外数据的情况，跳转首页
                KLog.e("tag"," 主界面");
                i = new Intent(context,HomeActivityV2.class);
            }

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(i);
        }catch (Throwable throwable){

        }


    }




    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        KLog.e("tag", "[onMessage] " + customMessage);
        processCustomMessage(context, customMessage);
    }


    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Log.e("tag", "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if(nActionExtra==null){
            Log.d("tag","ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
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
        if (HomeActivityV2.isForeground) {
            String message = customMessage.message;
            String extras = customMessage.extra;
            Intent msgIntent = new Intent(HomeActivityV2.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(HomeActivityV2.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(HomeActivityV2.KEY_EXTRAS, extras);
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