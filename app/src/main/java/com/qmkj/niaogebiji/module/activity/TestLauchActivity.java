package com.qmkj.niaogebiji.module.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.module.adapter.TestItemAdapter;
import com.qmkj.niaogebiji.module.adapter.TestLaunchItemAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.TestAllBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:开始测试
 * 下一题有效果
 * 1.选择答案 , 无倒计时
 * 2.倒计时结束，无选择答案
 * 3.倒计时中，选择答案
 */
public class TestLauchActivity extends BaseActivity {

    @BindView(R.id.current_page)
    TextView current_page;

    @BindView(R.id.total)
    TextView total;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.toNext)
    TextView toNext;

    @BindView(R.id.toSubmit)
    TextView toSubmit;


    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_conent)
    LinearLayout ll_conent;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.toNextbyAnim)
    TextView toNextbyAnim;


    //适配器
    TestLaunchItemAdapter mTestLaunchItemAdapter;
    //组合集合
    List<TestBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //此类包含所有的选项
    private TestAllBean mTestAllBean;

    private String answer_no;

    Disposable disposable;
    //文本显示倒计时
    public  int time_text = 3;
    //动画倒计时60秒
    public static int COUNT = 3;
    //总共有10题
    private int totalNum = 2;
    //当前的题数
    private int currentNum = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_launch;
    }

    @Override
    protected void initView() {
        tv_title.setText("初级ASO测试");
        total.setText(" /" + totalNum);
        initLayout();
        getData(1);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        current_page.setTypeface(typeface);
        total.setTypeface(typeface);
    }

    @Override
    public void initData() {
        setAnimation(progressBar,100);
    }

    private void getData(int currentNum) {

        toNext.setEnabled(false);
        toSubmit.setEnabled(false);

        if(currentNum == totalNum){
            toSubmit.setVisibility(View.GONE);
            toNext.setText("交卷");
        }

        mAllList.clear();
        if(currentNum == 1){
            TestBean bean1;
            for (int i = 0; i < 4; i++) {
                bean1 = new TestBean();
                bean1.setAnswer(" 留存是延长用户生命周期" + i);
                mAllList.add(bean1);
            }
        }else{
            TestBean bean1;
            for (int i = 0; i < 4; i++) {
                bean1 = new TestBean();
                bean1.setAnswer(" 我是题目" + currentNum);
                mAllList.add(bean1);
            }
        }

        mTestLaunchItemAdapter.setNewData(mAllList);
    }



    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mTestLaunchItemAdapter = new TestLaunchItemAdapter(mAllList);
        mRecyclerView.setAdapter(mTestLaunchItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        mTestLaunchItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                List<TestBean> mDatas = adapter.getData();
                //① 将所有的selected设置false，当前点击的设为true
                for (TestBean data : mDatas) {
                    data.setSelect(false);
                }
                TestBean temp = mDatas.get(position);
                temp.setSelect(true);
                toNext.setEnabled(true);
                toSubmit.setEnabled(true);
                mTestLaunchItemAdapter.notifyDataSetChanged();

        });
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.iv_back,R.id.toNext,R.id.toSubmit})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toSubmit:

                //动画暂停
                if(animator != null && animator.isRunning()){
                    animator.pause();
                }

                showSubmit();
                break;

            case R.id.toNext:
                //有动画走这里 取消动画
                if(animator != null && animator.isRunning()){
                    animator.cancel();
                    setAnimation(progressBar,100);
                    changeData();
                    return;
                }

                //没有动画走这里
                changeData();

                break;
            case R.id.iv_back:
                finish();
                break;

            default:
        }
    }

    private void changeData() {
        //加载下一页数据时判断 最后一题
        if(totalNum == currentNum){
            KLog.d("tag","到了最后一页了");
            finish();
            return;
        }
        //更换数据源
        ++currentNum;
        current_page.setText(currentNum + "");
        getData(currentNum);
        //恢复状态
        toNext.setTextSize(17);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void showSubmit(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("交卷", v -> {

            finish();

        }).setNegativeButton("再想想", v -> {

            //动画暂停
            if(animator != null && animator.isPaused()){
                animator.resume();
            }

        }).setMsg("你没有答完全部题目\n" +
                "\n" +
                "确定要提前交卷吗？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }




    private void initRxTime() {
        //参数依次为：从0开始，发送次数是9次 ，0秒延时,每隔1秒发射,主线程中
        disposable = Observable.intervalRange(0,time_text + 1,0,1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    if (toNextbyAnim != null) {
                        if(totalNum == currentNum){
                            toNextbyAnim.setText("本题回答时间已到，将自动提交答案  " + (time_text - aLong) +  " s");
                        }else{
                            toNextbyAnim.setText("本题回答时间已到，将自动切换到下一题  " + (time_text - aLong) +  " s");
                        }
                    }
                }).doOnComplete(() -> {
                    KLog.d("tag","文本倒计时结束");
                    toNextbyAnim.setVisibility(View.GONE);
                    changeData();
                    setAnimation(progressBar,100);
                })
                .subscribe();

    }

    boolean isAnimPause;
    ValueAnimator animator;
    private void setAnimation(final ProgressBar view, final int mProgressBar) {
        animator = ValueAnimator.ofInt(mProgressBar, 0).setDuration(COUNT * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimator -> view.setProgress((Integer) valueAnimator.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                KLog.d("tag","取消了");
                isAnimPause = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画正常结束 动画对象存在 界面存储 文本存在
               if(!isAnimPause && animation != null && null != this && toNextbyAnim != null){
                   KLog.d("tag","动画结束,显示文本倒计时");
                   toNextbyAnim.setVisibility(View.VISIBLE);
                   initRxTime();
               }

                //重新恢复状态
                isAnimPause = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }
        });
        animator.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d("tag","onDestroy");
        if(null != disposable){
            disposable.dispose();
        }

        if(null != animator){
            animator.cancel();
            animator = null;
        }
    }
}
