package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:1.构造器 build时自动生成
 */
@Entity
public class History extends BaseBean {

    @Id(autoincrement = true)
    private long id;


    private String name;


    private Long time;



    @Generated(hash = 1791447656)
    public History(long id, String name, Long time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    @Generated(hash = 869423138)
    public History() {
    }

   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
