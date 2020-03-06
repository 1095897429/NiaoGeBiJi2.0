package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.ShanYanCustomInterface;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.activity.LoginActivity;
import com.socks.library.KLog;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-24
 * 描述:
 */
public class ConfigUtils {

    /**
     * 闪验三网运营商授权页配置类
     *
     * @param context
     * @return
     */
    /**
     * 闪验三网运营商授权页配置类
     *
     * @param context
     * @return
     */
    public static ShanYanUIConfig getUiConfig(Context context,String wechat_token,String loginType ) {

        /************************************************自定义控件**************************************************************/

        Button close = new Button(context);
        close.setBackgroundResource(context.getResources().getIdentifier("close_black", "drawable", context.getPackageName()));
        RelativeLayout.LayoutParams mLayoutParamsClose = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParamsClose.setMargins(0, AbScreenUtils.dp2px(context, 15), AbScreenUtils.dp2px(context, 10), 0);
        mLayoutParamsClose.width = AbScreenUtils.dp2px(context, 20);
        mLayoutParamsClose.height = AbScreenUtils.dp2px(context, 20);
        mLayoutParamsClose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        close.setLayoutParams(mLayoutParamsClose);

        //loading自定义加载框
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout view_dialog = (RelativeLayout) inflater.inflate(R.layout.shanyan_demo_dialog_layout, null);
        RelativeLayout.LayoutParams mLayoutParamsLoading = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view_dialog.setLayoutParams(mLayoutParamsLoading);
        view_dialog.setVisibility(View.GONE);

        //其他方式登录
        TextView otherTV = new TextView(context);
        otherTV.setText("其他方式登录");
        otherTV.setTextColor(0xff3a404c);
        otherTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams1.setMargins(0, AbScreenUtils.dp2px(context, 175), 0, 0);
        mLayoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        otherTV.setLayoutParams(mLayoutParams1);

        //线 需加上标题栏的高度
        View view = new View(context);
        view.setBackgroundColor(context.getResources().getColor(R.color.divider_line));
        RelativeLayout.LayoutParams mLayoutParams3 = new RelativeLayout.LayoutParams(SizeUtils.dp2px(327.5f), AbScreenUtils.dp2px(context, 0.5f));
        mLayoutParams3.setMargins(0, SizeUtils.dp2px(212 + 25), 0, 0);
        mLayoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        view.setLayoutParams(mLayoutParams3);


        //切换手机号布局
        LayoutInflater inflater0 = LayoutInflater.from(context);
        RelativeLayout relativeLayout0 = (RelativeLayout) inflater0.inflate(R.layout.shanyan_demo_change_phone, null);
        RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams0.setMargins(0, SizeUtils.dp2px(212 + 25 + 122), 0, 0);
        layoutParams0.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relativeLayout0.setLayoutParams(layoutParams0);

        relativeLayout0.findViewById(R.id.change).setOnClickListener(v -> {
            KLog.d("tag","切换手机号");
            UIHelper.toPhoneInputActivity(context,wechat_token,loginType);
        });

//        TextView tip = new TextView(context);
//        tip.setClickable(true);
//        tip.setText("切换手机号");
//        tip.setTextColor(context.getResources().getColor(R.color.text_second_color));
//        tip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        RelativeLayout.LayoutParams mLayoutParamsTip = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        mLayoutParamsTip.setMargins(0, SizeUtils.dp2px(212 + 25 + 122), 0, 0);
//        mLayoutParamsTip.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        tip.setLayoutParams(mLayoutParamsTip);


        //头部布局
        LayoutInflater inflater1 = LayoutInflater.from(context);
        RelativeLayout relativeLayout = (RelativeLayout) inflater1.inflate(R.layout.shanyan_demo_other_login_item, null);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        layoutParamsOther.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.setLayoutParams(layoutParamsOther);
        otherLogin(context,relativeLayout);


        /****************************************************设置授权页*********************************************************/
        Drawable authNavHidden = context.getResources().getDrawable(R.drawable.shanyan_demo_auth_dialog_bg);
        Drawable navReturnImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_return_bg);
//        Drawable logoImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_logo);
        Drawable logBtnImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_auth_bt);
        Drawable uncheckedImgPath = context.getResources().getDrawable(R.drawable.umcsdk_uncheck_image);
        Drawable checkedImgPath = context.getResources().getDrawable(R.drawable.umcsdk_check_image);
        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                .setDialogTheme(false, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight(), 0, 0, false)
                //授权页导航栏：
                .setNavColor(context.getResources().getColor(R.color.roseEnd))  //设置导航栏颜色
                .setNavText("ddd")  //设置导航栏标题文字
                .setNavTextColor(0xff080808) //设置标题栏文字颜色
                .setNavReturnImgPath(navReturnImgPath)  //
                .setNavReturnImgHidden(false)
                .setAuthBGImgPath(authNavHidden)
                .setAuthNavHidden(true)//设置导航栏是否隐藏（true：隐藏；false：不隐藏）

                //授权页logo（logo的层级在次底层，仅次于自定义控件）
