package com.qmkj.niaogebiji.module.adapter;

import android.animation.StateListAnimator;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.event.RefreshActicleCommentEvent;
import com.qmkj.niaogebiji.module.event.RefreshCircleDetailCommentEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:二级评论item适配
 * 1.文章 + 圈子 共用
 * 2.注意此处有头布局，数据集合的获取数据时 -- position需要减去1
 *
 * 1.点击底部弹框，回复此评论，显示的时候不用显示回复，直接显示 自己评论内容
 * 2.点击item时，回复此评论，显示的时候显示回复 xxx + 自己 的 评论
 */
public class CommentSecondAdapter extends BaseMultiItemQuickAdapter<MulSecondCommentBean, BaseViewHolder> {

    public  static final int ACTICLE = 1;
    public static final int  CIRCLE = 2;

    public CommentSecondAdapter(List<MulSecondCommentBean> data) {
        super(data);
        //正常
        addItemType(ACTICLE, R.layout.second_comment_item);
        //正常
        addItemType(CIRCLE, R.layout.second_comment_item_circle);
    }


    @Override
    protected void convert(BaseViewHolder helper,MulSecondCommentBean bean) {

        switch (helper.getItemViewType()){
            case ACTICLE:
                CommentBean.FirstComment item = bean.getActicleComment();

                helper.setText(R.id.comment_text,item.getMessage());
                helper.setText(R.id.nickname,item.getUsername());
                ImageUtil.load(mContext,item.getAvatar(),helper.getView(R.id.head_icon));

                getReplyonActicle(helper,item);


                //发布时间
                if(StringUtil.checkNull(item.getDateline())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(item.getDateline()) * 1000L);
                    helper.setText(R.id.time,s);
                }

                //点赞数
                TextView zan_num = helper.getView(R.id.zan_num);
                ImageView imageView =  helper.getView( R.id.iamge_priase);
                zanChange(zan_num,imageView,item.getGood_num(),item.getIs_good());


                helper.getView(R.id.circle_priase).setOnClickListener(view -> {
                    if("0".equals(item.getIs_good() + "")){
                        goodArticle(item,helper.getAdapterPosition() );
                        KLog.d("tag","此处的positon " + helper.getAdapterPosition());
                    }else if("1".equals(item.getIs_good() + "")){
                        cancelGoodArticle(item,helper.getAdapterPosition() );
                    }
                });

                getIconTypeOnActile(helper,item);

                //删除文章评论
                helper.getView(R.id.comment_delete).setOnClickListener(view -> {
                    showRemoveDialog(item,helper.getAdapterPosition());
                });


                break;
            case CIRCLE:
                CommentBeanNew comment = bean.getCircleComment();
                helper.setText(R.id.comment_text,comment.getComment());
                helper.setText(R.id.nickname,comment.getUser_info().getName());
                ImageUtil.load(mContext,comment.getUser_info().getAvatar(),helper.getView(R.id.head_icon));

                getReply(helper,comment);

                //发布时间
                if(StringUtil.checkNull(comment.getCreate_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(comment.getCreate_at()) * 1000L);
                    helper.setText(R.id.time,s);
                }

                //点赞数
                TextView zan_num_circle = helper.getView(R.id.zan_num);
                ImageView imageView_circle =  helper.getView( R.id.iamge_priase);
                zanChangeOnCircle(zan_num_circle,imageView_circle,comment.getLike_num(),comment.getIs_like());

                helper.getView(R.id.circle_priase).setOnClickListener(view -> {
                    likeComment(comment,helper.getAdapterPosition());
                });

                helper.setVisible(R.id.toSecondComment,true);

                helper.addOnClickListener(R.id.toSecondComment);


                getIconType(helper,comment);

                //删除评论
                helper.getView(R.id.comment_delete).setOnClickListener(view -> showRemoveDialog(comment,helper.getAdapterPosition()));

                default:
        }
    }

    private void getIconTypeOnActile(BaseViewHolder helper, CommentBean.FirstComment item) {
        String username = item.getUsername();
        String replyname = item.getReplyed_username();
        if(!TextUtils.isEmpty(username) && username.equals(replyname)){
            helper.setVisible(R.id.comment_delete,true);
        }else{
            helper.setVisible(R.id.comment_delete,false);
        }
    }

    private void getReplyonActicle(BaseViewHolder helper, CommentBean.FirstComment item) {
            sb.setLength( 0 );
            //如果回复者 和 被回复者 一样，则不显示 【回复】
            String username = item.getUsername();
            String replyedUsername = item.getReplyed_username();
            if(!TextUtils.isEmpty(username) &&  !username.equals(replyedUsername)){
                sb.append("回复 ").append(replyedUsername)
                        .append(":").append(item.getMessage());
            }else{
                sb.append(item.getMessage());
            }

            if(!TextUtils.isEmpty(username) &&  !username.equals(replyedUsername)){
                int authorNamelength = replyedUsername.length();
                spannableString = new SpannableString(sb.toString());
                ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
                //中间有 回复 两个字 + 1个空格
                spannableString.setSpan(fCs2, 3,  3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }else{
                spannableString = new SpannableString(sb.toString());
            }

            helper.setText(R.id.comment_text,spannableString);
    }


    private void showRemoveDialog(CommentBean.FirstComment itemBean, int position) {
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(mContext).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            deleteComment(itemBean,position);
        }).setNegativeButton("取消", v -> {
        }).setMsg("确定要删除这条评论？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    private void deleteComment(CommentBean.FirstComment itemBean, int position) {
        Map<String,String> map = new HashMap<>();
        map.put("target_type","1");
        map.put("target_id",itemBean.getCid());

        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().deleteComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        mData.remove(position - 1);
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new RefreshActicleCommentEvent());
                    }
                });
    }


    /** 删除帖子的评论 */
    private void showRemoveDialog(CommentBeanNew comment, int position) {
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(mContext).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            blogdeleteComment(comment,position);
        }).setNegativeButton("取消", v -> {
        }).setMsg("确定要删除这条评论？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    private void blogdeleteComment(CommentBeanNew comment, int position) {
        Map<String,String> map = new HashMap<>();
        map.put("comment_id",comment.getId());
        map.put("class",comment.getComment_class());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().blogdeleteComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        mData.remove(position - 1);
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        //TODO 二级评论 - 删除需通知前一个界面列表中某一条数据
                        EventBus.getDefault().post(new RefreshCircleDetailCommentEvent());

                    }
                });
    }

    private void getIconType(BaseViewHolder helper, CommentBeanNew comment) {
        String uid = comment.getUid();
        String myUid = StringUtil.getUserInfoBean().getUid();
        if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
            helper.setVisible(R.id.comment_delete,true);
        }else{
            helper.setVisible(R.id.comment_delete,false);
        }
    }

    //和文章二级评论不一样的是，没回复时不显示赞
    private void zanChangeOnCircle(TextView zan_num, ImageView imageView, String good_num, int is_good) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        zan_num.setTypeface(typeface);
        if("0".equals(good_num)){
            zan_num.setText("");
        }else{
            int size = Integer.parseInt(good_num);
            if(size > 99){
                zan_num.setText(99 + "+");
            }else{
                zan_num.setText(size + "");
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


    private void zanChange(TextView zan_num,ImageView imageView, String good_num, int is_good) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        zan_num.setTypeface(typeface);
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
        //点赞图片
        if("0".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select_no));
        }else if("1".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_select_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select));
        }
    }

    //文章评论点赞
    private void goodArticle(CommentBean.FirstComment bean, int position) {
        Map<String,String> map = new HashMap<>();
        map.put("type","4");
        map.put("id",bean.getCid());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().goodArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //手动修改
                        MulSecondCommentBean m = mData.get(position - 1);
                        CommentBean.FirstComment t = m.getActicleComment();
                        t.setIs_good(1);
                        t.setGood_num((Integer.parseInt(t.getGood_num()) + 1) + "");
                        notifyItemChanged(position);
                    }
                });
    }

    //文章评论取赞
    private void cancelGoodArticle(CommentBean.FirstComment bean, int position) {
        Map<String,String> map = new HashMap<>();
        map.put("type","4");
        map.put("id",bean.getCid());

        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().cancelGoodArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //手动修改
                        MulSecondCommentBean m = mData.get(position - 1);
                        CommentBean.FirstComment t = m.getActicleComment();
                        t.setIs_good(0);
                        t.setGood_num((Integer.parseInt(t.getGood_num()) - 1) + "");
                        notifyItemChanged(position);
                    }
                });
    }

    //圈子
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
                        CommentBeanNew t =  mData.get(position  - 1).getCircleComment();
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


    SpannableString spannableString ;
    StringBuilder sb = new StringBuilder();

    private void getReply(BaseViewHolder helper, CommentBeanNew item) {
        sb.setLength( 0 );
        //如果回复者 和 被回复者 一样，则不显示 【回复】
        User_info userInfo = item.getUser_info();
        User_info p_userInfo = item.getP_user_info();
        if(!TextUtils.isEmpty(userInfo.getUid()) &&  !userInfo.getUid().equals(p_userInfo.getUid())){
            sb.append("回复 ").append(p_userInfo.getName())
                    .append(":").append(item.getComment());
        }else{
            sb.append(item.getComment());
        }


        if(!TextUtils.isEmpty(userInfo.getName()) &&  !userInfo.getName().equals(p_userInfo.getName())){
            int authorNamelength = p_userInfo.getName().length();
            spannableString = new SpannableString(sb.toString());
            ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
            //中间有 回复 两个字 + 1个空格
            spannableString.setSpan(fCs2,   3,   3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }else{
            spannableString = new SpannableString(sb.toString());
        }
        helper.setText(R.id.comment_text,spannableString);
    }
}
