package com.qmkj.niaogebiji.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.KeyboardUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.adapter.TopicFocusAdapter;
import com.qmkj.niaogebiji.module.adapter.TopicSelectAdapter;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.qmkj.niaogebiji.module.event.SearchCleanEvent;
import com.qmkj.niaogebiji.module.event.UpdateTopicEvent;
import com.qmkj.niaogebiji.module.fragment.TopicFocusFragment;
import com.qmkj.niaogebiji.module.fragment.TopicSelectFragment;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-13
 * 描述:话题选择
 */
public class TopSelectActivity extends BaseActivity {


    @BindView(R.id.tools_scrlllview)
    ScrollView scrollView;

    @BindView(R.id.tools)
    LinearLayout toolsLayout;

    @BindView(R.id.goods_pager)
    VerticalViewPager goods_pager;

    View icon_select;


    @BindView(R.id.search_first)
    EditText search_first;

    @BindView(R.id.ll_auto_results)
    LinearLayout ll_auto_results;

    @BindView(R.id.ll_manual_result)
    LinearLayout ll_manual_result;


    private TextView toolsTextViews[];
    private View views[];
    private int currentItem = 0;
    private LayoutInflater inflater;
    private int scrllViewWidth = 0, scrollViewMiddle = 0;
    private FirstFragmentAdapter shopAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();

    //测试数据
//    private String toolsList[] = new String[]{"常用分类", "热门", "品牌男装", "内衣配饰", "家用电器", "手机数码", "电脑办公", "个护化妆", "母婴频道", "食物生鲜", "酒水饮料", "家居家纺", "整车车品", "鞋靴箱包", "运动户外", "图书", "玩具乐器", "钟表", "居家生活", "珠宝饰品", "音像制品", "家具建材", "计生情趣", "营养保健", "奢侈礼品", "生活服务", "旅游出行"};

    private String toolsList[];
    //搜索部分
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;


