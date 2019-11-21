package com.qmkj.niaogebiji.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.activity.CircleMakeActivity;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MulMediaFile;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.xzh.imagepicker.bean.MediaFile;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-20
 * 描述:圈子图片选择器
 */
public class CirclePicItemAdapter extends BaseMultiItemQuickAdapter<MulMediaFile, BaseViewHolder> {

    public  static final int NORMAL = 1;
    public static final int ADD = 2;

    public CirclePicItemAdapter(List<MulMediaFile> data) {
        super(data);
        addItemType(NORMAL, R.layout.circle_pic_nomal);
        addItemType(ADD,R.layout.circle_pic_add);
    }

    @Override
    protected void convert(BaseViewHolder helper, MulMediaFile item) {

        switch (helper.getItemViewType()){

            case NORMAL:
                helper.addOnClickListener(R.id.pic_nomal_delete);
                MediaFile mediaFile = item.getMediaFile();
                ImageUtil.load(mContext,mediaFile.getPath(),helper.getView(R.id.pic_nomal));
                break;
            case ADD:
                //如果是9，表示是9张图 + 1个图片
                if(helper.getAdapterPosition() == CircleMakeActivity.pic_num){
                    helper.setVisible(R.id.pic_add,false);
                    //如果是0，表示是0张图 + 1个图片
                }else if(helper.getAdapterPosition() == 0){
                    helper.setVisible(R.id.pic_add,false);
                }else{
                    helper.setVisible(R.id.pic_add,true);
                }
                break;

            default:
                break;
        }
    }
}
