package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;

import java.util.List;

/**
 * @author zhouliang
 * 版本 2.0
 * 创建时间 2019-11-13
 * 描述:快讯分享框
 * 1.布局样式改了
 */
public class ShareFlashDialogV2 {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private LinearLayout poster_wx;
    private LinearLayout poster_circle;
    private LinearLayout link_wx;
    private LinearLayout link_circle;
    private TextView cancel;


    /** 回调接口 开始 */
    public OnDialogItemClickListener mOnDialogItemClickListener;

    public interface OnDialogItemClickListener{
        void func(int position);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        mOnDialogItemClickListener = onDialogItemClickListener;
    }

    /** 回调接口 结束 */



    public ShareFlashDialogV2(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public ShareFlashDialogV2 builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_share_flash_v2,null);
        link_wx = view.findViewById(R.id.link_wx);
        link_circle = view.findViewById(R.id.link_circle);
        poster_wx = view.findViewById(R.id.poster_wx);
        poster_circle = view.findViewById(R.id.poster_circle);
        cancel = view.findViewById(R.id.cancel);
        // 获取自定义Dialog布局中的控件
        dialog = new Dialog(mContext, R.style.MyDialog);
        dialog.setContentView(view);
        //默认设置
        dialog.setCanceledOnTouchOutside(false);
        // 调整dialog背景大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 1.0f);
        lp.gravity = Gravity.BOTTOM;
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        return this;
    }


    public ShareFlashDialogV2 setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public ShareFlashDialogV2 setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){
        setData();
        setEvent();
    }

    private void setEvent() {

        poster_wx.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(0);
                dialog.dismiss();
            }
        });
        poster_circle.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(1);
                dialog.dismiss();
            }
        });

        link_wx.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(2);
                dialog.dismiss();
            }
        });

        link_circle.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(3);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void setData(){

    }

    public void show(){
        setLayuot();
        dialog.show();
    }



}
