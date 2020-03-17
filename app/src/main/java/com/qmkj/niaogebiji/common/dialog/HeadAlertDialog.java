package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:头像上传 弹出框
 */
public class HeadAlertDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private TextView cancel;
    private TextView take_pic;
    private TextView open_pic;


    /** 回调接口 开始 */
    public OnDialogItemClickListener mOnDialogItemClickListener;

    public interface OnDialogItemClickListener{
        void func(int position);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        mOnDialogItemClickListener = onDialogItemClickListener;
    }

    /** 回调接口 结束 */



    public HeadAlertDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public HeadAlertDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_head,null);
        open_pic = view.findViewById(R.id.open_pic);
        take_pic = view.findViewById(R.id.take_pic);
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
//        lp.y = (int) (display.getWidth() * 0.0f);//设置Dialog距离底部的距离
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        return this;
    }


    public HeadAlertDialog setHeadBeanList(List<String> list){
        return this;
    }


    public HeadAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public HeadAlertDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){
        setData();
        setEvent();
    }

    private void setEvent() {


        cancel.setOnClickListener(v -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(2);
                dialog.dismiss();
            }
        });

        take_pic.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(0);
                dialog.dismiss();
            }
        });
        open_pic.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(1);
                dialog.dismiss();
            }
        });
    }

    private void setData(){

    }

    public void show(){
        setLayuot();
        dialog.show();
    }



}
