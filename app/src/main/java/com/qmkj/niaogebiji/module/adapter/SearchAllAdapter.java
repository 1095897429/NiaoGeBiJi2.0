package com.qmkj.niaogebiji.module.adapter;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.FouBBBB;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultSearchBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:适配器全部适配器
 */
public class SearchAllAdapter extends BaseMultiItemQuickAdapter<MultSearchBean, BaseViewHolder> {

    public  static final int NORMAL  = 1;
    public static final int RIGHT_IMG_TYPE = 2;
    public static final int GUESSLOVE = 3;

    public static final int GANHUO = 1;

    private ToMoreListenerListener mToMoreListenerListener;

    public SearchAllAdapter(List<MultSearchBean> data) {
        super(data);
        addItemType(NORMAL, R.layout.first_search_item1);
        //文章 右图
        addItemType(RIGHT_IMG_TYPE,R.layout.firtst_focus_item2);
        //猜你喜欢
        addItemType(GUESSLOVE,R.layout.firtst_focus_item3);
    }



    FirstItemNewAdapter mFirstItemNewAdapter;

    @Override
    protected void convert(BaseViewHolder helper, MultSearchBean item) {

        //查看更多
        helper.addOnClickListener(R.id.toMoreList);

        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:

                break;
            case GUESSLOVE:

                break;

            case NORMAL:


                //采用的思路：公用一个Adapter,设置不同的type即可
                MultSearchBean.SearchNewBean searchBean = item.getSearchNewBean();
                List<NewsItemBean> temps = searchBean.getNewsItemBeans();

                List<MultiNewsBean> mAllList = new ArrayList<>();
                MultiNewsBean bean1 ;
                for (int i = 0; i < temps.size(); i++) {
                    bean1 = new MultiNewsBean();
                    bean1.setItemType(1);
                    bean1.setNewsItemBean(temps.get(i));
                    mAllList.add(bean1);
                }
                RecyclerView recyclerView = helper.getView(R.id.recycler00);
                //二级
                LinearLayoutManager talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mFirstItemNewAdapter = new FirstItemNewAdapter(mAllList);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mFirstItemNewAdapter);

                //事件
                mFirstItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去新闻明细页");
                });

                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(GANHUO,"干货 查看更多");
                    }
                });


                break;
            default:
                break;
        }
    }



    //参数表示是哪个部分
    public interface ToMoreListenerListener{
         void toMoreList(int partPosition,String des);
    }


    public void setToMoreListenerListener(ToMoreListenerListener toMoreListenerListener) {
        mToMoreListenerListener = toMoreListenerListener;
    }
}
