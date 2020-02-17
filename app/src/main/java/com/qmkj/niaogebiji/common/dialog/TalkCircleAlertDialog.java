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

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:
 */
public class TalkCircleAlertDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private TextView et_input;
    private TextView cancel;
    private TextView send;

    private LinearLayout comment_succuss_transfer;


    //默认是评论文章
    private int myPosition = -1;

    //文章需要转发 圈子不需要
    private boolean isneedtotrans ;

    public void setIsneedtotrans(boolean isneedtotrans) {
        this.isneedtotrans = isneedtotrans;
        if(isneedtotrans){
            comment_succuss_transfer.setVisibility(View.VISIBLE);
        }
    }

    public void setMyPosition(int myPosition) {
        this.myPosition = myPosition;
    }

    /** 回调接口 开始 */
    public OnDialogItemClickListener mOnDialogItemClickListener;

    public interface OnDialogItemClickListener{
        void func(int position);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        mOnDialogItemClickListener = onDialogItemClickListener;
    }

    /** 回调接口 结束 */

    public TalkCircleAlertDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    //点击item时获取要被评论的人
    public TalkCircleAlertDialog setHint(String name) {
        et_input.setHint("回复 "  + name);
        return this;
    }




    public TalkCircleAlertDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_talk,null);
        et_input = view.findViewById(R.id.et_input);
        send = view.findViewById(R.id.send);
        cancel = view.findViewById(R.id.cancel);
        comment_succuss_transfer = view.findViewById(R.id.comment_succuss_transfer);
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


    public TalkCircleAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public TalkCircleAlertDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){
        setData();
        setEvent();
    }

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
                if(s.toString().length() > 500){
                    Log.d("tag","输入的字数过多");
                }
                if(s.toString().length() == 0){
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

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        send.setOnClickListener(view -> {
            if (TextUtils.isEmpty(et_input.getText().toString().trim())) {
                return;
            }

            if(null != mTalkLisenter){
                mTalkLisenter.talk(myPosition,et_input.getText().toString().trim());
                KeyboardUtils.hideSoftInput(et_input);
                dialog.dismiss();
            }
        });

    }

    private void setData(){
        KeyboardUtils.showSoftInput(et_input);
    }

    public void show(){
        setLayuot();
        dialog.show();
    }


    public interface  TalkLisenter{
        void talk(int position,String words);
    }

    public TalkAlertDialog.TalkLisenter mTalkLisenter;

    public void setTalkLisenter(TalkAlertDialog.TalkLisenter talkLisenter) {
        mTalkLisenter = talkLisenter;
    }


}
