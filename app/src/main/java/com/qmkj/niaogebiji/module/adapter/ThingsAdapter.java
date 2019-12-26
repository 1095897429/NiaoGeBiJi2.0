package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
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

        if(!TextUtils.isEmpty(item.getPic())){
            ImageUtil.load(mContext,item.getPic(),helper.getView(R.id.img_1));
        }


        helper.setText(R.id.content,item.getTitle());

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        TextView point = helper.getView(R.id.data_link_num_feather);
        TextView point11 = helper.getView(R.id.num_feather_text);
        point.setTypeface(typeface);
        point11.setTypeface(typeface);
        point.setText(item.getPointnum());

        //学习人数
        TextView num = helper.getView(R.id.tag);
        num.setText(StringUtil.formatPeopleNum(item.getViewnum()) + "次下载");


        helper.itemView.setOnClickListener(view -> {
            RecommendBean.Article_list temp = mData.get(helper.getAdapterPosition());
            UIHelper.toDataInfoActivity(mContext,temp.getAid());
        });


    }
}

