package com.qmkj.niaogebiji.common.base;

import android.app.Activity;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-02
 * 描述: 管理所有的Activity
 */
public class ActivityManager {

    //保存所有创建的Activity
    private Set<Activity> allActivities = new HashSet<>();

    /** 添加 Activity */
    public void addActivity(Activity activity){
        if(null != activity){
            allActivities.add(activity);
        }
    }

    /** 移除 Activity */
    public void removeActivity(Activity activity){
        if(null != activity){
            allActivities.remove(activity);
        }
    }

    /** 关闭所有 Activity */
    public void finishAll(){
        for (Activity activity : allActivities) {
            activity.finish();
        }
    }
}
