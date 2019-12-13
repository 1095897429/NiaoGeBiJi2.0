package com.qmkj.niaogebiji.module.activity;

import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
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
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.tab.TabLayout;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.CategoryAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolCategoryAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.event.ToolChangeEvent;
import com.qmkj.niaogebiji.module.fragment.ArcitleCollectionListFragment;
import com.qmkj.niaogebiji.module.fragment.FocusAuthorListFragment;
import com.qmkj.niaogebiji.module.fragment.ToolCollectionListFragment;
import com.qmkj.niaogebiji.module.fragment.ToolRecommentListFragment;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-28
 * 描述:工具编辑界面
 *
 *         ChannelBean channelBean;
 *         for (int i = 0; i < tags.length; i++) {
 *             channelBean = new ChannelBean();
 *             channelBean.setChaname(tags[i]);
 *             if(0 == i){
 *                 channelBean.setSelect(true);
 *             }
 *             mList.add(channelBean);
 *         }
 *         mCategoryAdapter.setNewData(mList);
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

    private List<ToollndexBean> cates = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tool_edit;
    }

    @Override
    protected void initView() {

        getToolCate();

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

        setUpTabLayout();

        initLayout();



    }


    private void getToolCate() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getToolCate(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ToollndexBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<ToollndexBean>> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        cates = response.getReturn_data();
                        setData();
                    }
                });
    }

    private void setData() {

        //手动添加 全部
        ToollndexBean bean = new ToollndexBean();
        bean.setSelect(true);
        bean.setTitle("全部");
        bean.setId(0 + "");
        cates.add(0,bean);

        mCategoryAdapter.setNewData(cates);
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
                ToolRecommentListFragment fragment1 = ToolRecommentListFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(fragment1);
            }else if(i == 1){
                ToolCollectionListFragment fragment1 = ToolCollectionListFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(fragment1);
            }

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
        mCategoryAdapter = new ToolCategoryAdapter(cates);
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

            String cateId = mCategoryAdapter.getData().get(position).getId();

            //发送事件
            EventBus.getDefault().post(new ToolChangeEvent(position,cateId));
        });


    }


}
