package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.CenterAlignImageSpan;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-20
 * 描述:搜索圈子适配器
 * 1.只显示 文本 + 文本link + 文本图片 + 文本文章 + 文本里link
 */
public class CircleSearchAdapterNew extends BaseQuickAdapter<CircleBean, BaseViewHolder> {

        public  static final int YC_PIC = 1;

        public  static final int YC_LINK = 2;

        public  static final int YC_ACTICLE = 3;

        public  static final int YC_TEXT = 4;

        public  static final int ZF_PIC = 11;

        public  static final int ZF_LINK = 22;

        public  static final int ZF_ACTICLE = 33;

        public  static final int ZF_TEXT = 44;

        public CircleSearchAdapterNew(List<CircleBean> data) {
            super(R.layout.first_circle_search_itemall,data);
        }


        private CirclePicAdapter mCirclePicAdapter;
        private List<String> mPics = new ArrayList<>();
        private List<String> mPics_transger;
        private CircleTransferPicAdapter mCircleTransferPicAdapter;

        @Override
        protected void convert(BaseViewHolder helper, CircleBean item) {

            getCircleType(helper,item);

            getIconType(helper,item);

            if(item.getUser_info() != null){
                User_info userInfo = item.getUser_info();
                //名字
                TextView sender_name = helper.getView(R.id.sender_name);
                sender_name.setText(userInfo.getName());
                //职位
                TextView sender_tag = helper.getView(R.id.sender_tag);
                sender_tag.setText(userInfo.getCompany_name() + userInfo.getPosition());
                //是否认证
                if("1".equals(userInfo.getAuth_com_status())){
                    Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                    drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    sender_tag.setCompoundDrawables(null,null,drawable,null);
                }else{
                    sender_tag.setCompoundDrawables(null,null,null,null);
                }
                //头像
                ImageUtil.load(mContext,userInfo.getAvatar(),helper.getView(R.id.head_icon));
                //时间
                if(!TextUtils.isEmpty(item.getCreated_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(item.getCreated_at()) * 1000L);
                    if(!TextUtils.isEmpty(s)){
                        if("天前".contains(s)){
                            helper.setText(R.id.publish_time, TimeUtils.millis2String(Long.parseLong(item.getCreated_at()) * 1000L,"yyyy/MM/dd"));
                        }else{
                            helper.setText(R.id.publish_time,s);
                        }
                    }
                }

                //点赞数
                TextView com_num = helper.getView(R.id.comment);
                TextView zan_num = helper.getView(R.id.zan_num);
                ImageView imageView =  helper.getView( R.id.image_circle_priase);
                zanCommentChange(com_num,zan_num,imageView,item.getLike_num(),item.getIs_like(),item.getComment_num());


                TextView msg = helper.getView(R.id.content);
                getIconLinkShow(item,msg);


            }


            if(item.getP_blog() != null){
                //转发文本
                helper.setText(R.id.transfer_zf_content,item.getP_blog().getBlog());

                if(item.getP_blog() != null && item.getP_blog().getP_user_info() != null){
                    CircleBean.P_user_info temp =item.getP_blog().getP_user_info();
                    helper.setText(R.id.transfer_zf_author,temp.getName()  + "  " + temp.getCompany_name() +
                            temp.getPosition());
                }
            }


            //item点击事件
            helper.itemView.setOnClickListener(view -> UIHelper.toCommentDetailActivity(mContext,item.getId(),item.getCircleType(),helper.getAdapterPosition()));


            //link点击
            helper.getView(R.id.part_yc_link).setOnClickListener(view -> UIHelper.toWebViewActivity(mContext, item.getLink()));
            //文章点击
            helper.getView(R.id.part_yc_acticle).setOnClickListener(view -> UIHelper.toNewsDetailActivity(mContext, item.getArticle_id()));

            //点赞
            helper.getView(R.id.circle_priase).setOnClickListener(view -> {
                likeBlog(item,helper.getAdapterPosition());
            });

            //分享
            helper.getView(R.id.circle_share).setOnClickListener(view -> {
                showShareDialog(item);
            });

            //转发帖子点击
            helper.getView(R.id.transfer_zf_ll).setOnClickListener(view -> {

                UIHelper.toCommentDetailActivity(mContext,item.getP_blog().getId(),item.getP_blog().getCircleType(),helper.getAdapterPosition());

            });

            //帖子举报/删除 -- 为了增大触摸面积
            helper.getView(R.id.ll_report).setOnClickListener(view -> {
                String uid = item.getUid();
                String myUid = StringUtil.getUserInfoBean().getUid();
                if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
                    showRemoveDialog(item,helper.getAdapterPosition());
                }else{
                    showPopupWindow(item,helper.getView(R.id.circle_report));
                    setBackgroundAlpha((Activity) mContext, 0.6f);
                }
            });

