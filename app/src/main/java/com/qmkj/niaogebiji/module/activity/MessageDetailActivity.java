package com.qmkj.niaogebiji.module.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-10-15
 * 描述:系统消息详情
 */
public class MessageDetailActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tv_title;


    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.content)
    TextView content;

    MessageAllH5Bean.MessageH5Bean mMessageItemBean;

    String myContents;

    String link;

    @Override
    protected int getLayoutId() {
        return R.layout.message_detail_activity;
    }

    @Override
    protected void initView() {

        tv_title.setText("消息详情");

        mMessageItemBean = (MessageAllH5Bean.MessageH5Bean) getIntent().getExtras().getSerializable("bean");
        if(null != mMessageItemBean){
            title.setText(mMessageItemBean.getRelatedtitle());
            content.setText(mMessageItemBean.getNote());

            myContents = mMessageItemBean.getNote();
            int first = myContents.indexOf("领取链接：");
            int last = myContents.indexOf("提取码");
            link =  myContents.substring(first + 5,last);
            //默认显示 http://xxxx.com
            KLog.d("tag","链接是  " + link);

            SpannableString spannableString = new SpannableString(myContents);
            ForegroundColorSpan fCs1 = new ForegroundColorSpan(Color.parseColor("#82A9C1"));

            spannableString.setSpan(fCs1,first + 5 ,last, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            ClickableSpan user_ll = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    ((TextView)view).setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));

                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            };


            spannableString.setSpan(user_ll, first + 5,last, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //必须设置才能响应点击事件
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setText(spannableString);

        }
    }


    @OnClick({R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }
}