//                .setLogoImgPath(logoImgPath)  //设置logo图片
                .setLogoWidth(108)   //设置logo宽度
                .setLogoHeight(36)   //设置logo高度
                .setLogoOffsetY(5)  //设置logo相对于标题栏下边缘y偏移
                .setLogoOffsetX(15)
                .setLogoHidden(true)   //是否隐藏logo

                //授权页号码栏：
                .setNumberColor(0xff333333)  //设置手机号码字体颜色
                .setNumFieldOffsetY(200)    //设置号码栏相对于标题栏下边缘y偏移
                .setNumberSize(28)
                .setNumFieldWidth(200)

                //授权页登录按钮：
                .setLogBtnText("本机号码一键登录")  //设置登录按钮文字
                .setLogBtnTextColor(context.getResources().getColor(R.color.text_first_color))   //设置登录按钮文字颜色
                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
                .setLogBtnOffsetY(275)   //设置登录按钮相对于标题栏下边缘y偏移
                .setLogBtnTextSize(17)
                .setLogBtnWidth(200)
                .setLogBtnHeight(56)

                //授权页隐私栏：
//                .setAppPrivacyOne("用户自定义协议条款", "https://www.253.com")  //设置开发者隐私条款1名称和URL(名称，url)
//                .setAppPrivacyTwo("用户服务条款", "https://www.253.com")  //设置开发者隐私条款2名称和URL(名称，url)
                //.setAppPrivacyColor(0xff666666, 0xff0085d0)   //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
//                .setPrivacyOffsetY(156)
                .setPrivacyOffsetBottomY(30)
                .setUncheckedImgPath(null)
                .setCheckedImgPath(null)
                .setPrivacyState(true)
//                .setPrivacyOffsetX(12)
                .setCheckBoxHidden(true)
                .setPrivacySmhHidden(true)
                .setPrivacyOffsetGravityLeft(false)
                .setPrivacyTextSize(11)
                .setPrivacyText("同意","","","","并授权闪验获取本机号码")

                //授权页slogan：
                .setSloganTextColor(0xff999999)  //设置slogan文字颜色
                .setSloganOffsetBottomY(50)
                .setSloganTextSize(9)
                .setSloganHidden(false)

                .setShanYanSloganHidden(true)//创蓝slogan

                // 添加自定义控件:
                //其他方式登录及监听，可以不写
                .addCustomView(close, true, false, new ShanYanCustomInterface() {
                    @Override
                    public void onClick(Context context, View view) {
                        Toast.makeText(context, "点击关闭", Toast.LENGTH_SHORT).show();
                    }
                })
                .addCustomView(relativeLayout, false, false, null)
                //标题栏下划线，可以不写
                .addCustomView(view, false, false, null)
                //切换手机号
                .addCustomView(relativeLayout0, false, false, null)
                //设置loading样式
                .setLoadingView(view_dialog)//设置自定义loading布局

                .build();
        return uiConfig;
    }





