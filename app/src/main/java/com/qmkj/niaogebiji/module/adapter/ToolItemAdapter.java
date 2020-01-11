package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.activity.CircleMakeActivity;
import com.qmkj.niaogebiji.module.bean.MulMediaFile;
import com.qmkj.niaogebiji.module.bean.MultiToolNewsBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.xzh.imagepicker.bean.MediaFile;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:
 */
public class ToolItemAdapter extends BaseQuickAdapter<ToollndexBean, BaseViewHolder> {

    public ToolItemAdapter(List<ToollndexBean> data) {
        super(R.layout.tool_pic_more_index,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ToollndexBean item) {

        if(!TextUtils.isEmpty(item.getIcon())){
            ImageUtil.load(mContext,item.getIcon(),helper.getView(R.id.baidu_icon));
        }

        if(item.getResid() != 0){
            ImageView imageView = helper.getView(R.id.baidu_icon);
            imageView.setImageResource(R.mipmap.icon_tool_more);
        }

        TextView textView = helper.getView(R.id.tool_txt);
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
        helper.setText(R.id.tool_txt,item.getTitle());
    }
}
