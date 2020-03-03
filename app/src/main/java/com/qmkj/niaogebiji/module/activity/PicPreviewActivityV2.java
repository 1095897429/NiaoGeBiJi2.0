package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.adapter.ImageBrowseAdapter;
import com.qmkj.niaogebiji.module.adapter.PicPreViewItemAdapter;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.PicBean;
import com.qmkj.niaogebiji.module.widget.RecyclerViewNoBugLinearLayoutManager;
import com.socks.library.KLog;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:快讯图片预览界面 -- recyclerView
 *
 * 1.查看原图，
 */
public class PicPreviewActivityV2 extends BaseActivity {

    @BindView(R.id.icon_preview_back)
    ImageView icon_preview_back;

    @BindView(R.id.loading_progress)
    ProgressBar loading_progress;

    @BindView(R.id.number_textview)
    TextView mNumberTextView;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.icon_preview_download)
    ImageView icon_preview_download;

    @BindView(R.id.head_part)
    RelativeLayout head_part;

    @BindView(R.id.pic_look)
    TextView pic_look;



    private PicPreViewItemAdapter mImageBrowseAdapter;


    private ArrayList<PicBean> imagePicList = new ArrayList<>();
    PicBean mPicBean;

    private ArrayList<String> imageList = new ArrayList<>();

    //点击是哪个索引的图片
    private int currentIndex = 0;
    //是否是网络图片
    private boolean fromNet = false;

    private boolean isShowDown;

    private ExecutorService mExecutorService;

    private Bitmap bitmap =  null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_preview_v2;
    }

    @Override
    protected void initView() {
        mExecutorService = Executors.newFixedThreadPool(2);
        //加载动画
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initLayout();

        loadData();

        initEvents();
        //展示数据
        showData();
    }

    String scaleSize = "?imageMogr2/auto-orient/format/jpg/ignore-error/1/thumbnail/!50p";

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
                mPicBean.setScalePic(imageList.get(i) + scaleSize);
                mPicBean.setPic(imageList.get(i));
                imagePicList.add(mPicBean);
            }

            mImageBrowseAdapter.setNewData(imagePicList);

            //移动到具体item
            recycler.scrollToPosition(currentIndex);


            if(!isShowDown){
                head_part.setVisibility(View.GONE);
            }else{
                head_part.setVisibility(View.VISIBLE);
            }
        }
    }



    LinearLayoutManager mLinearLayoutManager;
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        //设置布局管理器
        recycler.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mImageBrowseAdapter = new PicPreViewItemAdapter (imagePicList,this);
        recycler.setAdapter (mImageBrowseAdapter);
        ((SimpleItemAnimator)recycler.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        recycler.setNestedScrollingEnabled(true);
        recycler.setHasFixedSize(true);
        initEvents();

        //切换时整体移动
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recycler);

    }



    private void initEvents(){
        recycler.setOnScrollListener(new RvScrollListener());
    }



    private class RvScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取第一个可见view的位置
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                //获取最后一个可见view的位置
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                currentIndex = firstItemPosition;

                showData();
            }

        }
    }


    private void showData(){
        if(imageList.size() > 0) {
            updateBottomIndex(currentIndex + 1);

            //隐藏查看原图按钮
//            if(imagePicList.get(currentIndex).isYuanTu()){
//                pic_look.setVisibility(View.GONE);
//            }else{
//                pic_look.setVisibility(View.VISIBLE);
//            }
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



    /**
     * 获取网络图片
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl){
        URL url;
        HttpURLConnection connection;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream=connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x113){
                StringUtil.saveImageToGallery(bitmap, BaseApp.getApplication());
            }else if(msg.what == 0x112){
                imagePicList.get(currentIndex).setBitmap(bitmap);
                imagePicList.get(currentIndex).setYuanTu(true);
                mImageBrowseAdapter.notifyItemChanged(currentIndex);
                pic_look.setVisibility(View.GONE);

                bitmap = null;
            }

        }
    };





    @OnClick({R.id.icon_preview_back,
            R.id.icon_preview_download,
            R.id.pic_look

    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.pic_look:
                //得到当前索引
                KLog.d("tag","获取图片的原图路径是 " + imageList.get(currentIndex) );


                mExecutorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = GetImageInputStream(imagePicList.get(currentIndex).getPic());
                        mHandler.sendEmptyMessage(0x112);
                    }
                });


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


}
