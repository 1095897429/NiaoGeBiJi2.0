package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-13
 * 描述:话题bean
 */
@Entity
public class TopicBean extends BaseBean {

    @Id(autoincrement = true)
    private long id;
    //话题名称
    private String name;

    private String type;

    //基本本地的时间戳（毫秒），后台返回的是 秒
    private long currentTime;

    private int is_select;

    //后台返回
    private String title;
    private String icon;
    private String follow_num;
    //后台返回的时间，
    private String updated_at;

    //后台定义的
    private boolean is_follow;

    public boolean isIs_follow() {
        return is_follow;
    }

    public void setIs_follow(boolean is_follow) {
        this.is_follow = is_follow;
    }

    //是否关注 -- 我定义的(主要用于在圈子中点击关注)
    private int is_focus;

    public int getIs_focus() {
        return is_focus;
    }

    public void setIs_focus(int is_focus) {
        this.is_focus = is_focus;
    }

    public String getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(String follow_num) {
        this.follow_num = follow_num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Generated(hash = 804890643)
    public TopicBean(long id, String name, String type, long currentTime,
            int is_select, String title, String icon, String follow_num,
            String updated_at, boolean is_follow, int is_focus) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currentTime = currentTime;
        this.is_select = is_select;
        this.title = title;
        this.icon = icon;
        this.follow_num = follow_num;
        this.updated_at = updated_at;
        this.is_follow = is_follow;
        this.is_focus = is_focus;
    }

    @Generated(hash = 1961217991)
    public TopicBean() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getIs_select() {
        return is_select;
    }

    public void setIs_select(int is_select) {
        this.is_select = is_select;
    }

    public boolean getIs_follow() {
        return this.is_follow;
    }
}
