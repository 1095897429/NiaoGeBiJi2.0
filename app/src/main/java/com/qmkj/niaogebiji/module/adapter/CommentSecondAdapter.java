package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.CommentDetailActivity;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
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

    //上级圈子评论
    private CommentCircleBean superiorComment;

    //上级文章评论
    private CommentBean.FirstComment superiorActicleComment;



    public void setSuperiorActicleComment(CommentBean.FirstComment superiorActicleComment) {
        this.superiorActicleComment = superiorActicleComment;
    }

    public void setSuperiorComment(CommentCircleBean superiorComment) {
        this.superiorComment = superiorComment;
    }

    public CommentSecondAdapter(List<MulSecondCommentBean> data) {
        super(data);
        //正常
        addItemType(ACTICLE, R.layout.second_comment_item);
        //圈子
        addItemType(CIRCLE, R.layout.second_comment_item_circle);
    }


    @Override
    protected void convert(BaseViewHolder helper,MulSecondCommentBean bean) {

        switch (helper.getItemViewType()){
            case ACTICLE:
                CommentBean.FirstComment item = bean.getActicleComment();

                helper.setText(R.id.comment_text,item.getMessage());
                helper.setText(R.id.nickname,item.getUsername());
                ImageUtil.loadByDefaultHead(mContext,item.getAvatar(),helper.getView(R.id.head_icon));

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

                //头像跳转
                helper.getView(R.id.head_icon).setOnClickListener(v -> UIHelper.toUserInfoActivity(mContext,item.getUid()));


                break;
            case CIRCLE:
                CommentCircleBean comment = bean.getCircleComment();
                helper.setText(R.id.comment_text,comment.getComment());
                helper.setText(R.id.nickname,comment.getUser_info().getName());
                ImageUtil.loadByDefaultHead(mContext,comment.getUser_info().getAvatar(),helper.getView(R.id.head_icon));

                getCircleReply(helper,comment);

                //发布时间
                if(StringUtil.checkNull(comment.getCreated_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(comment.getCreated_at()) * 1000L);
                    helper.setText(R.id.time,s);
                }else{
                    helper.setText(R.id.time,"");
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

                //头像跳转
                helper.getView(R.id.head_icon).setOnClickListener(v -> UIHelper.toUserInfoActivity(mContext,comment.getUid()));

                default:
        }
    }

    private void getIconTypeOnActile(BaseViewHolder helper, CommentBean.FirstComment item) {
        String uid = item.getUid();
        String myUid = StringUtil.getUserInfoBean().getUid();
        if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
            helper.setVisible(R.id.comment_delete,true);
        }else{
            helper.setVisible(R.id.comment_delete,false);
        }
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
                        mData.remove(position);
                        notifyDataSetChanged();
                        ToastUtils.showShort("删除成功");
                        EventBus.getDefault().post(new RefreshActicleCommentEvent());
                        //和圈子有点区别
                        if(mOnReduceListener != null && mData.size() == 0){
                            mOnReduceListener.reduce();
                        }
                    }
                });
    }


    /** 删除帖子的评论 */
    private void showRemoveDialog(CommentCircleBean comment, int position) {
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(mContext).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            blogdeleteComment(comment,position);
        }).setNegativeButton("取消", v -> {
        }).setMsg("确定要删除这条评论？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    private void blogdeleteComment(CommentCircleBean comment, int position) {
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
                        mData.remove(position);
                        notifyDataSetChanged();
                        ToastUtils.showShort("删除成功");
                        //TODO 二级评论 - 删除需通知前一个界面列表中某一条数据
                        EventBus.getDefault().post(new RefreshCircleDetailCommentEvent());

                        if(mOnReduceListener != null){
                            mOnReduceListener.reduce();
                        }

                    }
                });
    }

    private void getIconType(BaseViewHolder helper, CommentCircleBean comment) {
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
                        MulSecondCommentBean m = mData.get(position);
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
                        MulSecondCommentBean m = mData.get(position);
                        CommentBean.FirstComment t = m.getActicleComment();
                        t.setIs_good(0);
                        t.setGood_num((Integer.parseInt(t.getGood_num()) - 1) + "");
                        notifyItemChanged(position);
                    }
                });
    }

    //圈子
    private void likeComment(CommentCircleBean circleBean, int position) {
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
                        CommentCircleBean t =  mData.get(position).getCircleComment();
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



    private void getCircleReply(BaseViewHolder helper, CommentCircleBean item) {
        sb.setLength( 0 );

        //被回复者信息
        User_info p_userInfo = item.getP_user_info();
        if(!TextUtils.isEmpty(item.getPuid()) &&  !item.getPuid().equals(superiorComment.getUid())){
            sb.append("回复 ").append(p_userInfo.getName())
                    .append(":").append(item.getComment());
        }else{
            sb.append(item.getComment());

        }

        if(!TextUtils.isEmpty(item.getPuid()) &&  !item.getPuid().equals(superiorComment.getUid())){
            int authorNamelength = p_userInfo.getName().length();
            spannableString = new SpannableString(sb.toString());
            ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
            //中间有 回复 两个字 + 1个空格
            spannableString.setSpan(fCs2,   3,   3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    KLog.d("tag","点击的是 " + p_userInfo.getName());
                    UIHelper.toUserInfoActivity(mContext,p_userInfo.getUid());
                }
            };
            spannableString.setSpan(clickableSpan, 3, 3 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }else{
            spannableString = new SpannableString(sb.toString());
        }

        helper.setText(R.id.comment_text,spannableString);
        ((TextView)helper.getView(R.id.comment_text)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void getReplyonActicle(BaseViewHolder helper, CommentBean.FirstComment item) {
        sb.setLength( 0 );
        //如果回复者 和 被回复者 一样，则不显示 【回复】
        if(!TextUtils.isEmpty(item.getRelatedid()) &&  !item.getRelatedid().equals(superiorActicleComment.getUid())){
            sb.append("回复 ").append(item.getReplyed_username())
                    .append(":").append(item.getMessage());
        }else {
            sb.append(item.getMessage());
        }

        if(!TextUtils.isEmpty(item.getRelatedid()) &&  !item.getRelatedid().equals(superiorActicleComment.getUid())){
            int authorNamelength = item.getReplyed_username().length();
            spannableString = new SpannableString(sb.toString());
            ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
            //中间有 回复 两个字 + 1个空格
            spannableString.setSpan(fCs2, 3,  3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    KLog.d("tag","点击的是 " + authorNamelength);
                    UIHelper.toUserInfoActivity(mContext,superiorActicleComment.getUid());
                }
            };
            spannableString.setSpan(clickableSpan, 3, 3 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            spannableString = new SpannableString(sb.toString());
        }

        helper.setText(R.id.comment_text,spannableString);
        ((TextView)helper.getView(R.id.comment_text)).setMovementMethod(LinkMovementMethod.getInstance());

    }





    //自己删自己的评论，回调
    public interface OnReduceListener{
        void reduce();
    }

    private OnReduceListener mOnReduceListener;

    public void setOnReduceListener(OnReduceListener onReduceListener) {
        mOnReduceListener = onReduceListener;
    }
}
