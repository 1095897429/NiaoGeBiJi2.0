package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.event.ToolHomeChangeEvent;
import com.qmkj.niaogebiji.module.widget.tagflowlayout.TagFlowLayout;
import com.socks.library.KLog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:
 */
public class ToolRecommentItemAdapter extends BaseQuickAdapter<ToolBean, BaseViewHolder> {


    public ToolRecommentItemAdapter(List<ToolBean> data) {
        super(R.layout.item_tool_recomment,data);
        mMap = new HashMap<>();

    }


    MyToolEditTagAdapter mMyToolEditTagAdapter;
    List<String> mStringLIst = new ArrayList();
    //展开
    int defaultLine = 1;

    //保存每个item对应的文本linecount
    Map<Integer,Integer> mMap ;

    @Override
    protected void convert(BaseViewHolder helper, ToolBean bean) {

        //标签
        TagFlowLayout flowLayout = helper.getView(R.id.flowlayout);
        mStringLIst.clear();
        if(helper.getAdapterPosition() == 0){
            mStringLIst.add("新媒体");
            mStringLIst.add("数据咨询");
        }else{
            mStringLIst.add("新媒体");
            mStringLIst.add("数据咨询");
            mStringLIst.add("嘿嘿");
        }

        mMyToolEditTagAdapter = new MyToolEditTagAdapter(mStringLIst);
        flowLayout.setAdapter(mMyToolEditTagAdapter);


        TextView textView_1 = helper.getView(R.id.tool_collect);
        TextPaint textPaint = textView_1.getPaint();
        //收藏
        if(!bean.isSave()){
            textPaint.setFakeBoldText(true);
            helper.setText(R.id.tool_collect,"收藏");
            helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_yellow);
        }else{
            helper.setText(R.id.tool_collect,"已收藏");
            textPaint.setFakeBoldText(false);
            helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_gray);
        }


        ImageView toShowMore = helper.getView(R.id.toShowMore);

        TextView tool_collect = helper.getView(R.id.tool_collect);

        LinearLayout ll_show = helper.getView(R.id.ll_show);
        //获取当前文本的行数
        TextView textView = helper.getView(R.id.comment_text);
        helper.setText(R.id.comment_text,bean.getMes());


        //需异步获取行数
        textView.post(() -> {
            int linecount;
            linecount = textView.getLineCount();
//            KLog.d("tag", "Number of lines is " + linecount);
            mMap.put(helper.getAdapterPosition(), linecount);
            if (linecount > defaultLine) {
                textView.setMaxLines(defaultLine);
            }else{
                helper.setVisible(R.id.toShowMore,false);
            }
        });



        tool_collect.setOnClickListener(view -> {

            if(!bean.isSave()){
                bean.setSave(true);
                helper.setText(R.id.tool_collect,"已收藏");
                textPaint.setFakeBoldText(false);
                helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_gray);
            }else{
                bean.setSave(false);
                helper.setText(R.id.tool_collect,"收藏");
                textPaint.setFakeBoldText(true);
                helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_yellow);
            }

            //发送数据


            //发送更改主界面的数据源
            EventBus.getDefault().post(new ToolHomeChangeEvent("改变主界面数据"));
        });


        ll_show.setOnClickListener(view -> {
            if(!bean.isOpenState()){
                int lineCount11 = mMap.get(helper.getAdapterPosition());
                ((TextView)helper.getView(R.id.comment_text)).setMaxLines(lineCount11);
                helper.setTag(R.id.toShowMore,"可收起");
                bean.setOpenState(true);
                toShowMore.setImageResource(R.mipmap.icon_tool_up);
            }else{
                bean.setOpenState(false);
                ((TextView)helper.getView(R.id.comment_text)).setMaxLines(defaultLine);
                helper.setTag(R.id.toShowMore,"可展开");
                toShowMore.setImageResource(R.mipmap.icon_tool_down);
            }
        });

    }

}
