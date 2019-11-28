package com.qmkj.niaogebiji.module.adapter;

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
import com.socks.library.KLog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

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

    }


    MyToolEditTagAdapter mMyToolEditTagAdapter;
    List<String> mStringLIst = new ArrayList();
    //展开
    int defaultLine = 1;

    //保存每个item对应的文本linecount
    Map<Integer,Integer> mMap = new HashMap<>();

    @Override
    protected void convert(BaseViewHolder helper, ToolBean bean) {

        helper.addOnClickListener(R.id.tool_collect);


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

        //收藏
        if(!bean.isSave()){
            helper.setText(R.id.tool_collect,"收藏");
            helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_yellow);
        }else{
            helper.setText(R.id.tool_collect,"已收藏");
            helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_gray);
        }


        ImageView toShowMore = helper.getView(R.id.toShowMore);

        LinearLayout ll_show = helper.getView(R.id.ll_show);
        //获取当前文本的行数
        TextView textView = helper.getView(R.id.comment_text);
        helper.setText(R.id.comment_text,bean.getMes());

        //需异步获取行数
        textView.post(() -> {
            KLog.d("tag","文本是 " + textView.getText().toString());

            int linecount = textView.getLineCount();
            KLog.d("tag", "Number of lines is " + linecount);
            mMap.put(helper.getAdapterPosition(), linecount);
            if (linecount > defaultLine) {
                textView.setMaxLines(defaultLine);
            }else{
                helper.setVisible(R.id.toShowMore,false);
            }
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
