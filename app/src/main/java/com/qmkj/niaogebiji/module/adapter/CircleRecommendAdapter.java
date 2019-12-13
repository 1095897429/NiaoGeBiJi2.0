package com.qmkj.niaogebiji.module.adapter;

import android.animation.StateListAnimator;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.fresco.animation.drawable.AnimationListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
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
 * 创建时间 2019-11-14
 * 修改时间 2019-11.25
 * 描述:圈子推荐适配器
 * 1.多个地方用的，不好把多个事件放到activity中
 * 2.修改让子事件就在这里处理,Nonono !!!
 */
public class CircleRecommendAdapter extends BaseMultiItemQuickAdapter<MultiCircleNewsBean, BaseViewHolder> {

    public  static final int RIGHT_IMG_TYPE = 1;
    public static final int TRANSFER_IMG_TYPE = 2;
    public static final int TRANSFER_LIN_TYPE = 3;
    public static final int LINK_OUT_TYPE = 4;
    public static final int ALL_TEXT = 5;


    public CircleRecommendAdapter(List<MultiCircleNewsBean> data) {

        super(data);
        //多图
        addItemType(RIGHT_IMG_TYPE, R.layout.first_circle_item1);
        //转发图片
        addItemType(TRANSFER_IMG_TYPE,R.layout.first_circle_item3);
        //转发链接
        addItemType(TRANSFER_LIN_TYPE,R.layout.first_circle_item4);
        //原创外链
        addItemType(LINK_OUT_TYPE,R.layout.first_circle_item2);
        //全文本
        addItemType(ALL_TEXT,R.layout.first_circle_item5);

    }