//    public static ShanYanUIConfig getUiConfig(Context context) {
//        /************************************************自定义控件**************************************************************/
//        //其他方式登录
//        TextView otherTV = new TextView(context);
//        otherTV.setText("手机号登陆");
//        otherTV.setTextColor(0xff3a404c);
//        otherTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//        RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        mLayoutParams1.setMargins(0, AbScreenUtils.dp2px(context, 20), 0, 0);
//        mLayoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        otherTV.setLayoutParams(mLayoutParams1);
//
//        //loading自定义加载框
//        LayoutInflater inflater = LayoutInflater.from(context);
//        RelativeLayout view_dialog = (RelativeLayout) inflater.inflate(R.layout.shanyan_demo_dialog_layout, null);
//        RelativeLayout.LayoutParams mLayoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        view_dialog.setLayoutParams(mLayoutParams3);
//        view_dialog.setVisibility(View.VISIBLE);
//
//        LayoutInflater inflater1 = LayoutInflater.from(context);
//        LinearLayout relativeLayout = (LinearLayout) inflater1.inflate(R.layout.shanyan_demo_other_login_item, null);
//        LinearLayout.LayoutParams layoutParamsOther = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParamsOther.setMargins(0, AbScreenUtils.dp2px(context, 16), 0, 0);
//        relativeLayout.setLayoutParams(layoutParamsOther);
//        otherLogin(context, relativeLayout);
//
//        //设置授权页固有控件
//        Drawable authNavHidden = context.getResources().getDrawable(R.drawable.shanyan_demo_auth_dialog_bg);
////        Drawable navReturnImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_return_bg);
////        Drawable logoImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_logo);
//        Drawable logBtnImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_auth_bt);
////        Drawable uncheckedImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_uncheck_image);
////        Drawable checkedImgPath = context.getResources().getDrawable(R.drawable.shanyan_demo_check_image);
//        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
//                .setDialogTheme(false, AbScreenUtils.getScreenWidth(context, true) - 66, 400, 0, 0, false)//设置弹窗样式
//                .setAuthBGImgPath(authNavHidden)//设置授权页背景图片
//                .setDialogDimAmount(0.6f)
//
//                //授权页导航栏：
//                .setNavColor(Color.parseColor("#ffffff"))  //设置导航栏颜色
//                .setNavText("你好")  //设置导航栏标题文字
//                .setNavTextColor(0xff080808) //设置标题栏文字颜色
////                .setNavReturnImgPath(navReturnImgPath)  //设置标题栏返回按钮图片
//                .setNavReturnBtnWidth(25)//设置标题栏返回按钮宽度
//                .setNavReturnBtnHeight(25)//设置标题栏返回按钮高度
//                .setNavReturnBtnOffsetRightX(15)//设置标题栏返回按钮据屏幕右边距
//
//                //授权页logo
////                .setLogoImgPath(logoImgPath)  //设置logo图片
//                .setLogoWidth(108)   //设置logo宽度
//                .setLogoHeight(45)   //设置logo高度
//                .setLogoOffsetY(25)  //设置logo相对于标题栏下边缘y偏移
//                .setLogoHidden(true)   //是否隐藏logo
//
//                //授权页号码栏：
//                .setNumberColor(context.getResources().getColor(R.color.text_first_color))  //设置手机号码字体颜色
//                .setNumFieldOffsetY(74)    //设置号码栏相对于标题栏下边缘y偏移
//                .setNumberSize(28)//设置手机号码字体大小
//
//                //授权页登录按钮：
//                .setLogBtnText("本机号码一键登录")  //设置登录按钮文字
//                .setLogBtnTextColor(context.getResources().getColor(R.color.text_first_color))   //设置登录按钮文字颜色
//                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
//                .setLogBtnOffsetY(140)   //设置登录按钮相对于标题栏下边缘y偏移
//                .setLogBtnTextSize(17)//设置登录按钮文字大小
//                .setLogBtnWidth(327)//设置登录按钮宽度
//                .setLogBtnHeight(56)//设置登录按钮高度
//
//                //授权页隐私栏：
////                .setAppPrivacyOne("闪验用户协议", "https://api.253.com/api_doc/yin-si-zheng-ce/wei-hu-wang-luo-an-quan-sheng-ming.html")  //设置隐私条款1名称和URL(名称，url)
////                .setAppPrivacyTwo("闪验隐私政策", "https://api.253.com/api_doc/yin-si-zheng-ce/ge-ren-xin-xi-bao-hu-sheng-ming.html")  //设置隐私条款2名称和URL(名称，url)
////                .setAppPrivacyColor(0xff666666, 0xff0085d0)   //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
////                .setPrivacyOffsetBottomY(20)//设置隐私条款相对于屏幕下边缘y偏
////                .setUncheckedImgPath(uncheckedImgPath)//设置隐私栏复选框未选中时背景
////                .setCheckedImgPath(checkedImgPath)//设置隐私栏复选框选中时背景
////                .setPrivacyState(false)//设置隐私条款
//
//                //授权页slogan：
////                .setSloganTextColor(0xff999999)  //设置slogan文字颜色
////                .setSloganOffsetY(104)  //设置slogan相对于标题栏下边缘y偏移
////                .setSloganTextSize(9)//设置slogan文字大小
//                //设置loading样式
//                .setLoadingView(view_dialog)//设置自定义loading布局
//                // 添加自定义控件
//                .addCustomView(relativeLayout, false, false, null) // 添加自定义控件布局
//                .build();
//        return uiConfig;
//
//    }


    private static void otherLogin(final Context context, RelativeLayout relativeLayout) {
        ImageView back = relativeLayout.findViewById(R.id.icon_back);
        back.setOnClickListener(v -> {
            OneKeyLoginManager.getInstance().finishAuthActivity();
            OneKeyLoginManager.getInstance().removeAllListener();
        });

    }

}
