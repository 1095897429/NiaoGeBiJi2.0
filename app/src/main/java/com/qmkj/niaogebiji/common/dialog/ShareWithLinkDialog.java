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
 * 创建时间 2019-11-18
 * 描述:文章详情里的分享
 */
public class ShareWithLinkDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private TextView title;
    private TextView cancel;
    private TextView share_dynamic_text;
    private LinearLayout share_dynamic;
    private LinearLayout share_circle;
    private LinearLayout share_friend;
    private LinearLayout share_copy_link;


    /** 回调接口 开始 */
    public OnDialogItemClickListener mOnDialogItemClickListener;

    public interface OnDialogItemClickListener{
        void func(int position);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        mOnDialogItemClickListener = onDialogItemClickListener;
    }

    /** 回调接口 结束 */



    public ShareWithLinkDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public ShareWithLinkDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_share_news,null);
        share_dynamic = view.findViewById(R.id.share_dynamic);
        share_circle = view.findViewById(R.id.share_circle);
        share_friend = view.findViewById(R.id.share_friend);
        share_copy_link = view.findViewById(R.id.share_copy_link);
        share_dynamic_text = view.findViewById(R.id.share_dynamic_text);
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
//        lp.y = (int) (display.getWidth() * 0.0f);//设置Dialog距离底部的距离
        // 添加动画
//        getWindow().setWindowAnimations(R.style.TransparentDialogAddanmi);
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        return this;
    }


    public ShareWithLinkDialog setHeadBeanList(List<String> list){
        return this;
    }

    public ShareWithLinkDialog setTitleGone() {
        title.setVisibility(View.GONE);
        return this;
    }

    public ShareWithLinkDialog setShareDynamicView() {
        share_dynamic.setVisibility(View.VISIBLE);
        return this;
    }

    public ShareWithLinkDialog setShareDynamicViewText(String text) {
        share_dynamic_text.setText("转发到圈子");
        return this;
    }

    public ShareWithLinkDialog setSharelinkView() {
        share_copy_link.setVisibility(View.VISIBLE);
        return this;
    }

    public ShareWithLinkDialog setTranferView() {
        share_copy_link.setVisibility(View.VISIBLE);
        return this;
    }


    public ShareWithLinkDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public ShareWithLinkDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){
        setData();
        setEvent();
    }

    private void setEvent() {

        cancel.setOnClickListener(view -> dialog.dismiss());

        share_dynamic.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(4);
                dialog.dismiss();
            }
        });

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
        share_copy_link.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(2);
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
