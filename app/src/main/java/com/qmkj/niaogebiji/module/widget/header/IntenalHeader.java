package com.qmkj.niaogebiji.module.widget.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-05
 * 描述:
 */
public class IntenalHeader extends InternalAbstract implements RefreshHeader {

    //动画视图
    private ImageView header;

    public IntenalHeader(Context context, @LayoutRes int resource) {
        this(context, null,resource);
    }

    public IntenalHeader(Context context, AttributeSet attrs, @LayoutRes int resource) {
        this(context, attrs, 0,resource);
    }

    public IntenalHeader(Context context, AttributeSet attrs, int defStyleAttr,@LayoutRes int resource) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, resource, this);
        header = view.findViewById(R.id.image);

        ImageUtil.load(getContext(),R.mipmap.loading,header);
    }

}