    @Override
    protected void convert(BaseViewHolder helper, MultiCircleNewsBean item) {

        helper.addOnClickListener(R.id.part1111)
                .addOnClickListener(R.id.part2222)
                .addOnClickListener(R.id.circle_share)
                .addOnClickListener(R.id.circle_comment)
                .addOnClickListener(R.id.ll_report);

        CircleBean bean  = item.getCircleBean();
        User_info userInfo;
        //测试 uid = 300579
        if(null!= bean.getUser_info()){
            userInfo = bean.getUser_info();
            //uid 判断
            if("300579".equals(bean.getUid())){
                helper.setVisible(R.id.circle_remove,true);
                helper.setVisible(R.id.circle_report,false);
            }else{
                helper.setVisible(R.id.circle_report,true);
                helper.setVisible(R.id.circle_remove,false);
            }
            //正文
            TextView content = helper.getView(R.id.content);
            content.setText(bean.getBlog());
            //发布时间
            if(!TextUtils.isEmpty(bean.getCreated_at())){
                String s =  GetTimeAgoUtil.getTimeAgo(Long.parseLong(bean.getCreated_at()) * 1000L);
                if(!TextUtils.isEmpty(s)){
                    if("天前".contains(s)){
                        helper.setText(R.id.publish_time,TimeUtils.millis2String(Long.parseLong(bean.getCreated_at()) * 1000L,"yyyy/MM/dd"));
                    }else{
                        helper.setText(R.id.publish_time,s);
                    }
                }
            }

            //发帖人
            helper.setText(R.id.sender_name,bean.getUser_info().getName());
            //头像
            ImageUtil.load(mContext,bean.getUser_info().getAvatar(),helper.getView(R.id.head_icon));
            //职位
            helper.setText(R.id.sender_tag,bean.getUser_info().getPosition());
            //徽章
            if(userInfo.getBadge() != null && !userInfo.getBadge().isEmpty()){
                LinearLayout ll = helper.getView(R.id.ll_badge);
                ll.removeAllViews();
                for (int i = 0; i < userInfo.getBadge().size(); i++) {
                    ImageView imageView = new ImageView(mContext);
                    String icon = userInfo.getBadge().get(i).getIcon();
                    if(!TextUtils.isEmpty(icon)){
                        ImageUtil.load(mContext,icon,imageView);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.width = SizeUtils.dp2px(22);
                    lp.height = SizeUtils.dp2px(22);
                    lp.gravity = Gravity.CENTER;
                    lp.setMargins(0,0,SizeUtils.dp2px(8),0);
                    imageView.setLayoutParams(lp);
                    ll.addView(imageView);
                }
            }

            //评论
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
            TextView textComment = helper.getView(R.id.comment);
            textComment.setTypeface(typeface);
            if(bean.getComment_num().equals("0")){
                textComment.setText("评论");
            }else{
                textComment.setText(bean.getComment_num() + "");
            }
            //点赞数
            TextView zan_num = helper.getView(R.id.zan_num);
            zan_num.setTypeface(typeface);
            if(bean.getLike_num().equals("0")){
                zan_num.setText("赞");
            }else{
                zan_num.setText(bean.getLike_num() + "+");
            }
            //点赞图片
            if("0".equals(bean.getIs_like() + "")){
                helper.setImageResource(R.id.iamge_priase,R.mipmap.icon_flash_priase);
                zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select_no));
            }else if("1".equals(bean.getIs_like() + "")){
                helper.setImageResource(R.id.iamge_priase,R.mipmap.icon_flash_priase_select);
                zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select));
            }

            helper.getView(R.id.circle_priase).setOnClickListener(view -> {
//                LottieAnimationView lottie = helper.getView(R.id.lottieAnimationView);
//                lottie.setImageAssetsFolder("images");
//                lottie.setAnimation("images/new_like_20.json");
//                lottie.playAnimation();

                likeBlog(item.getCircleBean(),helper.getAdapterPosition());
            });

        }


        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:
                List<String> imgs = bean.getImages();
                setImageStatus(helper,imgs);

                break;
            case TRANSFER_IMG_TYPE:
                CircleBean.P_blog pBlog = bean.getP_blog();
                CircleBean.P_user_info pUserInfo = pBlog.getP_user_info();
                helper.setText(R.id.transfer_content,pBlog.getBlog());
                helper.setText(R.id.transfer_name,pUserInfo.getName() + pUserInfo.getCompany_name());

                setImageStatus(helper,pBlog.getImages());

                break;
            case TRANSFER_LIN_TYPE:
                CircleBean.P_blog pBlog1 = bean.getP_blog();
                if(null != pBlog1){
                    CircleBean.P_user_info pUserInfo1 = pBlog1.getP_user_info();
                    if(null != pUserInfo1){
                        helper.setText(R.id.transfer_content,pBlog1.getBlog());
                        helper.setText(R.id.transfer_name,pUserInfo1.getName() + pUserInfo1.getCompany_name());
                    }
                }
                break;
            case LINK_OUT_TYPE:
                helper.setText(R.id.link_text,bean.getLink_title());
                helper.setText(R.id.link_http,bean.getLink());
                helper.setImageResource(R.id.link_img,R.mipmap.icon_link_logo);

                break;
            case ALL_TEXT:

                break;
            default:
                break;
        }
    }



    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    private void setImageStatus(BaseViewHolder helper,List<String> imgs) {
        int size = imgs.size();
        if(size < 3){
            if(1 == size){
                ImageUtil.load(mContext,imgs.get(0) + scaleSize,helper.getView(R.id.one_img_imgs));
                helper.setVisible(R.id.one_img_imgs,true);
                helper.setVisible(R.id.two_img_imgs,false);
                helper.setVisible(R.id.three_img_imgs,false);
            }
            if(2 == size){
                ImageUtil.load(mContext,imgs.get(0) + scaleSize,helper.getView(R.id.one_img_imgs));
                ImageUtil.load(mContext,imgs.get(1) + scaleSize,helper.getView(R.id.two_img_imgs));
                helper.setVisible(R.id.one_img_imgs,true);
                helper.setVisible(R.id.two_img_imgs,true);
                helper.setVisible(R.id.three_img_imgs,false);
            }
        }else{
            ImageUtil.load(mContext,imgs.get(0) + scaleSize,helper.getView(R.id.one_img_imgs));
            ImageUtil.load(mContext,imgs.get(1) + scaleSize,helper.getView(R.id.two_img_imgs));
            ImageUtil.load(mContext,imgs.get(2) + scaleSize,helper.getView(R.id.three_img_imgs));
            helper.setVisible(R.id.one_img_imgs,true);
            helper.setVisible(R.id.two_img_imgs,true);
            helper.setVisible(R.id.three_img_imgs,true);
        }
    }





    private void showRemoveDialog(int position) {
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(mContext).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            getData().remove(position);
            notifyDataSetChanged();
            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
        }).setNegativeButton("取消", v -> {
        }).setMsg("确定要删除这条动态？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    private void likeBlog(CircleBean circleBean,int position) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",circleBean.getId());

        int like ;
        if("0".equals(circleBean.getIs_like())){
            like = 1;
        }else{
            like = 0;
        }
        map.put("like",like + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().likeBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {

                       // 测试的
                       int islike = circleBean.getIs_like();
                       if(islike == 0){
                           mData.get(position).getCircleBean().setIs_like(1);
                       }else{
                           mData.get(position).getCircleBean().setIs_like(0);
                       }
                        notifyDataSetChanged();
                    }
                });
    }



}
