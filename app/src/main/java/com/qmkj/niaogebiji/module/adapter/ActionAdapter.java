package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public class ActionAdapter extends BaseQuickAdapter<ActionBean.Act_list, BaseViewHolder> {
    public ActionAdapter(@Nullable List<ActionBean.Act_list> data) {
        super(R.layout.item_action,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActionBean.Act_list item) {
        helper.addOnClickListener(R.id.relate_data);

        TextView chineseTv = helper.getView(R.id.content);
        TextPaint paint = chineseTv.getPaint();
        paint.setFakeBoldText(true);

        String sss = "";
        //截取district
        if(!TextUtils.isEmpty(item.getDistrict())){
            if(item.getDistrict().contains("，") || item.getDistrict().contains(",")){
                String  string = item.getDistrict().replaceAll("，",",");
                String [] result = string.split(",");
                if(result != null && result.length > 0){
                    StringBuilder sb = new StringBuilder();
                    for (String temp : result) {
                        sb.append(temp).append("/");
                    }
                    sss =  "|" + sb.substring(0,sb.length() - 1);
                }

            }else{
                //没有,，直接获取即可
                sss = "|" + item.getDistrict();
            }
        }


        if(!TextUtils.isEmpty(item.getTitle())){
            helper.setText(R.id.content,item.getTitle() + sss);
        }else{
            helper.setText(R.id.content,"");
        }


        helper.setText(R.id.tag,"");

        //地区
        if(!TextUtils.isEmpty(item.getDistrict())){
            helper.setText(R.id.tag_loc,item.getDistrict());
            helper.setVisible(R.id.tag_loc,true);
        }else{
            helper.setVisible(R.id.tag_loc,false);
        }

        //相关资料
        if(!TextUtils.isEmpty(item.getDocument_link())){
            helper.setVisible(R.id.relate_data,true);
        }else{
            helper.setVisible(R.id.relate_data,false);
        }

        //头像
        ImageUtil.load(mContext,item.getPic(),helper.getView(R.id.img_1));

        //时间
        if(!TextUtils.isEmpty(item.getStart_time()) && !TextUtils.isEmpty(item.getEnd_time())){

            String first =  TimeUtils.millis2String(Long.parseLong(item.getStart_time())* 1000L,"yyyy/MM/dd");
            String last = TimeUtils.millis2String(Long.parseLong(item.getEnd_time())* 1000L,"yyyy/MM/dd");
            if(first.equals(last)){

                helper.setText(R.id.time,
                        (TimeUtils.millis2String(Long.parseLong(item.getStart_time())* 1000L,"yyyy/MM/dd") + " " +
                                TimeUtils.millis2String(Long.parseLong(item.getStart_time())* 1000L,"HH:mm")) + " - " +
                                TimeUtils.millis2String(Long.parseLong(item.getEnd_time())* 1000L,"HH:mm"));
            }else{
                helper.setText(R.id.time,
                        (TimeUtils.millis2String(Long.parseLong(item.getStart_time())* 1000L,"yyyy/MM/dd HH:mm") + " - " +
                                TimeUtils.millis2String(Long.parseLong(item.getEnd_time())* 1000L,"yyyy/MM/dd HH:mm")));
            }
        }


        //类型：1-线上，2-线下
        String type = item.getType();
        if("1".equals(type)){
            helper.setText(R.id.tag,"线上活动");
        }else  if("2".equals(type)){
            helper.setText(R.id.tag,"线下活动");
        }

        //活动状态：2-报名中，4-已结束
        String status = item.getAct_status() + "";
        if("2".equals(status)){
            helper.setImageResource(R.id.action_status,R.mipmap.icon_apply);
        }else if("4".equals(status)){
            helper.setImageResource(R.id.action_status,R.mipmap.icon_finish);
        }
    }
}

