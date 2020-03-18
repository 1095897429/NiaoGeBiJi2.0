package com.qmkj.niaogebiji.module.activity;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.tab.TabLayout;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.fragment.ActionFragment;
import com.qmkj.niaogebiji.module.fragment.ArcitleCollectionListFragment;
import com.qmkj.niaogebiji.module.fragment.FirstItemFragment;
import com.qmkj.niaogebiji.module.fragment.FocusAuthorListFragment;
import com.qmkj.niaogebiji.module.fragment.FocusFragment;
import com.qmkj.niaogebiji.module.fragment.HotNewsFragment;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-25
 * 描述:我的收藏列表
 */
public class MyCollectionListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private List<ChannelBean> mChannelBeanList;
    private List<String> mTitls = new ArrayList<>();
    private FirstFragmentAdapter mFirstFragmentAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initView() {
        tv_title.setText("我的收藏");

        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;
        bean = new ChannelBean("0","收藏文章");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("1","关注作者");
        mChannelBeanList.add(bean);

        if(null != mChannelBeanList){
            setUpAdater();
        }

        setUpTabLayout();

    }


    private void setUpTabLayout() {

        //设置指示器颜色
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.yellow));
        //设置可滑动模式
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //设置指示器的高度
        mTabLayout.setSelectedTabIndicatorHeight(SizeUtils.dp2px(4f));
        //设置选中状态
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if(null != view){
                    TextView textView = view.findViewById(R.id.tv_header);
                    textView.setTextSize(17);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    textView.setTextColor(getResources().getColor(R.color.text_news_title_color));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view=tab.getCustomView();
                if(null != view){
                    TextView textView=view.findViewById(R.id.tv_header);
                    textView.setTextSize(16);
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);

        //设置自定义tab,这个需要在setupWithViewPager方法后
        for (int i = 0; i < mTabLayout.getTabCount(); i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.tool_item_tablyout, null);
                TextView textView=view.findViewById(R.id.tv_header);
                textView.setText(mChannelBeanList.get(i).getChaname());
                tab.setCustomView(view);
            }
        }
        //设置第二个为选中状态时的tab文字颜色
        View view = mTabLayout.getTabAt(0).getCustomView();
        TextView textView = view.findViewById(R.id.tv_header);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        textView.setSelected(true);
    }



    private void setUpAdater() {

        mFragmentList.clear();
        mTitls.clear();
        for (int i = 0; i < mChannelBeanList.size(); i++) {
            if(i == 0){
                ArcitleCollectionListFragment fragment1 = ArcitleCollectionListFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(fragment1);
            }else if(i == 1){
                FocusAuthorListFragment fragment2 = FocusAuthorListFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(fragment2);
            }

            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(this,getSupportFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(0);

        //设置事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                KLog.d("tag", "选中的位置 ：" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
