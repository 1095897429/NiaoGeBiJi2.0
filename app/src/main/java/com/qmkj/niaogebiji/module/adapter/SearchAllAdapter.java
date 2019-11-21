package com.qmkj.niaogebiji.module.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.MultSearchBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
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

    public  static final int SEACHER_GANHUO  = 1;
    public static final int SEACHER_ACTION = 2;
    public static final int SEACHER_FLASH = 3;

    public static final int GANHUO = 1;
    public static final int ACTION = 2;
    public static final int FLASH = 3;

    private ToMoreListenerListener mToMoreListenerListener;

    public SearchAllAdapter(List<MultSearchBean> data) {
        super(data);
        //干货
        addItemType(SEACHER_GANHUO, R.layout.first_search_item1);
        //活动
        addItemType(SEACHER_ACTION,R.layout.first_search_item1);
        //快讯
        addItemType(SEACHER_FLASH,R.layout.first_search_item1);
    }



    FirstItemNewAdapter mFirstItemNewAdapter;
    FlashSeacherItemAdapter mFlashSeacherItemAdapter;
    ActionAdapter mActionAdapter;

    LinearLayoutManager talkManager;
    RecyclerView recyclerView;

    @Override
    protected void convert(BaseViewHolder helper, MultSearchBean item) {

        //查看更多
        helper.addOnClickListener(R.id.toMoreList);

        switch (helper.getItemViewType()){
            case SEACHER_FLASH:

                helper.setText(R.id.text_name,"快讯");

                List<FlashBulltinBean.BuilltinBean> temps = item.getFlashBulltinBeanList();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mFlashSeacherItemAdapter = new FlashSeacherItemAdapter(temps);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mFlashSeacherItemAdapter);

                //事件
                mFlashSeacherItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去快讯明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(FLASH,"快讯 查看更多");
                    }
                });

                break;
            case SEACHER_ACTION:
                helper.setText(R.id.text_name,"活动");
                List<ActionBean.Act_list> tempsss = item.getActionBeanList();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mActionAdapter = new ActionAdapter(tempsss);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mActionAdapter);

                //事件
                mActionAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去活动明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(ACTION,"活动 查看更多");
                    }
                });

                break;

            case SEACHER_GANHUO:
                //采用的思路：公用一个Adapter,设置不同的type即可
                List<NewsItemBean> temps11 = item.getNewsItemBeanList();
                List<MultiNewsBean> mAllList = new ArrayList<>();
                MultiNewsBean bean1 ;
                for (int i = 0; i < temps11.size(); i++) {
                    bean1 = new MultiNewsBean();
                    bean1.setItemType(1);
                    bean1.setNewsItemBean(temps11.get(i));
                    mAllList.add(bean1);
                }
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
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
