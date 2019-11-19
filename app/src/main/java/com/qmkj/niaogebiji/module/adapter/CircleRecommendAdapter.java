package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:圈子推荐适配器
 */
public class CircleRecommendAdapter extends BaseMultiItemQuickAdapter<MultiCircleNewsBean, BaseViewHolder> {

    public  static final int RIGHT_IMG_TYPE = 1;
    public static final int TRANSFER_IMG_TYPE = 2;
    public static final int TRANSFER_LIN_TYPE = 3;
    public static final int LINK_OUT_TYPE = 4;

    public CircleRecommendAdapter(List<MultiCircleNewsBean> data) {
        super(data);
        //3图
        addItemType(RIGHT_IMG_TYPE, R.layout.first_circle_item1);
        //转发图片
        addItemType(TRANSFER_IMG_TYPE,R.layout.first_circle_item3);
        //转发链接
        addItemType(TRANSFER_LIN_TYPE,R.layout.first_circle_item4);
        //外链
        addItemType(LINK_OUT_TYPE,R.layout.first_circle_item2);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiCircleNewsBean item) {
        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:


                break;
            case TRANSFER_IMG_TYPE:


                break;
            case TRANSFER_LIN_TYPE:


                break;
            case LINK_OUT_TYPE:
                //设置子View点击事件
                helper.addOnClickListener(R.id.toMoreFlash);


                break;
            default:
                break;
        }
    }
}
