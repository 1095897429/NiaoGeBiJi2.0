package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
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
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.TopicFocusBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.HorizontalSpacesDecoration;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-21
 * 描述:圈子新布局
 * 1.设置布局展示的type
 *
 * 1.圈子展示 +关注展示
 * 2.我的个人信息展示 -- 注意position-- 用另一套
 */
public class CircleRecommentAdapterNew extends BaseQuickAdapter<CircleBean, BaseViewHolder> {

    public  static final int YC_PIC = 1;

    public  static final int YC_LINK = 2;

    public  static final int YC_ACTICLE = 3;

    public  static final int YC_TEXT = 4;

    public  static final int ZF_PIC = 11;

    public  static final int ZF_LINK = 22;

    public  static final int ZF_ACTICLE = 33;

    public  static final int ZF_TEXT = 44;

    public  static final int FOCUS_TOPIC = 55;

    //用户在个人信息界面删除的时候，position多1(头布局)
    private boolean  isFromUserInfo ;

    public boolean isFromUserInfo() {
        return isFromUserInfo;
    }

    public void setFromUserInfo(boolean fromUserInfo) {
        isFromUserInfo = fromUserInfo;
    }


    private String chainName;

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public CircleRecommentAdapterNew(List<CircleBean> data) {
        super(R.layout.first_circle_item_all,data);
    }


    private CirclePicAdapter mCirclePicAdapter;
    private List<String> mPics = new ArrayList<>();
    private List<String> mPics_transger = new ArrayList<>();
    private CircleTransferPicAdapter mCircleTransferPicAdapter;

    private  int i ;
    private  Rect rect ;
    private int j;
    private  Rect firstAndLastRect;
    private  Rect firstAndLastRect1;
    HorizontalSpacesDecoration spacesDecoration1;

    @Override
    protected void convert(BaseViewHolder helper, CircleBean item) {

        getCircleType(helper,item);

        getIconType(helper,item);


        if(CircleRecommentAdapterNew.FOCUS_TOPIC == item.getCircleType()){
            //设置数据
            initFocusLayout(helper);
            return;
        }


        if(item.getUser_info() != null){
            User_info userInfo = item.getUser_info();
            //名字
            TextView sender_name = helper.getView(R.id.sender_name);

            sender_name.setText(userInfo.getName());

//            sender_name.setText(StringEscapeUtils.unescapeJava(userInfo.getName().replace("\\\\u","\\u")));


            //职位
            TextView sender_tag = helper.getView(R.id.sender_tag);

            //之前的逻辑
//            if("1".equals(userInfo.getAuth_email_status()) ||
//                "1".equals(userInfo.getAuth_card_status())){
//                sender_tag.setText( (StringUtil.checkNull((userInfo.getCompany_name()))?userInfo.getCompany_name():"") +
//                        (TextUtils.isEmpty(userInfo.getPosition())?"":userInfo.getPosition()));
//            }else{
//                sender_tag.setText("TA 还未职业认证");
//            }

            //TODO 2020.1.7 根据返回的内容
            if(!StringUtil.checkNull((userInfo.getCompany_name()))
                && !StringUtil.checkNull((userInfo.getPosition()))){
                sender_tag.setText("TA 还未职业认证");
            }else{
                sender_tag.setText( (StringUtil.checkNull((userInfo.getCompany_name()))?userInfo.getCompany_name() + " ":"") +
                        (TextUtils.isEmpty(userInfo.getPosition())?"":userInfo.getPosition()));
            }



            //是否认证
            if("1".equals(userInfo.getAuth_email_status()) || "1".equals(userInfo.getAuth_card_status())){
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                sender_tag.setCompoundDrawables(null,null,drawable,null);
            }else{
                sender_tag.setCompoundDrawables(null,null,null,null);
            }

            //头像
            ImageUtil.loadByDefaultHead(mContext,userInfo.getAvatar(),helper.getView(R.id.head_icon));
            //时间
            if(StringUtil.checkNull(item.getCreated_at())){
                String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(item.getCreated_at()) * 1000L);
                helper.setText(R.id.publish_time,s);
            }

            //徽章
            LinearLayout ll_badge = helper.getView(R.id.ll_badge);
            if(userInfo.getBadge() != null && !userInfo.getBadge().isEmpty()){
                ll_badge.setVisibility(View.VISIBLE);
                ll_badge.removeAllViews();
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
                    ll_badge.addView(imageView);
                }
            }else {
                ll_badge.removeAllViews();
                ll_badge.setVisibility(View.GONE);
            }

