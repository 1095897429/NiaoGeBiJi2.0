package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.widget.StarBar;
import com.socks.library.KLog;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:文章评分弹框
 */
public class ActiclePointDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private TextView submit;
    private ImageView iv_back;
    private StarBar mStarBar;

    //评论的分数
    private double result;


    /** 回调接口 开始 */
    public OnDialogItemClickListener mOnDialogItemClickListener;

    public interface OnDialogItemClickListener{
        void func(int position, double value);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        mOnDialogItemClickListener = onDialogItemClickListener;
    }

    /** 回调接口 结束 */



    public ActiclePointDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public ActiclePointDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_acticle_point,null);
        // 获取自定义Dialog布局中的控件
        submit = view.findViewById(R.id.submit);
        iv_back = view.findViewById(R.id.iv_back);
        mStarBar = view.findViewById(R.id.starBar);
        mStarBar.setIntegerMark(true);

        mStarBar.setOnStarChangeListener(mark -> {
            KLog.d("tag","rating = " + mark);
            result = mark;

            if(result == 0.0){
                submit.setEnabled(false);
                submit.setBackgroundResource(R.drawable.bg_corners_10_light_yellow);
                submit.setTextColor(Color.parseColor("#61242629"));
            }else{
                submit.setBackgroundResource(R.drawable.bg_corners_10_gradient);
                submit.setEnabled(true);
                submit.setTextColor(Color.parseColor("#242629"));
            }
        });

        dialog = new Dialog(mContext, R.style.MyDialog);
        dialog.setContentView(view);
        //默认设置
        dialog.setCanceledOnTouchOutside(false);
        // 调整dialog背景大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 1.0f);
        lp.height = SizeUtils.dp2px(340);
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);

        return this;
    }


    public ActiclePointDialog setHeadBeanList(List<String> list){
        return this;
    }


    public ActiclePointDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public ActiclePointDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){
        setData();
        setEvent();
    }

    private void setEvent() {

        submit.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){

                if(result > 0){
                    mOnDialogItemClickListener.func(1,result);
                    dialog.dismiss();
                }
            }
        });
        iv_back.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(0,1);
                KeyboardUtils.hideSoftInput(submit);
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
