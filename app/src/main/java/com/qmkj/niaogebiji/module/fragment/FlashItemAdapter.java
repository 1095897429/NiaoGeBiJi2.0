package com.qmkj.niaogebiji.module.fragment;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.widget.CenterAlignImageSpan;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:recyclerView顶部固定
 */
public class FlashItemAdapter extends BaseQuickAdapter<FlashBulltinBean.BuilltinBean, BaseViewHolder> {


    public FlashItemAdapter(@Nullable List<FlashBulltinBean.BuilltinBean> data) {
        super(R.layout.item_flash,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FlashBulltinBean.BuilltinBean mBean) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.flash_priase)
                .addOnClickListener(R.id.flash_share).addOnClickListener(R.id.content_des);

        helper.addOnClickListener(R.id.part1111).addOnClickListener(R.id.part2222).addOnClickListener(R.id.part3333);

        if(helper.getAdapterPosition() == 0){
            helper.setVisible(R.id.sticky_header,false);
        }else{
            //比较当前和之前一个item的展示时间 一样则不显示
            if(mData.get(helper.getAdapterPosition() - 1).getShow_time().equals(mBean.getShow_time())){
                helper.setVisible(R.id.sticky_header,false);
            }else{
                helper.setVisible(R.id.sticky_header,true);
            }
        }
        helper.setText(R.id.content_des,mBean.getContent());

        //时间
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        ((TextView)helper.getView(R.id.time)).setTypeface(typeface);



        //拼接链接
        if(!TextUtils.isEmpty(mBean.getLink())){
            Drawable drawableLink = mContext.getResources().getDrawable(R.mipmap.icon_flash_link_pic);
            drawableLink.setBounds(0, 0, drawableLink.getMinimumWidth(), drawableLink.getMinimumHeight());
            //居中对齐imageSpan
            CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawableLink);
            SpannableString spanString2 = new SpannableString("icon");
            spanString2.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView)helper.getView(R.id.content_des)).append(spanString2);
        }else{

        }

        //图片
        if(!TextUtils.isEmpty(mBean.getPic())){
            helper.setVisible(R.id.flash_img,true);
            ImageUtil.load(mContext,mBean.getPic(),helper.getView(R.id.flash_img));
        }else{
            helper.setVisible(R.id.flash_img,false);
        }


        //时间
        Typeface typeface2= Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        ((TextView)helper.getView(R.id.zan_num)).setTypeface(typeface2);

        if(!TextUtils.isEmpty(mBean.getPub_time())){
            helper.setText(R.id.time, TimeUtils.millis2String(Long.parseLong(mBean.getPub_time())* 1000L,"HH:mm"));
        }

        if(1 == mBean.getIs_good()){
            helper.setTextColor(R.id.zan_num,mContext.getResources().getColor(R.color.zan_select));
            helper.setImageResource(R.id.zan_img,R.mipmap.icon_flash_priase_select);
        }else{
            helper.setTextColor(R.id.zan_num,mContext.getResources().getColor(R.color.zan_select_no));
            helper.setImageResource(R.id.zan_img,R.mipmap.icon_flash_priase);
        }

        if (Integer.parseInt(mBean.getGood_num()) > 0) {
            helper.setText(R.id.zan_num,mBean.getGood_num() + "");
            helper.setVisible(R.id.zan_num,true);
        } else {
            helper.setVisible(R.id.zan_num,true);
            helper.setText(R.id.zan_num,"赞");
        }


    }
}