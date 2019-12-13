package com.qmkj.niaogebiji.module.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.MultSearchBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
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
    public static final int SEACHER_DATA = 4;
    public static final int SEACHER_TOOL = 5;
    public static final int SEACHER_DYNAMIC = 6;
    public static final int SEACHER_AUTHOR = 7;
    public static final int SEACHER_PEOPLE = 8;
    public static final int SEACHER_SCHOOL = 9;
    public static final int SEACHER_BAIDU = 10;
    public static final int SEACHER_TEST = 11;

    public static final int GANHUO = 1;
    public static final int ACTION = 2;
    public static final int FLASH = 3;
    public static final int DATA = 4;
    public static final int TOOL = 5;
    public static final int DYNAMIC = 6;
    public static final int AUTHOR = 7;
    public static final int PEOPLE = 8;
    public static final int SCHOOL = 9;
    public static final int BAIDU = 10;
    public static final int TEST = 11;

    private ToMoreListenerListener mToMoreListenerListener;

    public SearchAllAdapter(List<MultSearchBean> data) {
        super(data);
        //干货
        addItemType(SEACHER_GANHUO, R.layout.first_search_item1);
        //活动
        addItemType(SEACHER_ACTION,R.layout.first_search_item1);
        //快讯
        addItemType(SEACHER_FLASH,R.layout.first_search_item1);
        //资料
        addItemType(SEACHER_DATA,R.layout.first_search_item1);
        //工具
        addItemType(SEACHER_TOOL,R.layout.first_search_item1);
        addItemType(SEACHER_DYNAMIC,R.layout.first_search_item1);
        addItemType(SEACHER_AUTHOR,R.layout.first_search_item1);
        addItemType(SEACHER_PEOPLE,R.layout.first_search_item1);
        addItemType(SEACHER_SCHOOL,R.layout.first_search_item1);
        addItemType(SEACHER_BAIDU,R.layout.first_search_item1);
        addItemType(SEACHER_TEST,R.layout.first_search_item1);
    }



    FirstItemNewAdapter mFirstItemNewAdapter;
    FlashSeacherItemAdapter mFlashSeacherItemAdapter;
    ActionAdapter mActionAdapter;
    DataItemAdapter mDataItemAdapter;
    ToolRecommentItemAdapter mToolRecommentItemAdapter;
    CircleRecommendAdapter mCircleRecommendAdapter;
    AuthorAdapter mAuthorAdapter;
    PeopleItemAdapter mPeopleItemAdapter;
    SchoolBookAdapter mSchoolBookAdapter;
    BaiduItemAdapter mBaiduItemAdapter;
    TestItemAdapter mTestItemAdapter;

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

            case  SEACHER_DATA:
                helper.setText(R.id.text_name,"资料");
                List<ToolBean> toolBeanList = item.getToolBeanList();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mDataItemAdapter = new DataItemAdapter(toolBeanList);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mDataItemAdapter);

                //事件
                mDataItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去资料明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(DATA,"资料 查看更多");
                    }
                });

                break;
            case  SEACHER_TOOL:
                helper.setText(R.id.text_name,"工具");
                List<ToolBean> toolBeanList2 = item.getToolBeanList();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mToolRecommentItemAdapter = new ToolRecommentItemAdapter(toolBeanList2);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mToolRecommentItemAdapter);

                //事件
                mToolRecommentItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去工具明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(TOOL,"工具 查看更多");
                    }
                });

                break;
            case  SEACHER_DYNAMIC:
                helper.setText(R.id.text_name,"动态");
                List<MultiCircleNewsBean> itemToolBeanList = new ArrayList<>();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mCircleRecommendAdapter = new CircleRecommendAdapter(itemToolBeanList);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mCircleRecommendAdapter);

                //事件
                mCircleRecommendAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去动态明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(DYNAMIC,"动态 查看更多");
                    }
                });

                break;
            case  SEACHER_AUTHOR:
                helper.setText(R.id.text_name,"作者");
                List<AuthorBean.Author> authors = new ArrayList<>();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mAuthorAdapter = new AuthorAdapter(authors);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mAuthorAdapter);

                //事件
                mAuthorAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去作者明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(AUTHOR,"作者 查看更多");
                    }
                });

                break;

            case  SEACHER_PEOPLE:
                helper.setText(R.id.text_name,"人脉");
                List<PeopleBean> peopleBeans = new ArrayList<>();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mPeopleItemAdapter = new PeopleItemAdapter(peopleBeans);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mPeopleItemAdapter);

                //事件
                mPeopleItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去人脉明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(PEOPLE,"人脉 查看更多");
                    }
                });

                break;
            case  SEACHER_SCHOOL:
                helper.setText(R.id.text_name,"课程");
                List<SchoolBean.SchoolBook> schoolBooks = new ArrayList<>();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mSchoolBookAdapter = new SchoolBookAdapter(schoolBooks);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mSchoolBookAdapter);

                //事件
                mSchoolBookAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去课程明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(SCHOOL,"课程 查看更多");
                    }
                });

                break;
            case  SEACHER_BAIDU:
                helper.setText(R.id.text_name,"百科");
                List<PeopleBean> schoolBooks111 = new ArrayList<>();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mBaiduItemAdapter = new BaiduItemAdapter(schoolBooks111);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mBaiduItemAdapter);

                //事件
                mBaiduItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去百科明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(BAIDU,"百科 查看更多");
                    }
                });

                break;
            case  SEACHER_TEST:
                helper.setText(R.id.text_name,"测试");
//                List<TestBean> testList = new ArrayList<>();
                List<SchoolBean.SchoolTest> testList = new ArrayList<>();
                recyclerView = helper.getView(R.id.recycler00);
                //二级
                talkManager = new LinearLayoutManager(mContext);
                talkManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(talkManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);
                mTestItemAdapter = new TestItemAdapter(testList);

                //禁用change动画
                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mTestItemAdapter);

                //事件
                mTestItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去测试明细页");
                });
                helper.getView(R.id.toMoreList).setOnClickListener(view -> {
                    if(null != mToMoreListenerListener){
                        mToMoreListenerListener.toMoreList(TEST,"测试 查看更多");
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
