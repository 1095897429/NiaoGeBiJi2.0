package com.vhall.uilibs.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.socks.library.KLog;
import com.vhall.uilibs.R;

/**
 * @author zhouliang
 * 版本 2.0
 * 创建时间
 * 描述:课程弹框
 */
public class ShowResDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;




    //默认是评论文章
    private int myPosition = -1;

    public void setMyPosition(int myPosition) {
        this.myPosition = myPosition;
    }




    public ShowResDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public ShowResDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_to_res,null);

        // 获取自定义Dialog布局中的控件
        dialog = new Dialog(mContext, R.style.MyDialog111);
        dialog.setContentView(view);
        //默认设置
        dialog.setCanceledOnTouchOutside(true);
        // 调整dialog背景大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 1.0f);
        lp.gravity = Gravity.BOTTOM;
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        return this;
    }


    public ShowResDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }


    public ShowResDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }



    private void setLayuot(){
        setEvent();
    }



    private void setEvent() {


    }


    public void show(){
        setLayuot();
        dialog.show();
    }



}
