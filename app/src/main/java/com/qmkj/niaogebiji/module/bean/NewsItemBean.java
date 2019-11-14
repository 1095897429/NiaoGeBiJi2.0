package com.qmkj.niaogebiji.module.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:新闻Item 数据
 */
public  class NewsItemBean extends BaseBean implements Parcelable {


    private String h5url;
    private String nid;
    private String signs;
    private String show_type;
    private String chaid;
    private String fromid;
    private int pubtime;
    private List<String> show_img;
    private int is_precious;
    private boolean is_click;
    //测试
    private boolean is_love;

    //关注
    private boolean is_focus;

    //干货下载积分数
    private String dl_point;;
    //是否下载过：1-下载过，0-未下载
    private String is_dl;

    //自定义的属性，排序
    private String rank;




    private String aid;
    private String title;
    private String author;
    private String author_id;
    private String pic;
    private String published_at;

    private String created_at;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getIs_dl() {
        return is_dl;
    }

    public void setIs_dl(String is_dl) {
        this.is_dl = is_dl;
    }

    public String getDl_point() {
        return dl_point;
    }

    public void setDl_point(String dl_point) {
        this.dl_point = dl_point;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    public boolean isIs_focus() {
        return is_focus;
    }

    public void setIs_focus(boolean is_focus) {
        this.is_focus = is_focus;
    }

    public NewsItemBean(){}

    public NewsItemBean(String h5url, String nid, String show_type, String chaid, String title, String author, List<String> show_img) {
        this.h5url = h5url;
        this.nid = nid;
        this.show_type = show_type;
        this.chaid = chaid;
        this.title = title;
        this.author = author;
        this.show_img = show_img;
    }


    public boolean isIs_love() {
        return is_love;
    }

    public void setIs_love(boolean is_love) {
        this.is_love = is_love;
    }

    public String getUrl() {
        return h5url;
    }

    public void setUrl(String url) {
        this.h5url = url;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getSigns() {
        return signs;
    }

    public void setSigns(String signs) {
        this.signs = signs;
    }

    public String getShow_type() {
        return show_type;
    }

    public void setShow_type(String show_type) {
        this.show_type = show_type;
    }

    public String getChaid() {
        return chaid;
    }

    public void setChaid(String chaid) {
        this.chaid = chaid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public int getPubtime() {
        return pubtime;
    }

    public void setPubtime(int pubtime) {
        this.pubtime = pubtime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getShow_img() {
        return show_img;
    }

    public void setShow_img(List<String> show_img) {
        this.show_img = show_img;
    }

    public int getIs_precious() {
        return is_precious;
    }

    public void setIs_precious(int is_precious) {
        this.is_precious = is_precious;
    }

    public boolean isIs_click() {
        return is_click;
    }

    public void setIs_click(boolean is_click) {
        this.is_click = is_click;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.h5url);
        dest.writeString(this.nid);
        dest.writeString(this.signs);
        dest.writeString(this.show_type);
        dest.writeString(this.chaid);
        dest.writeString(this.title);
        dest.writeString(this.fromid);
        dest.writeInt(this.pubtime);
        dest.writeString(this.author);
        dest.writeStringList(this.show_img);
        dest.writeInt(this.is_precious);
        dest.writeByte(this.is_click ? (byte) 1 : (byte) 0);
    }

    protected NewsItemBean(Parcel in) {
        this.h5url = in.readString();
        this.nid = in.readString();
        this.signs = in.readString();
        this.show_type = in.readString();
        this.chaid = in.readString();
        this.title = in.readString();
        this.fromid = in.readString();
        this.pubtime = in.readInt();
        this.author = in.readString();
        this.show_img = in.createStringArrayList();
        this.is_precious = in.readInt();
        this.is_click = in.readByte() != 0;
    }

    public static final Creator<NewsItemBean> CREATOR = new Creator<NewsItemBean>() {
        @Override
        public NewsItemBean createFromParcel(Parcel source) {
            return new NewsItemBean(source);
        }

        @Override
        public NewsItemBean[] newArray(int size) {
            return new NewsItemBean[size];
        }
    };
}
