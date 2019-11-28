package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CollectArticleBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:
 */
public class NewsCollectItemAdapter extends BaseQuickAdapter<CollectArticleBean.Collect_list, BaseViewHolder> {

    public NewsCollectItemAdapter(List<CollectArticleBean.Collect_list> data) {
        super(R.layout.item_new_collect_right_img,data);

    }

    public void updateData(int position){
        notifyItemRemoved(position);
    }



    @Override
    protected void convert(BaseViewHolder helper, CollectArticleBean.Collect_list bean) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.img_tag);
        helper.setText(R.id.one_img_title,bean.getTitle());
        //时间
        if(!TextUtils.isEmpty(bean.getCreated_at())){
            helper.setText(R.id.one_img_time, TimeUtils.millis2String(Long.parseLong(bean.getCreated_at())* 1000L,"yyyy.MM.dd"));
        }
        //作者
        helper.setText(R.id.one_img_auth,bean.getAuthor());
        ImageUtil.load(mContext,bean.getPic(),helper.getView(R.id.one_img_imgs));


    }
}
