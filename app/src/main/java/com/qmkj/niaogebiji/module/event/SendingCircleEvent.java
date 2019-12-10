package com.qmkj.niaogebiji.module.event;

import com.qmkj.niaogebiji.module.bean.TempMsgBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:发布帖子中
 */
public class SendingCircleEvent {

    private TempMsgBean mTempMsgBean;

    public SendingCircleEvent(TempMsgBean tempMsgBean){
        this.mTempMsgBean = tempMsgBean;
    }

    public TempMsgBean getTempMsgBean() {
        return mTempMsgBean;
    }
}
