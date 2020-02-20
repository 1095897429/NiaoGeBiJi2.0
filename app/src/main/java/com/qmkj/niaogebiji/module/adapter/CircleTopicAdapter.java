package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.FouBBBB;
import com.qmkj.niaogebiji.module.bean.History;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.qmkj.niaogebiji.module.db.DBManager;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:圈子上面  的关注主题
 */
public class CircleTopicAdapter extends BaseQuickAdapter<TopicBean, BaseViewHolder> {

    public CircleTopicAdapter(@Nullable List<TopicBean> data) {
        super(R.layout.topic_circle_recommend_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TopicBean item) {

        String url = "https://desk-fd.zol-img.com.cn/t_s2560x1440c5/g2/M00/05/09/ChMlWl1BAz-IcV0oADKEXBJ0ncgAAMP0gAAAAAAMoR0279.jpg";
        ImageUtil.load(mContext,url,helper.getView(R.id.flash_img1));



        if("1".equals(item.getType())){
            //发现话题
            helper.setVisible(R.id.rl_specific_topic,false);
            helper.setVisible(R.id.rl_last_topic,false);
            helper.setVisible(R.id.rl_find_topic,true);
        }else if("2".equals(item.getType())){
            //显示话题
            helper.setVisible(R.id.rl_specific_topic,true);
            helper.setVisible(R.id.rl_find_topic,false);
            helper.setVisible(R.id.rl_last_topic,false);
        }else if("3".equals(item.getType())){
           //显示剩余数
            helper.setVisible(R.id.rl_specific_topic,false);
            helper.setVisible(R.id.rl_find_topic,false);
            helper.setVisible(R.id.rl_last_topic,true);

            helper.setText(R.id.last_topic_text," +"  + item.getName());

        }

        //显示红点
        TopicBean history = DBManager.getInstance().queryTopic(item.getName());
        if(null != history){
          long localTime =  history.getCurrentTime();
          long serverTime = item.getCurrentTime();
          if(localTime < serverTime){
              helper.setVisible(R.id.red_point,true);
          }else{
              helper.setVisible(R.id.red_point,false);
          }
        }


        helper.itemView.setOnClickListener(v -> {

            if(StringUtil.isFastClick()){
                return;
            }
            //记录点击时间，保存在本地
            if("2".equals(item.getType())){
                insertToDb(item.getName());
                UIHelper.toTopicDetailActivity(mContext);
            }else if("1".equals(item.getType())){
                UIHelper.toTopListActivity(mContext);
            }else if("3".equals(item.getType())){
                UIHelper.toTopListActivity(mContext);
            }
        });
    }



    public void insertToDb(String tag){
        TopicBean history = DBManager.getInstance().queryTopic(tag);
        if(null != history){
            history.setName(tag);
            history.setCurrentTime(System.currentTimeMillis());
            DBManager.getInstance().updateTopic(history);
        }else{
            history = new TopicBean();
            history.setId(DBManager.getInstance().queryTopic().size() + 1);
            history.setName(tag);
            history.setCurrentTime(System.currentTimeMillis());
            DBManager.getInstance().insertTopic(history);
        }
    }

}
