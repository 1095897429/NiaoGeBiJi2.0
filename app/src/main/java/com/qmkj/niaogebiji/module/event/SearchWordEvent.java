package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:
 */
public class SearchWordEvent {

    private String word;
    //哪个界面
    private int position;

    public SearchWordEvent(String word) {
        this.word = word;
    }

    public SearchWordEvent(String word, int position) {
        this.word = word;
        this.position = position;
    }

    public String getWord() {
        return word;
    }

    public int getPosition() {
        return position;
    }
}
