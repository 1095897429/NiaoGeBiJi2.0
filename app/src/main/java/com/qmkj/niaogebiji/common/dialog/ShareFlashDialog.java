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
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:分享框
 */
public class ShareFlashDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private LinearLayout share_circle;
    private LinearLayout share_friend;
    private TextView cancel;
    private TextView title;


    /** 回调接口 开始 */
    public OnDialogItemClickListener mOnDialogItemClickListener;

    public interface OnDialogItemClickListener{
        void func(int position);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        mOnDialogItemClickListener = onDialogItemClickListener;
    }

    /** 回调接口 结束 */



    public ShareFlashDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public ShareFlashDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_share,null);
        share_circle = view.findViewById(R.id.share_circle);
        share_friend = view.findViewById(R.id.share_friend);
        title = view.findViewById(R.id.title);
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
        // 添加动画
//         dialogWindow.setWindowAnimations(R.style.TransparentDialog);
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        return this;
    }


    public ShareFlashDialog setHeadBeanList(List<String> list){
        return this;
    }


    public ShareFlashDialog setTitle(String title1) {
        title.setText(title1);
        return this;
    }


    public ShareFlashDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public ShareFlashDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){
        setData();
        setEvent();
    }

    private void setEvent() {

        share_friend.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(1);
                dialog.dismiss();
            }
        });
        share_circle.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(0);
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
