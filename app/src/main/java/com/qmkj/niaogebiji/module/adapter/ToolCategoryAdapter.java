package com.qmkj.niaogebiji.module.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ChannelBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-28
 * 描述:工具编辑中的tag
 */
public class ToolCategoryAdapter extends BaseQuickAdapter<ChannelBean, BaseViewHolder> {
    public ToolCategoryAdapter(@Nullable List<ChannelBean> data) {
        super(R.layout.edit_tool_head_tag,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChannelBean item) {

        helper.addOnClickListener(R.id.tag_name);

        helper.setText(R.id.tag_name,item.getChaname());

        TextView textView = helper.getView(R.id.tag_name);
        if(item.isSelect()){
            textView.setSelected(true);
            textView.setTextSize(14);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_first_color));
//            textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_corners_4_yellow));
        }else{
            textView.setSelected(false);
            textView.setTextSize(13);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_second_color));
//            textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_corners_4_gray));

        }
    }

}
