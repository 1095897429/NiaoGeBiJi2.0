package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.SizeUtils;
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
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.socks.library.KLog;

import java.util.List;

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

    private AnimationDrawable animationDrawable;

    public CircleRecommendAdapter(List<MultiCircleNewsBean> data) {
        super(data);
        //3图
        addItemType(RIGHT_IMG_TYPE, R.layout.first_circle_item1);
        //转发图片
        addItemType(TRANSFER_IMG_TYPE,R.layout.first_circle_item3);
        //转发链接
        addItemType(TRANSFER_LIN_TYPE,R.layout.first_circle_item4);
        //外链
        addItemType(LINK_OUT_TYPE,R.layout.first_circle_item2);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiCircleNewsBean item) {

        helper.addOnClickListener(R.id.part1111)
                .addOnClickListener(R.id.part2222)
                .addOnClickListener(R.id.circle_share)
                .addOnClickListener(R.id.circle_comment)
                .addOnClickListener(R.id.ll_report);
//                .addOnClickListener(R.id.circle_priase);


        switch (helper.getItemViewType()){

            case RIGHT_IMG_TYPE:
                helper.setText(R.id.sender_name,item.getNewsItemBean().getTitle());

                //测试
                if(helper.getAdapterPosition() == 0){
                    helper.setVisible(R.id.circle_remove,true);
                    helper.setVisible(R.id.circle_report,false);
                }else{
                    helper.setVisible(R.id.circle_report,true);
                    helper.setVisible(R.id.circle_remove,false);
                }


                //徽章
                LinearLayout ll = helper.getView(R.id.ll_badge);
                ll.removeAllViews();
                for (int i = 0; i < 2; i++) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageResource(R.mipmap.icon_test_detail_icon1);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.width = SizeUtils.dp2px(22);
                    lp.height = SizeUtils.dp2px(22);
                    lp.gravity = Gravity.CENTER;
                    lp.setMargins(0,0,SizeUtils.dp2px(8),0);
                    imageView.setLayoutParams(lp);
                    ll.addView(imageView);
                }

                //评论
                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
                TextView textComment = helper.getView(R.id.comment);
                textComment.setTypeface(typeface);
                textComment.setText(20 + "");

                //点赞数
                TextView zan_num = helper.getView(R.id.zan_num);
                zan_num.setTypeface(typeface);
                zan_num.setText(99 + "+");

                LottieAnimationView lottie = helper.getView(R.id.lottieAnimationView);

                ImageView animationIV = helper.getView(R.id.iamge_priase);

                helper.getView(R.id.circle_priase).setOnClickListener(view -> {

                    lottie.setImageAssetsFolder("images");
                    lottie.setAnimation("images/new_like_20.json");
                    //硬件加速，解决lottie卡顿问题
                    lottie.useHardwareAcceleration(true);
                    lottie.playAnimation();


                    zan_num.setTextColor(mContext.getResources().getColor(R.color.prise_select_color));

                      //帧动画
//                    animationIV.setImageResource(R.drawable.splash_animation2);
//                    animationDrawable = (AnimationDrawable) animationIV.getDrawable();
//                    animationDrawable.start();

//                    SimpleDraweeView simple = helper.getView(R.id.gif_pic);
//                    // 设置箭头gif
//                    Uri uri = new Uri.Builder()
//                            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
//                            .path(String.valueOf(R.drawable.icon_zan20))
//                            .build();
//                    DraweeController controller = Fresco.newDraweeControllerBuilder()
//                            .setUri(uri)
//                            .setAutoPlayAnimations(true)
//                            .setControllerListener(new BaseControllerListener<ImageInfo>(){
//                                @Override
//                                public void onFinalImageSet(String id, ImageInfo imageInfo,  Animatable animatable) {
//                                    super.onFinalImageSet(id, imageInfo, animatable);
//                                    if(animatable != null && !animatable.isRunning()){
//                                        animatable.start();
//                                        AnimatedDrawable2 drawable2 = (AnimatedDrawable2) animatable;
//                                        drawable2.setAnimationListener(new AnimationListener() {
//                                            @Override
//                                            public void onAnimationStart(AnimatedDrawable2 drawable) {
//                                                KLog.d("tag","gif动画开始");
//                                            }
//
//                                            @Override
//                                            public void onAnimationStop(AnimatedDrawable2 drawable) {
//                                                KLog.d("tag","gif动画结束");
//
//                                            }
//
//                                            @Override
//                                            public void onAnimationReset(AnimatedDrawable2 drawable) {
//                                                KLog.d("tag","gif动画重置");
//                                            }
//
//                                            @Override
//                                            public void onAnimationRepeat(AnimatedDrawable2 drawable) {
//                                                KLog.d("tag","gif动画重复");
//                                                drawable.stop();
//                                                KLog.d("tag","gif动画重复停止，设置新图片");
//                                                animationIV.setImageResource(R.mipmap.icon_flash_priase_select);
//                                            }
//
//                                            @Override
//                                            public void onAnimationFrame(AnimatedDrawable2 drawable, int frameNumber) {
//                                                KLog.d("tag"," --- gif动画 --- ");
//                                            }
//                                        });
//                                    }
//
//                                }
//                            })
//                            .build();
//                    simple.setController(controller);

                });



                //正文
                TextView content = helper.getView(R.id.content);
//                TextPaint paint = content.getPaint();
//                paint.setFakeBoldText(true);
                content.setText("10月31日，格力电器公告拟修订公司章程，其中，经营范围新增了「研发、制造、销售新能源发电产品、储能系统及充电桩」的内容。");

                helper.getView(R.id.circle_remove).setOnClickListener(view -> showRemoveDialog(helper.getAdapterPosition()));

                break;
            case TRANSFER_IMG_TYPE:


                break;
            case TRANSFER_LIN_TYPE:


                break;
            case LINK_OUT_TYPE:
                //设置子View点击事件
                helper.addOnClickListener(R.id.toMoreFlash);


                break;
            default:
                break;
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

}
