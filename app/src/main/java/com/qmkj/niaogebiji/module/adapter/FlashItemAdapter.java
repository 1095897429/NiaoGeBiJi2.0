package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.widget.CenterAlignImageSpan;
import com.qmkj.niaogebiji.module.widget.CustomImageSpan;
import com.qmkj.niaogebiji.module.widget.HorizontalSpacesDecoration;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:recyclerView顶部固定
 */
public class FlashItemAdapter extends BaseQuickAdapter<FlashBulltinBean.BuilltinBean, BaseViewHolder> {

    private CirclePicAdapter mCirclePicAdapter;

    private  int i ;
    private  Rect rect ;
    private int j;
    private  Rect firstAndLastRect;


    public FlashItemAdapter(@Nullable List<FlashBulltinBean.BuilltinBean> data) {
        super(R.layout.item_flash_v2,data);
    }


    @Override
    protected void convert(BaseViewHolder helper, FlashBulltinBean.BuilltinBean mBean) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.flash_priase)
                .addOnClickListener(R.id.flash_share).addOnClickListener(R.id.content_des);

        helper.addOnClickListener(R.id.part2222).addOnClickListener(R.id.part3333);


        //旧的隐藏 -- 让第一个item的布局不显示
        if(helper.getAdapterPosition() == 0){
            helper.setVisible(R.id.sticky_header,true);
        }else{
            //比较当前和之前一个item的展示时间 一样则不显示
            if(mData.get(helper.getAdapterPosition() - 1).getShow_time().equals(mBean.getShow_time())){
                helper.setVisible(R.id.sticky_header,false);
            }else{
                helper.setVisible(R.id.sticky_header,true);
            }
        }



        //头条 || 顶置
//        if("1".equals(mBean.getTop())){
//            helper.setVisible(R.id.dingzhi,true);
//        }else{
//            helper.setVisible(R.id.dingzhi,false);
//        }
//
//        helper.setText(R.id.title,mBean.getTitle());



        //TODO 3.24 修改icon  头条 || 顶置
        if("1".equals(mBean.getTop())){
            //居中对齐imageSpan
            CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_flash_dingzhi,2);
            SpannableString spanString2 = new SpannableString("icon  " + mBean.getTitle());
            spanString2.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            helper.setText(R.id.title,spanString2);
        }else{
            //标题
            helper.setText(R.id.title,mBean.getTitle());
        }



        //showTime --几月几号 目前后台返回的是 12/25 的形式
//        helper.setText(R.id.header_textview,mBean.getShow_time());
        //星期几
//        helper.setText(R.id.header_textview_weekend,mBean.getShow_time());

        helper.setText(R.id.content_des,mBean.getContent());

        //时间
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        ((TextView)helper.getView(R.id.time)).setTypeface(typeface);



        //图片
