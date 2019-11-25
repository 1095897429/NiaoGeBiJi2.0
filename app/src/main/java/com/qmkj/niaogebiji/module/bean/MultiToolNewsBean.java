package com.qmkj.niaogebiji.module.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:工具的多实体
 */
public class MultiToolNewsBean extends BaseBean implements MultiItemEntity {

    private ToolBean mToolBean;

    //类型
    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }


    public ToolBean getToolBean() {
        return mToolBean;
    }

    public void setToolBean(ToolBean toolBean) {
        mToolBean = toolBean;
    }
}
