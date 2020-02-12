package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:
 */
public class TestItemAdapter extends BaseQuickAdapter<SchoolBean.SchoolTest, BaseViewHolder> {

    public TestItemAdapter(@Nullable List<SchoolBean.SchoolTest> data) {
        super(R.layout.activity_test_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SchoolBean.SchoolTest item) {

        helper.setText(R.id.test_name,item.getTitle());
        if(!TextUtils.isEmpty(item.getNum())){
            helper.setText(R.id.test_count,StringUtil.formatPeopleNum(item.getNum()) + "人已测");
        }


        ImageUtil.load(mContext,item.getIcon(),helper.getView(R.id.test_icon));

        //是否展示限时标志 1是 0否
        if("0".equals(item.getIs_limit_time())){
            helper.setVisible(R.id.test_time,false);
        }else if("1".equals(item.getIs_limit_time())){
            helper.setVisible(R.id.test_time,true);
        }

        //是否已参加过测试 1是 0否 不及格不显示已完成
        if("0".equals(item.getRecord().getIs_tested() + "")){
            helper.setVisible(R.id.is_test_ok,false);
        }else if("1".equals(item.getRecord().getIs_tested() + "")){
            //通过显示，不通过不显示
            if(Integer.parseInt(item.getRecord().getScore()) < Integer.parseInt(item.getPass_score())){
                KLog.d("tag","不及格");
                helper.setVisible(R.id.is_test_ok,false);
            }else{
                helper.setVisible(R.id.is_test_ok,true);
            }


        }

    }
}
