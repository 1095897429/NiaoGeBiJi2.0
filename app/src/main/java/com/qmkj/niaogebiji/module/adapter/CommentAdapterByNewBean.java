package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
                .addOnClickListener(R.id.toFirstComment);

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


        //点赞数
        TextView zan_num = helper.getView(R.id.zan_num);
        ImageView imageView =  helper.getView( R.id.iamge_priase);
        zanChange(zan_num,imageView,item.getLike_num(),item.getIs_like());


        helper.getView(R.id.circle_priase).setOnClickListener(view -> {
            if("0".equals(item.getIs_like() + "")){
                likeComment(item,helper.getAdapterPosition());
            }else if("1".equals(item.getIs_like() + "")){
                likeComment(item,helper.getAdapterPosition());
            }
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



    //显示一些数据
    private void zanChange(TextView zan_num,ImageView imageView, String good_num, int is_good) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        zan_num.setTypeface(typeface);
        if(StringUtil.checkNull(good_num)){
            if("0".equals(good_num)){
                zan_num.setText("赞");
            }else{
                int size = Integer.parseInt(good_num);
                if(size > 99){
                    zan_num.setText(99 + "+");
                }else{
                    zan_num.setText(size + "");
                }
            }
        }
        //点赞图片
        if("0".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select_no));
        }else if("1".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_select_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select));
        }
    }



    private void likeComment(CommentBeanNew circleBean, int position) {
        Map<String,String> map = new HashMap<>();
        map.put("comment_id",circleBean.getId());
        int like = 0;
        if("0".equals(circleBean.getIs_like() + "")){
            like = 1;
        }else if("1".equals(circleBean.getIs_like() + "")){
            like = 0;
        }
        map.put("like",like + "");
        map.put("class",circleBean.getComment_class());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().likeComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        CommentBeanNew t =  mData.get(position);
                        // 测试的
                        int islike = circleBean.getIs_like();
                        if(islike == 0){
                            t.setIs_like(1);
                            t.setLike_num((Integer.parseInt(t.getLike_num()) + 1) + "");
                        }else{
                            t.setIs_like(0);
                            t.setLike_num((Integer.parseInt(t.getLike_num()) - 1) + "");
                        }
                        notifyItemChanged(position);
                    }
                });
    }


}
