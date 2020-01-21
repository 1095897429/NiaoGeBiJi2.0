package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.HeadAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.FileHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.PicPathHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CirclePicItemAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MulMediaFile;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.QINiuTokenBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.db.DBManager;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.qmkj.niaogebiji.module.event.SendingCircleEvent;
import com.qmkj.niaogebiji.module.widget.GlideLoader;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.xzh.imagepicker.ImagePicker;
import com.xzh.imagepicker.bean.MediaFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-15
 * 描述:圈子帖发布界面
 * 1.发布帖子成功,删除草稿 (没有的原因是销毁了 )  -- eventbus失效了
 * 2.采用全局变量
 *
 * 1.文本没输入 -- 不可用
 * 2.url 有问题 -- 不可用
 *
 * 思路：获取url是否可用，不可用的话isUrlOk = false;同时设置send不可点击
 *      保存数据刚进入界面时，逻辑同上
 */
public class CircleMakeActivity extends BaseActivity {

    @BindView(R.id.et_input)
    EditText mEditText;

    @BindView(R.id.listentext)
    TextView listentext;

    @BindView(R.id.cancel)
    TextView cancel;

    @BindView(R.id.send)
    TextView send;

    @BindView(R.id.part2222)
    LinearLayout part2222;

    @BindView(R.id.link_title)
    TextView link_title;

    @BindView(R.id.link_url)
    TextView link_url;

    @BindView(R.id.make)
    ImageView make;

