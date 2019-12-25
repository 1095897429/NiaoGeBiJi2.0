package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.FouBBBB;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

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
    private AuthorDetailListener mAuthorDetailListener;

    public FocusAdapter(List<MultiNewsBean> data) {
        super(data);
        //推荐关注
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
                IndexFocusBean.Article_list  bean = item.getArticleList();
                helper.setText(R.id.one_img_title,bean.getTitle());
                helper.setText(R.id.one_img_auth,bean.getAuthor());

                //发布时间
                if(StringUtil.checkNull(bean.getPublished_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(bean.getPublished_at()) * 1000L);
                    helper.setText(R.id.one_img_time, s);
                }

                if(!TextUtils.isEmpty(bean.getPic())){
                    ImageUtil.load(mContext,bean.getPic(),helper.getView(R.id.one_img_imgs));
                }

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
                mFirstAuthorAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                    if(null != mAuthorCancleListener){
                        mAuthorCancleListener.canleOrFocus(position);
                    }
                });

                mFirstAuthorAdapter.setOnItemClickListener((adapter, view, position) -> {

                    if(null != mAuthorDetailListener){
                        mAuthorDetailListener.authordetail(position);
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


    public interface AuthorDetailListener{
        void authordetail(int position);
    }

    public void setAuthorDetailListener(AuthorDetailListener authorDetailListener) {
        mAuthorDetailListener = authorDetailListener;
    }
}
