package com.qmkj.niaogebiji.module.event;

import com.qmkj.niaogebiji.module.bean.TempMsgBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述: 发送显示布局事件
 */
public class SendEvent {

    public SendEvent(TempMsgBean tempMsgBean) {
        mTempMsgBean = tempMsgBean;
    }

    private TempMsgBean mTempMsgBean;

    public TempMsgBean getTempMsgBean() {
        return mTempMsgBean;
    }

}
