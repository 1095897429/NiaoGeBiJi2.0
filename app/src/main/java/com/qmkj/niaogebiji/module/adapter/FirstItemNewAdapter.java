package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.FristActionBean;
import com.qmkj.niaogebiji.module.bean.IndexBulltin;
import com.qmkj.niaogebiji.module.bean.MessageBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.Timmm;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.event.FlashSpecificEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class FirstItemNewAdapter extends BaseMultiItemQuickAdapter<MultiNewsBean, BaseViewHolder> {

    public  static final int RIGHT_IMG_TYPE = 1;
    public static final int THREE_IMG_TYPE = 2;
    public static final int LONG_IMG_TYPE = 3;
    public static final int FLASH_TYPE = 4;
    public static final int ACTIVITY_TYPE = 5;

    public FirstItemNewAdapter(List<MultiNewsBean> data) {
        super(data);
        //文章 右图
        addItemType(RIGHT_IMG_TYPE, R.layout.first_item1);
        //3图
        addItemType(THREE_IMG_TYPE,R.layout.first_item2);
        //长图
        addItemType(LONG_IMG_TYPE,R.layout.first_item6);
        //实时快讯
        addItemType(FLASH_TYPE,R.layout.first_item4);
        //推荐活动
        addItemType(ACTIVITY_TYPE,R.layout.first_item5);
    }


    private HashMap<String , Timmm> mTimmmHashMap = new HashMap<>();
    Timmm mTimmm;
    ArrayList<MessageBean> list = new ArrayList<>();
    private Typeface typeface;

    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    @Override
    protected void convert(BaseViewHolder helper, MultiNewsBean item) {
        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:
                RecommendBean.Article_list bean = item.getNewsActicleList();
                helper.setText(R.id.one_img_title,bean.getTitle());
                helper.setText(R.id.one_img_auth,bean.getAuthor());

                //发布时间
                if(StringUtil.checkNull(bean.getPublished_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(bean.getPublished_at()) * 1000L);
                    helper.setText(R.id.one_img_time, s);
                }

                //热门 + 顶置
                int act_state= bean.getAct_state();
                if(1 == act_state){
                    helper.setVisible(R.id.top,true);
                    helper.setText(R.id.top,"置顶");
                }else if(2 == act_state){
                    helper.setVisible(R.id.top,true);
                    helper.setText(R.id.top,"热门");
                }else{
                    helper.setVisible(R.id.top,false);
                }


                if(!TextUtils.isEmpty(bean.getPic())){
                    ImageUtil.load(mContext,bean.getPic() + scaleSize,helper.getView(R.id.one_img_imgs));
                }

                break;
            case THREE_IMG_TYPE:

                RecommendBean.Article_list bean3 = item.getNewsActicleList();
                helper.setText(R.id.one_img_title,bean3.getTitle());
                helper.setText(R.id.one_img_auth,bean3.getAuthor());

                //发布时间
                if(StringUtil.checkNull(bean3.getPublished_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(bean3.getPublished_at()) * 1000L);
                    helper.setText(R.id.one_img_time, s);
                }

                int act_state2= bean3.getAct_state();
                if(1 == act_state2){
                    helper.setVisible(R.id.top,true);
                    helper.setText(R.id.top,"置顶");
                }else if(2 == act_state2){
                    helper.setVisible(R.id.top,true);
                    helper.setText(R.id.top,"热门");
                }else{
                    helper.setVisible(R.id.top,false);
                }



                if(!TextUtils.isEmpty(bean3.getPic())){
                    ImageUtil.load(mContext,bean3.getPic() + scaleSize,helper.getView(R.id.one_img_imgs));
                    ImageUtil.load(mContext,bean3.getPic2() + scaleSize,helper.getView(R.id.two_img_imgs));
                    ImageUtil.load(mContext,bean3.getPic3() + scaleSize,helper.getView(R.id.three_img_imgs));
                }

                break;
            case LONG_IMG_TYPE:
                RecommendBean.Article_list beanLong = item.getNewsActicleList();
                helper.setText(R.id.one_img_title,beanLong.getTitle());
                helper.setText(R.id.one_img_auth,beanLong.getAuthor());

                //发布时间
                if(StringUtil.checkNull(beanLong.getPublished_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(beanLong.getPublished_at()) * 1000L);
                    helper.setText(R.id.one_img_time, s);
                }

                if(!TextUtils.isEmpty(beanLong.getPic())){
                    ImageUtil.load(mContext,beanLong.getPic() + scaleSize,helper.getView(R.id.long_img_imgs));
                }

                int act_state3= beanLong.getAct_state();
                if(1 == act_state3){
                    helper.setVisible(R.id.top,true);
                    helper.setText(R.id.top,"置顶");
                }else if(2 == act_state3){
                    helper.setVisible(R.id.top,true);
                    helper.setText(R.id.top,"热门");
                }else{
                    helper.setVisible(R.id.top,false);
                }

                break;
            case FLASH_TYPE:

                typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");

                //设值
//                TextView mins = helper.getView(R.id.mins);
                TextView title = helper.getView(R.id.title);
                ViewFlipper vp = helper.getView(R.id.viewflipper);

                IndexBulltin indexBulltin = item.getIndexBulltin();

                for (IndexBulltin.Bulletn_list temp:indexBulltin.getBulletn_list()) {
                    String hh = TimeUtils.millis2String(Long.parseLong(temp.getPub_time()) * 1000L,"HH");
//                    String mm = TimeUtils.millis2String(Long.parseLong(temp.getPub_time()) * 1000L,"mm");
                    list.add(new MessageBean(hh,temp.getTitle(),temp.getId()));
                    mTimmm = new Timmm();
                    mTimmm.setMyTitle(temp.getTitle());
//                    mTimmm.setMm(mm);
                    mTimmmHashMap.put(temp.getTitle(),mTimmm);
                }

                //默认加载第一个
                Timmm temp = mTimmmHashMap.get(indexBulltin.getBulletn_list().get(0).getTitle());
                if(null != temp){
//                    mins.setText(temp.getHh());
//                    sends.setText(temp.getMm());
//                    mins.setTypeface(typeface);
//                    sends.setTypeface(typeface);
                }

                startFlipping(mContext,vp,list,title);


                helper.getView(R.id.toMoreFlash).setOnClickListener(v -> EventBus.getDefault().post(new toFlashEvent("去快讯信息流")));

                break;
            case ACTIVITY_TYPE:

                FristActionBean t = item.getFristActionBean();

                if(t.getActivity() != null && !TextUtils.isEmpty(t.getActivity().getPic())){
                    ImageUtil.load(mContext,t.getActivity().getPic() + scaleSize,helper.getView(R.id.one_img_imgs));

                }

                helper.getView(R.id.one_img_imgs).setOnClickListener(view -> {
                    MobclickAgentUtils.onEvent(UmengEvent.index_flow_activity_2_0_0);

                    if(StringUtil.isFastClick()){
                        return;
                    }

                    String jump_link = t.getActivity().getJump_link();
                    String linkType = t.getActivity().getLink_type();

                    if(!TextUtils.isEmpty(linkType)){
                        if("1".equals(linkType)){
                            UIHelper.toWebViewActivity(mContext,jump_link);
                        }else if("2".equals(linkType)){
                            if(!TextUtils.isEmpty(jump_link)){
                                UIHelper.toNewsDetailActivity(mContext,jump_link);
                            }
                        }
                    }

                });

                helper.getView(R.id.toMoreActivity).setOnClickListener(view -> {
                    EventBus.getDefault().post(new toFlashEvent("去活动信息流"));
                    MobclickAgentUtils.onEvent(UmengEvent.index_flow_activity_more_2_0_0);
                });

                break;
            default:
                break;
        }
    }

    public void startFlipping(Context context, ViewFlipper vf, ArrayList<MessageBean> infos,
                              TextView title){
        //设置自动切换的间隔时间
        vf.setFlipInterval(5 * 1000);
        vf.setInAnimation(context, R.anim.notice_in);
        vf.setOutAnimation(context, R.anim.notice_out);
        int len = infos.size();
        for (int i = 0; i < len; i++) {
            MessageBean info = infos.get(i);
            View v = ((Activity) context).getLayoutInflater().inflate(R.layout.home_flipper_item, null);
            TextView titleTv =  v.findViewById(R.id.home_news_text);
            titleTv.setOnClickListener(v1 -> {
                //TODO 12.18晚修改
                EventBus.getDefault().post(new toFlashEvent("去快讯信息流"));
                EventBus.getDefault().post(new FlashSpecificEvent((String) v1.getTag()));
            });
            titleTv.setTag(info.getFlash_id());
            //中文加粗
            TextPaint paint = titleTv.getPaint();
            paint.setFakeBoldText(true);

            titleTv.setText(info.getText());
            vf.addView(v);
        }
        vf.startFlipping();

        vf.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                KLog.d("tag","动画开启");
                View currentView = vf.getCurrentView();
                final TextView textView = currentView.findViewById(R.id.home_news_text);
                Timmm temp = mTimmmHashMap.get(textView.getText().toString());
                if(null != temp){
                    title.setText(temp.getMyTitle());
                    title.setTypeface(typeface);
//                    sends.setText(temp.getMm());
//                    sends.setTypeface(typeface);
                }

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


}
