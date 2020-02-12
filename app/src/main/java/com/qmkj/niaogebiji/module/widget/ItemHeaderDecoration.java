package com.qmkj.niaogebiji.module.widget;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-11
 * 描述:
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by User on 2017/7/24.
 */

public class ItemHeaderDecoration extends RecyclerView.ItemDecoration {
    private Context mContext;
    private List<FlashBulltinBean.BuilltinBean> mList;
    public static String currentTag = "0";
    private LayoutInflater mLayoutInflater;
    private int mTitleHight = 50;
    private int mTitleTextSize = 20;
    public ItemHeaderDecoration(Context context , List<FlashBulltinBean.BuilltinBean> dataList) {
        super();
        this.mContext = context;
        this.mList = dataList;
        mTitleHight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics());
        // mTitleTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, context.getResources().getDisplayMetrics());
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    public static void setCurrentTag(String tag){
        ItemHeaderDecoration.currentTag = tag;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //获取到视图中第一个可见的item的position
        int position = ((LinearLayoutManager)parent.getLayoutManager()).findFirstVisibleItemPosition();
        String tag = mList.get(position).getCreated_at();
        View child = parent.findViewHolderForLayoutPosition(position).itemView;
        boolean flag = false;
        if((position+1) < mList.size()){
            String suspensionTag = mList.get(position+1).getCreated_at();
            if(null!=tag && !tag.equals(suspensionTag)){
                Log.e("ZHG-TEST","!!!!!!!!!!!!!child.Height() = "+child.getHeight()+" , child.top = "+child.getTop()+" , mTitleHight = "+mTitleHight);
                if(child.getHeight()+child.getTop() < mTitleHight){
                    c.save();
                    flag = true;
                    c.translate(0,child.getHeight()+child.getTop()-mTitleHight);
                }
            }
        }
        View topTitleView = mLayoutInflater.inflate(R.layout.detail_title,parent,false);
        TextView textView = (TextView) topTitleView.findViewById(R.id.header_textview);
        TextView textView2 = (TextView) topTitleView.findViewById(R.id.header_textview_weekend);
        //textView.setTextSize(mTitleTextSize);
//        textView.setText("分类"+tag);

        textView.setText(TimeUtils.millis2String(Long.parseLong(mList.get(position).getCreated_at())* 1000L,"MM月dd日"));
        String temp = TimeUtils.millis2String(Long.parseLong(mList.get(position).getCreated_at())* 1000L,"yyyy-MM-dd HH:mm:ss");
        String string = TimeUtils.getChineseWeek(temp);
        textView2.setText(string);





        int toDrawWidthSpec;//用于测量的widthMeasureSpec
        int toDrawHeightSpec;//用于测量的heightMeasureSpec
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) topTitleView.getLayoutParams();
        if (lp == null) {
            lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//这里是根据复杂布局layout的width height，new一个Lp
            topTitleView.setLayoutParams(lp);
        }
        topTitleView.setLayoutParams(lp);
        if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            //如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
        } else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.AT_MOST);
        } else {
            //否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
        }
        //高度同理
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
        } else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.AT_MOST);
        } else {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(mTitleHight, View.MeasureSpec.EXACTLY);
        }
        //依次调用 measure,layout,draw方法，将复杂头部显示在屏幕上。
        topTitleView.measure(toDrawWidthSpec, toDrawHeightSpec);
        topTitleView.layout(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingLeft() + topTitleView.getMeasuredWidth(), parent.getPaddingTop() + topTitleView.getMeasuredHeight());
        topTitleView.draw(c);//Canvas默认在视图顶部，无需平移，直接绘制
        if (flag){
            c.restore();//恢复画布到之前保存的状态

        }
        if (!TextUtils.equals(tag, currentTag)) {
            currentTag = tag;
        }

    }
}
