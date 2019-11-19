package com.qmkj.niaogebiji.module.activity;

import android.view.View;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:用户协议界面
 */
public class UserAgreeActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.text)
    TextView text;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_secret;
    }

    @Override
    protected void initView() {
        tv_title.setText("用户协议");
        text.setText(R.string.user_text);
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
