package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:权限弹出框
 */
public class LaunchPermissDialog {

        private Context mContext;
        private Display mDisplay;
        private Dialog mDialog;
        //按钮
        private TextView doit;
        //是否显示内容
        private boolean showMsg = false;
        private ImageView icon1;
        private ImageView icon2;

        private boolean isSdcardOk;
        private boolean isPhoneok;


    public LaunchPermissDialog(Context context){
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }



    public LaunchPermissDialog(Context context, boolean isSdcardOk, boolean isPhoneok){
            this.mContext = context;
            this.isSdcardOk = isSdcardOk;
            this.isPhoneok = isPhoneok;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context
                    .WINDOW_SERVICE);
            mDisplay = windowManager.getDefaultDisplay();
        }


        public LaunchPermissDialog builder(){
            // 获取Dialog布局
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_launchpermiss, null);
            // 获取自定义Dialog布局中的控件
            doit = view.findViewById(R.id.doit);
            icon1 = view.findViewById(R.id.icon1);
            icon2 = view.findViewById(R.id.icon2);
            if(isSdcardOk){
                icon1.setImageResource(R.mipmap.icon_permission_ok);
            }

            if(isPhoneok){
                icon2.setImageResource(R.mipmap.icon_permission_ok);
            }

            // 定义Dialog布局和参数
            mDialog = new Dialog(mContext,R.style.MyDialog);
            mDialog.setContentView(view);

            // 调整dialog背景大小
            Window dialogWindow = mDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (mDisplay.getWidth() * 0.8);
            return this;
        }

    public LaunchPermissDialog setSdcardOK(){
        icon1.setImageResource(R.mipmap.icon_permission_ok);
        return this;
    }


    public LaunchPermissDialog setPhoneOK(){
        icon2.setImageResource(R.mipmap.icon_permission_ok);
        return this;
    }

    //右边按钮
    public LaunchPermissDialog setPositionButton(String text, final View.OnClickListener listener) {

        if ("".equals(text)) {
            doit.setText("立即开启");
        } else {
            doit.setText(text);
        }
        doit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
        });
        return this;
    }

        //false 点击物理返回键不消失
        public LaunchPermissDialog setCancelable(boolean cancel) {
            mDialog.setCancelable(cancel);
            return this;
        }

        //false 点击外部不消失
        public LaunchPermissDialog setCanceledOnTouchOutside(boolean b) {
            mDialog.setCanceledOnTouchOutside(b);
            return this;
        }



        //设置填充内容
        private void setLayout() {
        }

        public void show() {
            setLayout();
            mDialog.show();
        }

        public void dismiss() {
            if(mDialog.isShowing()){
                mDialog.dismiss();
            }
        }


        public boolean isShowing(){
            return mDialog.isShowing();
        }


}
