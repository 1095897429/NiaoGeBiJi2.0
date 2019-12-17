package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
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

    private Limit2ReplyCircleAdapter mLimit2ReplyCircleAdapter;
    private List<CommentBeanNew.SecondComment> mLimitComments;


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


        //二级评论数据
        List<CommentBeanNew.SecondComment> list = item.getComment_comment();
        mLimitComments = list;
        //二级评论布局
        LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
        talkManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView recyclerView =  helper.getView(R.id.show_limit_2_reply);
        recyclerView.setLayoutManager(talkManager);
        mLimit2ReplyCircleAdapter = new Limit2ReplyCircleAdapter(mLimitComments);
        //禁用change动画
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mLimit2ReplyCircleAdapter);


        int size = 0;
        if(!TextUtils.isEmpty(item.getComment_num())){
            size = Integer.parseInt(item.getComment_num());
        }
        //如果一级评论下的二级评论条数大于 1 条才显示
        if(size != 0 ){
            helper.setVisible(R.id.ll_has_second_comment,true);
            helper.setText(R.id.all_comment,"查看全部" + size + "条回复");
        }else{
            helper.setVisible(R.id.ll_has_second_comment,false);
        }


    }
}

