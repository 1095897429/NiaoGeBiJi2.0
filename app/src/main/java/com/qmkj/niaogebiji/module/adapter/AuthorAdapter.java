package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public class AuthorAdapter extends BaseQuickAdapter<AuthorBean.Author, BaseViewHolder> {
    public AuthorAdapter(@Nullable List<AuthorBean.Author> data) {
        super(R.layout.author_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AuthorBean.Author item) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.focus).addOnClickListener(R.id.focus_aleady);


        TextView chineseTv = helper.getView(R.id.author_name);
        TextPaint paint = chineseTv.getPaint();
        paint.setFakeBoldText(true);

        helper.setText(R.id.author_name,item.getName()).setText(R.id.author_tag,item.getSummary());

        //图片
        ImageUtil.load(mContext,item.getImg(),helper.getView(R.id.head_icon));

        //是否关注：1-关注，0-未关注
        if(0 == item.getIs_follow()){
            helper.setBackgroundRes(R.id.focus,R.drawable.bg_corners_8_yellow);
            helper.setVisible(R.id.focus,true);
            helper.setVisible(R.id.focus_aleady,false);
        }else{
            helper.setBackgroundRes(R.id.focus,R.drawable.bg_corners_8_gray);
            helper.setVisible(R.id.focus,false);
            helper.setVisible(R.id.focus_aleady,true);
        }
    }
}

