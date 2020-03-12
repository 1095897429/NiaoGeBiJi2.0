package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
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
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

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


    //标题 -- 实体bean(标题，内容，发布时间)
    private HashMap<String , MessageBean> mTimmmHashMap = new HashMap<>();
    Timmm mTimmm;
    MessageBean mMessageBean;
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

                ViewFlipper vp = helper.getView(R.id.viewflipper);

                IndexBulltin indexBulltin = item.getIndexBulltin();

                //每次清空一下
                list.clear();

                for (IndexBulltin.Bulletn_list temp:indexBulltin.getBulletn_list()) {
                    String time = "";
                    //发布时间
                    if(StringUtil.checkNull(temp.getPub_time())){
                        time =  GetTimeAgoUtil.getTimeAgoByAppFlash(Long.parseLong(temp.getPub_time()) * 1000L);
                    }
                    mMessageBean = new MessageBean(temp.getContent(),time,temp.getTitle(),temp.getId());
                    mMessageBean.setTop(temp.getTop());
                    list.add(mMessageBean);
                    mTimmmHashMap.put(temp.getTitle() + temp.getId(),mMessageBean);
                }

                startFlipping(mContext,vp,list);


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

    public void startFlipping(Context context, ViewFlipper vf, ArrayList<MessageBean> infos){
        //设置自动切换的间隔时间
        vf.setFlipInterval(5 * 1000);
        vf.setInAnimation(context, R.anim.notice_in);
        vf.setOutAnimation(context, R.anim.notice_out);
        int len = infos.size();
        for (int i = 0; i < len; i++) {
            MessageBean info = infos.get(i);
            View v = ((Activity) context).getLayoutInflater().inflate(R.layout.home_flipper_item, null);
            TextView content_tv =  v.findViewById(R.id.home_news_text);
            TextView title_tv = v.findViewById(R.id.title);
            TextView time_tv = v.findViewById(R.id.one_img_time);
            ImageView dingzhi = v.findViewById(R.id.dingzhi);


            //头条 || 顶置
            if("1".equals(info.getTop())){
                dingzhi.setVisibility(View.VISIBLE);
            }else{
                dingzhi.setVisibility(View.GONE);
            }


            content_tv.setOnClickListener(v1 -> {
                //TODO 12.18晚修改
                EventBus.getDefault().post(new toFlashEvent("去快讯信息流"));
                EventBus.getDefault().post(new FlashSpecificEvent((String) v1.getTag()));
            });
            content_tv.setTag(info.getFlash_id());

            TextPaint paint = title_tv.getPaint();
            paint.setFakeBoldText(true);
            content_tv.setText(info.getText());

            title_tv.setText(info.getMyTitle());

            time_tv.setText(info.getTime());

            vf.addView(v);
        }
        vf.startFlipping();

        vf.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                View currentView = vf.getCurrentView();
                final TextView textView = currentView.findViewById(R.id.home_news_text);
                final TextView title_tv = currentView.findViewById(R.id.title);
                final TextView time_tv = currentView.findViewById(R.id.one_img_time);
//                KLog.d("tag","文本的内容是 " + textView.getText().toString());
                MessageBean temp = mTimmmHashMap.get(title_tv.getText().toString() + textView.getTag());
                if(null != temp){
                    textView.setText(temp.getText());
                    title_tv.setText(temp.getMyTitle());
                    time_tv.setText(temp.getTime());
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
