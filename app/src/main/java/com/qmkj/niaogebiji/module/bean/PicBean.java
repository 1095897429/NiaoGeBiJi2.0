package com.qmkj.niaogebiji.module.bean;

import android.graphics.Bitmap;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-02
 * 描述:查看原图
 */
public class PicBean extends BaseBean {

    //正常图
    private String pic;
    //缩略图
    private String scalePic;

    //bitmap
    private Bitmap mBitmap;

    //是否展示查看原图
    private boolean isNoShowLook;

    public boolean isNoShowLook() {
        return isNoShowLook;
    }

    public void setNoShowLook(boolean noShowLook) {
        isNoShowLook = noShowLook;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    private boolean isYuanTu;

    public String getScalePic() {
        return scalePic;
    }

    public void setScalePic(String scalePic) {
        this.scalePic = scalePic;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isYuanTu() {
        return isYuanTu;
    }

    public void setYuanTu(boolean yuanTu) {
        isYuanTu = yuanTu;
    }
}
