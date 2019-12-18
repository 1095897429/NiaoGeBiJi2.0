package com.qmkj.niaogebiji.module.adapter;

import android.animation.StateListAnimator;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
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
 * 创建时间 2019-11-27
 * 描述:二级评论item适配
 */
public class CommentSecondAdapter extends BaseMultiItemQuickAdapter<MulSecondCommentBean, BaseViewHolder> {

    public  static final int ACTICLE = 1;
    public static final int  CIRCLE = 2;


    public CommentSecondAdapter(List<MulSecondCommentBean> data) {
        super(data);
        //正常
        addItemType(ACTICLE, R.layout.second_comment_item);
        //正常
        addItemType(CIRCLE, R.layout.second_comment_item);
    }


    @Override
    protected void convert(BaseViewHolder helper,MulSecondCommentBean bean) {

        switch (helper.getItemViewType()){
            case ACTICLE:
                CommentBean.FirstComment item = bean.getActicleComment();

                helper.setText(R.id.comment_text,item.getMessage());
                helper.setText(R.id.nickname,item.getUsername());
                ImageUtil.load(mContext,item.getAvatar(),helper.getView(R.id.head_icon));

//
//
//                StringBuilder sb = new StringBuilder();
//                //如果回复者 和 被回复者 一样，则不显示 【回复】
//                User_info p_userInfo = bean.getP_user_info();
//                if(!TextUtils.isEmpty(userInfo.getName()) &&  !userInfo.getName().equals(p_userInfo.getName())){
//                    sb.append(userInfo.getName()).append(" 回复 ").append(p_userInfo.getName())
//                            .append(":").append(bean.getComment());
//                }else{
//                    sb.append(userInfo.getName()).append(":").append(bean.getComment());
//                }
//                helper.setText(R.id.comment_text,sb.toString());
//
//
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

                default:
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
                        KLog.d("tag","-0---");
                        MulSecondCommentBean m = mData.get(position - 1);
                        CommentBean.FirstComment t = m.getActicleComment();
                        t.setIs_good(1);
                        t.setGood_num((Integer.parseInt(t.getGood_num()) + 1) + "");
                        notifyItemChanged(position);
                    }
                });
    }


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
}