            //点赞数
            TextView com_num = helper.getView(R.id.comment);
            TextView zan_num = helper.getView(R.id.zan_num);
            ImageView imageView =  helper.getView( R.id.image_circle_priase);
            zanCommentChange(com_num,zan_num,imageView,item.getLike_num(),item.getIs_like(),item.getComment_num());


            TextView msg = helper.getView(R.id.content);
            if(item.getPcLinks() !=  null && !item.getPcLinks().isEmpty()){
                //对有links的原创文本进行富文本
                StringUtil.getIconLinkShow(item, (Activity) mContext,msg);
            }else{
                msg.setText(item.getBlog());

//                msg.setText(StringEscapeUtils.unescapeJava(item.getBlog().replace("\\\\u","\\u")));

            }

        }


        if(item.getP_blog() != null){
            //转发文本
            TextView trans_msg = helper.getView(R.id.transfer_zf_content);
            if(item.getP_blog().getPcLinks() !=  null && !item.getP_blog().getPcLinks().isEmpty()){
                //对有links的转发文本进行富文本
                StringUtil.getTransIconLinkShow(item.getP_blog(), (Activity) mContext,trans_msg);
            }else{
                trans_msg.setText(item.getP_blog().getBlog());
            }


            if(item.getP_blog() != null && item.getP_blog().getP_user_info() != null){
                CircleBean.P_user_info temp =item.getP_blog().getP_user_info();
                TextView  transfer_zf_author  = helper.getView(R.id.transfer_zf_author);

//                if("1".equals(temp.getAuth_email_status()) ||
//                        "1".equals(temp.getAuth_card_status())){
//                    transfer_zf_author.setText(temp.getName() +(TextUtils.isEmpty(temp.getCompany_name())?"":item.getCompany_name() + " ") +
//                            (TextUtils.isEmpty(temp.getPosition())?"":temp.getPosition()));
//                }else{
//                    transfer_zf_author.setText(temp.getName() + "  TA 还未职业认证");
//                }

                //TODO 2020.1.8 这处没改
                if(!StringUtil.checkNull((temp.getCompany_name()))
                        && !StringUtil.checkNull((temp.getPosition()))){
                    transfer_zf_author.setText(temp.getName() + " TA 还未职业认证");
                }else{
                    transfer_zf_author.setText(temp.getName() + " " + (StringUtil.checkNull((temp.getCompany_name()))?temp.getCompany_name() + " ":"") +
                            (TextUtils.isEmpty(temp.getPosition())?"":temp.getPosition()));
                }

                //是否认证
                if("1".equals(temp.getAuth_email_status()) || "1".equals(temp.getAuth_card_status())){
                    Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                    drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    transfer_zf_author.setCompoundDrawables(null,null,drawable,null);
                }else{
                    transfer_zf_author.setCompoundDrawables(null,null,null,null);
                }


            }
        }

        //item点击事件
        helper.itemView.setOnClickListener(v -> {
            if(helper.getAdapterPosition() <= 9){
                if("推荐".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_recommendlist_quanzi"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }else if("关注 ".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_follow_quanzi"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }
            }

            if(StringUtil.isFastClick()){
                return;
            }

            UIHelper.toCommentDetailActivity(mContext,item.getId());
        });



        //item长按事件
        helper.itemView.setOnLongClickListener(view -> {
            StringUtil.copyLink(item.getBlog());
            ToastUtils.showShort("复制成功");
            return true;
        });


        //link点击
        helper.getView(R.id.part_yc_link).setOnClickListener(view -> {
            if(StringUtil.isFastClick()){
                return;
            }
            UIHelper.toWebViewActivity(mContext, item.getLink());
        });

        //文章点击
        helper.getView(R.id.part_yc_acticle).setOnClickListener(view -> {
                if(StringUtil.isFastClick()){
                    return;
                }
                 UIHelper.toNewsDetailActivity(mContext, item.getArticle_id());
                });



        //点赞
        helper.getView(R.id.circle_priase).setOnClickListener(view -> {
            if(helper.getAdapterPosition() <= 9){
                if("推荐".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_recommendlist_laud"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }else if("关注 ".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_follow_laud"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }
            }
            if(StringUtil.isFastClick()){
                return;
            }
            likeBlog(item,helper.getAdapterPosition());
        });

        //评论
        helper.getView(R.id.circle_comment).setOnClickListener(view -> {
            if(helper.getAdapterPosition() <= 9){

                if("推荐".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_recommendlist_comment"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }else if("关注 ".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_follow_comment"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }
            }
            if(StringUtil.isFastClick()){
                return;
            }
            UIHelper.toCommentDetailActivity(mContext,item.getId());
        });

        //分享
        helper.getView(R.id.circle_share).setOnClickListener(view -> {
            if(helper.getAdapterPosition() <= 9){

                if("推荐".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_recommendlist_share"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }else if("关注 ".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_follow_share"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }
            }
            if(StringUtil.isFastClick()){
                return;
            }
            showShareDialog(item,helper.getAdapterPosition());
        });

        //转发帖子点击事件
        helper.getView(R.id.transfer_zf_ll).setOnClickListener(view -> {
            if(StringUtil.isFastClick()){
                return;
            }
            UIHelper.toCommentDetailActivity(mContext,item.getP_blog().getId());

        });

        //点击话题事件
        helper.getView(R.id.ll_topic).setOnClickListener(v -> {
            if(StringUtil.isFastClick()){
                return;
            }
            UIHelper.toTopicDetailActivity(mContext);
        });

        //转发图片点击预览
