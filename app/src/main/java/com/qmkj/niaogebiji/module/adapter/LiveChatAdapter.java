package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:直播聊天
 */
public class LiveChatAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public LiveChatAdapter(@Nullable List<String> data) {
        super(R.layout.item_live_chat,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {



    }
}

