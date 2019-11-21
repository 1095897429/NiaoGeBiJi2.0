package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CirclePicItemAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MulMediaFile;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.db.DBManager;
import com.qmkj.niaogebiji.module.widget.GlideLoader;
import com.socks.library.KLog;
import com.xzh.imagepicker.ImagePicker;
import com.xzh.imagepicker.bean.MediaFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-15
 * 描述:圈子帖发布界面
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
    //集合
    List<MulMediaFile> mList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private String mString;
    private int textLength;
    //编辑字数限制
    private int num = 140;
    public static final int pic_num = 9;

    //url地址
    private String linkurl;
    private String linkTitle;
    //图片选择器临时返回数据
    private  ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    private TempMsgBean mTempMsgBean;

    private static final int REQCODE = 100;

    private int REQUEST_SELECT_IMAGES_CODE = 0x01;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_circle_make;
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
                    KLog.d("tag", "accept: " + charSequence.toString() );
                    //　trim()是去掉首尾空格
                    mString = charSequence.toString().trim();
                    KLog.d("tag",mString);
                    if(!TextUtils.isEmpty(mString) && mString.length() != 0){
                        send.setEnabled(true);
                        send.setTextColor(getResources().getColor(R.color.text_first_color));
                        textLength = mString.length();
                        //设置光标在最后
                        mEditText.setSelection(charSequence.toString().length());

                        if(mString.length() > num){
                            KLog.d("tag","超出了");
                            return;
                        }
                        listentext.setText(textLength + " / " + num);
                    }else{
                        send.setEnabled(false);
                        send.setTextColor(Color.parseColor("#CC818386"));
                    }
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
                initPicker();
                break;
            case R.id.to_delete_link:
                part2222.setVisibility(View.GONE);
                linkurl = "";
                linkTitle = "";
                setStatus(true);
                break;
            case R.id.link:
                KLog.d("tag","添加链接");
                UIHelper.toCircleMakeAddLinkActivity(this,REQCODE);
                overridePendingTransition(R.anim.activity_enter_right,R.anim.activity_alpha_exit);
                break;
            case R.id.send:
                KLog.d("tag","发布");
                break;
            case R.id.cancel:
                //如果有正文 有外链 有图片
                if(!TextUtils.isEmpty(mString) || !TextUtils.isEmpty(linkTitle)
                    || !mediaFiles.isEmpty()){
                    showSaveConetentExit();
                    return;
                }

                saveTempMsg(null);
                finish();
                break;
            default:
        }
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
            if(!TextUtils.isEmpty(linkurl)){
                part2222.setVisibility(View.VISIBLE);
                link_url.setText(linkurl);
                link_title.setText(linkTitle);
                setStatus(false);
            }
        }
        //添加图片返回图片路径
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            mediaFiles = (ArrayList<MediaFile>) data.getSerializableExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            if(null != mediaFiles && !mediaFiles.isEmpty()){
                setPicData(mediaFiles);
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
        }).setMsg("你有保存的草稿，是否使用？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    private void setData() {
        if(null == mTempMsgBean){
            mTempMsgBean = new TempMsgBean();
            mTempMsgBean.setContent(mString);
            mTempMsgBean.setLinkTitle(linkTitle);
            mTempMsgBean.setLinkurl(linkurl);
            if(!mediaFiles.isEmpty()){
                mTempMsgBean.setImgPath(mediaFiles);
            }
            saveTempMsg(mTempMsgBean);
        }
    }

    private void getData() {
        if(null != mTempMsgBean) {
            if(!TextUtils.isEmpty(mTempMsgBean.getContent())){
                mEditText.setText(mTempMsgBean.getContent());
                mString = mTempMsgBean.getContent();
            }

            if(!TextUtils.isEmpty(mTempMsgBean.getLinkurl())){
                part2222.setVisibility(View.VISIBLE);
                linkTitle = mTempMsgBean.getLinkTitle();
                linkurl = mTempMsgBean.getLinkurl();
                link_url.setText(mTempMsgBean.getLinkurl());
                link_title.setText(mTempMsgBean.getLinkTitle());
                setStatus(false);
            }

            if(null != mTempMsgBean.getImgPath() && !mTempMsgBean.getImgPath().isEmpty()){
                setPicData(mTempMsgBean.getImgPath());
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
        ImagePicker.getInstance()
                .setTitle("相册与视频")
                //选择上次选中的图片
                .setImages(mediaFiles)
                //设置是否展示图片
                .showImage(true)
                //设置是否展示视频
                .showVideo(false)
                //设置最大选择图片数目(默认为1，单选)
                .setMaxCount(pic_num)
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

            mediaFiles.remove(position);
            mList.remove(position);
            //TODO 19.20 == 0 表示原始数据无
            if(mediaFiles.size() == 0){
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
                    initPicker();
                    break;
                case CirclePicItemAdapter.NORMAL:
                    KLog.d("tag","预览");

                    toPicPrewView();
                    break;
                    default:
            }
        });
    }

    //手动的创建第二个加号图片实体bean
    private void setPicData(List<MediaFile> mediaFiles) {
        part3333.setVisibility(View.VISIBLE);
        mList.clear();
        MulMediaFile mulMediaFile;

        for (int i = 0; i < mediaFiles.size(); i++) {
            mulMediaFile = new MulMediaFile();
            mulMediaFile.setMediaFile(mediaFiles.get(i));
            mulMediaFile.setItemType(1);
            mList.add(mulMediaFile);
        }

        //情况一 添加最后的加号
        mulMediaFile = new MulMediaFile();
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


    /** --------------------------------- 图片预览  ---------------------------------*/
    private void toPicPrewView() {
        ArrayList<String> photos = new ArrayList<>();
        for (int i = 0; i < mediaFiles.size(); i++) {
            photos.add(mediaFiles.get(i).getPath());
        }
        Bundle bundle = new Bundle ();
        bundle.putStringArrayList ("imageList", photos);
        bundle.putBoolean("fromNet",true);
        bundle.putInt("index",0);
        Intent intent = new Intent(this, PicPreviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }



}
