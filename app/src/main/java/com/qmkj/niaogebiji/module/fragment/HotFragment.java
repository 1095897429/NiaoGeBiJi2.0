package com.qmkj.niaogebiji.module.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.tab.TabLayout;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-12
 * 描述:
 */
public class HotFragment extends BaseLazyFragment {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;

    @BindView(R.id.id_rewen)
    RadioButton id_rewen;

    @BindView(R.id.id_author)
    RadioButton id_author;


    private List<ChannelBean> mChannelBeanList;
    private FirstFragmentAdapter mFirstFragmentAdapter;
    private List<String> mTitls = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();


    public static HotFragment getInstance(String chainId, String chainName) {
        HotFragment newsItemFragment = new HotFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hot_fragment;
    }

    @Override
    protected void initView() {
        //默认选中第一个
        id_rewen.setChecked(true);

        radiogroup.setOnCheckedChangeListener((group, checkedId) -> {
            // 选中
            if (id_rewen.isChecked()) {
                mViewPager.setCurrentItem(0);
            }

            if (id_author.isChecked()) {
                mViewPager.setCurrentItem(1);
            }
        });


        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;
        bean = new ChannelBean("0","一周热文");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("1","作者周榜");
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

            if(i== 0){
                HotNewsFragment fragment1 = HotNewsFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(fragment1);
            }else if(i == 1){
                HotAuthorFragment fragment1 = HotAuthorFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(fragment1);
            }

            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(getContext(),getChildFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(0 == position){
                    id_rewen.setChecked(true);
                    id_author.setChecked(false);
                }else if(1 == position){
                    id_rewen.setChecked(false);
                    id_author.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
