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


    @Generated(hash = 2124835529)
    public TopicBean(long id, String name, String type, long currentTime,
            int is_select) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currentTime = currentTime;
        this.is_select = is_select;
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
}