    @BindView(R.id.link)
    ImageView link;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.part3333)
    LinearLayout part3333;


    //适配器
    CirclePicItemAdapter mCirclePicItemAdapter;
    //总的集合
    List<MulMediaFile> mList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private String mString;
    //编辑字数限制
    private int num = 140;
    public  int pic_num = 9;

    //url地址
    private String linkurl = "";
    private String linkTitle = "";
    //临时 图片选择器临时返回数据
    private  ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    //临时拍照 图片路径集合
    private ArrayList<MediaFile> pathList = new ArrayList<>();

    private TempMsgBean mTempMsgBean;


    private Uri imageUri;
    public static final int TAKE_PHOTO = 2;

    private static final int REQCODE = 100;

    private int REQUEST_SELECT_IMAGES_CODE = 0x01;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_circle_make;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    public void initData() {

//        mHandler = new MyHandler(this);

        mTempMsgBean = getTempMsg();
        //上次是保存草稿的
        if(null != mTempMsgBean){
            showSaveConetentEnter();
        }else{
            removeTempMsg();
            KeyboardUtils.showSoftInput(mEditText);
        }

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        RxTextView
                .textChanges(mEditText)
                .subscribe(charSequence -> {
                    //英文单词占1个字符  表情占2个字符 中文占1个字符
                    //目前用上面的方案的
                    KLog.d("tag", "accept: " + charSequence.toString() + " 字符长度 " + charSequence.length() );
                    //trim()是去掉首尾空格
                    mString = charSequence.toString().trim();
                    if(!TextUtils.isEmpty(mString) && mString.length() != 0){

                        //如果是网址  并且url无效，同样的不给点击
                        KLog.d("tag","长度是 " + link_title.getText().toString());
                        if(!TextUtils.isEmpty(link_title.getText().toString()) && !isUrlOk){
                            return;
                        }

                        setSendStatus(true);

                        if(mString.length() > num){
                            listentext.setTextColor(Color.parseColor("#FFFF5040"));
                        }else{
                            listentext.setTextColor(Color.parseColor("#818386"));
                        }

                    }else{
                        setSendStatus(false);
                    }

                    listentext.setText(mString.length() + "");
                });

        initLayout();
    }


    private void setSendStatus(boolean sendStatus){
        if(sendStatus
                &&  !TextUtils.isEmpty(mEditText.getText().toString().trim())
                && mEditText.getText().toString().trim().length() > 0){
            send.setEnabled(true);
            send.setTextColor(getResources().getColor(R.color.text_first_color));
        }else{
            send.setEnabled(false);
            send.setTextColor(Color.parseColor("#CC818386"));
        }
    }

    @OnClick({R.id.cancel,R.id.send,
                R.id.link,
                R.id.to_delete_link,
                R.id.make,
                R.id.part2222})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(mEditText);

        switch (view.getId()){
            case R.id.part2222:
                if(StringUtil.isFastClick()){
                    return;
                }
                KLog.d("tag","跳转到外链");
                UIHelper.toWebViewActivity(this,linkurl);
                break;
            case R.id.make:
                MobclickAgentUtils.onEvent(UmengEvent.quanzi_publish_pictbtn_2_0_0);
                showHeadDialog();
                break;
            case R.id.to_delete_link:
                part2222.setVisibility(View.GONE);
                linkurl = "";
                linkTitle = "";
                isUrlOk = true;
                setStatus(true);
                setSendStatus(true);
                break;
            case R.id.link:
                MobclickAgentUtils.onEvent(UmengEvent.quanzi_publish_linkbtn_2_0_0);

                UIHelper.toCircleMakeAddLinkActivity(this,REQCODE);
                overridePendingTransition(R.anim.activity_enter_right,R.anim.activity_alpha_exit);
                break;
            case R.id.send:
                if(StringUtil.isFastClick()){
                    return;
                }
                MobclickAgentUtils.onEvent(UmengEvent.quanzi_message_onekeybtn_2_0_0);

                if(mString.length() > num){
                    ToastUtils.showShort("内容最多输入140字");
                    return;
                }
                sendPicToQiuNiu();
                break;
            case R.id.cancel:
                if(StringUtil.isFastClick()){
                    return;
                }
                //如果有正文 有外链 有图片
                if(!TextUtils.isEmpty(mString) || !TextUtils.isEmpty(linkTitle)
                    || !mediaFiles.isEmpty() || !pathList.isEmpty()){
                    showSaveConetentExit();
                    return;
                }

                saveTempMsg(null);
                finish();
                break;
            default:
        }
    }

    private void sendPicToQiuNiu() {

        sendData();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("key",mTempMsgBean);
        intent.putExtras(bundle);
        setResult(200,intent);
        finish();

    }

    private void showHeadDialog(){
        HeadAlertDialog dialog = new HeadAlertDialog(this).builder();
        dialog.setOnDialogItemClickListener(position1 -> {
            if(0 == position1){
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    openTakePhoto();
                }
            }else if(1 == position1){
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    initPicker();
                }
            }
        });
        dialog.show();
    }

    private void openTakePhoto() {
        File outputImage = FileHelper.getOutputCircleImageFile(this);
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,
                    AppUtils.getAppPackageName() + ".fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void onBackPressed() {
        clicks(cancel);
    }


    public  int testWsdlConnection(String address) {
        int status = 404;
        try {
            URL urlObj = new URL(address);
            HttpURLConnection oc = (HttpURLConnection) urlObj.openConnection();
            oc.setUseCaches(false);
            // 设置超时时间
            oc.setConnectTimeout(10 * 1000);

            oc.setReadTimeout(10 * 1000);
            // 请求状态
            status = oc.getResponseCode();
            if (200 == status) {
                // 200是请求地址顺利连通
                // 404是服务器的资源丢失或者连接失败
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            KLog.d("tag",e.getMessage());
        }
        return status;
    }


    public static String getWebTitle(String url){
        try {
            //还是一样先从一个URL加载一个Document对象。
            Document doc = Jsoup.connect(url).get();
            String title = doc.title();
            return title;
        }catch(Exception e) {
            return "";
        }
    }

    /**
     * 创建静态内部类
     */
//    private static class MyHandler extends Handler{
//        //持有弱引用HandlerActivity,GC回收时会被回收掉.
//        private final WeakReference<Activity> mActivty;
//        public MyHandler(Activity activity){
//            mActivty =new WeakReference<Activity>(activity);
//        }
//        @Override
//        public void handleMessage(Message msg) {
//            Activity activity = mActivty.get();
//            super.handleMessage(msg);
//            if(activity != null){
//                //执行业务逻辑
//                link_url.setText(tempLink);
//                link_title.setText(TextUtils.isEmpty(linkTitle) ? "无标题网址" : linkTitle);
//                setStatus(false);
//            }
//        }
//    }


    @SuppressLint("HandlerLeak")
    public  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(this != null){
                int code = (int) msg.obj;
                if(200 != code){
                    setSendStatus(false);
                }else{
                    setSendStatus(true);
                }
                link_url.setText(tempLink);
                link_title.setText(TextUtils.isEmpty(linkTitle) ? "无标题网址" : linkTitle);
                setStatus(false);
            }
        }
    };

    String title;
    String tempLink;
    //判断此url是否可用
    boolean isUrlOk;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //添加链接返回
        if(REQCODE == requestCode && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            linkurl = bundle.getString("add_link");
            //这里是没有值的
            linkTitle = bundle.getString("link_title");

            //先回复默认值
            link_url.setText("");
            link_title.setText("链接解析中...");

            //解析的时候不能点击
            setStatus(false);
            setSendStatus(false);

            //检查link 并且是否符合规范
            if(!TextUtils.isEmpty(linkurl)){
                //① 获取到是 https://m.weibo.cn/detail/4452636415281894?display=0&retcode=6102，需显示域名
                tempLink   = StringUtil.getDomain(linkurl);
                //② 显示布局
                part2222.setVisibility(View.VISIBLE);

                new Thread(() -> {

                    //③ 获取网页标题
                    linkTitle = getWebTitle(linkurl);
                    //④ 判断链接是否可用
                    int status = testWsdlConnection(linkurl);
                    KLog.e("tag",status + "");
                    if(this != null && !this.isFinishing()){
                        if(200 != status){
                            isUrlOk = false;
                            ToastUtils.showShort("解析失败，请重新添加");
                            linkTitle = "解析失败，请重新添加";
                        }else{
                            isUrlOk = true;

                        }
                        Message message = Message.obtain();
                        message.obj = status;
                        mHandler.sendMessage(message);
                    }
                }).start();

            }
        }
        //添加图片返回图片路径
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            mediaFiles = (ArrayList<MediaFile>) data.getSerializableExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            if(null != mediaFiles && !mediaFiles.isEmpty()){
                setPicData(mediaFiles,pathList);
                setStatus(false);
            }
        }

        //拍照 添加图片返回图片路径
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            String realPath = PicPathHelper.getRealFilePath(this,imageUri);
            KLog.d("tag","真实路径 " + realPath);
            MediaFile mediaFile = new MediaFile();
            mediaFile.setPath(realPath);
            pathList.add(mediaFile);
            if(null != pathList && !pathList.isEmpty()){
                setPicData(mediaFiles,pathList);
                setStatus(false);
            }
        }
    }


    public void showSaveConetentExit(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("保存", v -> {
           setData();
           finish();
        }).setNegativeButton("不保存", v -> {
            saveTempMsg(null);
            finish();
        }).setMsg("要保存草稿吗？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }



    public void showSaveConetentEnter(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("使用", v -> {
            getData();
        }).setNegativeButton("不使用", v -> {
            removeTempMsg();
        }).setMsg("你有保存的草稿，是否使用？").setCanceledOnTouchOutside(false).setCancelable(false);
        iosAlertDialog.show();
    }

    private void setData() {
        if(null == mTempMsgBean){
            mTempMsgBean = new TempMsgBean();
        }

        mTempMsgBean.setContent(mString);
        mTempMsgBean.setLinkTitle(linkTitle);
        mTempMsgBean.setLinkurl(linkurl);

        if(!mediaFiles.isEmpty()){
            mTempMsgBean.setImgPath(mediaFiles);
        }else{
            mTempMsgBean.setImgPath(null);
        }


        if(!pathList.isEmpty()){
            mTempMsgBean.setImgPath2(pathList);
        }else {
            mTempMsgBean.setImgPath2(null);
        }

        saveTempMsg(mTempMsgBean);
    }

    private void sendData() {
        if(null == mTempMsgBean){
            mTempMsgBean = new TempMsgBean();
        }

        mTempMsgBean.setContent(mEditText.getText().toString().trim());
        //12.26 link 在点击删除按钮时清空了
        mTempMsgBean.setLinkTitle(link_title.getText().toString().trim());
        mTempMsgBean.setLinkurl(linkurl);
        //
        if(!mediaFiles.isEmpty()){
            mTempMsgBean.setImgPath(mediaFiles);
        }else{
            mTempMsgBean.setImgPath(null);
        }
        if(!pathList.isEmpty()){
            mTempMsgBean.setImgPath2(pathList);
        }else{
            mTempMsgBean.setImgPath2(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getData() {
        if(null != mTempMsgBean) {
            if(!TextUtils.isEmpty(mTempMsgBean.getContent())){
                mEditText.setText(mTempMsgBean.getContent());
                mString = mTempMsgBean.getContent();
                mEditText.setSelection(mEditText.getEditableText().toString().length());
                mEditText.setCursorVisible(true);
                //TODO 这句很重要，只有调用了这句，光标才能显示出来
                mEditText.requestFocus();
            }

            if(!TextUtils.isEmpty(mTempMsgBean.getLinkurl())){
                part2222.setVisibility(View.VISIBLE);
                linkTitle = mTempMsgBean.getLinkTitle();
                linkurl = mTempMsgBean.getLinkurl();
                link_title.setText(mTempMsgBean.getLinkTitle());
                if(!TextUtils.isEmpty(mTempMsgBean.getLinkurl())){
                    //① 显示域名
                    tempLink   = StringUtil.getDomain(mTempMsgBean.getLinkurl());
                    link_url.setText(tempLink);

                    new Thread(() -> {
                        //③ 获取网页标题
                        linkTitle = mTempMsgBean.getLinkTitle();
                        //④ 判断链接是否可用
                        int status = testWsdlConnection(linkurl);
                        KLog.d("tag",status + "");
                        if(this != null && !this.isFinishing()){
                            if(200 != status){
                                isUrlOk = false;
                                ToastUtils.showShort("解析失败，请重新添加");
                                linkTitle = "解析失败，请重新添加";
                                setSendStatus(false);
                            }else{
                                setSendStatus(true);
                                isUrlOk = true;
                            }
                            Message message = Message.obtain();
                            mHandler.sendMessage(message);
                        }

                    }).start();
                }
                setStatus(false);
            }

            if(null != mTempMsgBean.getImgPath2() && !mTempMsgBean.getImgPath2().isEmpty()){
                setPicData(mTempMsgBean.getImgPath(),mTempMsgBean.getImgPath2());
                pathList.addAll(mTempMsgBean.getImgPath2());
                part3333.setVisibility(View.VISIBLE);
                setStatus(false);
            }

            if(null != mTempMsgBean.getImgPath() && !mTempMsgBean.getImgPath().isEmpty()){
                setPicData(mTempMsgBean.getImgPath(),mTempMsgBean.getImgPath2());
                mediaFiles.addAll(mTempMsgBean.getImgPath());
                part3333.setVisibility(View.VISIBLE);
                setStatus(false);
            }
        }
    }


    /** --------------------------------- 草稿  ---------------------------------*/

    public void saveTempMsg(TempMsgBean tempMsgBean) {
        SPUtils.getInstance().put(Constant.TMEP_MSG_INFO, new Gson().toJson(tempMsgBean));
    }

    public  TempMsgBean getTempMsg() {
        synchronized (this) {
            if (mTempMsgBean == null) {
                String string = SPUtils.getInstance().getString(Constant.TMEP_MSG_INFO);
                if (!TextUtils.isEmpty(string)) {
                    Gson gson = new Gson();
                    mTempMsgBean = gson.fromJson(string, TempMsgBean.class);
                }
            }
        }
        return mTempMsgBean;
    }

    public  void removeTempMsg() {
        mTempMsgBean = null;
        SPUtils.getInstance().remove(Constant.TMEP_MSG_INFO);
    }

    /** --------------------------------- 相册  ---------------------------------*/
    private void initPicker() {

        //减去拍照的数量
        int temp_pic_num = pic_num - pathList.size();
        KLog.d("tag","剩余选择的条数是 " + temp_pic_num);
        ImagePicker.getInstance()
                .setTitle("相册与视频")
                //选择上次选中的图片
                .setImages(mediaFiles)
                //设置是否展示图片
                .showImage(true)
                //设置是否展示视频
                .showVideo(false)
                //设置最大选择图片数目(默认为1，单选)
                .setMaxCount(temp_pic_num)
                .setImageLoader(new GlideLoader())
                .start(this, REQUEST_SELECT_IMAGES_CODE);
    }


    //初始化布局管理器
    private void initLayout() {
        mGridLayoutManager = new GridLayoutManager(this,4);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mCirclePicItemAdapter = new CirclePicItemAdapter(mList);
        mRecyclerView.setAdapter(mCirclePicItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }



    private void initEvent() {
        mCirclePicItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            KLog.d("tag","点击删除的是 " + position);

           String temp = mList.get(position).getLastType();
           if("相册".equals(temp)){
               mediaFiles.remove(position);
           }else if("拍照".equals(temp)){
               pathList.remove(position);
           }

            mList.remove(position);
            //TODO == 0 表示原始数据无
            if(mediaFiles.size() == 0 && pathList.size() == 0){
                mList.clear();
                setStatus(true);
            }
            mCirclePicItemAdapter.notifyDataSetChanged();
        });

        mCirclePicItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            int type = adapter.getItemViewType(position);
            switch (type){
                case CirclePicItemAdapter.ADD:
                    KLog.d("tag","增加");
                    showHeadDialog();
                    break;
                case CirclePicItemAdapter.NORMAL:
                    KLog.d("tag","预览");
                    toPicPrewView(position);
                    break;
                    default:
            }
        });
    }

    //手动的创建第二个加号图片实体bean
    private void setPicData(List<MediaFile> mediaFiles,List<MediaFile> pathList) {
        part3333.setVisibility(View.VISIBLE);
        mList.clear();
        MulMediaFile mulMediaFile;

        if(null != pathList && !pathList.isEmpty()){
            for (int i = 0; i < pathList.size(); i++) {
                mulMediaFile = new MulMediaFile();
                mulMediaFile.setMediaFile(pathList.get(i));
                mulMediaFile.setLastType("拍照");
                mulMediaFile.setItemType(1);
                mList.add(mulMediaFile);
            }
        }


        if(null != mediaFiles && !mediaFiles.isEmpty()){
            for (int i = 0; i < mediaFiles.size(); i++) {
                mulMediaFile = new MulMediaFile();
                mulMediaFile.setMediaFile(mediaFiles.get(i));
                mulMediaFile.setLastType("相册");
                mulMediaFile.setItemType(1);
                mList.add(mulMediaFile);
            }
        }

        //情况一 添加最后的加号
        mulMediaFile = new MulMediaFile();
        mulMediaFile.setLastType("加号");
        mulMediaFile.setItemType(2);

        mList.add(mulMediaFile);

        mCirclePicItemAdapter.setNewData(mList);
    }


    //添加link 添加图片返回时改变状态
    private void setStatus(boolean isEnable) {
        if(isEnable){
            link.setImageResource(R.mipmap.icon_link);
            make.setImageResource(R.mipmap.icon_pic_make);
        }else{
            make.setImageResource(R.mipmap.icon_pic_make_false);
            link.setImageResource(R.mipmap.icon_link_false);
        }
        make.setEnabled(isEnable);
        link.setEnabled(isEnable);
    }


    /** --------------------------------- 图片预览  ---------------------------------
     * @param position*/
    private void toPicPrewView(int position) {

        ArrayList<String> photos = new ArrayList<>();

        for (int i = 0; i < pathList.size(); i++) {
            photos.add(pathList.get(i).getPath());
        }

        //之前只是从相册中拿去
        for (int i = 0; i < mediaFiles.size(); i++) {
            photos.add(mediaFiles.get(i).getPath());
        }


        if(!photos.isEmpty()){
            Bundle bundle = new Bundle ();
            bundle.putStringArrayList ("imageList", photos);
            bundle.putBoolean("fromNet",true);
            bundle.putInt("index",position);
            Intent intent = new Intent(this, PicPreviewActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mHandler != null){
            // 移除所有消息
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
