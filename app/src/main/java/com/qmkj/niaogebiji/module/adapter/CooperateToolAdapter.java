package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-27
 * 描述:
 */
public class CooperateToolAdapter extends BaseQuickAdapter<ToolBean, BaseViewHolder> {


    public CooperateToolAdapter(List<ToolBean> data) {
        super(R.layout.item_tool_cooperate,data);

    }

    @Override
    protected void convert(BaseViewHolder helper, ToolBean bean) {

        if(!TextUtils.isEmpty(bean.getIcon())){
            ImageUtil.load(mContext,bean.getIcon(),helper.getView(R.id.head_icon));
        }


        //标题
        helper.setText(R.id.toolTitle,bean.getTitle());
    }

}
