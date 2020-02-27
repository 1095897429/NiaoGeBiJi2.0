package com.qmkj.niaogebiji.module.activity;

import android.view.View;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;

import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-26
 * 描述:招合作
 */
public class CooperationActivity extends BaseActivity {

    @Override
    public void initFirstData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cooperation;
    }

    @Override
    protected void initView() {

    }


    @OnClick({
            R.id.iv_back, R.id.icon_cooperate_close,
            R.id.icon_tool

    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.icon_tool:

                break;
            case R.id.icon_cooperate_close:
            case R.id.iv_back:
                finish();
                //参数一：Activity1进入动画，参数二：Activity2退出动画
                overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_exit_bottom);
                break;
            default:
        }
    }

}
