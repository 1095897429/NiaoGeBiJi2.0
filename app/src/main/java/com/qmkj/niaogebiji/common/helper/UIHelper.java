package com.qmkj.niaogebiji.common.helper;

import android.content.Context;
import android.content.Intent;

import com.qmkj.niaogebiji.module.activity.CategoryActivity;
import com.qmkj.niaogebiji.module.activity.DataInfomationActivity;
import com.qmkj.niaogebiji.module.activity.HomeActivity;
import com.qmkj.niaogebiji.module.activity.LoginActivity;
import com.qmkj.niaogebiji.module.activity.MoringNewsListActivity;
import com.qmkj.niaogebiji.module.activity.PhoneInputActivity;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.activity.SearchActivity;
import com.qmkj.niaogebiji.module.activity.VertifyCodeActivity;
import com.qmkj.niaogebiji.module.activity.WelcomeActivity;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:
 */
public class UIHelper {

    /** 打开主界面 */
    public static void toHomeActivity(Context ctx, int type) {
        Intent intent = new Intent(ctx, HomeActivity.class);
//        intent.putExtra("type",type);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }


    /** 打开欢迎界面 */
    public static void toWelcomeActivity(Context ctx) {
        Intent intent = new Intent(ctx, WelcomeActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开登录界面 */
    public static void toLoginActivity(Context ctx) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开输入手机界面 */
    public static void toPhoneInputActivity(Context ctx) {
        Intent intent = new Intent(ctx, PhoneInputActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开验证码界面 */
    public static void toVertifyCodeActivity(Context ctx) {
        Intent intent = new Intent(ctx, VertifyCodeActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开搜索结果界面 */
//    public static void toSearchResultActivity(Context ctx,String keyword) {
//        Intent intent = new Intent(ctx, SearchResultActivity.class);
//        intent.putExtra("keyword",keyword);
//        ctx.startActivity(intent);
//    }

      /** 打开搜索界面 */
    public static void toSearchActivity(Context ctx) {
        Intent intent = new Intent(ctx, SearchActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开早报界面 */
    public static void toMoringActivity(Context ctx) {
        Intent intent = new Intent(ctx, MoringNewsListActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开分类界面 */
    public static void toCategoryActivity(Context ctx) {
        Intent intent = new Intent(ctx, CategoryActivity.class);
        ctx.startActivity(intent);
    }

    /** 打开资料详情界面 */
    public static void toDataInfoActivity(Context ctx) {
        Intent intent = new Intent(ctx, DataInfomationActivity.class);
        ctx.startActivity(intent);
    }


    /** 打开图片预览界面 */
    public static void toPicPreViewActivity(Context ctx) {
        Intent intent = new Intent(ctx, PicPreviewActivity.class);
        ctx.startActivity(intent);
    }






}
