package com.qmkj.niaogebiji.common.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.event.MyEvent;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-03
 * 描述:视频的懒加载基类
 * 1.12.18 晚 多个fragment基本上都走了onViewCreate生命周期 -- 可以初始化一些界面的数据(initData函数中请求方法)
 */
public abstract class BaseLazyFragment extends Fragment {

    public static final String TAG = "BaseLazyFragment";
    //上下文
    protected Context mContext;
    //ButterKnife
    private Unbinder mUnbinder;
    //错误布局
    private ViewStub emptyView;
    //fragment是否可见
    protected boolean isVisible;
    //控件是否已经初始化完成
    protected boolean isPrepared;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        KLog.e(TAG,getClass().getSimpleName() + " -- onAttach");
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
            KLog.e(TAG,getClass().getSimpleName() + " -- setUserVisibleHint -- " +isVisibleToUser);
        } else {
            isVisible = false;
            onInvisible();
//            KLog.e(TAG,getClass().getSimpleName() + " -- setUserVisibleHint -- " +isVisibleToUser);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KLog.e(TAG,getClass().getSimpleName() + " -- onCreateView");
        View view = getLayoutInflater().inflate(getLayoutId(),null);
        mUnbinder = ButterKnife.bind(this,view);
        if(regEvent()){
            EventBus.getDefault().register(this);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KLog.e(TAG,getClass().getSimpleName() + " -- onViewCreated");
        initView();
        finishCreateView();
        initData();
    }



    @Override
    public void onDestroy() {
        KLog.e(TAG,getClass().getSimpleName() + " -- onDestroy");
        super.onDestroy();
        //解绑
        if(null != mUnbinder){
            mUnbinder.unbind();
            mUnbinder = null;
        }
        //解绑
        if (regEvent()) {
            EventBus.getDefault().unregister(this);
        }
    }

    /** 子类接受事件 重写该方法 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventThread(MyEvent event){
        KLog.d("tag","envent");
    }


    /** 默认加载数据 */
    protected void initData() {
        KLog.e(TAG,getClass().getSimpleName() + " -- initData");
    }

    /** 第一次都会走这里，不过后续通过Fragment是否可见判断 */
    public void finishCreateView() {
        isPrepared = true;

        //TODO 2019.12.18 晚 -- 通过debug，发现问题：第一个中的lazyload请求方法会加载 -- 没有注释下面的方法
        //TODO 2019.12.18 晚 -- 分析：当将第二个作为首展示页，那么第一个大概率的会走 懒加载方法
//        lazyLoad();
    }

    protected void onInvisible() {

    }

    protected void onVisible() {
        lazyLoad();
    }


    /** 懒加载 */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        lazyLoadData();
        isPrepared = false;
    }


    protected void lazyLoadData() {
        KLog.e(TAG,getClass().getSimpleName() + " -- 懒加载数据开始");
    }



    /** 需要接收事件 重写该方法 并返回true */
    protected boolean regEvent(){
        return false;
    }


    protected abstract int getLayoutId();

    protected abstract void initView();




    //点赞
    protected void goodBulletin(String flash_id) {
        Map<String,String> map = new HashMap<>();
        map.put("type",1 +"");
        map.put("id",flash_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().goodBulletin(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        changePriaseStatus();
                    }
                });
    }

    //取赞
    protected void cancleGoodBulletin(String flash_id) {
        Map<String,String> map = new HashMap<>();
        map.put("type",1 +"");
        map.put("id",flash_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().cancleGoodBulletin(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        changePriaseStatus();
                    }
                });
    }


    //子类可以重写
    protected void changePriaseStatus() {

    }


}
