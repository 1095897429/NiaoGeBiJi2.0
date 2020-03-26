package com.qmkj.niaogebiji.module.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.socks.library.KLog;

public class HorizontalSpacesDecorationV2 extends RecyclerView.ItemDecoration {

    int delta = SizeUtils.dp2px(8f);

    float sss = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16 * 2);


    public HorizontalSpacesDecorationV2() {
        KLog.d("tag","每个item占据的大小是 " + (sss / 3 ));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        if(position % 3 == 0){
            outRect.left = 0;
            outRect.right = delta / 2;
        }else  if(position % 3 == 1){
            outRect.left = delta / 2;
            outRect.right = delta / 2 ;
        }else  if(position % 3 == 2){
            outRect.left = delta / 2;
            outRect.right = 0;
        }




    }

    private void setOutRect(Rect outRect, Rect rect) {
    }
}