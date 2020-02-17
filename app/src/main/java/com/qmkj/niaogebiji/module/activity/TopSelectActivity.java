package com.qmkj.niaogebiji.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.qmkj.niaogebiji.module.event.UpdateTopicEvent;
import com.qmkj.niaogebiji.module.fragment.ToolRecommentListFragment;
import com.qmkj.niaogebiji.module.fragment.TopicFragment;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-13
 * 描述:话题选择
 */
public class TopSelectActivity extends BaseActivity {

    @BindView(R.id.tools_scrlllview)
    ScrollView scrollView;

    @BindView(R.id.tools)
    LinearLayout toolsLayout;

    @BindView(R.id.goods_pager)
    VerticalViewPager goods_pager;

    View icon_select;


    private String toolsList[];
    private TextView toolsTextViews[];
    private View views[];
    private int currentItem = 0;
    private LayoutInflater inflater;
    private int scrllViewWidth = 0, scrollViewMiddle = 0;
    private FirstFragmentAdapter shopAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topselect;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {
        //设置适配器

        inflater = LayoutInflater.from(this);
        showToolsView();
        initPager();
    }

    /**
     * initPager<br/>
     * 初始化ViewPager控件相关内容
     */
    private void initPager() {
        mFragmentList.clear();
        for (int i = 0; i < toolsList.length; i++) {
            TopicFragment fragment1 = TopicFragment.getInstance(toolsList[i]);
            mFragmentList.add(fragment1);
            mTitls.add(toolsList[i]);
        }
        shopAdapter = new FirstFragmentAdapter(this,getSupportFragmentManager(), mFragmentList, mTitls);
        goods_pager.setOffscreenPageLimit(toolsList.length);
        goods_pager.setAdapter(shopAdapter);
        goods_pager.setOnPageChangeListener(onPageChangeListener);
    }


    /**
     * OnPageChangeListener<br/>
     * 监听ViewPager选项卡变化事的事件
     */

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            if (goods_pager.getCurrentItem() != arg0){
                goods_pager.setCurrentItem(arg0);
            }
            if (currentItem != arg0) {
                changeTextColor(arg0);
                changeTextLocation(arg0);
            }
            currentItem = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    /**
     * 改变栏目位置
     *
     * @param clickPosition
     */
    private void changeTextLocation(int clickPosition) {

        int x = (views[clickPosition].getTop() - getScrollViewMiddle() + (getViewheight(views[clickPosition]) / 2));
        scrollView.smoothScrollTo(0, x);
    }

    /**
     * 返回scrollview的中间位置
     *
     * @return
     */
    private int getScrollViewMiddle() {
        if (scrollViewMiddle == 0){
            scrollViewMiddle = getScrollViewheight() / 2;
        }
        return scrollViewMiddle;
    }

    /**
     * 返回ScrollView的宽度
     *
     * @return
     */
    private int getScrollViewheight() {
        if (scrllViewWidth == 0){
            scrllViewWidth = scrollView.getBottom() - scrollView.getTop();
        }
        return scrllViewWidth;
    }

    /**
     * 返回view的宽度
     *
     * @param view
     * @return
     */
    private int getViewheight(View view) {
        return view.getBottom() - view.getTop();
    }


    /**
     * 动态生成显示items中的textview
     */
    private void showToolsView() {
        toolsList = new String[]{"常用分类", "热门", "品牌男装", "内衣配饰", "家用电器", "手机数码", "电脑办公", "个护化妆", "母婴频道", "食物生鲜", "酒水饮料", "家居家纺", "整车车品", "鞋靴箱包", "运动户外", "图书", "玩具乐器", "钟表", "居家生活", "珠宝饰品", "音像制品", "家具建材", "计生情趣", "营养保健", "奢侈礼品", "生活服务", "旅游出行"};
        toolsTextViews = new TextView[toolsList.length];
        views = new View[toolsList.length];

        for (int i = 0; i < toolsList.length; i++) {
            View view = inflater.inflate(R.layout.item_left_layout, null);
            view.setId(i);
            view.setOnClickListener(toolsItemListener);
            TextView textView = view.findViewById(R.id.text);
            icon_select = view.findViewById(R.id.icon_select);

            textView.setText(toolsList[i]);
            toolsLayout.addView(view);
            toolsTextViews[i] = textView;
            views[i] = view;
        }
        changeTextColor(0);
    }


    private View.OnClickListener toolsItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goods_pager.setCurrentItem(v.getId());
        }
    };


    /**
     * 改变textView 和 背景颜色的颜色
     *
     * @param id
     */
    private void changeTextColor(int id) {
        for (int i = 0; i < toolsTextViews.length; i++) {
            if (i != id) {
                views[i].setBackgroundResource(R.color.bg_color);
                toolsTextViews[i].setTextColor(getResources().getColor(R.color.text_second_color));
            }
        }
        views[id].setBackgroundResource(android.R.color.white);
        toolsTextViews[id].setTextColor(getResources().getColor(R.color.text_first_color));
        icon_select.setVisibility(View.VISIBLE);
    }



    @OnClick({
            R.id.iv_back
    })
    public void clicks(View view) {
        if (StringUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    /** 选中的话题 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(UpdateTopicEvent event){
        KLog.d("tag","选择的话题是：" + event.getTitle());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("topicName",event.getTitle());
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }

}