//        helper.getView(R.id.part_zf_pic).setOnClickListener(view -> {
//            UIHelper.toCommentDetailActivity(mContext,item.getP_blog().getId());
//
//
//        });

        //帖子举报/删除 -- 为了增大触摸面积
        helper.getView(R.id.ll_report).setOnClickListener(view -> {
            if(StringUtil.isFastClick()){
                return;
            }
            String uid = item.getUid();
            String myUid = StringUtil.getUserInfoBean().getUid();
            if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
                showRemoveDialog(item,helper.getAdapterPosition());
            }else{
                showPopupWindow(item,helper.getView(R.id.circle_report));
                StringUtil.setBackgroundAlpha((Activity) mContext, 0.6f);
            }
        });

        //去个人中心
        helper.getView(R.id.part1111).setOnClickListener(view -> {
            if(StringUtil.isFastClick()){
                return;
            }
            if(helper.getAdapterPosition() <= 9){
                if("推荐".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_recommendlist_"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }else if("关注 ".equals(chainName)){
                    MobclickAgentUtils.onEvent("quanzi_follow_"+ (helper.getAdapterPosition() + 1) +"_2_0_0");
                }
            }
            UIHelper.toUserInfoV2Activity(mContext,item.getUid());
        });


        switch (item.getCircleType()){
            case 1:
                mPics = item.getImages();
                if(mPics != null && mPics.size() > 3){
                    mPics = mPics.subList(0,3);

                }
                //二级评论布局
                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                RecyclerView recyclerView =  helper.getView(R.id.pic_recyler);
                recyclerView.setLayoutManager(layoutManager);

                //TODO 如果没有加这个，这个会导致重复添加
                if(firstAndLastRect == null){
                    i = SizeUtils.dp2px( 8);
                    rect =  new Rect(0, 0, i, 0);
                    j = SizeUtils.dp2px( 0);
                    firstAndLastRect = new Rect(j, 0, i, 0);
                    HorizontalSpacesDecoration spacesDecoration = new HorizontalSpacesDecoration(rect, firstAndLastRect);
                    recyclerView.addItemDecoration(spacesDecoration);

                }

                mCirclePicAdapter = new CirclePicAdapter(mPics);
                mCirclePicAdapter.setTotalSize(item.getImages().size());
                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mCirclePicAdapter);


                //预览事件
                mCirclePicAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","点击预览");
                    if(StringUtil.isFastClick()){
                        return;
                    }
                    if(!item.getImages().isEmpty()){
                        UIHelper.toPicPreViewActivity(mContext,  item.getImages(),position,true);
                    }
                });


                break;
            case 2:
                helper.setText(R.id.link_text,item.getLink_title());
                String tempLink   = StringUtil.getDomain(item.getLink());
                helper.setText(R.id.link_http,tempLink);
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
                GridLayoutManager layoutManager2 = new GridLayoutManager(mContext, 3);
                RecyclerView recyclerView2 =  helper.getView(R.id.pic_recyler_transfer);
                recyclerView2.setLayoutManager(layoutManager2);



