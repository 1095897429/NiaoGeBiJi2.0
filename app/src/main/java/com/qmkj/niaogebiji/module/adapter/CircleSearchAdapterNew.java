package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.qmkj.niaogebiji.module.widget.HorizontalSpacesDecoration;
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


        public CircleSearchAdapterNew(List<CircleBean> data) {
            super(R.layout.first_circle_search_itemall,data);
        }


    private CirclePicAdapter mCirclePicAdapter;
    private List<String> mPics = new ArrayList<>();

    private  int i ;
    private Rect rect ;
    private int j;
    private  Rect firstAndLastRect;


    @Override
    protected void convert(BaseViewHolder helper, CircleBean item) {
        getCircleType(helper,item);


            //名字
            TextView sender_name = helper.getView(R.id.sender_name);
            sender_name.setText(item.getName());
            //职位
            //职位
            TextView sender_tag = helper.getView(R.id.sender_tag);
            sender_tag.setText( (TextUtils.isEmpty(item.getCompany_name())?"":item.getCompany_name()) +
                (TextUtils.isEmpty(item.getPosition())?"":item.getPosition()));
            //是否认证
            if("1".equals(item.getIs_auth())){
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                sender_tag.setCompoundDrawables(null,null,drawable,null);
            }else{
                sender_tag.setCompoundDrawables(null,null,null,null);
            }
            //头像
            ImageUtil.load(mContext,item.getAvatar(),helper.getView(R.id.head_icon));


            TextView msg = helper.getView(R.id.content);
            if(item.getPcLinks() !=  null && !item.getPcLinks().isEmpty()){
                getIconLinkShow(item,msg);
            }else{
                msg.setText(item.getBlog());
            }


        //item点击事件
        helper.itemView.setOnClickListener(view -> UIHelper.toCommentDetailActivity(mContext,item.getId()));


        switch (item.getCircleType()){
            case YC_PIC:
                mPics = item.getImages();
                if(mPics != null && mPics.size() > 3){
                    mPics = mPics.subList(0,3);
                }
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
                    UIHelper.toPicPreViewActivity(mContext,  item.getImages(),position);
                });


                break;
            case YC_LINK:
                helper.setText(R.id.link_text,item.getLink_title());
                helper.setText(R.id.link_http,item.getLink());
                helper.setImageResource(R.id.link_img,R.mipmap.icon_link_logo);
                break;
            case YC_ACTICLE:
                if(!TextUtils.isEmpty(item.getArticle_image())){
                    ImageUtil.load(mContext,item.getArticle_image(),helper.getView(R.id.acticle_img));
                }
                helper.setText(R.id.acticle_title,item.getArticle_title());
                break;

            default:
        }
    }


    SpannableString spanString2;
    private void getIconLinkShow(CircleBean item, TextView msg) {
        String content = item.getBlog();
        String icon = "[icon]";
        //获取链接
        int size  =  item.getPcLinks().size();
        if(size >  0){
            for (int k = 0; k < size; k++) {
                content = content.replace(item.getPcLinks().get(k),icon);
            }
        }
        KLog.d("tag","最新字符串是 " + content);

        String newContent = content;

        //保存字符的开始下标
        List<Integer> pos = new ArrayList<>();

        int c = 0;
        for(int i = 0; i< size ;i++ ){
            c = content.indexOf(icon,c);
            //如果有S这样的子串。则C的值不是-1.
            if(c != -1){
                //记录找到字符的索引
                pos.add(c);
                //记录字符串后面的
                c = c + 1;
                //这里的c+1 而不是 c+ s.length();这是因为。如果str的字符串是“aaaa”， s = “aa”，则结果是2个。但是实际上是3个子字符串
                //将剩下的字符冲洗取出放到str中
                //content = content.substring(c + 1);
            }
            else {
                //i++;
                KLog.d("tag","没有");
                break;
            }
        }

        //拼接链接
        Drawable drawableLink = mContext.getResources().getDrawable(R.mipmap.icon_link_http);
        drawableLink.setBounds(0, 0, drawableLink.getMinimumWidth(), drawableLink.getMinimumHeight());

        spanString2 = new SpannableString(newContent);

        int w;
        for (int k = 0; k < size; k++) {
            w = k;
            int finalW = w;
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    String li = item.getPcLinks().get(finalW);
                    KLog.d("tag","点击了网页 " + li);
                    UIHelper.toWebViewActivity(mContext,li);
                }
            };

            //居中对齐imageSpan  -- 每次都要创建一个新的 才有效果
            CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawableLink);
            spanString2.setSpan(imageSpan, pos.get(k), pos.get(k) + icon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString2.setSpan(clickableSpan, pos.get(k), pos.get(k) + icon.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        //累加的原因找到了，用了append,需要用setText
        msg.setText(spanString2);
        //下面语句不写的话，点击clickablespan没效果
        msg.setMovementMethod(LinkMovementMethod.getInstance());

    }


    //通过type加载布局 并且 设置转发帖子的type
    private void getCircleType(BaseViewHolder helper, CircleBean item) {

        if(CircleSearchAdapterNew.YC_PIC == item.getCircleType()){
            helper.setVisible(R.id.part_yc_pic,true);
            helper.setVisible(R.id.part_yc_link,false);
            helper.setVisible(R.id.part_yc_acticle,false);
        }else if(CircleSearchAdapterNew.YC_LINK == item.getCircleType()){
            helper.setVisible(R.id.part_yc_pic,false);
            helper.setVisible(R.id.part_yc_link,true);
            helper.setVisible(R.id.part_yc_acticle,false);
        }else if(CircleSearchAdapterNew.YC_ACTICLE == item.getCircleType()){
            helper.setVisible(R.id.part_yc_pic,false);
            helper.setVisible(R.id.part_yc_link,false);
            helper.setVisible(R.id.part_yc_acticle,true);
        }else if(CircleSearchAdapterNew.YC_TEXT == item.getCircleType()) {
            helper.setVisible(R.id.part_yc_pic, false);
            helper.setVisible(R.id.part_yc_link, false);
            helper.setVisible(R.id.part_yc_acticle, false);
        }
    }



}