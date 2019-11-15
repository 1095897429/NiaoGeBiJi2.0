package com.qmkj.niaogebiji.module.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:圈子推荐适配器
 */
public class CircleRecommendAdapter extends BaseMultiItemQuickAdapter<MultiCircleNewsBean, BaseViewHolder> {

    public  static final int RIGHT_IMG_TYPE = 1;
    public static final int THREE_IMG_TYPE = 2;
    public static final int LONG_IMG_TYPE = 3;
    public static final int FLASH_TYPE = 4;
    public static final int ACTIVITY_TYPE = 5;

    public CircleRecommendAdapter(List<MultiCircleNewsBean> data) {
        super(data);
        //多图
        addItemType(RIGHT_IMG_TYPE, R.layout.first_circle_item1);
        //3图
        addItemType(THREE_IMG_TYPE,R.layout.first_item2);
        //长图
        addItemType(LONG_IMG_TYPE,R.layout.first_item6);
        //实时快讯
        addItemType(FLASH_TYPE,R.layout.first_item4);
        //实时快讯
        addItemType(ACTIVITY_TYPE,R.layout.first_item5);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiCircleNewsBean item) {
        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:


                break;
            case THREE_IMG_TYPE:


                break;
            case LONG_IMG_TYPE:


                break;
            case FLASH_TYPE:
                //设置子View点击事件
                helper.addOnClickListener(R.id.toMoreFlash);


                break;
            case ACTIVITY_TYPE:
                helper.addOnClickListener(R.id.toMoreActivity);


                break;
            default:
                break;
        }
    }
}
