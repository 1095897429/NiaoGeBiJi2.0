package com.qmkj.niaogebiji.module.adapter;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.listener.ToActivityFocusListener;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.MultSearchBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.SearchAllBaiduBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:适配器全部适配器
 * 1.大于等于3显示查看更多
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

    public SearchAllAdapter(List<MultSearchBean> data) {
        super(data);
        //干货
        addItemType(SEACHER_GANHUO, R.layout.first_search_item1);
        //人脉
        addItemType(SEACHER_PEOPLE,R.layout.first_search_item1);
        //动态
        addItemType(SEACHER_DYNAMIC,R.layout.first_search_item1);
        //百科
        addItemType(SEACHER_BAIDU,R.layout.first_search_item1);
        //资料
        addItemType(SEACHER_DATA,R.layout.first_search_item1);
        //作者
        addItemType(SEACHER_AUTHOR,R.layout.first_search_item1);


        //活动
        addItemType(SEACHER_ACTION,R.layout.first_search_item1);
        //快讯
        addItemType(SEACHER_FLASH,R.layout.first_search_item1);

        //工具
        addItemType(SEACHER_TOOL,R.layout.first_search_item1);



        addItemType(SEACHER_SCHOOL,R.layout.first_search_item1);

        addItemType(SEACHER_TEST,R.layout.first_search_item1);
    }



    FirstItemNewAdapter mFirstItemNewAdapter;
    ThingsAdapter mThingsAdapter;
    CircleSearchAdapterNew mCircleSearchAdapterNew;
    AuthorSearchAdapter mAuthorAdapter;
    PeopleItemAdapter mPeopleItemAdapter;
    SchoolBookAdapter mSchoolBookAdapter;
    BaiduItemAdapter mBaiduItemAdapter;

    LinearLayoutManager talkManager;
    RecyclerView recyclerView;

    @Override
    protected void convert(BaseViewHolder helper, MultSearchBean item) {
        //通用数据
        recyclerView = helper.getView(R.id.recycler00);
        //二级
        talkManager = new LinearLayoutManager(mContext);
        talkManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(talkManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        //禁用change动画
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        //查看更多
        helper.addOnClickListener(R.id.toMoreList);


        switch (helper.getItemViewType()){

            case SEACHER_GANHUO:
                //根据类型添加数据
                List<RecommendBean.Article_list> temps11 = item.getNewsItemBeanList();
                List<MultiNewsBean> mAllList = new ArrayList<>();
                //仅数据条数>3条时，展示
                if(temps11.size() >= 3){
                    temps11 = temps11.subList(0,3);
                    helper.setVisible(R.id.toMoreList,true);
                }else {
                    helper.setVisible(R.id.toMoreList, false);
                }

                MultiNewsBean bean1 ;
                int  size = temps11.size();
                String pic_type;
                for (int i = 0; i < size; i++) {
                    bean1 = new MultiNewsBean();
                    pic_type = temps11.get(i).getPic_type();
                    if("1".equals(pic_type)){
                        bean1.setItemType(1);
                    }else if("2".equals(pic_type)){
                        bean1.setItemType(3);
                    }else if("3".equals(pic_type)){
                        bean1.setItemType(2);
                    }else{
                        bean1.setItemType(1);
                    }
                    bean1.setNewsActicleList(temps11.get(i));
                    mAllList.add(bean1);
                }

                mFirstItemNewAdapter = new FirstItemNewAdapter(mAllList);
                recyclerView.setAdapter(mFirstItemNewAdapter);
                //事件
                mFirstItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去干货明细页" + position);
                    String aid = mFirstItemNewAdapter.getData().get(position).getNewsActicleList().getAid();
                    if (!TextUtils.isEmpty(aid)) {
                        UIHelper.toNewsDetailActivity(mContext, aid);
                    }
                });

                break;

            case  SEACHER_DATA:
                helper.setText(R.id.text_name,"资料");
                List<RecommendBean.Article_list> toolBeanList = item.getThings();
                if(toolBeanList.size() >= 3){
                    toolBeanList = toolBeanList.subList(0,3);
                    helper.setVisible(R.id.toMoreList,true);
                }else{
                    helper.setVisible(R.id.toMoreList,false);
                }
                mThingsAdapter = new ThingsAdapter(toolBeanList);
                recyclerView.setAdapter(mThingsAdapter);

                break;

            case  SEACHER_DYNAMIC:
                helper.setText(R.id.text_name,"动态");
                List<CircleBean> circleBeans = item.getCircleBeanList();
                if(circleBeans.size() >= 3){
                    circleBeans = circleBeans.subList(0,3);
                    helper.setVisible(R.id.toMoreList,true);
                }else{
                    helper.setVisible(R.id.toMoreList,false);
                }
                int type ;
                CircleBean temp;
                List<CircleBean> teList = new ArrayList<>();
                for (int i = 0; i < circleBeans.size(); i++) {
                    temp = circleBeans.get(i);
                    type = StringUtil.getCircleType(temp);
                    if(temp != null && !TextUtils.isEmpty(temp.getBlog())){
                        temp = StringUtil.addLinksData(temp);
                    }

                    //如果判断有空数据，则遍历下一个数据
                    if(100 == type){
                        continue;
                    }
                    temp.setCircleType(type);
                    teList.add(temp);
                }


                mCircleSearchAdapterNew = new CircleSearchAdapterNew(teList);
                recyclerView.setAdapter(mCircleSearchAdapterNew);
                //事件
                mCircleSearchAdapterNew.setOnItemClickListener((adapter, view, position) -> {
                    KLog.d("tag","去评论明细页" + position);
                    String blog_id = mCircleSearchAdapterNew.getData().get(position).getId();
                    UIHelper.toCommentDetailActivity(mContext,blog_id);
                });

                break;
            case  SEACHER_AUTHOR:
                helper.setText(R.id.text_name,"作者");
                List<AuthorBean.Author> authors = item.getAuthorBeanList();


                if(authors.size() >= 3){
                    authors =  authors.subList(0,3);
                    helper.setVisible(R.id.toMoreList,true);
                }else {
                    helper.setVisible(R.id.toMoreList,false);
                }
                mAuthorAdapter = new AuthorSearchAdapter(authors);

                ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mAuthorAdapter);


                //关注事件 -- 获取uid
