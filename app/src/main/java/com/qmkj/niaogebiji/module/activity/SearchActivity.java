package com.qmkj.niaogebiji.module.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.History;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.db.DBManager;
import com.socks.library.KLog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

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

    @BindView(R.id.et_input)
    EditText et_input;


    @BindView(R.id.flowlayout_history)
    TagFlowLayout flowlayout_history;

    List<History> mList1 = new ArrayList<>();

    @BindView(R.id.flowlayout)
    TagFlowLayout mTagFlowLayout;

    private String[] mVals = new String[]{"小程序","APP推广1","小程序","APP推广2","小程序","APP推广3","小程序","APP推广4"};



    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }


    @Override
    protected void initView() {

        //显示弹框
        KeyboardUtils.showSoftInput(et_input);

        searchIndex();

       List<History> temps =  DBManager.getInstance().queryHistory();
       if(null != temps && !temps.isEmpty()){
           mList1.addAll(temps);
           initHistoryData();
       }

        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    //点击搜索的时候隐藏软键盘
                    KeyboardUtils.hideSoftInput(et_input);
                    //存储数据库
                    String myKeyword = v.getEditableText().toString().trim();
                    if(!TextUtils.isEmpty(myKeyword)){

                        insertToDb(myKeyword);
                        // 在这里写搜索的操作,一般都是网络请求数据
                        toSearch(myKeyword);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void toSearch(String keyword) {
//        UIHelper.toSearchResultActivity(SearchActivity.this,keyword);
        finish();
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
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KeyboardUtils.hideSoftInput(et_input);
                        toSearch(textView.getText().toString());
                    }
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

        }).setNegativeButton("取消", v -> {}).setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }






    /** --------------------------------- 热搜  ---------------------------------*/
    private SearchBean mSearchBean;
    private List<SearchBean.Hot_search> mHot_searches;
    private SearchBean.Hot_search mHot_search;

    private void searchIndex() {

        //测试
        mHot_searches = new ArrayList<>();
        for (int i = 0; i < mVals.length; i++) {
            mHot_search = new SearchBean.Hot_search();
            mHot_search.setSearch_string(mVals[i]);
            mHot_searches.add(mHot_search);
        }
        initTagData();


//        Map<String,String> map = new HashMap<>();
//        String result = RetrofitHelper.commonParam(map);
//
//        RetrofitHelper.getApiService().searchIndex(result)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(new BaseObserver<HttpResponse<SearchBean>>() {
//                    @Override
//                    public void onSuccess(HttpResponse<SearchBean> response) {
//
//                        KLog.e("tag",response.getReturn_code());
//                        mSearchBean = response.getReturn_data();
//                        if(null != mSearchBean){
//                            mHot_searches = mSearchBean.getHot_search();
//                            initTagData();
//
//                            //TODO 10.12设置第一个热搜此默认显示在文本上
//                            if(null != mHot_searches && !mHot_searches.isEmpty()){
//                                et_input.setHint(mHot_searches.get(0).getSearch_string());
//                            }
//
//                        }
//
//                    }

//                });
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
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super .onTouchEvent(event);
    }



}
