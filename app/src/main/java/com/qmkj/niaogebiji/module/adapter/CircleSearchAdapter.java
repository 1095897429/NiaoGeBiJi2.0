package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.TimeUtils;
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
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.CenterAlignImageSpan;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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
public class CircleSearchAdapter extends BaseMultiItemQuickAdapter<MultiCircleNewsBean, BaseViewHolder> {

    public  static final int TYPE1 = 1;
    public static final int TYPE2 = 2;
    public static final int TYPE3 = 3;
    public static final int TYPE4 = 4;

    public static final int TYPEALL = 5;


    public CircleSearchAdapter(List<MultiCircleNewsBean> data) {

        super(data);
        //文本 + 图片
        addItemType(TYPE1, R.layout.first_circle_search_item1);
        //文本
        addItemType(TYPE2,R.layout.first_circle_search_item2);
        //文本  + link
        addItemType(TYPE3,R.layout.first_circle_search_item3);
        //文本 + 文章
        addItemType(TYPE4,R.layout.first_circle_search_item4);
        //all
        addItemType(TYPEALL,R.layout.first_circle_search_itemall);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiCircleNewsBean item) {

        CircleBean bean = item.getCircleBean();
        //正文
        helper.setText(R.id.content,bean.getBlog());
        //名字 + 公司 +  职位
        TextView name = helper.getView(R.id.name);
        if(bean.getUser_info() != null){
            name.setText(bean.getName() +
                   bean.getCompany_name() + bean.getPosition());
        }

        //是否认证
        if("0".equals(bean.getIs_auth())){
            name.setCompoundDrawables(null,null,null,null);
        }else if("1".equals(bean.getIs_auth())){
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            name.setCompoundDrawables(null,null,drawable,null);
        }

        //事件
        helper.itemView.setOnClickListener(view -> {
            String blog_id = bean.getId();
//            UIHelper.toCommentDetailActivity(mContext,blog_id,"1",helper.getAdapterPosition());
        });

        switch (helper.getItemViewType()){
            case TYPE1:
                List<String> imgs = bean.getImages();
                setImageStatus(helper,imgs);

                break;
            case TYPE2:

                break;
            case TYPE3:
                helper.setText(R.id.link_text,bean.getLink_title());
                helper.setText(R.id.link_http,bean.getLink());
                helper.setImageResource(R.id.link_img,R.mipmap.icon_link_logo);
                break;
            case TYPE4:
                if(!TextUtils.isEmpty(bean.getArticle_image())){
                    ImageUtil.load(mContext,bean.getArticle_image(),helper.getView(R.id.article_img));
                }

                helper.setText(R.id.acticle_title,bean.getArticle_title());
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

}
