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
    //新闻id
    private String newId;

    public AudioEvent(String  url){
        this.url = url;
    }

    public AudioEvent(String  url,String  title,String newId){
        this.url = url;
        this.newId = newId;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }
}
