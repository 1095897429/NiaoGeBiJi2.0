package com.qmkj.niaogebiji.module.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-28
 * 描述:编辑工具item的标签
 */
public class MyToolEditTagAdapter extends TagAdapter<String> {

    public MyToolEditTagAdapter(List<String> datas) {
        super(datas);
    }

    @Override
    public View getView(FlowLayout parent, int position, String bean) {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tool_edit_item_tag,null);
        TextView textView = ll.findViewById(R.id.tag_name);
        textView.setText(bean);
        return ll;
    }
}
