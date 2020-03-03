package com.qmkj.niaogebiji.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.activity.CooperationActivity;
import com.qmkj.niaogebiji.module.adapter.CooperateToolAdapter;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.widget.StarBar;
import com.socks.library.KLog;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:文章评分弹框
 */
public class ToolDialog {
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



    public ToolDialog(Activity context){
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (setStatusTopTextLightColor()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setStatusBarDarkMode(true, (Activity) mContext);
            } else{
                //使用默认的白色系,这里需要用此方式切换
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getStatusTopColor());
        }
    }

    public int getStatusTopColor() {
        return Color.TRANSPARENT;
    }

    public boolean setStatusTopTextLightColor() {
        return true;
    }

    public void setStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public ToolDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_tool,null);
        rl_tools = view.findViewById(R.id.rl_tools);
        dialog = new Dialog(mContext, R.style.MyDialog);
        dialog.setContentView(view);
        //默认设置
        dialog.setCanceledOnTouchOutside(false);
        // 调整dialog背景大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 1.0f);
        lp.height = SizeUtils.dp2px(430);
        lp.gravity = Gravity.TOP;
        dialogWindow.setAttributes(lp);

        initLayout();

        return this;
    }








    public ToolDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }



    public ToolDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }


    private void setLayuot(){

        setData();
        setEvent();
    }


    public ToolDialog setList(List<ToolBean> lisss) {
        mLists = lisss;
        mCooperateToolAdapter.setNewData(mLists);
        return this;
    }


    RecyclerView rl_tools;

    //适配器
    private CooperateToolAdapter mCooperateToolAdapter;
    //组合集合
    private List<ToolBean> mLists = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private void initLayout() {
        mGridLayoutManager = new GridLayoutManager(mContext,4);
        //设置布局管理器
        rl_tools.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mCooperateToolAdapter = new CooperateToolAdapter(mLists);
        rl_tools.setAdapter(mCooperateToolAdapter);
        ((SimpleItemAnimator)rl_tools.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        rl_tools.setNestedScrollingEnabled(true);
        rl_tools.setHasFixedSize(true);

        mCooperateToolAdapter.setOnItemClickListener((adapter, view, position) -> {

            ToolBean temp = mLists.get(position);
            if("0".equals(temp.getType())){
                //外链
                String link = temp.getUrl();
                if(!TextUtils.isEmpty(link)){
                    UIHelper.toCooperationActivity((Activity) mContext,temp.getUrl());
                }
            }else if("1".equals(temp.getType())){
                //小程序
                String appid = temp.getUrl();
                if(!TextUtils.isEmpty(appid)){
                    toJumpWX(appid);
                }
            }
        });
    }

    private void toJumpWX(String appId) {
        //小程序跳转
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constant.WXAPPKEY);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = appId; // 填小程序原始id -- 后台传递的
        req.path = ""; //拉起小程序页面的可带参路径，不填默认拉起小程序首页 /pages/media是固定的
        req.miniprogramType = Integer.valueOf("0");// 可选打开 开发版，体验版 和 正式版0
        api.sendReq(req);

    }



    private void setEvent() {


//        iv_back.setOnClickListener(view -> {
//            if(null != mOnDialogItemClickListener){
//                mOnDialogItemClickListener.func(0,1);
//                KeyboardUtils.hideSoftInput(submit);
//                dialog.dismiss();
//            }
//        });
    }

    private void setData(){

    }

    public void show(){
        setLayuot();
        dialog.show();
    }



}
