package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:搜索全部中的资料
 */
public class DataItemAdapter extends BaseQuickAdapter<RecommendBean.Article_list, BaseViewHolder> {

    public DataItemAdapter(List<RecommendBean.Article_list> data) {
        super(R.layout.search_data_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendBean.Article_list item) {

        helper.setText(R.id.content,item.getTitle());

        ImageUtil.load(mContext,item.getPic(),helper.getView(R.id.img_1));
    }
}
