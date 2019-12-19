package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:收货地址弹框
 */
public class LocationExchangeDialog {
    private Context mContext;
    private Dialog dialog;
    private Display display;

    private TextView submit;
    private ImageView iv_back;

    private EditText name_et;
    private EditText phone_et;
    private EditText location_et;


    public class CallBean{
        public String name;
        public String phone;
        public String location;
    }


    /** 回调接口 开始 */
    public OnDialogItemClickListener mOnDialogItemClickListener;

    public interface OnDialogItemClickListener{
        void func(int position,CallBean bean);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        mOnDialogItemClickListener = onDialogItemClickListener;
    }

    /** 回调接口 结束 */



    public LocationExchangeDialog(Context context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public LocationExchangeDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_location_exchange,null);
        // 获取自定义Dialog布局中的控件
        submit = view.findViewById(R.id.submit);
        iv_back = view.findViewById(R.id.iv_back);
        location_et = view.findViewById(R.id.location_et);
        phone_et = view.findViewById(R.id.phone_et);
        name_et = view.findViewById(R.id.name_et);

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


    public LocationExchangeDialog setHeadBeanList(List<String> list){
        return this;
    }


    public LocationExchangeDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public LocationExchangeDialog setCanceledOnTouchOutside(boolean b) {
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
                CallBean bean = new CallBean();
                bean.name = name_et.getText().toString().trim();
                bean.phone = phone_et.getText().toString().trim();
                bean.location = location_et.getText().toString().trim();

                if(TextUtils.isEmpty(bean.name)){
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("收货人不能为空");
                    return;
                }

                if(TextUtils.isEmpty( bean.phone)){
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("联系方式不能为空");
                    return;
                }

                if(TextUtils.isEmpty( bean.location)){
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("收货地址不能为空");
                    return;
                }

                KeyboardUtils.hideSoftInput(submit);

                mOnDialogItemClickListener.func(1,bean);

                dialog.dismiss();

            }
        });
        iv_back.setOnClickListener(view -> {
            if(null != mOnDialogItemClickListener){
                mOnDialogItemClickListener.func(0,null);
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
        //弹出软键盘
        KeyboardUtils.showSoftInput(submit);
    }



}
