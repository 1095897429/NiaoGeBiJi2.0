package com.qmkj.niaogebiji.module.widget;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:自定义加载更多样式
 */
public class MyLoadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.layout_loading;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.loding;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.loding_fail;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.loding_complete;
    }
}
