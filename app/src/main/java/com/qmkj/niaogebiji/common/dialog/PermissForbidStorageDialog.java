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
 * 创建时间 2019-11-08
 * 描述:
 */
public class PermissForbidStorageDialog {
    
        private Context mContext;
        private Display mDisplay;
        private Dialog mDialog;
        //内容
        private TextView txt_msg;
        //前往设置
        private TextView gotoSetting;
        //退出
        private TextView exit;
        //是否显示内容
        private boolean showMsg = false;


        private CallBack callBack;

        public interface CallBack {
            void forward();

            void exit();
        }

        public void setCallBack(CallBack callBack) {
            this.callBack = callBack;
        }


        public PermissForbidStorageDialog(Context context){
            this.mContext = context;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context
                    .WINDOW_SERVICE);
            mDisplay = windowManager.getDefaultDisplay();
        }

        public PermissForbidStorageDialog builder(){
            // 获取Dialog布局
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_permissforbid, null);
            // 获取自定义Dialog布局中的控件
            txt_msg =  view.findViewById(R.id.txt_msg);
            gotoSetting = view.findViewById(R.id.gotoSetting);
            exit = view.findViewById(R.id.exit);

            gotoSetting.setOnClickListener(view1 -> {
                if (callBack != null) {
                    callBack.forward();
                }
                mDialog.dismiss();
            });


            exit.setOnClickListener(view1 -> {
                if (callBack != null) {
                    callBack.exit();
                }
                mDialog.dismiss();
            });

            // 定义Dialog布局和参数
            mDialog = new Dialog(mContext,R.style.MyDialog);
            mDialog.setContentView(view);

            // 调整dialog背景大小
            Window dialogWindow = mDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//            lp.width = (int) (mDisplay.getWidth() * 0.8);

            return this;
        }


        //false 点击物理返回键不消失
        public PermissForbidStorageDialog setCancelable(boolean cancel) {
            mDialog.setCancelable(cancel);
            return this;
        }

        //false 点击外部不消失
        public PermissForbidStorageDialog setCanceledOnTouchOutside(boolean b) {
            mDialog.setCanceledOnTouchOutside(b);
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


        public boolean isShowing(){
            return mDialog.isShowing();
        }


}
