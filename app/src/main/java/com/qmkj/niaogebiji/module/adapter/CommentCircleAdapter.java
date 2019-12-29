package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.event.BlogPriaseEvent;
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
 * 创建时间 2019-11-21
 * 描述:圈子 1级评论适配器
 */
public class CommentCircleAdapter extends BaseQuickAdapter<CommentCircleBean, BaseViewHolder> {

    private int myPotion;
    //对应具体的圈子数据
    private CircleBean mCircleBean;

    public void setCircleBean(CircleBean circleBean) {
        mCircleBean = circleBean;
    }

    public void setMyPotion(int myPotion1){
        this.myPotion = myPotion1;
    }

    public CommentCircleAdapter(@Nullable List<CommentCircleBean> data) {
        super(R.layout.first_comment_item,data);
    }

    private Limit2ReplyCircleAdapter mLimit2ReplyCircleAdapter;
    private List<CommentCircleBean> mLimitComments;


    @Override
    protected void convert(BaseViewHolder helper, CommentCircleBean item) {
        //设置子View点击事件
        helper
                .addOnClickListener(R.id.ll_has_second_comment)
                .addOnClickListener(R.id.comment_priase)
                .addOnClickListener(R.id.toFirstComment);

        User_info userInfo = item.getUser_info();

        helper.setText(R.id.nickname,userInfo.getName());


        //职位
        TextView sender_tag = helper.getView(R.id.name_tag);
        //是否认证
        if("1".equals(item.getUser_info().getAuth_email_status())||
            "1".equals(item.getUser_info().getAuth_card_status())){
            User_info temp = item.getUser_info();
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            sender_tag.setCompoundDrawables(null,null,drawable,null);
            sender_tag.setText( (TextUtils.isEmpty(temp.getCompany_name())?"":temp.getCompany_name()) +
                    (TextUtils.isEmpty(temp.getPosition())?"":temp.getPosition()));
        }else{
            sender_tag.setCompoundDrawables(null,null,null,null);
            sender_tag.setText("");
        }



        ImageUtil.load(mContext,userInfo.getAvatar(),helper.getView(R.id.head_icon));

        //评论文本
        helper.setText(R.id.comment_text,item.getComment());

        //发布时间
        if(StringUtil.checkNull(item.getCreated_at())){
            String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(item.getCreated_at()) * 1000L);
            helper.setText(R.id.time,s);
        }else{
            helper.setText(R.id.time,"");
        }

        //头像点击
        helper.getView(R.id.head_icon).setOnClickListener(v -> UIHelper.toUserInfoActivity(mContext,item.getUid()));


        //点赞数
        TextView zan_num = helper.getView(R.id.zan_num);
        ImageView imageView =  helper.getView( R.id.iamge_priase);
        zanChange(zan_num,imageView,item.getLike_num(),item.getIs_like());




        helper.getView(R.id.circle_priase).setOnClickListener(view -> {
            if("0".equals(item.getIs_like() + "")){
                likeComment(item,helper.getAdapterPosition());
            }else if("1".equals(item.getIs_like() + "")){
                likeComment(item,helper.getAdapterPosition());
            }
        });


        //二级评论数据
        List<CommentCircleBean> list = item.getComment_comment();
        mLimitComments = list;
        //二级评论布局
        LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
        talkManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView recyclerView =  helper.getView(R.id.show_limit_2_reply);
        recyclerView.setLayoutManager(talkManager);
        mLimit2ReplyCircleAdapter = new Limit2ReplyCircleAdapter(mLimitComments);
        mLimit2ReplyCircleAdapter.setFatherComment(item);

        helper.itemView.setOnClickListener(v -> {
            KLog.d("tag","11111");
            if(mToShowDialogListener != null){
                mToShowDialogListener.func(item);
            }
        });

        //禁用change动画
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mLimit2ReplyCircleAdapter);

        int size = 0;
        if(!TextUtils.isEmpty(item.getComment_num())){
            size = Integer.parseInt(item.getComment_num());
        }
        //如果一级评论下的二级评论条数大于 1 条才显示
        if(size != 0 ){
            helper.setVisible(R.id.ll_has_second_comment,true);
            if(size > 2){
                helper.setVisible(R.id.all_comment,true);
                helper.setText(R.id.all_comment,"查看全部" + size + "条回复");
            }else{
                helper.setVisible(R.id.all_comment,false);
            }
        }else{
            helper.setVisible(R.id.ll_has_second_comment,false);
        }


        getIconType(helper,item);

        //删除评论
        helper.getView(R.id.comment_delete).setOnClickListener(view -> showRemoveDialog(item,helper.getAdapterPosition()));
    }

    private void getIconType(BaseViewHolder helper, CommentCircleBean item) {
        String uid = item.getUid();
        String myUid = StringUtil.getUserInfoBean().getUid();
        if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
            helper.setVisible(R.id.comment_delete,true);
        }else{
            helper.setVisible(R.id.comment_delete,false);
        }
    }

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
                        //手动添加 评论数1
                        mCircleBean.setComment_num((Integer.parseInt(mCircleBean.getComment_num()) - 1) + "");
                        EventBus.getDefault().post(new BlogPriaseEvent(myPotion,mCircleBean.getIs_like(),
                                mCircleBean.getLike_num(),mCircleBean.getComment_num()));

                        if(mChangeDetailListener != null){
                            mChangeDetailListener.func(mCircleBean.getLike_num(),mCircleBean.getIs_like(),mCircleBean.getComment_num());
                        }
                        mData.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        //TODO 删除到没有数据时，显示空布局
                        if(mData.isEmpty()){
                            View view = LayoutInflater.from(mContext).inflate(R.layout.empty_layout,null);
                            ((ImageView)(view.findViewById(R.id.iv_empty))).setImageResource(R.mipmap.icon_empty_comment);
                            ((TextView)(view.findViewById(R.id.tv_empty))).setText("成为第一个评论者～");

                            setEmptyView(view);
                        }
                    }
                });
    }



    //显示一些数据
    private void zanChange(TextView zan_num,ImageView imageView, String good_num, int is_good) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        zan_num.setTypeface(typeface);
        if(StringUtil.checkNull(good_num)){
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
                        CommentCircleBean t =  mData.get(position);
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


    public ChangeDetailListener mChangeDetailListener;
    /**  更新上面circle的内容 */
    public interface ChangeDetailListener{
        void func(String good_num, int is_good,String com_num);
    }

    public void setChangeDetailListener(ChangeDetailListener changeDetailListener) {
        mChangeDetailListener = changeDetailListener;
    }


    public toShowDialogListener mToShowDialogListener;
    public interface toShowDialogListener{
        void func(CommentCircleBean item);
    }

    public void setToShowDialogListener(toShowDialogListener toShowDialogListener) {
        mToShowDialogListener = toShowDialogListener;
    }
}

