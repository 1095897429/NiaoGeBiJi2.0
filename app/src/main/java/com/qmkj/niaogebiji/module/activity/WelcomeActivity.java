package com.qmkj.niaogebiji.module.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SPUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:
 */
public class WelcomeActivity extends BaseActivity {


    @BindView(R.id.btn)
    TextView btn;

    @BindView(R.id.viewpager)
    ViewPager viewpager;


    private ViewPagerAdapter viewPagerAdapter;

    //底部小图片数组
    private ImageView[] dotArray;
    //图片列表数据源
    private List<View> views;
    //小圆点id
    private int[] ids={R.id.dot1,R.id.dot2,R.id.dot3,R.id.dot4};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {
        initViewPage();
        initDot();

    }

    //初始化小圆点的id
    private void initDot() {
        dotArray = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dotArray[i] =  findViewById(ids[i]);
        }
    }


    //初始化viewpager
    private void initViewPage() {

        LayoutInflater inflater=LayoutInflater.from(this);
        views =new ArrayList<>();
        //构造View1
        View view = inflater.inflate(R.layout.guide_1,null);
        views.add(view);
        views.add(inflater.inflate(R.layout.guide_2,null));
        views.add(inflater.inflate(R.layout.guide_3,null));
        views.add(inflater.inflate(R.layout.guide_4,null));
        viewPagerAdapter = new ViewPagerAdapter(views);
        viewpager.setAdapter(viewPagerAdapter);


        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                // 设置底部小点选中状态
                for(int i = 0;i<ids.length;i ++){
                    if(position==i){
                        dotArray[i].setImageResource(R.mipmap.icon_welcome_select);
                    }else {
                        dotArray[i].setImageResource(R.mipmap.icon_welcome_default);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    @OnClick({R.id.btn})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.btn:
                SPUtils.getInstance().put("isFirstCome",true);
                UIHelper.toLoginActivity(WelcomeActivity.this);
                finish();
                break;
            default:
        }
    }

}
