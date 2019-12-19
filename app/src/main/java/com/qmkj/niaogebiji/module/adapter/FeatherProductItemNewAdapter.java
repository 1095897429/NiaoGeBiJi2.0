package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FeatherProductBean;

import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:羽毛商品列表Item适配器
 */
public class FeatherProductItemNewAdapter extends BaseQuickAdapter<FeatherProductBean.Productean, BaseViewHolder> {

    @BindView(R.id.recycler1111)
    RecyclerView mRecyclerView;

    //每个item下的子个数
    private int itemCount;

    private FeatherItemAdapterNew mFeatherItemAdapterNew;


    //获取到每个bean
    private List<FeatherProductBean.Productean.ProductItemBean> mProductItemBeanList ;

    public FeatherProductItemNewAdapter(@Nullable List<FeatherProductBean.Productean> data) {
        super(R.layout.item_feather_product_item_newnew,data);
    }



    @Override
    protected void convert(BaseViewHolder helper, FeatherProductBean.Productean item) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.all_part1111);

        helper.setText(R.id.id_name,item.getTitle());


        mProductItemBeanList = item.getPoint_list();
        if(null != mProductItemBeanList && !mProductItemBeanList.isEmpty()){
            itemCount = mProductItemBeanList.size();
            if(itemCount < 3){
                helper.setVisible(R.id.all_part1111,false);
            }else{
                helper.setVisible(R.id.all_part1111,true);
            }
        }


        //二级
        LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
        talkManager.setOrientation(RecyclerView.HORIZONTAL);
        ((RecyclerView)helper.getView(R.id.recycler1111)).setLayoutManager(talkManager);



        mFeatherItemAdapterNew = new FeatherItemAdapterNew(mProductItemBeanList);
        ((RecyclerView)helper.getView(R.id.recycler1111)).setAdapter(mFeatherItemAdapterNew);


    }
}