            //去个人中心
            helper.getView(R.id.part1111).setOnClickListener(view -> {
                UIHelper.toUserInfoActivity(mContext,item.getUid());
            });



            switch (item.getCircleType()){
                case 1:
                    mPics = item.getImages();
                    if(mPics != null && mPics.size() > 3){
                        mPics = mPics.subList(0,3);
                    }
                    //二级评论布局
                    GridLayoutManager talkManager = new GridLayoutManager(mContext,3);
                    RecyclerView recyclerView =  helper.getView(R.id.pic_recyler);
                    recyclerView.setLayoutManager(talkManager);
                    mCirclePicAdapter = new CirclePicAdapter(mPics);
                    //禁用change动画
                    ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                    recyclerView.setAdapter(mCirclePicAdapter);

                    //预览事件
                    mCirclePicAdapter.setOnItemClickListener((adapter, view, position) -> {
                        KLog.d("tag","点击预览");
                        UIHelper.toPicPreViewActivity(mContext,  item.getImages(),position);
                    });


                    break;
                case 2:
                    helper.setText(R.id.link_text,item.getLink_title());
                    helper.setText(R.id.link_http,item.getLink());
                    helper.setImageResource(R.id.link_img,R.mipmap.icon_link_logo);
                    break;
                case 3:
                    if(!TextUtils.isEmpty(item.getArticle_image())){
                        ImageUtil.load(mContext,item.getArticle_image(),helper.getView(R.id.acticle_img));
                    }
                    helper.setText(R.id.acticle_title,item.getArticle_title());
                    break;

                case 11:

                    mPics_transger = item.getP_blog().getImages();
                    if(mPics_transger != null && mPics_transger.size() > 3){
                        mPics_transger = mPics_transger.subList(0,3);
                    }
                    //二级评论布局
                    talkManager = new GridLayoutManager(mContext,3);
                    recyclerView =  helper.getView(R.id.pic_recyler_transfer);
                    recyclerView.setLayoutManager(talkManager);
                    mCircleTransferPicAdapter = new CircleTransferPicAdapter(mPics_transger);
                    //禁用change动画
                    ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                    recyclerView.setAdapter(mCircleTransferPicAdapter);


                    break;
                case 22:
                    helper.setText(R.id.transfer_link_text,item.getP_blog().getLink_title());
                    helper.setText(R.id.transfer_link_http,item.getP_blog().getLink());
                    helper.setImageResource(R.id.transer_link_img,R.mipmap.icon_link_logo);
                    break;
                case 33:
                    if(!TextUtils.isEmpty(item.getP_blog().getArticle_image())){
                        ImageUtil.load(mContext,item.getP_blog().getArticle_image(),helper.getView(R.id.transfer_article_img));
                    }
                    helper.setText(R.id.transfer_article_img,item.getP_blog().getArticle_title());
                    break;
                default:
            }


        }

        private void getIconLinkShow(CircleBean item, TextView msg) {
            //正文里link判断 并装换
            String content = "测试http://www.baidu.com 测测https://www.qq.com/再来一个 https://china.nba.com";
            //判断内容是否中link
            String regex = "https?://(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_/?&=#%:]{0}";
            Matcher matcher = Pattern.compile(regex).matcher(content);
            int size = matcher.groupCount();

            Map<String,String> map = new HashMap<>();
            while (matcher.find()){
                int start =  matcher.start();
                int end = matcher.end();
                KLog.d("tag","start " + start + " end " + end);
                KLog.d("tag"," matcher.group() " +  matcher.group());
                map.put(matcher.group(),matcher.group());
            }
            KLog.d("tag","link条数 " + map.size());


            //拼接链接
            Drawable drawableLink = mContext.getResources().getDrawable(R.mipmap.icon_link_http);
            drawableLink.setBounds(0, 0, drawableLink.getMinimumWidth(), drawableLink.getMinimumHeight());
            //居中对齐imageSpan
            CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawableLink);
            SpannableString spanString2 = new SpannableString("icon");
            spanString2.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            msg.append(spanString2);

        }

        //通过uid加载布局
        private void getIconType(BaseViewHolder helper, CircleBean item) {
            String uid = item.getUid();
            String myUid = StringUtil.getUserInfoBean().getUid();
            if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
                helper.setVisible(R.id.circle_report,false);
                helper.setVisible(R.id.circle_remove,true);
            }else{
                helper.setVisible(R.id.circle_report,true);
                helper.setVisible(R.id.circle_remove,false);
            }
        }

        //通过type加载布局 并且 设置转发帖子的type
        private void getCircleType(BaseViewHolder helper, CircleBean item) {
            if(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.YC_PIC == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,true);
                helper.setVisible(R.id.part_yc_link,false);
                helper.setVisible(R.id.part_yc_acticle,false);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else if(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.YC_LINK == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,false);
                helper.setVisible(R.id.part_yc_link,true);
                helper.setVisible(R.id.part_yc_acticle,false);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else if(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.YC_ACTICLE == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,false);
                helper.setVisible(R.id.part_yc_link,false);
                helper.setVisible(R.id.part_yc_acticle,true);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else if(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.YC_TEXT == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,false);
                helper.setVisible(R.id.part_yc_link,false);
                helper.setVisible(R.id.part_yc_acticle,false);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else {
                helper.setVisible(R.id.transfer_zf_ll,true);
                helper.setVisible(R.id.part_yc_pic,false);
                helper.setVisible(R.id.part_yc_link,false);
                helper.setVisible(R.id.part_yc_acticle,false);

                if(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.ZF_PIC == item.getCircleType()){
                    helper.setVisible(R.id.part_zf_pic,true);
                    helper.setVisible(R.id.part_zf_link,false);
                    helper.setVisible(R.id.part_zf_article,false);
                    item.getP_blog().setCircleType(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.YC_PIC);
                }else if(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.ZF_LINK == item.getCircleType()){
                    helper.setVisible(R.id.part_zf_link,true);
                    helper.setVisible(R.id.part_zf_pic,false);
                    helper.setVisible(R.id.part_zf_article,false);
                    item.getP_blog().setCircleType(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.YC_LINK);
                }else if(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.ZF_ACTICLE == item.getCircleType()){
                    helper.setVisible(R.id.part_zf_article,true);
                    helper.setVisible(R.id.part_zf_link,false);
                    helper.setVisible(R.id.part_zf_pic,false);
                    item.getP_blog().setCircleType(com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew.YC_ACTICLE);
                }

            }
        }




        private void zanCommentChange(TextView com_text,TextView zan_num, ImageView zan_img,
                                      String good_num, int is_good,String com_num) {
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
            zan_num.setTypeface(typeface);
            com_text.setTypeface(typeface);

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
                zan_img.setImageResource(R.mipmap.icon_flash_priase_28);
                zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select_no));
            }else if("1".equals(is_good + "")){
                zan_img.setImageResource(R.mipmap.icon_flash_priase_select_28);
                zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select));
            }


            if(StringUtil.checkNull(com_num)){
                if("0".equals(com_num)){
                    com_text.setText("评论");
                }else{
                    int size = Integer.parseInt(com_num);
                    if(size > 99){
                        com_text.setText(99 + "");
                    }else{
                        com_text.setText(size + "");
                    }
                }
            }
        }



        private void likeBlog(CircleBean circleBean,int position) {
            Map<String,String> map = new HashMap<>();
            map.put("blog_id",circleBean.getId());
            int like = 0;
            if("0".equals(circleBean.getIs_like() + "")){
                like = 1;
            }else if("1".equals(circleBean.getIs_like() + "")){
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
                                circleBean.setIs_like(1);
                                circleBean.setLike_num((Integer.parseInt(circleBean.getLike_num()) + 1) + "");
                            }else{
                                circleBean.setIs_like(0);
                                circleBean.setLike_num((Integer.parseInt(circleBean.getLike_num()) - 1) + "");
                            }
                            notifyItemChanged(position);
                        }
                    });
        }




        private void showShareDialog(CircleBean item) {
            ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(mContext).builder();
            alertDialog.setShareDynamicView().setTitleGone();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.setOnDialogItemClickListener(position -> {
                switch (position) {
                    case 0:
                        ShareBean bean1 = new ShareBean();
                        bean1.setShareType("circle_link");
                        bean1.setLink(item.getShare_url());
                        bean1.setTitle(item.getBlog());
                        bean1.setContent(item.getBlog());
                        StringUtil.shareWxByWeb((Activity) mContext,bean1);
                        break;
                    case 1:
                        KLog.d("tag","朋友 是链接");
                        ShareBean bean = new ShareBean();
                        bean.setShareType("weixin_link");
                        bean.setLink(item.getShare_url());
                        bean.setTitle(item.getBlog());
                        bean.setContent(item.getBlog());
                        StringUtil.shareWxByWeb((Activity) mContext,bean);
                        break;
                    case 4:
                        KLog.d("tag", "转发到动态");
                        UIHelper.toTranspondActivity(mContext,item);
                        //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                        ((Activity)mContext).overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                        break;
                    default:
                }
            });
            alertDialog.show();
        }


        /**- ------------------------------- 浮层  --------------------------------- */

        private void showPopupWindow(CircleBean circleBean, View view) {
            //加载布局
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_report, null);
            PopupWindow mPopupWindow = new PopupWindow(inflate);
            TextView report = inflate.findViewById(R.id.report);
            TextView share = inflate.findViewById(R.id.share);
            //必须设置宽和高
            mPopupWindow.setWidth(SizeUtils.dp2px(80f));
            mPopupWindow.setHeight(SizeUtils.dp2px(88f));
            //点击其他地方隐藏,false为无反应
            mPopupWindow.setFocusable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //以view的左下角为原点，xoff为正表示向x轴正方向偏移像素
                mPopupWindow.showAsDropDown(view, -SizeUtils.dp2px(52f + 12), SizeUtils.dp2px(10f));
            }
            //对popupWindow进行显示
            mPopupWindow.update();
            //消失时将透明度设置回来
            mPopupWindow.setOnDismissListener(() -> {
                if (null != mContext) {
                    setBackgroundAlpha((Activity) mContext, 1f);
                }
            });

            report.setOnClickListener(view1 -> {
                reportBlog(circleBean);
                mPopupWindow.dismiss();
            });

            share.setOnClickListener(view1 -> {
//            showShareDialog();
                mPopupWindow.dismiss();
            });
        }




        private void reportBlog(CircleBean mCircleBean) {
            Map<String,String> map = new HashMap<>();
            map.put("blog_id",mCircleBean.getId());
            String result = RetrofitHelper.commonParam(map);
            RetrofitHelper.getApiService().reportBlog(result)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                    .subscribe(new BaseObserver<HttpResponse>() {
                        @Override
                        public void onSuccess(HttpResponse response) {
                            ToastUtils.showShort("举报成功");
                        }

                    });
        }


        /** --------------------------------- 删除帖子  ---------------------------------v*/


        private void showRemoveDialog(CircleBean circleBean, int position) {
            final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(mContext).builder();
            iosAlertDialog.setPositiveButton("删除", v -> {
                deleteBlog(circleBean,position);
            }).setNegativeButton("取消", v -> {
            }).setMsg("确定要删除这条动态？").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }



        private void deleteBlog(CircleBean mCircleBean, int position) {
            Map<String,String> map = new HashMap<>();
            map.put("blog_id",mCircleBean.getId());
            String result = RetrofitHelper.commonParam(map);
            RetrofitHelper.getApiService().deleteBlog(result)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                    .subscribe(new BaseObserver<HttpResponse>() {
                        @Override
                        public void onSuccess(HttpResponse response) {
                            mData.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        //设置页面的透明度   1表示不透明
        public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgAlpha;
            if (bgAlpha == 1) {
                //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            } else {
                //此行代码主要是解决在华为手机上半透明效果无效的bug
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
            activity.getWindow().setAttributes(lp);
        }

    }