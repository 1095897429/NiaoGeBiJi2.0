package com.qmkj.niaogebiji.module.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.CategoryAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolRecommentItemAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.TestNewBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.event.ToolHomeChangeEvent;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

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
 * 创建时间 2019-11-21
 * 描述: 工具搜索
 */
public class ToolSearchActivity extends BaseActivity {

    @BindView(R.id.et_input)
    EditText et_input;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    ToolRecommentItemAdapter mAdapter;
    //集合
    List<ToolBean> mList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tool_search;
    }

    @Override
    protected void initView() {

        //显示弹框
        KeyboardUtils.showSoftInput(et_input);

        initLayout();

    }


    @Override
    public void initData() {
    }

    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mAdapter = new ToolRecommentItemAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        initEvent();

    }

    private void initEvent() {


        et_input.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                //点击搜索的时候隐藏软键盘
                KeyboardUtils.hideSoftInput(et_input);
                //存储数据库
                String myKeyword = v.getEditableText().toString().trim();
                // 在这里写搜索的操作,一般都是网络请求数据
                toSearch(myKeyword);
                return true;
            }
            return false;
        });


        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.tool_collect:
                    KLog.d("tag","toast 收藏 or 不收藏");
                    ToolBean bean = mAdapter.getData().get(position);
                    if(bean.isSave()){
                        bean.setSave(false);
                    }else{
                        bean.setSave(true);
                    }

                    mAdapter.notifyItemChanged(position);

                    //发送更改主界面的数据源
                    EventBus.getDefault().post(new ToolHomeChangeEvent("改变主界面数据"));

                    break;
                default:
            }
        });
    }


    @OnClick({R.id.cancel})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(et_input);
        switch (view.getId()){
            case R.id.cancel:
                finish();
                break;
            default:
        }
    }




    private void toSearch(String keyword) {
        et_input.setText(keyword);
        et_input.setSelection(keyword.length());

        searchTool(keyword);
    }

    private void getData() {

        ToolBean channelBean;
        for (int i = 0; i < 10; i++) {
            channelBean = new ToolBean();
            mList.add(channelBean);
        }

        mAdapter.setNewData(mList);
    }

    private void searchTool(String keyword) {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",keyword + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchTool(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ToolBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<ToolBean>> httpResponse) {
                        KLog.d("tag",httpResponse.getReturn_data());

                        mList = httpResponse.getReturn_data();

                        setData();
                    }

                    //{"return_code":"200","return_msg":"success","return_data":{}} -- 后台空集合返回{}，那么会出现解析异常，在这里所判断
                    @Override
                    public void onNetFail(String msg) {
                        if("解析错误".equals(msg)){
                            setData();
                        }
                    }
                });
    }

    private void setData() {
        if(!mList.isEmpty()){
            mAdapter.setNewData(mList);
        }else{
            View emptyView = LayoutInflater.from(this).inflate(R.layout.activity_empty,null);
            mAdapter.setEmptyView(emptyView);
            ((TextView)emptyView.findViewById(R.id.tv_empty)).setText("没有搜索结果哦～");
        }
    }


}
