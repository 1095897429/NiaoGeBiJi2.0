package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.PeopleFocusEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:搜索人脉适配器
 * 1.adater中并不能用eventbus接受事件
 */
public class PeopleItemAdapter extends BaseQuickAdapter<RegisterLoginBean.UserInfo, BaseViewHolder> {

    public PeopleItemAdapter(List<RegisterLoginBean.UserInfo> data) {
        super(R.layout.search_people_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RegisterLoginBean.UserInfo item) {

        helper.addOnClickListener(R.id.focus).addOnClickListener(R.id.already_focus);

        helper.setText(R.id.sender_name,item.getName());


        TextView  sender_tag = helper.getView(R.id.sender_tag);

        //认证
        if("1".equals(item.getAuth_email_status()) || "1".equals(item.getAuth_card_status())){
            sender_tag.setText( (TextUtils.isEmpty(item.getCompany_name())?"":item.getCompany_name()) +
                    (TextUtils.isEmpty(item.getPosition())?"":item.getPosition()));
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            sender_tag.setCompoundDrawables(null,null,drawable,null);
        }else{
            sender_tag.setCompoundDrawables(null,null,null,null);
        }


        //徽章
        LinearLayout ll_badge = helper.getView(R.id.ll_badge);
        if(item.getBadge_arr() != null && !item.getBadge_arr().isEmpty()){
            ll_badge.setVisibility(View.VISIBLE);
            ll_badge.removeAllViews();
            for (int i = 0; i < item.getBadge_arr().size(); i++) {
                ImageView imageView = new ImageView(mContext);
                String icon = item.getBadge_arr().get(i).getIcon();
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

        ImageUtil.loadByDefaultHead(mContext,item.getAvatar(),helper.getView(R.id.head_icon));


        showStateByFollow(item,helper);

        helper.itemView.setOnClickListener(view -> {

            if(helper.getAdapterPosition() <= 2) {
                MobclickAgentUtils.onEvent("index_search_person_" + (helper.getAdapterPosition() + 1) + "_2_0_0");
            }


            RegisterLoginBean.UserInfo temp = mData.get(helper.getAdapterPosition());
            UIHelper.toUserInfoActivity(mContext,temp.getUid());
        });

        helper.getView(R.id.focus).setOnClickListener(view -> {
            followUser(helper,item.getUid());
        });

        helper.getView(R.id.already_focus).setOnClickListener(view -> {

            unfollowUser(helper,item.getUid());
        });


    }




    private void showStateByFollow(RegisterLoginBean.UserInfo temp, BaseViewHolder helper) {
        //是自己的话，修改上面的图片
        RegisterLoginBean.UserInfo u = StringUtil.getUserInfoBean();
        if(u != null){
            if(!temp.getUid().equals(u.getUid())){
                String follow_status = temp.getFollow_status()+ "";
                if("0".equals(follow_status)){
                    helper.setVisible(R.id.focus,true);
                    helper.setVisible(R.id.already_focus,false);
                }else if("1".equals(follow_status)){
                    helper.setVisible(R.id.focus,false);
                    helper.setVisible(R.id.already_focus,true);
                }else{
                    helper.setVisible(R.id.focus,false);
                    helper.setVisible(R.id.already_focus,false);
                }
            }
        }

    }


    private String message = "";
    private void followUser(BaseViewHolder helper, String otherUid) {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",otherUid);
        map.put("message",message + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        helper.setVisible(R.id.focus,false);
                        helper.setVisible(R.id.already_focus,true);
                        ToastUtils.showShort("关注成功");
                        // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
                        EventBus.getDefault().post(new PeopleFocusEvent(otherUid,1));
                    }
                });
    }

    private void unfollowUser(BaseViewHolder helper, String otherUid) {

        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",otherUid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().unfollowUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        helper.setVisible(R.id.focus,true);
                        helper.setVisible(R.id.already_focus,false);

                        ToastUtils.showShort("取消关注成功");
                        // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
                        EventBus.getDefault().post(new PeopleFocusEvent(otherUid,0));

                    }
                });
    }


}
