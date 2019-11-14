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
 * 创建时间 2019-11-13
 * 描述:资料详情
 */
public class DataInfomationActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_info;
    }

    @Override
    protected void initView() {
        tv_title.setText("资料下载");
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
