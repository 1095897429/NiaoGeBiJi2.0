package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-2-12
 * 描述:热榜作者的适配器
 */
public class HotAuthorAdapter extends BaseQuickAdapter<AuthorBean.Author, BaseViewHolder> {
    public HotAuthorAdapter(@Nullable List<AuthorBean.Author> data) {
        super(R.layout.hot_author_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AuthorBean.Author item) {

        //名称
        helper.setText(R.id.one_img_title,item.getName());

        //介绍
        helper.setText(R.id.author_tag,item.getSummary());

        ImageUtil.loadByDefaultHead(mContext,item.getImg(),helper.getView(R.id.head_icon));



        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Black.otf");
        ((TextView)helper.getView(R.id.author_rank)).setTypeface(typeface);

        TextView  author_rank = helper.getView(R.id.author_rank);
        author_rank.setText((helper.getAdapterPosition() + 1)+ "");
        if(helper.getAdapterPosition() == 0){
            author_rank.setBackgroundResource(R.mipmap.hot_author1);
        }else if(helper.getAdapterPosition() == 1){
            author_rank.setBackgroundResource(R.mipmap.hot_author2);
        }else if(helper.getAdapterPosition() == 2){
            author_rank.setBackgroundResource(R.mipmap.hot_author3);
        }else{
            author_rank.setBackgroundResource(R.mipmap.hot_author_other);
        }


        //作者类型:1-作者（不显示），2-新手作者，3-新锐作者，4-专栏作者',
        if("1".equals(item.getType())){
            helper.setVisible(R.id.author_type,false);
        }else if("2".equals(item.getType())){
            helper.setVisible(R.id.author_type,true);
            helper.setImageResource(R.id.author_type,R.mipmap.hot_author_newuser);
        }else if("3".equals(item.getType())){
            helper.setVisible(R.id.author_type,true);
            helper.setImageResource(R.id.author_type,R.mipmap.hot_author_new);
        }else if("4".equals(item.getType())){
            helper.setVisible(R.id.author_type,true);
            helper.setImageResource(R.id.author_type,R.mipmap.hot_author_professor);
        }


        //类型：排名显示颜色
//        if("1".equals(item.getNewsItemBean().getRank()) ||
//                "2".equals(item.getNewsItemBean().getRank()) ||
//                "3".equals(item.getNewsItemBean().getRank()) ){
//            helper.setImageResource(R.id.author_type,R.mipmap.hot_author_professor);
//        }else{
//            helper.setImageResource(R.id.author_type,R.mipmap.hot_author_new);
//        }




//        //影响数 x>=100000，展示10w+
        if(!TextUtils.isEmpty(item.getHit_count())){
            long count = Long.parseLong(item.getHit_count());
            if(count < 100000 ){
                helper.setText(R.id.effect_num,"影响力 " + item.getHit_count());
            }else{
                double temp = count  ;
                //1.将数字转换成以万为单位的数字
                double num = temp / 100000;
                BigDecimal b = new BigDecimal(num);
                //2.转换后的数字四舍五入保留小数点后一位;
                double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                helper.setText(R.id.effect_num,"影响力 " + f1 + " w");
            }
        }

        helper.itemView.setOnClickListener(v -> {
            if(StringUtil.isFastClick()){
                return;
            }

            UIHelper.toAuthorDetailActivity(mContext,item.getId());
        });


    }
}
