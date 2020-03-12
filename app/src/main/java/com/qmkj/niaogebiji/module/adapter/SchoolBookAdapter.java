package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.event.UpdateRecommendTopicFocusListEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:学院课程
 */
public class SchoolBookAdapter extends BaseQuickAdapter<SchoolBean.SchoolBook, BaseViewHolder> {

    public SchoolBookAdapter(@Nullable List<SchoolBean.SchoolBook> data) {
        super(R.layout.school_book_item_1,data);
    }

//    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";
//    String scaleSize = "?imageMogr2/auto-orient/format/jpg/ignore-error/1/thumbnail/!40p/imageslim";


    @Override
    protected void convert(BaseViewHolder helper,SchoolBean.SchoolBook item) {
        //Argument must not be null -- 控件不对
        if(!TextUtils.isEmpty(item.getImage_url())){
            ImageUtil.load(mContext,item.getImage_url() + Constant.scaleSize,helper.getView(R.id.img_1));
        }

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        TextView money = helper.getView(R.id.money);
        TextView money$ = helper.getView(R.id.money_tag);
        money$.setTypeface(typeface);
        money.setTypeface(typeface);

        helper.setText(R.id.content,item.getTitle());
        money.setText(item.getPrice());
        helper.setText(R.id.tag,item.getNum() + "人学习");

    }

}

