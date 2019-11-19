package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:干货下载的弹框
 */
public class ThingDownAlertDialog {

    private Context mContext;
    //
    private Display mDisplay;
    //组合Dialog
    private Dialog mDialog;
    //标题
    private TextView txt_title;
    //内容
    private TextView txt_msg;
    //左边按钮
    private TextView btn_neg;
    //右边按钮
    private TextView btn_pos;
    //是否显示标题
    private boolean showTitle = false;
    //是否显示内容
    private boolean showMsg = false;
    //是否显示右按钮
    private boolean showPosBtn = false;
    //是否显示左按钮
    private boolean showNegBtn = false;



    public ThingDownAlertDialog(Context context){
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }

    //内部添加样式
    public ThingDownAlertDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_thing_down, null);
        // 获取自定义Dialog布局中的控件
        txt_title =  view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg =  view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.VISIBLE);
        btn_neg =  view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.VISIBLE);
        btn_pos =  view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.VISIBLE);

        // 定义Dialog布局和参数
        mDialog = new Dialog(mContext,R.style.MyDialog);
        mDialog.setContentView(view);

        // 调整dialog背景大小
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (mDisplay.getWidth() * 0.8);

        return this;
    }


    //设置标题
    public ThingDownAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    //设置内容
    public ThingDownAlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    //false 点击物理返回键不消失
    public ThingDownAlertDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    //false 点击外部不消失
    public ThingDownAlertDialog setCanceledOnTouchOutside(boolean b) {
        mDialog.setCanceledOnTouchOutside(b);
        return this;
    }

    //右边按钮
    public ThingDownAlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            mDialog.dismiss();
        });
        return this;
    }

    //左边按钮
    public ThingDownAlertDialog setNegativeButton(String text, final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            mDialog.dismiss();
        });
        return this;
    }


    //设置填充内容
    private void setLayout() {



        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }
    }

    public void show() {
        setLayout();
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }


}