    List<TopicBean> list =  new ArrayList<>();
    TopicFocusAdapter mTopicFocusAdapter;
    String typename;

    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mTopicFocusAdapter = new TopicFocusAdapter(list);
        mRecyclerView.setAdapter(mTopicFocusAdapter);
        //禁用change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_list;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    public void initFirstData() {
        getTopicCate();
    }


    private List<TopicBean> mTopicBeanList;
    private void getTopicCate() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getTopicCate(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<TopicBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<TopicBean>> response) {

                        mTopicBeanList = response.getReturn_data();
                        if(null != mTopicBeanList && !mTopicBeanList.isEmpty()){
                            toolsList = new String[mTopicBeanList.size()];
                            for (int i = 0; i < mTopicBeanList.size(); i++) {
                                toolsList[i] = mTopicBeanList.get(i).getTitle();
                            }

                            showToolsView();
                            initPager();
                        }
                    }

                });
    }

    @Override
    protected void initView() {
        //设置适配器
        inflater = LayoutInflater.from(this);


        initLayout();

        //软键盘 -- 搜索
        search_first.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                String content = search_first.getText().toString().trim();
                if(TextUtils.isEmpty(content)){
                    KLog.d("不能搜索空内容");
                    return true;
                }

                KLog.d("tag","搜索内容:" + content);

//                getData();

                searchTopic(content);

            }
            return false;
        });
    }

    private List<TopicBean> mSearchTopicList;
    private void searchTopic(String keyword) {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",keyword);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchTopic(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<TopicBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<TopicBean>> response) {

                        mSearchTopicList = response.getReturn_data();
                        getData();
                    }

                    @Override
                    public void onNetFail(String msg) {

                    }
                });
    }


    private void getData() {
        KeyboardUtils.hideSoftInput(ll_manual_result);

//        list.clear();
//        TopicBean bean;
//        for (int i = 0; i < 10; i++) {
//            bean = new TopicBean();
//            bean.setName(typename);
//            list.add(bean);
//        }

        ll_auto_results.setVisibility(View.GONE);
        ll_manual_result.setVisibility(View.VISIBLE);

        if(mSearchTopicList != null && !mSearchTopicList.isEmpty()){
            mRecyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            mTopicFocusAdapter.setNewData(mSearchTopicList);
        }else{
            ll_empty.setVisibility(View.VISIBLE);
            ll_empty.findViewById(R.id.iv_empty).setBackgroundResource(R.mipmap.icon_empty_comment);
            ((TextView) ll_empty.findViewById(R.id.tv_empty)).setText("没有搜索到相关话题~");
            mRecyclerView.setVisibility(View.GONE);
        }

    }



    /**
     * initPager<br/>
     * 初始化ViewPager控件相关内容
     */
    private void initPager() {
        mFragmentList.clear();
        for (int i = 0; i < toolsList.length; i++) {
            TopicSelectFragment fragment1 = TopicSelectFragment.getInstance(toolsList[i],
                    mTopicBeanList.get(i).getId() + "");
            mFragmentList.add(fragment1);
            mTitls.add(toolsList[i]);
        }
        shopAdapter = new FirstFragmentAdapter(this,getSupportFragmentManager(), mFragmentList, mTitls);
        goods_pager.setOffscreenPageLimit(toolsList.length);
        goods_pager.setAdapter(shopAdapter);
        goods_pager.setOnPageChangeListener(onPageChangeListener);
    }


    /**
     * OnPageChangeListener<br/>
     * 监听ViewPager选项卡变化事的事件
     */

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            if (goods_pager.getCurrentItem() != arg0){
                goods_pager.setCurrentItem(arg0);
            }
            if (currentItem != arg0) {
                changeTextColor(arg0);
                changeTextLocation(arg0);
            }
            currentItem = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    /**
     * 改变栏目位置
     *
     * @param clickPosition
     */
    private void changeTextLocation(int clickPosition) {

        int x = (views[clickPosition].getTop() - getScrollViewMiddle() + (getViewheight(views[clickPosition]) / 2));
        scrollView.smoothScrollTo(0, x);
    }

    /**
     * 返回scrollview的中间位置
     *
     * @return
     */
    private int getScrollViewMiddle() {
        if (scrollViewMiddle == 0){
            scrollViewMiddle = getScrollViewheight() / 2;
        }
        return scrollViewMiddle;
    }

    /**
     * 返回ScrollView的宽度
     *
     * @return
     */
    private int getScrollViewheight() {
        if (scrllViewWidth == 0){
            scrllViewWidth = scrollView.getBottom() - scrollView.getTop();
        }
        return scrllViewWidth;
    }

    /**
     * 返回view的宽度
     *
     * @param view
     * @return
     */
    private int getViewheight(View view) {
        return view.getBottom() - view.getTop();
    }


    /**
     * 动态生成显示items中的textview
     */
    private void showToolsView() {
        toolsTextViews = new TextView[toolsList.length];
        views = new View[toolsList.length];

        for (int i = 0; i < toolsList.length; i++) {
            View view = inflater.inflate(R.layout.item_left_layout, null);
            view.setId(i);
            view.setOnClickListener(toolsItemListener);
            TextView textView = view.findViewById(R.id.text);

            textView.setText(toolsList[i]);
            toolsLayout.addView(view);
            toolsTextViews[i] = textView;
            views[i] = view;
        }
        changeTextColor(0);
    }


    private View.OnClickListener toolsItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goods_pager.setCurrentItem(v.getId());
        }
    };


    /**
     * 改变textView 和 背景颜色的颜色
     *
     * @param id
     */
    private void changeTextColor(int id) {
        for (int i = 0; i < toolsTextViews.length; i++) {
            if (i != id) {
                views[i].findViewById(R.id.left_icon).setVisibility(View.GONE);
                views[i].setBackgroundResource(R.color.bg_color);
                toolsTextViews[i].setTextColor(getResources().getColor(R.color.text_second_color));
            }
        }

        views[id].findViewById(R.id.left_icon).setVisibility(View.VISIBLE);
        views[id].setBackgroundResource(android.R.color.white);
        toolsTextViews[id].setTextColor(getResources().getColor(R.color.text_first_color));
    }



    @OnClick({
            R.id.iv_back,
            R.id.search_part
    })
    public void clicks(View view) {
        if (StringUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.search_part:

                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    /** 选中的话题 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(UpdateTopicEvent event){
        KLog.d("tag","选择的话题是：" + event.getTitle());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("topicName",event.getTitle());
        bundle.putSerializable("topicId",event.getTopicId());
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }


    //点击删除回调的事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchBackEvent(SearchCleanEvent event) {
        if(null != this){
            ll_auto_results.setVisibility(View.VISIBLE);
            ll_manual_result.setVisibility(View.GONE);
        }
    }

}