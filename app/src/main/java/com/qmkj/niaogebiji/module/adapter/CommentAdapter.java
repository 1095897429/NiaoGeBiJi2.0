package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:1级评论适配器
 */
public class CommentAdapter extends BaseQuickAdapter<CommentBean.FirstComment, BaseViewHolder> {

    public CommentAdapter(@Nullable List<CommentBean.FirstComment> data) {
        super(R.layout.first_comment_item_new,data);
    }


    private Limit2ReplyAdapter mLimit2ReplyAdapter;
    private List<CommentBean.SecondComment> mLimitComments;

    @Override
    protected void convert(BaseViewHolder helper,CommentBean.FirstComment item) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.comment_delete)
                .addOnClickListener(R.id.ll_has_second_comment)
                .addOnClickListener(R.id.comment_priase)
                .addOnClickListener(R.id.toSecondComment);
        //评论时间
        if(!TextUtils.isEmpty(item.getDateline())){
            String s =  GetTimeAgoUtil.getTimeAgo(Long.parseLong(item.getDateline()) * 1000L);
            if(!TextUtils.isEmpty(s)){
                if("天前".contains(s)){
                    helper.setText(R.id.time, TimeUtils.millis2String(Long.parseLong(item.getDateline()) * 1000L,"yyyy/MM/dd"));
                }else{
                    helper.setText(R.id.time,s);
                }
            }
        }else{
            helper.setText(R.id.time,"");
        }

        //评论者
        helper.setText(R.id.nickname,item.getUsername());
        //评论者公司
        helper.setText(R.id.name_tag,item.getCompany_name());
        //评论者头像
        if(!TextUtils.isEmpty(item.getAvatar())){
            ImageUtil.load(mContext,item.getAvatar(),helper.getView(R.id.head_icon));
        }
        //评论正文
        helper.setText(R.id.comment_text,item.getMessage());
        //点赞数
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        TextView zan_num = helper.getView(R.id.zan_num);
        zan_num.setTypeface(typeface);
        if("0".equals(item.getGood_num())){
            zan_num.setText("赞");
        }else{
            int size = Integer.parseInt(item.getGood_num());
            if(size > 99){
                zan_num.setText(99 + "+");
            }else{
                zan_num.setText(size + "");
            }
        }

        //点赞
        LottieAnimationView lottie = helper.getView(R.id.lottieAnimationView);
        ImageView animationIV = helper.getView(R.id.iamge_priase);
        helper.getView(R.id.circle_priase).setOnClickListener(view -> {
            lottie.setImageAssetsFolder("images");
            lottie.setAnimation("images/new_like_28.json");
            //硬件加速，解决lottie卡顿问题
//            lottie.useHardwareAcceleration(true);
            lottie.playAnimation();


            zan_num.setTextColor(mContext.getResources().getColor(R.color.prise_select_color));
        });


        //二级评论数据
        List<CommentBean.SecondComment> list = item.getCommentslist();
        mLimitComments = list;
        //二级评论布局
        LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
        talkManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView recyclerView =  helper.getView(R.id.show_limit_2_reply);
        recyclerView.setLayoutManager(talkManager);
        mLimit2ReplyAdapter = new Limit2ReplyAdapter(mLimitComments);
        //禁用change动画
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mLimit2ReplyAdapter);


        //如果一级评论下的二级评论条数大于 1 条才显示
        if(null != list && !list.isEmpty()){
            helper.setVisible(R.id.ll_has_second_comment,true);
            helper.setText(R.id.all_comment,"查看全部" + list.size() + "条回复");
        }else{
            helper.setVisible(R.id.ll_has_second_comment,false);
        }

    }
}

