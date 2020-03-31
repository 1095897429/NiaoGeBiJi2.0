package com.vhall.uilibs.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-30
 * 描述:聊天还是提问
 */
public class ChatorAskEvent {

    private int mChatEvent;

    public ChatorAskEvent(int chatEvent) {
        mChatEvent = chatEvent;
    }

    public int getChatEvent() {
        return mChatEvent;
    }
}
