package com.qmkj.niaogebiji.module.activity;

import android.view.View;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.widget.MyWebView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:隐私界面
 */
public class SecretActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.webview)
    MyWebView mMyWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_secret;
    }

    @Override
    protected void initView() {
        tv_title.setText("隐私政策");
        mMyWebView.setDrawingCacheEnabled(false);
        mMyWebView.setLayerType(View.LAYER_TYPE_NONE, null);

        String link  = "file:///android_asset/policy/privacyPolicy.html";
        mMyWebView.loadUrl(link);
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
