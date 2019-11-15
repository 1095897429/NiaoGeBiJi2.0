package com.qmkj.niaogebiji.module.adapter;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FouBBBB;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class FocusAdapter extends BaseMultiItemQuickAdapter<MultiNewsBean, BaseViewHolder> {

    public  static final int AUHTOR  = 1;
    public static final int RIGHT_IMG_TYPE = 2;
    public static final int GUESSLOVE = 3;
    public static final int FLASH_TYPE = 4;
    public static final int ACTIVITY_TYPE = 5;
    private AuthorCancleListener mAuthorCancleListener;

    public FocusAdapter(List<MultiNewsBean> data) {
        super(data);

        addItemType(AUHTOR, R.layout.firtst_focus_item1);
        //文章 右图
        addItemType(RIGHT_IMG_TYPE,R.layout.firtst_focus_item2);
        //猜你喜欢
        addItemType(GUESSLOVE,R.layout.firtst_focus_item3);
    }



    FirstAuthorAdapter mFirstAuthorAdapter;


    @Override
    protected void convert(BaseViewHolder helper, MultiNewsBean item) {
        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:

                break;
            case GUESSLOVE:

                break;

            case AUHTOR:
                helper.addOnClickListener(R.id.more_author);

                FouBBBB bbbb  = item.getFouBBBB();
                List<IndexFocusBean.Auther_list> list = bbbb.getDjj();

                RecyclerView recyclerView = helper.getView(R.id.recycler00);
                //二级
                LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.HORIZONTAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mFirstAuthorAdapter = new FirstAuthorAdapter(list);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mFirstAuthorAdapter);

                //事件
                mFirstAuthorAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        if(null != mAuthorCancleListener){
                            mAuthorCancleListener.canleOrFocus(position);
                        }
                    }
                });


                break;
            default:
                break;
        }
    }



    public interface AuthorCancleListener{
         void canleOrFocus(int position);
    }

    public void setAuthorCancleListener(AuthorCancleListener authorCancleListener) {
        mAuthorCancleListener = authorCancleListener;
    }

}
