package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.event.ToolHomeChangeEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.tagflowlayout.TagFlowLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:
 *
 *              if(!bean.isSave()){
 *                 bean.setSave(true);
 *                 helper.setText(R.id.tool_collect,"已收藏");
 *                 textPaint.setFakeBoldText(false);
 *                 helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_gray);
 *             }else{
 *                 bean.setSave(false);
 *                 helper.setText(R.id.tool_collect,"收藏");
 *                 textPaint.setFakeBoldText(true);
 *                 helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_yellow);
 *             }
 *
 *
 *        //之前测试数据
 *         if(helper.getAdapterPosition() == 0){
 *             mStringLIst.add("新媒体");
 *             mStringLIst.add("数据咨询");
 *         }else{
 *             mStringLIst.add("新媒体");
 *             mStringLIst.add("数据咨询");
 *             mStringLIst.add("嘿嘿");
 *         }
 *
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

    private int myPosition;

    @Override
    protected void convert(BaseViewHolder helper, ToolBean bean) {

        //标题
        helper.setText(R.id.toolTitle,bean.getTitle());

        if(!TextUtils.isEmpty(bean.getIcon())){
            ImageUtil.load(mContext,bean.getIcon(),helper.getView(R.id.head_icon));
        }

        //运营人数
        helper.setText(R.id.name_tag,bean.getNum() + "的内容运营在用");

        //标签
        TagFlowLayout flowLayout = helper.getView(R.id.flowlayout);
        mStringLIst.clear();
        String tags = bean.getTag();
        if(!TextUtils.isEmpty(tags)){
            String[] tag = tags.split(",");
            for (String s : tag) {
                mStringLIst.add(s);
            }
            mMyToolEditTagAdapter = new MyToolEditTagAdapter(mStringLIst);
            flowLayout.setAdapter(mMyToolEditTagAdapter);
        }


        TextView textView_1 = helper.getView(R.id.tool_collect);
        TextPaint textPaint = textView_1.getPaint();
        //收藏
        if(0 == bean.getIs_collected()){
            textPaint.setFakeBoldText(true);
            helper.setText(R.id.tool_collect,"收藏");
            helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_yellow);
        }else if(1 == bean.getIs_collected()){
            helper.setText(R.id.tool_collect,"已收藏");
            textPaint.setFakeBoldText(false);
            helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_gray);
        }

        //默认设置行数为一行
        ImageView toShowMore = helper.getView(R.id.toShowMore);
        TextView tool_collect = helper.getView(R.id.tool_collect);
        LinearLayout ll_show = helper.getView(R.id.ll_show);
        //获取当前文本的行数
        TextView textView = helper.getView(R.id.comment_text);
        helper.setText(R.id.comment_text,bean.getDesc());
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

            myPosition  = helper.getAdapterPosition();

            if(0 == bean.getIs_collected()){
                collect(bean);
                helper.setText(R.id.tool_collect,"已收藏");
                textPaint.setFakeBoldText(false);
                helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_gray);
            }else if(1 == bean.getIs_collected()){
                KLog.d("tag","取消收藏");
                cancelCollect(bean);
                helper.setText(R.id.tool_collect,"收藏");
                textPaint.setFakeBoldText(true);
                helper.setBackgroundRes(R.id.tool_collect,R.drawable.bg_corners_6_yellow);
            }


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


    private void collect(ToolBean bean) {
        Map<String,String> map = new HashMap<>();
        map.put("tool_id",bean.getId());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().collect(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        bean.setIs_collected(1);
                        notifyItemChanged(myPosition);
                    }
                });
    }


    private void cancelCollect(ToolBean bean) {
        Map<String,String> map = new HashMap<>();
        map.put("tool_id",bean.getId());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().cancelCollect(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        bean.setIs_collected(0);
                        notifyItemChanged(myPosition);
                    }
                });
    }

}