//                if(firstAndLastRect1 == null){
//                    int i1 = SizeUtils.dp2px( 6);
//                    Rect rect1 = new Rect(0, 0, i1, 0);
//                    int j1 = SizeUtils.dp2px( 0);
//                    firstAndLastRect1  = new Rect(j1, 0, i1, 0);
//                    HorizontalSpacesDecoration spacesDecoration1= new HorizontalSpacesDecoration(rect1, firstAndLastRect1);
//                    recyclerView2.addItemDecoration(spacesDecoration1);
//                }


                mCircleTransferPicAdapter = new CircleTransferPicAdapter(mPics_transger);
                mCircleTransferPicAdapter.setTotalSize(item.getP_blog().getImages().size());
                //禁用change动画
                ((SimpleItemAnimator)recyclerView2.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView2.setAdapter(mCircleTransferPicAdapter);

                mCircleTransferPicAdapter.setOnItemClickListener((adapter, view, position) -> {
                    if (StringUtil.isFastClick()) {
                        return;
                    }

                    if(!item.getP_blog().getImages().isEmpty()){
                        UIHelper.toPicPreViewActivity(mContext, (ArrayList<String>) item.getP_blog().getImages(), position, true);

                    }
                });

                break;
            case 22:
                helper.setText(R.id.transfer_link_text,item.getP_blog().getLink_title());

                String tempLink2   = StringUtil.getDomain(item.getP_blog().getLink());
                helper.setText(R.id.transfer_link_http,tempLink2);
                helper.setImageResource(R.id.transer_link_img,R.mipmap.icon_link_logo);
                //转发link跳转
                helper.getView(R.id.part_zf_article).setOnClickListener(v ->{
                            if (StringUtil.isFastClick()) {
                                return;
                            }
                            UIHelper.toWebViewActivity(mContext,item.getP_blog().getLink());
                        }
                      );

                break;
            case 33:
                if(!TextUtils.isEmpty(item.getP_blog().getArticle_image())){
                    ImageUtil.load(mContext,item.getP_blog().getArticle_image(),helper.getView(R.id.transfer_article_img));
                }
                helper.setText(R.id.transfer_article_title,item.getP_blog().getArticle_title());

                //转发文章跳转
                helper.getView(R.id.part_zf_article).setOnClickListener(v -> {
                    if (StringUtil.isFastClick()) {
                        return;
                    }
                    UIHelper.toNewsDetailActivity(mContext,item.getP_blog().getArticle_id());
                });

                break;
                default:
        }


    }


    TopicRecommendFocusAdapter mTopicRecommendFocusAdapter;
    List<TopicFocusBean> list;

    public void setList(List<TopicFocusBean> list) {
        this.list = list;
    }

    private void initFocusLayout(BaseViewHolder helper) {
        RecyclerView recyclerView = helper.getView(R.id.recycler00);
        //二级
        LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
        talkManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(talkManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        mTopicRecommendFocusAdapter = new TopicRecommendFocusAdapter(list);

        //禁用change动画
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mTopicRecommendFocusAdapter);

        helper.getView(R.id.more_topic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UIHelper.toTopListActivity(mContext);
            }
        });



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

        helper.setVisible(R.id.ll_topic,true);

        if(CircleRecommentAdapterNew.FOCUS_TOPIC == item.getCircleType()){
            helper.setVisible(R.id.rl_circle,false);
            helper.setVisible(R.id.ll_focus_topic,true);

        }else{
            helper.setVisible(R.id.ll_focus_topic,false);
            helper.setVisible(R.id.rl_circle,true);



            helper.setVisible(R.id.transfer_zf_ll,false);
            helper.setVisible(R.id.transfer_yc_ll,true);

            if(CircleRecommentAdapterNew.YC_PIC == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,true);
                helper.setVisible(R.id.part_yc_link,false);
                helper.setVisible(R.id.part_yc_acticle,false);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else if(CircleRecommentAdapterNew.YC_LINK == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,false);
                helper.setVisible(R.id.part_yc_link,true);
                helper.setVisible(R.id.part_yc_acticle,false);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else if(CircleRecommentAdapterNew.YC_ACTICLE == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,false);
                helper.setVisible(R.id.part_yc_link,false);
                helper.setVisible(R.id.part_yc_acticle,true);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else if(CircleRecommentAdapterNew.YC_TEXT == item.getCircleType()){
                helper.setVisible(R.id.part_yc_pic,false);
                helper.setVisible(R.id.part_yc_link,false);
                helper.setVisible(R.id.part_yc_acticle,false);
                helper.setVisible(R.id.transfer_zf_ll,false);
            }else {
                helper.setVisible(R.id.transfer_zf_ll,true);
                helper.setVisible(R.id.transfer_yc_ll,false);



                if(CircleRecommentAdapterNew.ZF_PIC == item.getCircleType()){
                    helper.setVisible(R.id.part_zf_pic,true);
                    helper.setVisible(R.id.part_zf_link,false);
                    helper.setVisible(R.id.part_zf_article,false);
                    item.getP_blog().setCircleType(CircleRecommentAdapterNew.YC_PIC);
                }else if(CircleRecommentAdapterNew.ZF_LINK == item.getCircleType()){
                    helper.setVisible(R.id.part_zf_link,true);
                    helper.setVisible(R.id.part_zf_pic,false);
                    helper.setVisible(R.id.part_zf_article,false);
                    item.getP_blog().setCircleType(CircleRecommentAdapterNew.YC_LINK);
                }else if(CircleRecommentAdapterNew.ZF_ACTICLE == item.getCircleType()){
                    helper.setVisible(R.id.part_zf_article,true);
                    helper.setVisible(R.id.part_zf_link,false);
                    helper.setVisible(R.id.part_zf_pic,false);
                    item.getP_blog().setCircleType(CircleRecommentAdapterNew.YC_ACTICLE);
                }else if(CircleRecommentAdapterNew.ZF_TEXT == item.getCircleType()){
                    helper.setVisible(R.id.part_zf_pic,false);
                    helper.setVisible(R.id.part_zf_link,false);
                    helper.setVisible(R.id.part_zf_article,false);
                    item.getP_blog().setCircleType(CircleRecommentAdapterNew.YC_TEXT);
                }

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
//                        notifyItemChanged(position);

                        notifyDataSetChanged();


                    }
                });
    }




    private void showShareDialog(CircleBean item, int adapterPosition) {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(mContext).builder();
        alertDialog.setShareDynamicView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    if("推荐".equals(chainName)){
                        MobclickAgentUtils.onEvent("quanzi_recommendlist_share_moments"+ (adapterPosition + 1) +"_2_0_0");
                    }else if("关注 ".equals(chainName)){
                        MobclickAgentUtils.onEvent("quanzi_follow_share_moments"+ (adapterPosition + 1) +"_2_0_0");
                    }

                    ShareBean bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setLink(item.getShare_url());
                    String name1 = "";
                    if( null != item.getUser_info()){
                        name1 = item.getUser_info().getName();
                    }
                    bean1.setTitle("分享一条" + name1 + "的营销圈动态");
                    bean1.setContent(item.getBlog());
                    String img = "";
                    if(item.getImages() != null &&  !item.getImages().isEmpty()){
                        img  = item.getImages().get(0);
                    }else if(item.getUser_info() != null){
                        img = item.getUser_info().getAvatar();
                    }
                    bean1.setImg(img);
                    StringUtil.shareWxByWeb((Activity) mContext,bean1);
                    break;
                case 1:
                    if("推荐".equals(chainName)){
                        MobclickAgentUtils.onEvent("quanzi_recommendlist_share_friends"+ (adapterPosition + 1) +"_2_0_0");
                    }else if("关注 ".equals(chainName)){
                        MobclickAgentUtils.onEvent("quanzi_follow_share_friends"+ (adapterPosition + 1) +"_2_0_0");
                    }
                    ShareBean bean = new ShareBean();
                    bean.setShareType("weixin_link");
                    bean.setLink(item.getShare_url());
                    String name = "";
                    if( null != item.getUser_info()){
                        name = item.getUser_info().getName();
                    }
                    bean.setTitle("分享一条" + name + "的营销圈动态");
                    bean.setContent(item.getBlog());
                    String img2 = "";
                    if(item.getImages() != null &&  !item.getImages().isEmpty()){
                        img2  = item.getImages().get(0);
                    }else if(item.getUser_info() != null){
                        img2 = item.getUser_info().getAvatar();
                    }
                    bean.setImg(img2);
                    StringUtil.shareWxByWeb((Activity) mContext,bean);
                    break;
                case 4:
                    if("推荐".equals(chainName)){
                        MobclickAgentUtils.onEvent("quanzi_follow_share_forward"+ (adapterPosition + 1) +"_2_0_0");
                    }else if("关注 ".equals(chainName)){
                        MobclickAgentUtils.onEvent("quanzi_follow_share_forward"+ (adapterPosition + 1) +"_2_0_0");
                    }
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

    private void showPopupWindow(CircleBean circleBean,View view) {
        //加载布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_report, null);
        PopupWindow mPopupWindow = new PopupWindow(inflate);
        TextView report = inflate.findViewById(R.id.report);
        TextView share = inflate.findViewById(R.id.share);
        //必须设置宽和高
        mPopupWindow.setWidth(SizeUtils.dp2px(80f));
        mPopupWindow.setHeight(SizeUtils.dp2px(44f));
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
                StringUtil.setBackgroundAlpha((Activity) mContext, 1f);
            }
        });

        report.setOnClickListener(view1 -> {
            reportBlog(circleBean);
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
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

                        if(isFromUserInfo){
                            mData.remove(position - 1);
                        }else{
                            mData.remove(position);
                        }

                        notifyDataSetChanged();
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
