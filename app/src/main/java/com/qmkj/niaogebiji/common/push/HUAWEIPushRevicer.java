package com.qmkj.niaogebiji.common.push;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.socks.library.KLog;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-30
 * 描述:实现PUSH Token和透传消息的接收。
 */
public class HUAWEIPushRevicer extends PushReceiver {


    @Override
    public void onToken(Context context, String token, Bundle extras) {
        //开发者自行实现Token保存逻辑。 测试时 可放出来
//        KLog.e("tag","华为注册的token "+ token);
//        String value =  extras.getString("");
    }



}
