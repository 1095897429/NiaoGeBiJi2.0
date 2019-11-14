package com.qmkj.niaogebiji.module.activity;

import android.content.Intent;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.adapter.ImageBrowseAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:快讯图片预览界面
 */
public class PicPreviewActivity extends BaseActivity {

    @BindView(R.id.loading_progress)
    ProgressBar loading_progress;

    @BindView(R.id.number_textview)
    TextView mNumberTextView;

    @BindView(R.id.imageBrowseViewPager)
    ViewPager imageBrowseViewPager;

    private ImageBrowseAdapter mImageBrowseAdapter;

    private ArrayList<String> imageList = new ArrayList<>();

    private int currentIndex = 0;
    //是否是网络图片
    private boolean fromNet = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_preview;
    }

    @Override
    protected void initView() {
        //加载动画
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadData();

        initEvents();
        //展示数据
        showData();
    }


    private void loadData(){
        Intent intent = getIntent ();
        if(intent != null){
            currentIndex = intent.getIntExtra("index", 0);
            fromNet = intent.getBooleanExtra("fromNet",false);
            imageList =  intent.getStringArrayListExtra("imageList");
            mImageBrowseAdapter = new ImageBrowseAdapter (PicPreviewActivity.this,imageList);
            imageBrowseViewPager.setAdapter (mImageBrowseAdapter);
            imageBrowseViewPager.setCurrentItem (currentIndex);
        }
    }

    private void initEvents(){
        imageBrowseViewPager .addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                currentIndex = position;
                updateBottomIndex(position + 1);
            }
            @Override
            public void onPageSelected (int position) {
                updateBottomIndex(position + 1);
            }
            @Override
            public void onPageScrollStateChanged (int state) {

            }
        });
    }

    private void showData(){
        if(imageList.size() > 0) {
            updateBottomIndex(currentIndex + 1);
        }
    }

    //底部数字索引
    private void updateBottomIndex(int count){
        mNumberTextView.setText(count + "/" + imageList.size());
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //参数一：Activity2进入动画  参数二：Activity1退出动画
        overridePendingTransition(R.anim.fade_in_disappear,R.anim.fade_out_disappear);
    }


}
