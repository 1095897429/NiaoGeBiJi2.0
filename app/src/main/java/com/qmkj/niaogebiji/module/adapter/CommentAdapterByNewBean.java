package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:1级评论适配器
 */
public class CommentAdapterByNewBean extends BaseQuickAdapter<CommentBeanNew, BaseViewHolder> {

    public CommentAdapterByNewBean(@Nullable List<CommentBeanNew> data) {
        super(R.layout.first_comment_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,CommentBeanNew item) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.comment_delete)
                .addOnClickListener(R.id.ll_has_second_comment)
                .addOnClickListener(R.id.comment_priase)
                .addOnClickListener(R.id.toSecondComment);

        User_info userInfo = item.getUser_info();

        helper.setText(R.id.nickname,userInfo.getName());

        helper.setText(R.id.name_tag,userInfo.getCompany_name());

        ImageUtil.load(mContext,userInfo.getAvatar(),helper.getView(R.id.head_icon));

        //评论文本
        helper.setText(R.id.comment_text,item.getComment());

        //发布时间
        if(!TextUtils.isEmpty(item.getCreate_at())){
            String s =  GetTimeAgoUtil.getTimeAgo(Long.parseLong(item.getCreate_at()) * 1000L);
            if(!TextUtils.isEmpty(s)){
                if("天前".contains(s)){
                    helper.setText(R.id.time, TimeUtils.millis2String(Long.parseLong(item.getCreate_at()) * 1000L,"yyyy/MM/dd"));
                }else{
                    helper.setText(R.id.time,s);
                }
            }
        }else{
            helper.setText(R.id.time,"");
        }

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        //点赞数
        TextView zan_num = helper.getView(R.id.zan_num);
        zan_num.setTypeface(typeface);
        if(item.getLike_num().equals("0")){
            zan_num.setText("赞");
        }else{
            zan_num.setText(item.getLike_num() + "+");
        }

        //点赞
        LottieAnimationView lottie = helper.getView(R.id.lottieAnimationView);
        helper.getView(R.id.circle_priase).setOnClickListener(view -> {

            lottie.setImageAssetsFolder("images");
            lottie.setAnimation("images/new_like_28.json");
            //硬件加速，解决lottie卡顿问题
            lottie.playAnimation();
            zan_num.setTextColor(mContext.getResources().getColor(R.color.prise_select_color));
        });


        List<CommentBeanNew.SecondComment> list = item.getComment_comment();
        int size  = list.size();
        //如果一级评论下的二级评论条数大于 1 条才显示
        if(null != list && !list.isEmpty() && size <= 2){
            helper.setVisible(R.id.all_comment,true);

            if(size == 1){
                //xxx 回复  xxx : 内容
                User_info userInfo1 = list.get(0).getUser_info();
                User_info p_user_info = list.get(0).getP_user_info();
                String name1 = userInfo1.getName();
                String name11= p_user_info.getName();
                String result;
                if(!TextUtils.isEmpty(name11)){
                    result = " 回复 " + name11;
                }else{
                    result = " " ;
                }

                String comment = list.get(0).getComment();
                helper.setText(R.id.name1, name1 + result  + ":" + comment);

                helper.setVisible(R.id.name1,true);
                helper.setVisible(R.id.name2,false);
            }else if(size == 2){
                //xxx 回复  xxx : 内容
                User_info userInfo1 = list.get(0).getUser_info();
                User_info p_user_info1 = list.get(0).getP_user_info();

                String name1 = userInfo1.getName();
                String name11= p_user_info1.getName();
                String result;
                if(!TextUtils.isEmpty(name11)){
                    result = " 回复 " + name11;
                }else{
                    result = " " ;
                }

                String comment = list.get(0).getComment();
                helper.setText(R.id.name1, name1 + result  + ":" + comment);




                User_info userInfo2 = list.get(1).getUser_info();
                User_info p_user_info2 = list.get(1).getP_user_info();

                String name2 = userInfo2.getName();
                String name22= p_user_info2.getName();
                String result2;
                if(!TextUtils.isEmpty(name22)){
                    result2 = " 回复 " + name22;
                }else{
                    result2 = " " ;
                }

                String comment2 = list.get(1).getComment();
                helper.setText(R.id.name1, name1 + result  + ":" + comment);
                helper.setText(R.id.name2,name2 + result2  + ":" + comment2);
                helper.setVisible(R.id.name1,true);
                helper.setVisible(R.id.name2,true);
            }



        }else{
            helper.setVisible(R.id.all_comment,false);
        }



    }
}

