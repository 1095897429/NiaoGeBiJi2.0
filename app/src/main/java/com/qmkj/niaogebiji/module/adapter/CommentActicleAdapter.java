package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.NoLineCllikcSpan;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:文章1级评论适配器
 */
public class CommentActicleAdapter extends BaseQuickAdapter<CommentBean.FirstComment, BaseViewHolder> {

    public CommentActicleAdapter(@Nullable List<CommentBean.FirstComment> data) {
        super(R.layout.first_comment_item_new,data);
    }

    private Limit2ReplyAdapter mLimit2ReplyAdapter;
    private List<CommentBean.FirstComment> mLimitComments;





    private void setTextLine(TextView msg, String content, CommentBean.FirstComment item){
        //获取文字的总宽度
        float text_with = msg.getPaint().measureText(content);
        //根据自己的布局获取textview的总宽度，我的textview距离两边分别是10dp
        int tv_width = ScreenUtils.getScreenWidth()- SizeUtils.dp2px(16 * 2);
        //总长度除了textview长度，得到行数
        float lines_float = text_with/tv_width;
        //向上取整
        int lines = (int)Math.ceil(lines_float);
        item.setLines(lines);
        KLog.d("tag","文本的长度是 " + StringUtil.getCounts(content));
        if(StringUtil.getCounts(content) > 140 && lines > 5) {
            KLog.d("tag", "行数大于5行  " + " 行数是 " + lines);

            int perSize = (int) (StringUtil.getCounts(content) / (lines * 1.0f));
            KLog.d("tag","每行显示的字数是 " + perSize);

        }else{
            KLog.d("tag", "行数小于5行  " + " 行数是 " + lines);
        }

        if(StringUtil.getCounts(content)> 140){
            item.setLines(5);
        }else{
            item.setLines(lines);
        }
    }

    private void setTextOrigin(TextView msg, String content,CommentBean.FirstComment circleBean,int position){

        //这里获取到绘制过程中的textview行数
        int lineCount = circleBean.getLines();
        //此处根据你想设置的最大行数进行判断
        if (StringUtil.getCounts(content) > 140 && lineCount >= 5) {
            //4是索引 5是行数
//            msg.setLines(5);
//            String text = content.substring(0, circleBean.getPerSize() * 5) +"...全文";

//            if(msg.getLayout() != null){
//                int lineEndIndex = msg.getLayout().getLineEnd(4); //设置第4行打省略号
//                KLog.d("tag","lineEndIndex " + lineEndIndex);
//            }


            //取出第一行 0
            //取出第五行 4
            int width = ScreenUtils.getScreenWidth()- SizeUtils.dp2px(16 * 2 + 32 + 8 );
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            float spacingMultiplier = 1;
            float spacingAddition = 0;
            boolean includePadding = false;

            StaticLayout myStaticLayout = new StaticLayout(content, msg.getPaint(), width, alignment, spacingMultiplier, spacingAddition, includePadding);

            String firstLineText =  content.substring(0,myStaticLayout.getLineEnd(4));
            KLog.d("tag", "text1111  " + firstLineText);


            //减 3 意味着 ...占一个字数
            String text = firstLineText.substring(0,firstLineText.length() - 3) + "...全文";
//            String text = firstLineText;


            KLog.d("tag", "text  " + text);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue)), text.length() - 2, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            NoLineCllikcSpan clickableSpan = new NoLineCllikcSpan() {
                @Override
                public void onClick(View widget) {
                    if(null != mToShowActicleDialogListener){
                        mToShowActicleDialogListener.func(circleBean,position);
                    }
                }
            };
            spannableString.setSpan(clickableSpan, text.length() - 2,   text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            msg.setText(spannableString);
            msg.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            msg.setText(content);
        }

    }



    @Override
    protected void convert(BaseViewHolder helper,CommentBean.FirstComment item) {

        //评论正文
        TextView comment_text = helper.getView(R.id.comment_text);

        setTextLine(comment_text,item.getMessage(),item);

        if(!TextUtils.isEmpty(item.getMessage())){
            comment_text.setText(item.getMessage());
            setTextOrigin(comment_text,item.getMessage(),item,helper.getAdapterPosition());
        }




        //设置子View点击事件
        helper.addOnClickListener(R.id.comment_delete)
                .addOnClickListener(R.id.ll_has_second_comment)
                .addOnClickListener(R.id.comment_priase)
                .addOnClickListener(R.id.toFirstComment);

        getIconType(helper,item);

        //评论时间
        if(StringUtil.checkNull(item.getDateline())){
            String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(item.getDateline()) * 1000L);
            helper.setText(R.id.time,s);
        }else{
            helper.setText(R.id.time,"");
        }


        //评论者
        helper.setText(R.id.nickname,item.getUsername());
        //职位
        TextView sender_tag = helper.getView(R.id.name_tag);

