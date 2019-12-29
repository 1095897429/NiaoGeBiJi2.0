package com.qmkj.niaogebiji.module.widget;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-28
 * 描述:
 */
public class NoLineCllikcSpan extends ClickableSpan {

    public NoLineCllikcSpan() {
        super();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        /**set textColor**/
        ds.setColor(BaseApp.getApplication().getResources().getColor(R.color.text_blue));
        /**Remove the underline**/
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
    }
}