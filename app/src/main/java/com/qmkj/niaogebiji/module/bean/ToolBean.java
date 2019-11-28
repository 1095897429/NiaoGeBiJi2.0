package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:工具实体
 */
public class ToolBean extends BaseBean {

    private String mes;

    //默认为 false
    private boolean openState ;

    private boolean isSave;

    public boolean isSave() {
        return isSave;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public boolean isOpenState() {
        return openState;
    }

    public void setOpenState(boolean openState) {
        this.openState = openState;
    }
}