//        if("1".equals(item.getAuth_status())){
//            sender_tag.setText((TextUtils.isEmpty(item.getCompany_name())?"":item.getCompany_name()) +
//                    (TextUtils.isEmpty(item.getPosition())?"":item.getPosition()));
//        }else{
//            sender_tag.setText("TA还未职业认证");
//        }



        if(!StringUtil.checkNull((item.getCompany_name()))
                && !StringUtil.checkNull((item.getPosition()))){
            sender_tag.setText("TA 还未职业认证");
        }else{
            sender_tag.setText( (StringUtil.checkNull((item.getCompany_name()))?item.getCompany_name()+" ":"")+
                    (TextUtils.isEmpty(item.getPosition())?"":item.getPosition()));
        }


        //是否认证
        if("1".equals(item.getAuth_status())){
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            sender_tag.setCompoundDrawables(null,null,drawable,null);
        }else{
            sender_tag.setCompoundDrawables(null,null,null,null);
        }


        //评论者头像
        if(!TextUtils.isEmpty(item.getAvatar())){
            ImageUtil.loadByDefaultHead(mContext,item.getAvatar(),helper.getView(R.id.head_icon));
        }




        //点赞数
        TextView zan_num = helper.getView(R.id.zan_num);
        ImageView imageView =  helper.getView( R.id.iamge_priase);
        zanChange(zan_num,imageView,item.getGood_num(),item.getIs_good());

        helper.getView(R.id.circle_priase).setOnClickListener(view -> {
            if("0".equals(item.getIs_good() + "")){
                MobclickAgentUtils.onEvent("index_detail_comment_laud"+ (helper.getAdapterPosition()  + 1) +"_2_0_0");

                goodArticle(item,helper.getAdapterPosition());
            }else if("1".equals(item.getIs_good() + "")){
                cancelGoodArticle(item,helper.getAdapterPosition());
            }
        });


        //二级评论数据
        List<CommentBean.FirstComment> list = item.getCommentslist();
        mLimitComments = list;
        if(mLimitComments.size() > 2){
            mLimitComments = mLimitComments.subList(0,2);
        }
        //二级评论布局
        LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
        talkManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView recyclerView =  helper.getView(R.id.show_limit_2_reply);
        recyclerView.setLayoutManager(talkManager);
        mLimit2ReplyAdapter = new Limit2ReplyAdapter(mLimitComments);
        mLimit2ReplyAdapter.setFatherComment(item);

        mLimit2ReplyAdapter.setOnActicleToSecondListener(new Limit2ReplyAdapter.OnActicleToSecondListener() {
            @Override
            public void toActicleSecond() {
                if(null != mToShowActicleDialogListener){
                    mToShowActicleDialogListener.func(item,helper.getAdapterPosition());
                }
            }
        });

        //禁用change动画
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mLimit2ReplyAdapter);


        //如果一级评论下的二级评论条数大于 1 条才显示
        if(null != list && !list.isEmpty()){
            helper.setVisible(R.id.ll_has_second_comment,true);
            if( list.size() > 2){
                helper.setVisible(R.id.all_comment,true);
                helper.setText(R.id.all_comment,"查看全部" + list.size() + "条回复");
            }else{
                helper.setVisible(R.id.all_comment,false);
            }

        }else{
            helper.setVisible(R.id.ll_has_second_comment,false);
        }



        //评论删除
        helper.getView(R.id.comment_delete).setOnClickListener(view -> {
            showRemoveDialog(item,helper.getAdapterPosition());
        });


        //去个人中心
        helper.getView(R.id.head_icon).setOnClickListener(view -> {
            if(!TextUtils.isEmpty(item.getUid())){
                UIHelper.toUserInfoActivity(mContext,item.getUid());
            }
        });

    }

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
                        CommentBean.FirstComment t =  mData.get(position);
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
                        CommentBean.FirstComment t =  mData.get(position);
                        t.setIs_good(0);
                        t.setGood_num((Integer.parseInt(t.getGood_num()) - 1) + "");

                        notifyItemChanged(position);
                    }
                });
    }



    //通过uid加载布局 -- 自己的可以删除，其他的不管
    private void getIconType(BaseViewHolder helper, CommentBean.FirstComment item) {
        if(null != StringUtil.getUserInfoBean() && null != item){
            String uid = item.getUid();
            String myUid = StringUtil.getUserInfoBean().getUid();
            if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
                helper.setVisible(R.id.comment_delete,true);
            }else{
                helper.setVisible(R.id.comment_delete,false);
            }
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
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public toShowActicleDialogListener mToShowActicleDialogListener;
    public interface toShowActicleDialogListener{
        void func(CommentBean.FirstComment item,int position);
    }

    public void setToShowActicleDialogListener(toShowActicleDialogListener toShowActicleDialogListener) {
        mToShowActicleDialogListener = toShowActicleDialogListener;
    }
}

