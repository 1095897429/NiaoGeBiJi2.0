package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.NewUserTaskBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:羽毛任务 新手任务Item适配器
 */
public class NewTaskItemAdapter extends BaseQuickAdapter<NewUserTaskBean.NewTaskBean, BaseViewHolder> {

    public NewTaskItemAdapter(@Nullable List<NewUserTaskBean.NewTaskBean> data) {
        super(R.layout.item_newtask,data);
    }

    public void updateData(){
        notifyDataSetChanged();
    }



    @Override
    protected void convert(BaseViewHolder helper, NewUserTaskBean.NewTaskBean item) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.donetask).addOnClickListener(R.id.part2222);

        helper.setText(R.id.task_name,item.getTitle())
                .setText(R.id.task_tag,item.getDesc());


        ImageUtil.load(mContext,item.getIcon(),helper.getView(R.id.icon_111));


        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        ((TextView)helper.getView(R.id.dotask_text)).setTypeface(typeface);


        KLog.d("tag",helper.getAdapterPosition() + "  状态是 " + item.getStatus());
        // 0-去完成，1-去领取(新人见面礼)，2-已领取
        if("0".equals(item.getStatus())){
            helper.setVisible(R.id.donetask,false);
            helper.setVisible(R.id.part2222,true);
            helper.setText(R.id.dotask_text,"+180");
        }else if("1".equals(item.getStatus())){
            helper.setVisible(R.id.donetask,false);
            (helper.getView(R.id.donetask)).setBackgroundResource(R.drawable.bg_item_task_signed);
            helper.setVisible(R.id.part2222,true);
            if(item.getTitle().equals("新人见面礼")){
                helper.setText(R.id.dotask_text,"+10");
            }else{
                helper.setText(R.id.dotask_text,"+180");
            }

        }else if("2".equals(item.getStatus())){
            helper.setVisible(R.id.donetask,true);
            helper.setText(R.id.donetask,"已领取");
            (helper.getView(R.id.donetask)).setBackgroundResource(R.drawable.bg_item_task_signed);
            helper.setVisible(R.id.part2222,false);
        }

    }
}
