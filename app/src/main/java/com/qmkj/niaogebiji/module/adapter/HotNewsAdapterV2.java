package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:文章的适配器 -- 多图
 */
public class HotNewsAdapterV2 extends BaseMultiItemQuickAdapter<MultiNewsBean, BaseViewHolder> {

    public  static final int RIGHT_IMG_TYPE = 1;
    public static final int THREE_IMG_TYPE = 2;
    public static final int LONG_IMG_TYPE = 3;


    public HotNewsAdapterV2(List<MultiNewsBean> data) {
        super(data);
        //文章 右图
        addItemType(RIGHT_IMG_TYPE,R.layout.hot_news_item);
        //3图
        addItemType(THREE_IMG_TYPE,R.layout.hot_news_item_3);
        //长图
        addItemType(LONG_IMG_TYPE,R.layout.hot_news_item_long);
    }

//    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";
//    String scaleSize = "?imageMogr2/auto-orient/format/jpg/ignore-error/1/thumbnail/!40p/imageslim";


    @Override
    protected void convert(BaseViewHolder helper, MultiNewsBean item) {
        RecommendBean.Article_list bean = item.getNewsActicleList();

        //标题
        helper.setText(R.id.one_img_title,bean.getTitle());

        //作者
        helper.setText(R.id.one_img_auth,bean.getAuthor());

        //阅读
        if(!TextUtils.isEmpty(bean.getViewnum())){
            long count = Long.parseLong(bean.getViewnum());
            if(count < 100000 ){
                helper.setText(R.id.reading_num,"阅读 " + bean.getViewnum());
            }else{
                double temp = count  ;
                //1.将数字转换成以万为单位的数字
                double num = temp / 10000;
                BigDecimal b = new BigDecimal(num);
                //2.转换后的数字四舍五入保留小数点后一位;
                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                helper.setText(R.id.reading_num,"阅读 " + f1 + "w+");
            }

        }



        helper.setText(R.id.rank,helper.getAdapterPosition() + 1 + "");

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        ((TextView)helper.getView(R.id.rank)).setTypeface(typeface);

        //类型：排名显示颜色
        if(0 ==  helper.getAdapterPosition() ||
                1 ==  helper.getAdapterPosition()||
                2 == helper.getAdapterPosition() ){
            helper.setTextColor(R.id.rank, Color.parseColor("#FF5040"));
        }else{
            helper.setTextColor(R.id.rank, Color.parseColor("#818386"));
        }


        helper.itemView.setOnClickListener(v -> {
            if(StringUtil.isFastClick()){
                return;
            }

            if(helper.getAdapterPosition() <= 4){
                MobclickAgentUtils.onEvent("index_hot_hotweek_"+ (helper.getAdapterPosition()  + 1) +"_2_2_0");
            }

            String aid = bean.getAid();
            if (!TextUtils.isEmpty(aid)) {
                UIHelper.toNewsDetailActivity(mContext, aid);
            }
        });



        switch (helper.getItemViewType()) {
            case RIGHT_IMG_TYPE:
                if(!TextUtils.isEmpty(bean.getPic())){
                    ImageUtil.load(mContext,bean.getPic() + Constant.scaleSize,helper.getView(R.id.one_img_imgs));
                }
                break;
            case THREE_IMG_TYPE:
                if(!TextUtils.isEmpty(bean.getPic())){
                    ImageUtil.load(mContext,bean.getPic() + Constant.scaleSize,helper.getView(R.id.one_img_imgs));
                    ImageUtil.load(mContext,bean.getPic2() + Constant.scaleSize,helper.getView(R.id.two_img_imgs));
                    ImageUtil.load(mContext,bean.getPic3() + Constant.scaleSize,helper.getView(R.id.three_img_imgs));
                }

                break;
            case LONG_IMG_TYPE:
                if(!TextUtils.isEmpty(bean.getPic())){
                    ImageUtil.load(mContext,bean.getPic() + Constant.scaleSize,helper.getView(R.id.long_img_imgs));
                }
                break;
                default:
        }






    }
}
