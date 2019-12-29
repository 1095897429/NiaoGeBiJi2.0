package com.qmkj.niaogebiji.common.utils.MobClickEvent;

import com.qmkj.niaogebiji.common.BaseApp;
import com.umeng.analytics.MobclickAgent;


public class MobclickAgentUtils {

    public static void onEvent(String id) {
        MobclickAgent.onEvent(BaseApp.getApplication(), id);
    }

}
