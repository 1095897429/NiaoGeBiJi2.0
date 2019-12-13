package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-28
 * 描述:工具编辑中的tag
 */
public class ToolCategoryAdapter extends BaseQuickAdapter<ToollndexBean, BaseViewHolder> {
    public ToolCategoryAdapter(@Nullable List<ToollndexBean> data) {
        super(R.layout.edit_tool_head_tag,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ToollndexBean item) {

        helper.addOnClickListener(R.id.tag_name);

        helper.setText(R.id.tag_name,item.getTitle());

        TextView textView = helper.getView(R.id.tag_name);
        TextPaint paint = textView.getPaint();

        if(item.isSelect()){
            textView.setSelected(true);
            textView.setTextSize(14);
            paint.setFakeBoldText(true);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_first_color));
        }else{
            textView.setSelected(false);
            textView.setTextSize(13);
            paint.setFakeBoldText(false);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_second_color));

        }
    }

}
