package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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
 */
public class PeopleItemAdapter extends BaseQuickAdapter<RegisterLoginBean.UserInfo, BaseViewHolder> {

    public PeopleItemAdapter(List<RegisterLoginBean.UserInfo> data) {
        super(R.layout.search_people_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RegisterLoginBean.UserInfo item) {

        helper.addOnClickListener(R.id.focus).addOnClickListener(R.id.already_focus);

        helper.setText(R.id.sender_name,item.getName());
        helper.setText(R.id.sender_tag,item.getCompany());
        ImageUtil.load(mContext,item.getAvatar(),helper.getView(R.id.head_icon));


        showStateByFollow(item,helper);

        helper.itemView.setOnClickListener(view -> {
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
                    }
                });
    }



}
