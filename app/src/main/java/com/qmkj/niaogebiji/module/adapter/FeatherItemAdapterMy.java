package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.FeatherProductBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:
 */
public class FeatherItemAdapterMy extends BaseQuickAdapter<FeatherProductBean.Productean.ProductItemBean, BaseViewHolder> {



    public FeatherItemAdapterMy(@Nullable List<FeatherProductBean.Productean.ProductItemBean> data) {
        super(R.layout.my_feather_item,data);

    }


    @Override
    protected void convert(BaseViewHolder helper, FeatherProductBean.Productean.ProductItemBean mBean) {
//        ImageUtil.load(mContext,mBean.getImg_list(),helper.getView(R.id.one_img_imgs));
//
//        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
//        ((TextView)helper.getView(R.id.name)).setTypeface(typeface);
//
//        helper.setText(R.id.name,mBean.getTitle())
//                .setText(R.id.money,mBean.getPoint());
//
//        //点击事件
        helper.itemView.setOnClickListener(v -> {
            ToastUtils.showShort("去明细页");
//            UIHelper.toFeatherListDetailActivity(mContext,mBean.getId())
        });
    }






}
