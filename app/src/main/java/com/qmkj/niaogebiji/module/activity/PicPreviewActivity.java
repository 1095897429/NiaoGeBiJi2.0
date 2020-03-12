package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.ImageBrowseAdapter;
import com.qmkj.niaogebiji.module.bean.PicBean;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:快讯图片预览界面
 */
public class PicPreviewActivity extends BaseActivity {

    @BindView(R.id.icon_preview_back)
    ImageView icon_preview_back;

    @BindView(R.id.loading_progress)
    ProgressBar loading_progress;

    @BindView(R.id.number_textview)
    TextView mNumberTextView;

    @BindView(R.id.imageBrowseViewPager)
    ViewPager imageBrowseViewPager;

    @BindView(R.id.icon_preview_download)
    ImageView icon_preview_download;

    @BindView(R.id.head_part)
    RelativeLayout head_part;


    private ImageBrowseAdapter mImageBrowseAdapter;


    private ArrayList<PicBean> imagePicList = new ArrayList<>();
    PicBean mPicBean;

    private ArrayList<String> imageList = new ArrayList<>();

    private ArrayList<String> tempList = new ArrayList<>();

    //点击是哪个索引的图片
    private int currentIndex = 0;
    //是否是网络图片
    private boolean fromNet = false;

    private boolean isShowDown;

    private ExecutorService mExecutorService;

    private Bitmap bitmap =  null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_preview;
    }

    @Override
    protected void initView() {
        mExecutorService = Executors.newFixedThreadPool(2);
        //加载动画
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loadData();

        initEvents();
        //展示数据
        showData();
    }

//    String scaleSize = "?imageMogr2/auto-orient/format/jpg/ignore-error/1/thumbnail/!50p";
//    String scaleSize = "?imageMogr2/auto-orient/format/jpg/ignore-error/1/thumbnail/!40p/imageslim";


    private void loadData(){
        Intent intent = getIntent();
        if(intent != null){
            currentIndex = intent.getIntExtra("index", 0);
            fromNet = intent.getBooleanExtra("fromNet",false);
            isShowDown = intent.getBooleanExtra("isShowDown",true);
            imageList =  intent.getStringArrayListExtra("imageList");

            //封装新的对象

            for (int i = 0; i < imageList.size(); i++) {
                mPicBean = new PicBean();
                mPicBean.setPic(imageList.get(i) + Constant.scaleSize);
                mPicBean.setYuanTu(false);
                imagePicList.add(mPicBean);
            }

            mImageBrowseAdapter = new ImageBrowseAdapter (PicPreviewActivity.this,imagePicList);
            imageBrowseViewPager.setAdapter (mImageBrowseAdapter);
            imageBrowseViewPager.setCurrentItem (currentIndex);


            if(!isShowDown){
                head_part.setVisibility(View.GONE);
            }else{
                head_part.setVisibility(View.VISIBLE);
            }
        }
    }



    private void initEvents(){
        imageBrowseViewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                currentIndex = position;
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


    @OnClick({R.id.icon_preview_back,
            R.id.icon_preview_download,
            R.id.pic_look

    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.pic_look:
                //得到当前索引
                KLog.d("tag","获取图片的原图路径是 " + imageList.get(currentIndex) );
                //展示进度 --- 更新视图
                imagePicList.get(currentIndex).setPic(imageList.get(currentIndex));
                imagePicList.get(currentIndex).setYuanTu(true);

                mImageBrowseAdapter.notifyDataSetChanged();

                break;
            case R.id.icon_preview_back:
                finish();
                break;
            case R.id.icon_preview_download:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    KLog.d("tag",icon_preview_download.isEnabled() + "");
                    mExecutorService.submit(() -> {
                        bitmap =  StringUtil.getBitmap(imageList.get(currentIndex));
                        mHandler.sendEmptyMessage(0x113);
                    });
                }
                break;

            default:
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            StringUtil.saveImageToGallery(bitmap, BaseApp.getApplication());
        }
    };


}
