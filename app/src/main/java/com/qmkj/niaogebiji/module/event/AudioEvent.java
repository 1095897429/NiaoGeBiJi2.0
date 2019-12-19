package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:
 */
public class AudioEvent {
    public String  url;
    public String title;

    public AudioEvent(String  url){
        this.url = url;
    }

    public AudioEvent(String  url,String  title){
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
