package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:搜索人脉适配器
 */
public class PeopleItemAdapter extends BaseQuickAdapter<RegisterLoginBean.UserInfo, BaseViewHolder> {

    public PeopleItemAdapter(List<RegisterLoginBean.UserInfo> data) {
        super(R.layout.search_people_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RegisterLoginBean.UserInfo item) {

        helper.setText(R.id.sender_name,item.getName());
        helper.setText(R.id.sender_tag,item.getCompany());
        ImageUtil.load(mContext,item.getAvatar(),helper.getView(R.id.head_icon));

        helper.itemView.setOnClickListener(view -> {
            RegisterLoginBean.UserInfo temp = mData.get(helper.getAdapterPosition());
            UIHelper.toUserInfoActivity(mContext,temp.getUid());
        });
    }
}
