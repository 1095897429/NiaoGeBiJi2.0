package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:
 */
public class QuestionResultRightDialog {

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

    //羽毛布局
    private LinearLayout part1111;

    //内容
    private TextView  mContent;


    //是否显示标题
    private boolean showTitle = true;
    //是否显示内容
    private boolean showMsg = true;
    //是否显示右按钮
    private boolean showPosBtn = false;
    //是否显示左按钮
    private boolean showNegBtn = false;



    public QuestionResultRightDialog(Context context){
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }

    //内部添加样式
    public QuestionResultRightDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_question_right, null);
        // 获取自定义Dialog布局中的控件
        txt_title =  view.findViewById(R.id.txt_title);
        txt_msg =  view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        part1111 = view.findViewById(R.id.part1111);
        mContent = view.findViewById(R.id.content);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        txt_msg.setTypeface(typeface);

        btn_neg =  view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);


        // 定义Dialog布局和参数
        mDialog = new Dialog(mContext,R.style.MyDialog);
        mDialog.setContentView(view);

        // 调整dialog背景大小
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (mDisplay.getWidth() * 0.8);
        lp.gravity = Gravity.TOP;
        //设置margin为屏幕的20%
        lp.verticalMargin = 0.12f;
        dialogWindow.setAttributes(lp);

        return this;
    }


    //设置显示羽毛布局
    public QuestionResultRightDialog setFeatherNumShow(boolean show) {

        if(!show){
            part1111.setVisibility(View.GONE);
        }
        return this;
    }


    //设置内容
    public QuestionResultRightDialog setContent(String content) {
        mContent.setText(content);
        return this;
    }

    //设置标题
    public QuestionResultRightDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    //设置内容
    public QuestionResultRightDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    //false 点击物理返回键不消失
    public QuestionResultRightDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    //false 点击外部不消失
    public QuestionResultRightDialog setCanceledOnTouchOutside(boolean b) {
        mDialog.setCanceledOnTouchOutside(b);
        return this;
    }

    //左边按钮
    public QuestionResultRightDialog setNegativeButton(String text, final View.OnClickListener listener) {
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
        if (!showTitle && !showMsg) {
            txt_title.setText("下载链接");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }


        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
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
