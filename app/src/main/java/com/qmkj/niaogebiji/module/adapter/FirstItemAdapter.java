package com.qmkj.niaogebiji.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class FirstItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FirstItemBean> mList ;
    private LayoutInflater mLayoutInflater;
    private Context context;


    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    // 脚布局
    private final int TYPE_FOOTER = 2;

    public FirstItemAdapter(Context context, List<FirstItemBean> mList){
        this.mList = mList;
        mLayoutInflater = LayoutInflater.from(context);
    }


    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3,

        ITEM5
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载Item View的时候根据不同TYPE加载不同的布局
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.first_item1, parent, false));
        } else  if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.first_item4, parent, false));
        }else  if (viewType == ITEM_TYPE.ITEM3.ordinal()) {
            return new Item3ViewHolder(mLayoutInflater.inflate(R.layout.first_item3, parent, false));
        }else  if (viewType == ITEM_TYPE.ITEM5.ordinal()) {
            return new Item5ViewHolder(mLayoutInflater.inflate(R.layout.first_item5, parent, false));
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof Item1ViewHolder) {
//            ((Item1ViewHolder) holder).mTextView.setText(mList.get(position).getName());
        } else if (holder instanceof Item2ViewHolder) {
//            ((Item2ViewHolder) holder).mTextView.setText(mList.get(position).getName());
        } else if (holder instanceof Item5ViewHolder) {
        }
        else if (holder instanceof Item3ViewHolder) {
            Item3ViewHolder footViewHolder = (Item3ViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
//                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
//                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
//                    footViewHolder.llEnd.setVisibility(View.GONE);
                      footViewHolder.mTextView.setText("正在加载");
                       footViewHolder.mTextView.setVisibility(View.VISIBLE);
                    break;

                case LOADING_COMPLETE: // 加载完成
//                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
//                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
//                    footViewHolder.llEnd.setVisibility(View.GONE);
                    footViewHolder.mTextView.setText("加载完成");
                    footViewHolder.mTextView.setVisibility(View.INVISIBLE);
                    break;

                case LOADING_END: // 加载到底
//                    footViewHolder.pbLoading.setVisibility(View.GONE);
//                    footViewHolder.tvLoading.setVisibility(View.GONE);
//                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    footViewHolder.mTextView.setText("加载数据到底");
                    footViewHolder.mTextView.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    //设置ITEM类型，可以自由发挥，这里设置item position单数显示item1 偶数显示item2
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return ITEM_TYPE.ITEM3.ordinal();
        }else {
            if(mList.get(position).getType().equals("1")){
                return ITEM_TYPE.ITEM1.ordinal();
            }else if(mList.get(position).getType().equals("2")){
                return ITEM_TYPE.ITEM2.ordinal();
            }else if(mList.get(position).getType().equals("5")){
                return ITEM_TYPE.ITEM5.ordinal();
            }

        }
        return 0;
    }


    //first_item1 的ViewHolder
    public static class Item1ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public Item1ViewHolder(View itemView) {
            super(itemView);
            mTextView=(TextView)itemView.findViewById(R.id.one_img_title);
        }
    }
    //first_item2 的ViewHolder
    public static class Item2ViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        public Item2ViewHolder(View itemView) {
            super(itemView);
            mTextView=(TextView)itemView.findViewById(R.id.title);
        }
    }
    //first_item3 的ViewHolder
    public static class Item3ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public Item3ViewHolder(View itemView) {
            super(itemView);
            mTextView=(TextView)itemView.findViewById(R.id.text_name);
        }
    }

    //推荐
    public static class Item5ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public Item5ViewHolder(View itemView) {
            super(itemView);
//            mTextView=(TextView)itemView.findViewById(R.id.text_name);
        }
    }


    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public int getLoadState() {
        return loadState;
    }
}
