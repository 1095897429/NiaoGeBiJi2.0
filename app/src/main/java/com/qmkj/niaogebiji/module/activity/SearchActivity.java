package com.qmkj.niaogebiji.module.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.KeyboardUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.History;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.db.DBManager;
import com.qmkj.niaogebiji.module.event.LookMoreEvent;
import com.qmkj.niaogebiji.module.event.SearchCleanEvent;
import com.qmkj.niaogebiji.module.fragment.ActionFragment;
import com.qmkj.niaogebiji.module.fragment.CircleRecommendFragment;
import com.qmkj.niaogebiji.module.fragment.FirstItemFragment;
import com.qmkj.niaogebiji.module.fragment.FlashFragment;
import com.qmkj.niaogebiji.module.fragment.PeopletemFragment;
import com.qmkj.niaogebiji.module.fragment.SearchAllFragment;
import com.qmkj.niaogebiji.module.widget.tab1.ViewPagerTitle;
import com.qmkj.niaogebiji.module.widget.tab2.ViewPagerTitleSlide;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:搜索界面
 */
public class SearchActivity extends BaseActivity {


    @BindView(R.id.part1111)
    LinearLayout part1111;

    @BindView(R.id.part2222)
    LinearLayout part2222;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.et_input)
    EditText et_input;

    @BindView(R.id.ll_history)
    LinearLayout ll_history;

    @BindView(R.id.flowlayout_history)
    TagFlowLayout flowlayout_history;

    @BindView(R.id.pager_title)
    ViewPagerTitleSlide pager_title;


    List<History> mList1 = new ArrayList<>();

    @BindView(R.id.flowlayout)
    TagFlowLayout mTagFlowLayout;

    private String defaultHotKey;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }


    @Override
    protected void initView() {

        pager_title.initData(titile,mViewPager,0);

        //显示弹框
        KeyboardUtils.showSoftInput(et_input);

        searchIndex();

       List<History> temps =  DBManager.getInstance().queryHistory();
       if(null != temps && !temps.isEmpty()){
           mList1.addAll(temps);
           initHistoryData();
           ll_history.setVisibility(View.VISIBLE);
       }

        et_input.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                //点击搜索的时候隐藏软键盘
                KeyboardUtils.hideSoftInput(et_input);
                //存储数据库
                String myKeyword = v.getEditableText().toString().trim();
                //如果输入文本框为空，则默认热搜第一个此
                if(TextUtils.isEmpty(myKeyword)){
                    if(mHot_searches != null && !mHot_searches.isEmpty()){
                        myKeyword = defaultHotKey;
                    }
                }
                insertToDb(myKeyword);
                // 在这里写搜索的操作,一般都是网络请求数据
                toSearch(myKeyword);
                return true;
            }
            return false;
        });
    }

    private void toSearch(String keyword) {
        part2222.setVisibility(View.VISIBLE);
        part1111.setVisibility(View.GONE);
        et_input.setText(keyword);
        et_input.setSelection(keyword.length());

        //初始化tab数据
        initPartData2();
    }




    @Override
    public void initData() {

    }

    public void insertToDb(String tag){
        History history = DBManager.getInstance().queryHistory(tag);
        if(null != history){
            history.setName(tag);
            DBManager.getInstance().updateHistory(history);
        }else{
            history = new History();
            history.setId(DBManager.getInstance().queryHistory().size() + 1);
            history.setName(tag);
            DBManager.getInstance().insertHistory(history);
        }
    }


    private void initHistoryData() {
        flowlayout_history.removeAllViews();
        flowlayout_history.setAdapter(new TagAdapter<History>(mList1) {
            @Override
            public View getView(FlowLayout parent, int position, History history) {
                LinearLayout ll = (LinearLayout) LayoutInflater.from(SearchActivity.this).inflate(R.layout.tag_layout,mTagFlowLayout,false);
                TextView textView = ll.findViewById(R.id.tag_name);
                textView.setText(history.getName());
                textView.setOnClickListener(v -> {
                    KeyboardUtils.hideSoftInput(et_input);
                    toSearch(textView.getText().toString());
                });
                return ll;
            }
        });
    }


    @OnClick({R.id.cancel, R.id.delete_history})
    public void functions(View view){
        KeyboardUtils.hideSoftInput(et_input);
        if(view.getId() == R.id.cancel){
            finish();
        }else if(view.getId() == R.id.delete_history){
            showDeleteHistory();
        }
    }

    public void showDeleteHistory(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("清理", v -> {
            //清除历史，刷新界面
            DBManager.getInstance().deleteHistory();
            flowlayout_history.removeAllViews();
            mList1.clear();
            ll_history.setVisibility(View.GONE);
        }).setNegativeButton("取消", v -> {}).setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    /** --------------------------------- 热搜  ---------------------------------*/
    private SearchBean mSearchBean;
    private List<SearchBean.Hot_search> mHot_searches;
    private SearchBean.Hot_search mHot_search;

    private void searchIndex() {

        //测试
//        mHot_searches = new ArrayList<>();
//        for (int i = 0; i < mVals.length; i++) {
//            mHot_search = new SearchBean.Hot_search();
//            mHot_search.setSearch_string(mVals[i]);
//            mHot_searches.add(mHot_search);
//        }
//        initTagData();


        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().searchIndex(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<SearchBean> response) {

                        mSearchBean = response.getReturn_data();
                        if(null != mSearchBean){
                            mHot_searches = mSearchBean.getHot_search();
                            initTagData();
                            //TODO 10.12设置第一个热搜此默认显示在文本上
                            if(null != mHot_searches && !mHot_searches.isEmpty()){
                                defaultHotKey = mHot_searches.get(0).getSearch_string();
                                et_input.setHint(defaultHotKey);
                            }
                        }
                    }
                });
    }

    private void initTagData() {
        mTagFlowLayout.removeAllViews();
        mTagFlowLayout.setAdapter(new TagAdapter<SearchBean.Hot_search>(mHot_searches) {
            @Override
            public View getView(FlowLayout parent, int position, SearchBean.Hot_search bean) {
                LinearLayout ll = (LinearLayout) LayoutInflater.from(SearchActivity.this).inflate(R.layout.tag_layout,mTagFlowLayout,false);
                TextView textView = ll.findViewById(R.id.tag_name);
                textView.setText(bean.getSearch_string());
                textView.setOnClickListener(v -> {
                    Log.d("tag","我点击的是: " + bean.getSearch_string() + " 随后存入到数据库中 ");

                    //隐藏软键盘
                    KeyboardUtils.hideSoftInput(et_input);

                    insertToDb(bean.getSearch_string());

                    toSearch(bean.getSearch_string());

                });
                return ll;
            }
        });
    }


    // 点击空白区域 自动隐藏软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super .onTouchEvent(event);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchBackEvent(SearchCleanEvent event) {
        if(null != this){
            part1111.setVisibility(View.VISIBLE);
            part2222.setVisibility(View.GONE);
            et_input.setHint(defaultHotKey);
            //① 更新历史记录
            List<History> temps =  DBManager.getInstance().queryHistory();
            if(null != temps && !temps.isEmpty()){
                mList1.clear();
                mList1.addAll(temps);
                initHistoryData();
                ll_history.setVisibility(View.VISIBLE);
            }
        }
    }


    /** ---------------------------------  搜索结果  ---------------------------------*/
    //Fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();
    //存储频道集合
    private List<ChannelBean> mChannelBeanList;
    //适配器
    private FirstFragmentAdapter mFirstFragmentAdapter;

    String [] titile = new String[]{"全部","干货","活动","快讯","资料","工具","动态"};


    private void initPartData2(){
        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;
        bean = new ChannelBean("0","全部");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("1","干货");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("2","活动");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("3","快讯");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("4","资料");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("5","工具");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("6","动态");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("7","作者");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("8","人脉");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("9","课程");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("10","百科");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("11","测试");
        mChannelBeanList.add(bean);


        if(null != mChannelBeanList){
            setUpAdater();
        }
    }



    private void setUpAdater() {
        mFragmentList.clear();
        mTitls.clear();
        for (int i = 0; i < mChannelBeanList.size(); i++) {
            if(mChannelBeanList.get(i).getChaname().equals("全部")){
                SearchAllFragment searchAllFragment = SearchAllFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(searchAllFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("干货")){
                FirstItemFragment firstItemFragment = FirstItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(firstItemFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("活动")){
                ActionFragment actionFragment = ActionFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(actionFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("快讯")){
                FlashFragment flashFragment = FlashFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(flashFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("资料")){
                FirstItemFragment firstItemFragment = FirstItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(firstItemFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("动态")){
                CircleRecommendFragment circleRecommendFragment = CircleRecommendFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(circleRecommendFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("人脉")){
                PeopletemFragment peopletemFragment = PeopletemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(peopletemFragment);
            }else{
                FirstItemFragment newsItemFragment = FirstItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(newsItemFragment);
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


    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLookMoreEvent(LookMoreEvent event) {
        int position = event.getCurrentPosition();
        mViewPager.setCurrentItem(position);
    }


}
