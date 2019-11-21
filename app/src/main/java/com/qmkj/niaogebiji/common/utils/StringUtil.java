package com.qmkj.niaogebiji.common.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class StringUtil {

    private static final Object sInstanceSync = new Object();



    /** --------------------------------- 用户信息  ---------------------------------*/

    private static RegisterLoginBean.UserInfo userInfoBean;

    public static void setUserInfoBean(RegisterLoginBean.UserInfo userInfoBean) {
        removeUserInfoBean();
        SPUtils.getInstance().put(Constant.USER_INFO, new Gson().toJson(userInfoBean));
    }

    public static RegisterLoginBean.UserInfo getUserInfoBean() {
        synchronized (sInstanceSync) {
            if (userInfoBean == null) {
                String string = SPUtils.getInstance().getString(Constant.USER_INFO);
                if (!TextUtils.isEmpty(string)) {
                    Gson gson = new Gson();
                    userInfoBean = gson.fromJson(string, RegisterLoginBean.UserInfo.class);
                }
            }
        }
        return userInfoBean;
    }

    public static void removeUserInfoBean() {
        userInfoBean = null;
        SPUtils.getInstance().remove(Constant.USER_INFO);
    }



    /** --------------------------------- 快速点击  ---------------------------------*/

    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        //当点击时间 和 之前的时间对比，如果大于1秒，返回false ,表示不是快速点击
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

}
