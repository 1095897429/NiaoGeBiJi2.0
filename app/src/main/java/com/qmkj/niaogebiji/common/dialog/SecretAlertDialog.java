package com.qmkj.niaogebiji.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-21
 * 描述:隐私的弹框
 */
public class SecretAlertDialog {

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
    private boolean showMsg = true;
    //是否显示右按钮
    private boolean showPosBtn = false;
    //是否显示左按钮
    private boolean showNegBtn = false;



    public SecretAlertDialog(Context context){
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }

    //内部添加样式
    public SecretAlertDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_secret, null);
        // 获取自定义Dialog布局中的控件
        txt_title =  view.findViewById(R.id.txt_title);
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
    public SecretAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    //设置内容
    public SecretAlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {

            //spannableString
            SpannableString spannableString = new SpannableString(msg);
            ForegroundColorSpan fCs1 = new ForegroundColorSpan(Color.parseColor("#82A9C1"));
            ForegroundColorSpan fCs2 = new ForegroundColorSpan(Color.parseColor("#82A9C1"));

            spannableString.setSpan(fCs1,81,93, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(fCs2,94,100, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            ClickableSpan user_ll = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    ((TextView)view).setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
                    UIHelper.toUserAgreeActivity(mContext);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            };
            ClickableSpan secret_ll = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    ((TextView)view).setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
                    UIHelper.toSecretActivity(mContext);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    //设置颜色
                    ds.setUnderlineText(false);

                }
            };

            spannableString.setSpan(user_ll, 81,93, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(secret_ll,94,100, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //必须设置才能响应点击事件
            txt_msg.setMovementMethod(LinkMovementMethod.getInstance());
            txt_msg.setText(spannableString);



        }


        return this;
    }


    public SecretAlertDialog setMsg2(String msg){
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    //false 点击物理返回键不消失
    public SecretAlertDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    //false 点击外部不消失
    public SecretAlertDialog setCanceledOnTouchOutside(boolean b) {
        mDialog.setCanceledOnTouchOutside(b);
        return this;
    }

    //右边按钮
    public SecretAlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
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


    private void showSecretDialog2() {
        final SecretAlertDialog2 iosAlertDialog2 = new SecretAlertDialog2(mContext).builder();
        iosAlertDialog2.setTitle("您需要同意本注册协议\n和隐私政策才能使用").setMsg2(mContext.getResources().getString(R.string.secret_hint2))
                .setPositiveButton("同意并继续", v -> {
                    iosAlertDialog2.dismiss();
                }).setNegativeButton("不同意并退出", v -> {
            iosAlertDialog2.dismiss();
            mDialog.dismiss();

        }).setCanceledOnTouchOutside(false);
        iosAlertDialog2.setCancelable(false);
        iosAlertDialog2.show();

    }

    //左边按钮
    public SecretAlertDialog setNegativeButton(String text, final View.OnClickListener listener) {
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
            showSecretDialog2();
        });
        return this;
    }


    //设置填充内容
    private void setLayout() {



        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }



        //这边没有新增single_selector资源
        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.bg_corners_8_white);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.GONE);
            btn_neg.setBackgroundResource(R.drawable.bg_corners_8_white);
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
