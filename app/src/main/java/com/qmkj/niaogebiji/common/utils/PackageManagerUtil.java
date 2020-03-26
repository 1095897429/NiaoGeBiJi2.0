package com.qmkj.niaogebiji.common.utils;

import android.content.ComponentName;
import android.content.pm.PackageManager;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-24
 * 描述:
 */
public class PackageManagerUtil {


    private PackageManager mPackageManager;

    public PackageManagerUtil(PackageManager mPackageManager) {
        this.mPackageManager = mPackageManager;
    }

    /**
     * 启动组件
     * @param componentName 组件名
     */
    public void enableComponent(ComponentName componentName) {
        //此方法用以启用和禁用组件，会覆盖AndroidManifest文件下定义的属性
        mPackageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * 禁用组件
     * @param componentName 组件名
     */
    public void disableComponent(ComponentName componentName) {
        //此方法用以启用和禁用组件，会覆盖AndroidManifest文件下定义的属性
        mPackageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
