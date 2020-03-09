package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.qmkj.niaogebiji.R;
import com.socks.library.KLog;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:文章的框
 */
public class TalkAlertDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private TextView listentext;
    private TextView listentext2;
    private TextView et_input;
    private TextView cancel;
    private TextView send;
    private CheckBox mCheckBox;

    private LinearLayout comment_succuss_transfer;

    //默认是评论文章
    private int myPosition = -1;
    private boolean isneedtotrans;

    public void setIsneedtotrans(boolean isneedtotrans) {
        this.isneedtotrans = isneedtotrans;
        if(isneedtotrans){
            comment_succuss_transfer.setVisibility(View.VISIBLE);
//            mCheckBox.setVisibility(View.VISIBLE);
        }
    }

    public void setMyPosition(int myPosition) {
        this.myPosition = myPosition;
    }

    /** 是否转发到圈子 回调接口 开始 */
    public OnIsToCircleLister mOnIsToCircleLister;

    public interface OnIsToCircleLister{
        void func(boolean bug);
    }

    public void setOnIsToCircleLister(OnIsToCircleLister onIsToCircleLister) {
        mOnIsToCircleLister = onIsToCircleLister;
    }


    public void setNum(int num) {
        this.num = num;
        listentext2.setText("/" + num);
    }

    /** 回调接口 结束 */

    public TalkAlertDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    //点击item时获取要被评论的人
    public TalkAlertDialog setHint(String name) {
        et_input.setHint("回复 "  + name);
        return this;
    }

    //设置草稿
    public TalkAlertDialog setCaoGao(String content) {
        et_input.setText(content);
        if(isneedtotrans){
            setTextStatus(content);
        }

        if(content.trim().length() > num){
            setStatus(false);
        }else{
            setStatus(true);
        }
        return this;
    }



    public TalkAlertDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_talk,null);
        et_input = view.findViewById(R.id.et_input);
        mCheckBox = view.findViewById(R.id.checkbox);
        send = view.findViewById(R.id.send);
        cancel = view.findViewById(R.id.cancel);
        comment_succuss_transfer = view.findViewById(R.id.comment_succuss_transfer);
        listentext = view.findViewById(R.id.listentext);
        listentext2 = view.findViewById(R.id.listentext2);
        // 获取自定义Dialog布局中的控件
        dialog = new Dialog(mContext, R.style.MyDialog);

        //adjustResize可自动调整高度
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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


    public TalkAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public TalkAlertDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){
        setData();
        setEvent();
    }


    private String mString;
    //编辑字数限制
    private int num = 4;

    private void setEvent() {

        cancel.setOnClickListener(view ->{
            KeyboardUtils.hideSoftInput(et_input);
            dialog.dismiss();

        });

        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(isneedtotrans){
                  setTextStatus(s.toString());
                }

                if(s.toString().trim().length() == 0 || s.toString().trim().length() > num){
                    setStatus(false);
                }else{
                    setStatus(true);
                }

                if(null != mTalkLisenter){
                    mTalkLisenter.talk(myPosition,et_input.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                KLog.d("tag","afterTextChanged");
                if(isneedtotrans){
                    setTextStatus(s.toString());
                }

                if(s.toString().trim().length() == 0 || s.toString().trim().length() > num){
                    setStatus(false);
                }else{
                    setStatus(true);
                }
            }
        });

        //发送 类型(文本在输入框监听时已传入)
        send.setOnClickListener(view -> {
            if (TextUtils.isEmpty(et_input.getText().toString().trim())) {
                return;
            }

            if(null != mOnIsToCircleLister){
                mOnIsToCircleLister.func(mCheckBox.isChecked());
            }

            KeyboardUtils.hideSoftInput(et_input);
            dialog.dismiss();


        });

    }


    private void setTextStatus(String s){
        //trim()是去掉首尾空格
        mString = s.toString().trim();
        if(!TextUtils.isEmpty(mString) && mString.length() != 0){

            if(mString.length() > num){
                listentext.setTextColor(Color.parseColor("#FFFF5040"));
                listentext2.setTextColor(Color.parseColor("#FFFF5040"));
            }else{
                listentext.setTextColor(Color.parseColor("#818386"));
                listentext2.setTextColor(Color.parseColor("#818386"));
            }

        }
        listentext.setText(mString.length() + "");

    }

    //状态
    private void setStatus(boolean status) {
        if(!status){
            send.setEnabled(false);
            send.setSelected(false);
            //文字透明度
            send.setTextColor(0xCC818386);
        }else{
            send.setEnabled(true);
            send.setSelected(true);
            send.setTextColor(Color.parseColor("#242629"));
        }
    }

    private void setData(){
        KeyboardUtils.showSoftInput(et_input);
    }

    public void show(){
        setLayuot();
        dialog.show();
    }

    //输入字返回
    public interface  TalkLisenter{
        void talk(int position,String words);
    }

    public TalkLisenter mTalkLisenter;

    public void setTalkLisenter(TalkLisenter talkLisenter) {
        mTalkLisenter = talkLisenter;
    }




}
