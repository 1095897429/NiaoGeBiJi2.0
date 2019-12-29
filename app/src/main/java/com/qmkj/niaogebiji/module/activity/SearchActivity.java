package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.History;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.db.DBManager;
import com.qmkj.niaogebiji.module.event.LookMoreEvent;
import com.qmkj.niaogebiji.module.event.SearchCleanEvent;
import com.qmkj.niaogebiji.module.event.SearchWordEvent;
import com.qmkj.niaogebiji.module.fragment.SearchActicleItemFragment;
import com.qmkj.niaogebiji.module.fragment.SeachThinsItemFragment;
import com.qmkj.niaogebiji.module.fragment.SearchAllFragment;
import com.qmkj.niaogebiji.module.fragment.SearchAuthorItemFragment;
import com.qmkj.niaogebiji.module.fragment.SearchBaiDuItemFragment;
import com.qmkj.niaogebiji.module.fragment.SearchCircleFragment;
import com.qmkj.niaogebiji.module.fragment.SearchPeopleItemFragment;
import com.qmkj.niaogebiji.module.widget.tab2.ViewPagerTitleSlide;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
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

    private String searchKeyWord;

    //当前显示fragment的索引
    private int myPosition;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }


    @SuppressLint("ClickableViewAccessibility")
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

        //编辑框设置触摸监听
        et_input.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                // 再次点击显示光标
                et_input.setCursorVisible(true);
            }
            return false;
        });

        et_input.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

                MobclickAgentUtils.onEvent(UmengEvent.index_search_searchbtn_2_0_0);

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
                searchKeyWord = myKeyword;
                insertToDb(myKeyword);
                // 在这里写搜索的操作,一般都是网络请求数据

                if(myPosition == 0){
                    toSearch(myKeyword);
                }else{
                    EventBus.getDefault().post(new SearchWordEvent(searchKeyWord,myPosition));
                }


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

        et_input.setCursorVisible(false);

        //初始化tab数据
        initPartData2();

        //发送事件
        EventBus.getDefault().post(new SearchWordEvent(keyword));
    }




    @Override
    public void initData() {

    }

    public void insertToDb(String tag){
        History history = DBManager.getInstance().queryHistory(tag);
        if(null != history){
            history.setName(tag);
            history.setTime(System.currentTimeMillis());
            DBManager.getInstance().updateHistory(history);
        }else{
            history = new History();
            history.setId(DBManager.getInstance().queryHistory().size() + 1);
            history.setName(tag);
            history.setTime(System.currentTimeMillis());
            DBManager.getInstance().insertHistory(history);
        }
    }


    private void initHistoryData() {
        //只取10个元素
        if(mList1 != null && !mList1.isEmpty() && mList1.size() > 10){
            mList1 = mList1.subList(0,10);
        }
        flowlayout_history.removeAllViews();
        flowlayout_history.setAdapter(new TagAdapter<History>(mList1) {
            @Override
            public View getView(FlowLayout parent, int position, History history) {
                LinearLayout ll = (LinearLayout) LayoutInflater.from(SearchActivity.this).inflate(R.layout.tag_layout,mTagFlowLayout,false);
                TextView textView = ll.findViewById(R.id.tag_name);
                //手动判断
                String name = history.getName();
                if(name.length() > 7){
                    name = name.substring(0,7).trim() +"...";
                }
                textView.setText(name);
                textView.setOnClickListener(v -> {

                    searchKeyWord = history.getName().trim();
                    KeyboardUtils.hideSoftInput(et_input);
                    toSearch(history.getName().trim());
                    //更新时间
                    insertToDb(history.getName().trim());

                    //发送事件
                    EventBus.getDefault().post(new SearchWordEvent(searchKeyWord));
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

            MobclickAgentUtils.onEvent(UmengEvent.index_search_clear_2_0_0);

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

    private void searchIndex() {

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
                            //TODO 10.12设置第一个热搜此默认显示在文本上
                            if(null != mHot_searches && !mHot_searches.isEmpty()){
                                defaultHotKey = mHot_searches.get(0).getSearch_string();
                                et_input.setHint(defaultHotKey);
                            }
                            mHot_searches = mHot_searches.subList(1,mHot_searches.size());
                            initTagData();
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

                    KLog.d("tag","埋点 " + "index_search_hot"  + (position + 1) + "_2_0_0");
                    MobclickAgentUtils.onEvent("index_search_hot"  + (position + 1) + "_2_0_0");

                    //隐藏软键盘
                    KeyboardUtils.hideSoftInput(et_input);

                    searchKeyWord = bean.getSearch_string();
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



    //点击删除回调的事件
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

            //更新tablayout
            mViewPager.setCurrentItem(0);
            pager_title.setCurrentItem(0);
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

    String [] titile = new String[]{"全部","干货","人脉","动态","百科","资料","作者"};


    private void initPartData2(){
        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;
        bean = new ChannelBean("0","全部");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("1","干货");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("2","人脉");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("3","动态");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("4","百科");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("5","资料");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("6","作者");
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
                        searchKeyWord);
                mFragmentList.add(searchAllFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("干货")){
                SearchActicleItemFragment firstItemFragment = SearchActicleItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(firstItemFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("人脉")){
                SearchPeopleItemFragment actionFragment = SearchPeopleItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(actionFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("动态")){
                SearchCircleFragment flashFragment = SearchCircleFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(flashFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("百科")){
                SearchBaiDuItemFragment firstItemFragment = SearchBaiDuItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(firstItemFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("资料")){
                SeachThinsItemFragment newsThinsItemFragment = SeachThinsItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(newsThinsItemFragment);
            }else if(mChannelBeanList.get(i).getChaname().equals("作者")){
                SearchAuthorItemFragment peopletemFragment = SearchAuthorItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(peopletemFragment);
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
                myPosition =  position;
                KLog.d("tag", "选中的位置 ：" + position);
                switch (position){
                    case 0:
                        MobclickAgentUtils.onEvent(UmengEvent.index_search_all_2_0_0);
                        break;
                    case 1:
                        MobclickAgentUtils.onEvent(UmengEvent.index_search_index_2_0_0);
                        break;

                    case 2:
                        MobclickAgentUtils.onEvent(UmengEvent.index_search_person_2_0_0);
                        break;

                    case 3:
                        MobclickAgentUtils.onEvent(UmengEvent.index_search_weibo_1_2_0_0);
                        break;

                    case 4:
                        MobclickAgentUtils.onEvent(UmengEvent.index_search_wiki_2_0_0);
                        break;

                    case 5:
                        MobclickAgentUtils.onEvent(UmengEvent.index_search_resource_2_0_0);
                        break;
                    case 6:
                        MobclickAgentUtils.onEvent(UmengEvent.index_search_author_2_0_0);
                        break;

                    default:
                }

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
