package com.qmkj.niaogebiji.module.activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.KeyboardUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.tabs.TabLayout;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.CategoryAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolCategoryAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.event.ToolChangeEvent;
import com.qmkj.niaogebiji.module.fragment.ArcitleCollectionListFragment;
import com.qmkj.niaogebiji.module.fragment.FocusAuthorListFragment;
import com.qmkj.niaogebiji.module.fragment.ToolRecommentListFragment;
import com.socks.library.KLog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-28
 * 描述:工具编辑界面
 */
public class ToolEditActivity extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    //适配器
    ToolCategoryAdapter mCategoryAdapter;
    //集合
    List<ChannelBean> mList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private List<ChannelBean> mChannelBeanList;
    private List<String> mTitls = new ArrayList<>();
    private FirstFragmentAdapter mFirstFragmentAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();

    private String [] tags = new String[]{"全部","新媒体运营","活动运营","推广"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tool_edit;
    }

    @Override
    protected void initView() {


        tv_title.setText("编辑我的工具");

        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;
        bean = new ChannelBean("0","推荐工具");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("1","我收藏的工具");
        mChannelBeanList.add(bean);

        if(null != mChannelBeanList){
            setUpAdater();
        }


        initLayout();

        ChannelBean channelBean;
        for (int i = 0; i < tags.length; i++) {
            channelBean = new ChannelBean();
            channelBean.setChaname(tags[i]);
            if(0 == i){
                channelBean.setSelect(true);
            }
            mList.add(channelBean);
        }
        mCategoryAdapter.setNewData(mList);


    }


    private void setUpAdater() {

        mFragmentList.clear();
        mTitls.clear();
        for (int i = 0; i < mChannelBeanList.size(); i++) {
            ToolRecommentListFragment fragment1 = ToolRecommentListFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                    mChannelBeanList.get(i).getChaname());
            mFragmentList.add(fragment1);

            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(this,getSupportFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(0);

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



    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCategoryAdapter = new ToolCategoryAdapter(mList);
        mRecyclerView.setAdapter(mCategoryAdapter);
        initEvent();
    }


    private void initEvent() {

        mCategoryAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的索引 " + position);

            for (int i = 0; i < mCategoryAdapter.getData().size(); i++) {
                mCategoryAdapter.getData().get(i).setSelect(false);
            }

            mCategoryAdapter.getData().get(position).setSelect(true);
            mCategoryAdapter.notifyDataSetChanged();

            String name = mCategoryAdapter.getData().get(position).getChaname();

            //发送事件
            EventBus.getDefault().post(new ToolChangeEvent(position,name));
        });


    }


}
