package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:资料
 */
public class ThingsAdapter extends BaseQuickAdapter<RecommendBean.Article_list, BaseViewHolder> {

    public ThingsAdapter(@Nullable List<RecommendBean.Article_list> data) {
        super(R.layout.school_book_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,RecommendBean.Article_list item) {

//        if(!TextUtils.isEmpty(item.getPic())){
//            ImageUtil.load(mContext,item.getPic(),helper.getView(R.id.baidu_icon));
//        }

        helper.setText(R.id.content,item.getTitle());
        helper.setText(R.id.money,item.getPointnum());


        helper.itemView.setOnClickListener(view -> {
            RecommendBean.Article_list temp = mData.get(helper.getAdapterPosition());
            UIHelper.toDataInfoActivity(mContext,temp.getAid());
        });


    }
}

