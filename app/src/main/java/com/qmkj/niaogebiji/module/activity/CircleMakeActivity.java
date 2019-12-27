package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.io.File;
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
                        send.setEnabled(true);
                        send.setTextColor(getResources().getColor(R.color.text_first_color));

                        if(mString.length() > num){
                            listentext.setTextColor(Color.parseColor("#FFFF5040"));
                        }else{
                            listentext.setTextColor(Color.parseColor("#818386"));
                        }

                    }else{
                        send.setEnabled(false);
                        send.setTextColor(Color.parseColor("#CC818386"));
                    }

                    listentext.setText(mString.length() + "");
                });

        initLayout();
    }

    @OnClick({R.id.cancel,R.id.send,R.id.link,
                R.id.to_delete_link,
                R.id.make,
                R.id.part2222})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(mEditText);
        switch (view.getId()){
            case R.id.part2222:
                KLog.d("tag","跳转到外链");
                UIHelper.toWebViewActivity(this,linkurl);
                break;
            case R.id.make:
                showHeadDialog();
                break;
            case R.id.to_delete_link:
                part2222.setVisibility(View.GONE);
                linkurl = "";
                linkTitle = "";
                setStatus(true);
                break;
            case R.id.link:
                UIHelper.toCircleMakeAddLinkActivity(this,REQCODE);
                overridePendingTransition(R.anim.activity_enter_right,R.anim.activity_alpha_exit);
                break;
            case R.id.send:
                if(mString.length() > num){
                    ToastUtils.showShort("内容最多输入140字");
                    return;
                }
                sendPicToQiuNiu();
                break;
            case R.id.cancel:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //添加链接返回
        if(REQCODE == requestCode && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            linkurl = bundle.getString("add_link");
            linkTitle = bundle.getString("link_title");
            //获取到是 https://m.weibo.cn/detail/4452636415281894?display=0&retcode=6102，需显示域名

            String tempLink   = StringUtil.getDomain(linkurl);

            if(!TextUtils.isEmpty(linkurl)){
                part2222.setVisibility(View.VISIBLE);
                link_url.setText(tempLink);
                link_title.setText(linkTitle);
                setStatus(false);
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
        mTempMsgBean.setLinkTitle(linkTitle);
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
        if(!TextUtils.isEmpty(mEditText.getEditableText().toString())){

        }

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
                link_url.setText(mTempMsgBean.getLinkurl());
                link_title.setText(mTempMsgBean.getLinkTitle());
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


        Bundle bundle = new Bundle ();
        bundle.putStringArrayList ("imageList", photos);
        bundle.putBoolean("fromNet",true);
        bundle.putInt("index",position);
        Intent intent = new Intent(this, PicPreviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }




}