//        String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";



        //图片
        String pic_type =  mBean.getPic_type();
        if("1".equals(pic_type)){
            helper.setVisible(R.id.ll_pic_3,false);

            String pcc = mBean.getPic();
            if(!TextUtils.isEmpty(pcc)){
                helper.setVisible(R.id.ll_part1,true);
            }else{
                helper.setVisible(R.id.ll_part1,false);
            }


        }else if("2".equals(pic_type) || "3".equals(pic_type)){
            helper.setVisible(R.id.ll_part1,false);
            helper.setVisible(R.id.ll_pic_3,true);
        }


        if("1".equals(pic_type)){
            ImageUtil.load(mContext,mBean.getPic()  ,helper.getView(R.id.one_img));
            ArrayList<String> onePics = new ArrayList<>();
            onePics.add(mBean.getPic());
            helper.getView(R.id.one_img).setOnClickListener(v -> {

//                UIHelper.toPicPreViewActivity(mContext, onePics, 0,true);

                UIHelper.toPicPreViewActivityOld(mContext,  onePics,0,true);
            });
        }else if("2".equals(pic_type) || "3".equals(pic_type)) {
            ArrayList<String> mPics = new ArrayList<>();
            if (!TextUtils.isEmpty(mBean.getPic3())) {
                mPics.add(mBean.getPic() );
                mPics.add(mBean.getPic2() );
                mPics.add(mBean.getPic3() );
            } else {
                mPics.add(mBean.getPic() );
                mPics.add(mBean.getPic2() );
            }
            //二级评论布局
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, mPics.size());
            RecyclerView recyclerView = helper.getView(R.id.pic_recyler);
            recyclerView.setLayoutManager(layoutManager);

            //TODO 如果没有加这个，这个会导致重复添加
            if (firstAndLastRect == null) {
                i = SizeUtils.dp2px(8);
                rect = new Rect(0, 0, i, 0);
                j = SizeUtils.dp2px(0);
                firstAndLastRect = new Rect(j, 0, i, 0);
                HorizontalSpacesDecoration spacesDecoration = new HorizontalSpacesDecoration(rect, firstAndLastRect);
                recyclerView.addItemDecoration(spacesDecoration);

            }

            CirclePicAdapter mCirclePicAdapter = new CirclePicAdapter(mPics);
            mCirclePicAdapter.setTotalSize(mPics.size());
            //禁用change动画
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(mCirclePicAdapter);


            //预览事件
            mCirclePicAdapter.setOnItemClickListener((adapter, view, position) -> {
                KLog.d("tag", "点击预览");
                if(mPics != null && !mPics.isEmpty()){

//                    UIHelper.toPicPreViewActivity(mContext, mPics, position,true);

                    UIHelper.toPicPreViewActivityOld(mContext,  mPics,position,true);
                }
            });

        }


        //拼接链接
        if(!TextUtils.isEmpty(mBean.getLink())){
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    KLog.d("tag","点击了快讯link " + mBean.getLink());
                    UIHelper.toWebViewActivityWithOnLayout(mContext,mBean.getLink(),"");

//                    UIHelper.toNewWebView(mContext, mBean.getLink());

                }
            };
            Drawable drawableLink = mContext.getResources().getDrawable(R.mipmap.icon_flash_link_pic);
            drawableLink.setBounds(0, 0, drawableLink.getMinimumWidth(), drawableLink.getMinimumHeight());
            //居中对齐imageSpan
            CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_flash_link_pic,2);
            SpannableString spanString2 = new SpannableString("icon");
            spanString2.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString2.setSpan(clickableSpan,0,4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView)helper.getView(R.id.content_des)).append(spanString2);
            ((TextView)helper.getView(R.id.content_des)).setMovementMethod(LinkMovementMethod.getInstance());
        }else{

        }




        //时间
        Typeface typeface2= Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        ((TextView)helper.getView(R.id.zan_num)).setTypeface(typeface2);

        if(!TextUtils.isEmpty(mBean.getPub_time())){
            helper.setText(R.id.time, TimeUtils.millis2String(Long.parseLong(mBean.getPub_time())* 1000L,"HH:mm"));
        }

        //拿到它的publiceTime -- 显示日期 -- 再显示星期
        if(!TextUtils.isEmpty(mBean.getPub_time())){
            String temp = TimeUtils.millis2String(Long.parseLong(mBean.getPub_time())* 1000L,"yyyy-MM-dd HH:mm:ss");
            helper.setText(R.id.header_textview, TimeUtils.millis2String(Long.parseLong(mBean.getPub_time())* 1000L,"MM月dd日"));
//            KLog.e("tag","创建时间 " +  temp);

            String string = TimeUtils.getChineseWeek(temp);
            helper.setText(R.id.header_textview_weekend, string);
//            KLog.e("tag","星期： " +  string);
        }



        if(1 == mBean.getIs_good()){
            helper.setTextColor(R.id.zan_num,mContext.getResources().getColor(R.color.zan_select));
            helper.setImageResource(R.id.zan_img,R.mipmap.icon_flash_priase_select);
        }else{
            helper.setTextColor(R.id.zan_num,mContext.getResources().getColor(R.color.zan_select_no));
            helper.setImageResource(R.id.zan_img,R.mipmap.icon_flash_priase);
        }

        if(!TextUtils.isEmpty(mBean.getGood_num())){
            if (Integer.parseInt(mBean.getGood_num()) > 0) {
                helper.setText(R.id.zan_num,mBean.getGood_num() + "");
                helper.setVisible(R.id.zan_num,true);
            } else {
                helper.setVisible(R.id.zan_num,true);
                helper.setText(R.id.zan_num,"赞");
            }
        }
    }


}