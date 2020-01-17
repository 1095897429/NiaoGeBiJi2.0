package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.lang.reflect.Type;
import java.util.List;

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

    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    @Override
    protected void convert(BaseViewHolder helper,SchoolBean.SchoolBook item) {
        //Argument must not be null -- 控件不对
        if(!TextUtils.isEmpty(item.getImage_url())){
            ImageUtil.load(mContext,item.getImage_url() + scaleSize,helper.getView(R.id.img_1));
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