//                mAuthorAdapter.setToActivityFocusListener(position -> {
//
//                   if(mToActivityFocusListenerUP != null){
//                       //第一个是列表中的位置，第二个是列表的列表的位置
//                       mToActivityFocusListenerUP.toAFocus(helper.getAdapterPosition(),position);
//                   }
//                });

                break;

            case  SEACHER_PEOPLE:
                helper.setText(R.id.text_name,"人脉");
                List<RegisterLoginBean.UserInfo> peopleBeans = item.getUserInfos();
                if(peopleBeans.size() >= 3){
                    peopleBeans = peopleBeans.subList(0,3);
                    helper.setVisible(R.id.toMoreList,true);
                }else{
                    helper.setVisible(R.id.toMoreList,false);
                }

                mPeopleItemAdapter = new PeopleItemAdapter(peopleBeans);
                recyclerView.setAdapter(mPeopleItemAdapter);


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


                break;
            case  SEACHER_BAIDU:
                helper.setText(R.id.text_name,"百科");
                List<SearchAllBaiduBean.Wiki> wikis  = item.getWikis();
                if(wikis.size() >= 3){
                    wikis =  wikis.subList(0,3);
                    helper.setVisible(R.id.toMoreList,true);
                }else{
                    helper.setVisible(R.id.toMoreList,false);
                }
                mBaiduItemAdapter = new BaiduItemAdapter(wikis);
                recyclerView.setAdapter(mBaiduItemAdapter);

                mBaiduItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                    String link = StringUtil.getLink("wikidetail/" + mBaiduItemAdapter.getData().get(position).getWord_id());
                    UIHelper.toWebViewActivityWithOnStep(mContext,link);
                });

                break;
            default:
                break;
        }
    }



    private ToActivityFocusListenerUP mToActivityFocusListenerUP;

    public void setToActivityFocusListenerUP(ToActivityFocusListenerUP toActivityFocusListenerUP) {
        mToActivityFocusListenerUP = toActivityFocusListenerUP;
    }

    public interface ToActivityFocusListenerUP{
        void toAFocus(int orignPosition,int position);
    }

}
