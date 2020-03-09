package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.HeadAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.service.SendService;
import com.qmkj.niaogebiji.common.utils.FileHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.PicPathHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CirclePicItemAdapter;
import com.qmkj.niaogebiji.module.bean.MulMediaFile;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.qmkj.niaogebiji.module.event.SendEvent;
import com.qmkj.niaogebiji.module.widget.GlideLoader;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.xzh.imagepicker.ImagePicker;
import com.xzh.imagepicker.bean.MediaFile;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-15
 * 描述:圈子帖发布界面
 *
 * 思路：获取url是否可用，不可用的话isUrlOk = false;同时设置send不可点击
 *      保存数据刚进入界面时，逻辑同上
 *
 *  1.纯文本  + (话题) -- 可发送
 *  2.纯文本 + 图片 -- 可发送
 *  3.纯文本 + link(有效 isUrlOk) -- 可发送
 *  4.村文本 + 文章 -- 可发送
 *
 *
 *  保存帖子逻辑：
 *  1.只有点击圈子处的发送才显示 上次保存文稿【从文章处来不用显示】
 *
 */
public class CircleMakeActivityV2 extends BaseActivity {

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

    @BindView(R.id.ll_topic)
    LinearLayout ll_topic;

    @BindView(R.id.select_topic_text)
    TextView select_topic_text;


    @BindView(R.id.topic_first)
    ImageView topic_first;

    @BindView(R.id.topic_delete)
    ImageView topic_delete;


    @BindView(R.id.topic)
    ImageView topic;

    //文章详情页过来的文章
    @BindView(R.id.acticle_part)
    LinearLayout acticle_part;

    @BindView(R.id.acticle_img)
    ImageView acticle_img;

    @BindView(R.id.acticle_title)
    TextView acticle_title;




    //适配器
    CirclePicItemAdapter mCirclePicItemAdapter;
    //总的集合
    List<MulMediaFile> mList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private String mString;
    //编辑字数限制
    private int num = 500;
    public  int pic_num = 9;

    //话题
    private String topicId = "";
    private String topicName = "";

    //url地址
    private String linkurl = "";
    private String linkTitle = "";
    //临时 图片选择器临时返回数据
    private  ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    //临时拍照 图片路径集合
    private ArrayList<MediaFile> pathList = new ArrayList<>();

    //文章id
    private String articleId;
    private String articleTitle;
    private String articleImg;

    //发送的数据
    private TempMsgBean mTempMsgBean;


    private Uri imageUri;
    public static final int TAKE_PHOTO = 2;

    private static final int REQCODE = 100;

    private int REQUEST_SELECT_IMAGES_CODE = 0x01;

    public static final int REQUEST_SELECT_TOPIC_CODE = 4;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_circle_make;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }


    private TopicBean mTopicBean;

    //从哪里跳过来的
    private NewsDetailBean mNewsDetailBean;




    TextWatcher mTextWatcher = new TextWatcher() {

        private String temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s.toString().trim();
            mString = temp;
            KLog.d("tag", "accept: " + s.toString());


            //判断emoji输入的
            if(count - before >= 1){
                CharSequence input = s.subSequence(start + before, start + count);
                if(isEmoji(input.toString())){
                    Toast.makeText(mContext,"不支持emoji表情",Toast.LENGTH_SHORT).show();
                    listentext.setText((temp.length() + 2) + "");
                }else{
                    if (!TextUtils.isEmpty(temp)) {
                        String limitSubstring = getLimitSubstring(temp);
                        if (!TextUtils.isEmpty(limitSubstring)) {

                            if (!limitSubstring.equals(temp)) {
                                Toast.makeText(CircleMakeActivityV2.this,"字数已超过限制", Toast.LENGTH_SHORT).show();
                                mEditText.setText(limitSubstring);
                                mEditText.setSelection(limitSubstring.length());
                            }

                            //如果是网址  并且url无效，同样的不给点击
                            if(!TextUtils.isEmpty(link_title.getText().toString()) && !isUrlOk){
                                return;
                            }

                            setSendStatus(true);

                        }
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }


    public  boolean isHasEmoji(CharSequence source) {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        //过滤emoji
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            return true;
        }
        return false;
    }

    private boolean isEmoji(String input){
        Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\ud83e\udc00-\ud83e\udfff]" +
                "|[\u2100-\u32ff]|[\u0030-\u007f][\u20d0-\u20ff]|[\u0080-\u00ff]");
        Matcher m = p.matcher(input);
        return m.find();
    }


    private String getLimitSubstring(String inputStr) {

        int orignLen = inputStr.length();
        float resultLen = 0f;
        String temp ;

        //正常的逻辑
        for (int i = 0; i < orignLen; i++) {
            temp = inputStr.substring(i,i + 1);
            try {
                // 3 bytes to indicate chinese word,1 byte to indicate english word,in utf-8 encode
                if (temp.getBytes("utf-8").length == 3) {
                    resultLen += 1;
                }else if(temp.getBytes("utf-8").length == 1){
                    resultLen += 0.5;
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(resultLen > num){
                listentext.setTextColor(Color.parseColor("#FFFF5040"));
            }else{
                listentext.setTextColor(Color.parseColor("#818386"));
            }

            // Math.round()方法表示的是“四舍五入”的计算
            listentext.setText(Math.round(resultLen) + "");

            if (resultLen > num) {
                return inputStr.substring(0,i);
            }
        }
        return inputStr;
    }



    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

        boolean isTopicIconClick = SPUtils.getInstance().getBoolean("isTopicIconClick",false);
        if(!isTopicIconClick){
            topic_first.setVisibility(View.VISIBLE);
        }


        //放在这里让话题布局不显示 -- 赋值话题id
        if(getIntent().getExtras() != null){
            mTopicBean = (TopicBean) getIntent().getExtras().getSerializable("topicBean");
            mNewsDetailBean = (NewsDetailBean) getIntent().getExtras().getSerializable("articleBean");
            if(null != mTopicBean){
                topicId = mTopicBean.getId() + "";
                select_topic_text.setText(mTopicBean.getTitle());
                topic_delete.setVisibility(View.GONE);
                ll_topic.setVisibility(View.VISIBLE);
                topic.setEnabled(false);
                topic_first.setVisibility(View.GONE);
            }
        }

        mEditText.addTextChangedListener(mTextWatcher);

//        RxTextView
//                .textChanges(mEditText)
//                .subscribe(charSequence -> {
//                    //英文单词占1个字符  表情占2个字符 中文占1个字符
//                    //trim()是去掉首尾空格
//                    mString = charSequence.toString().trim();
//                    KLog.d("tag", "accept: " + charSequence.toString() + " 字符长度 " + charSequence.length() );
//                    if(!TextUtils.isEmpty(mString) && mString.length() != 0){
//                        //如果是网址  并且url无效，同样的不给点击
////                        KLog.d("tag","长度是 " + link_title.getText().toString());
//                        if(!TextUtils.isEmpty(link_title.getText().toString()) && !isUrlOk){
//                            return;
//                        }
//
//                        setSendStatus(true);
//
//                        if(mString.length() > num){
//                            listentext.setTextColor(Color.parseColor("#FFFF5040"));
//                        }else{
//                            listentext.setTextColor(Color.parseColor("#818386"));
//                        }
//
//                    }else{
//                        setSendStatus(false);
//                    }
//
//                    listentext.setText(mString.length() + "");
//                });

        initLayout();



        //TODO 3.6 从文章过来 [设置下方按钮不可点击]
        if(null != mNewsDetailBean){
            articleId = mNewsDetailBean.getAid();
            articleTitle = mNewsDetailBean.getTitle();
            articleImg = mNewsDetailBean.getPic();

            make.setImageResource(R.mipmap.icon_pic_make_false);
            link.setImageResource(R.mipmap.icon_link_false);
            make.setEnabled(false);
            link.setEnabled(false);

            acticle_part.setVisibility(View.VISIBLE);
            acticle_title.setText(mNewsDetailBean.getTitle());
            ImageUtil.loadByDefaultHead(this,mNewsDetailBean.getPic(),acticle_img);

            return;
        }



        //TODO 3.6 从文章过来 [不显示上次保存的文稿]
        mTempMsgBean = getTempMsg();
        //上次是保存草稿的
        if(null != mTempMsgBean){
            showSaveConetentEnter();
        }else{
            removeTempMsg();
            KeyboardUtils.showSoftInput(mEditText);
        }
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

    @OnClick({
                R.id.topic_first,
                R.id.topic_delete,
                R.id.topic,
                R.id.cancel,
                R.id.send,
                R.id.link,
                R.id.to_delete_link,
                R.id.make,
                R.id.part2222,
                R.id.ll_topic,
                R.id.acticle_part
    })
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(mEditText);

        switch (view.getId()){
            case R.id.acticle_part:
                if(!TextUtils.isEmpty(articleId)){
                    UIHelper.toNewsDetailActivity(this,articleId);
                }
                break;
            case R.id.topic_first:
                SPUtils.getInstance().put("isTopicIconClick",true);
                topic_first.setVisibility(View.GONE);
                break;
            case R.id.ll_topic:
                UIHelper.toTopicDetailActivity(this,topicId);
                break;
            case R.id.topic_delete:

                ll_topic.setVisibility(View.GONE);
                topicId = "";
                topicName = "";
                break;
            case R.id.topic:
                UIHelper.toTopicSelectivity(this);
                break;

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

                //判断网络是否连接 检测链接的网络能否上网[子线程中]
                if(!NetworkUtils.isConnected() ){
                    ToastUtils.showShort("无网络连接");
                    return;
                }

//                if(! NetworkUtils.isAvailable()){
//                    ToastUtils.showShort("当前网络不可用");
//                    return;
//                }


                if(mString.length() > num){
                    ToastUtils.showShort("内容最多输入500字");
                    return;
                }


                sendToService();



                break;
            case R.id.cancel:
                if(StringUtil.isFastClick()){
                    return;
                }
                //如果有正文 有外链 有图片 文章对象
                if(!TextUtils.isEmpty(mString) || !TextUtils.isEmpty(linkTitle)
                    || !mediaFiles.isEmpty() || !pathList.isEmpty()
                    || null != mNewsDetailBean){
                    showSaveConetentExit();
                    return;
                }

                saveTempMsg(null);
                finish();
                break;
            default:
        }
    }

    private void sendToService() {
        //设置发送数据
        sendData();

        //① 在这里设置进度条的最大进度
        List<String>  tempList = new ArrayList<>();
        tempList.clear();

        if(null != mediaFiles && !mediaFiles.isEmpty()){
            for (MediaFile fileBean  : mediaFiles) {
                tempList.add(fileBean.getPath());
            }
        }

        if(null != pathList && !pathList.isEmpty()){
            for (MediaFile fileBean  : pathList) {
                tempList.add(fileBean.getPath());
            }
        }

        if(!tempList.isEmpty()){
            BaseActivity.maxSendProgress = tempList.size() * 100;
        }else{
            BaseActivity.maxSendProgress = 100;
        }


//    toSendBlog(mTempMsgBean);


        toSendBlogByService(mTempMsgBean);

        sendPicToQiuNiu();
    }


    public void toSendBlogByService(TempMsgBean tempMsgBean){
        mTempMsgBean = tempMsgBean;

        HomeActivityV2.mSendBinder.setData(tempMsgBean);

        //启动
        HomeActivityV2.mService.sendRequest();
    }



    @Override
    public void toRetrunBack() {
        super.toRetrunBack();
        sendPicToQiuNiu();
    }

    boolean isAnimPause;
    ValueAnimator animator;

    private void setAnimation(ProgressBar view) {
        animator = ValueAnimator.ofInt(0, 100).setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            view.setProgress((Integer) valueAnimator.getAnimatedValue());
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                KLog.d("tag","取消了");
                isAnimPause = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画正常结束 动画对象存在 界面存储 文本存在
//                if(!isAnimPause && animation != null && null != this){
                    KLog.d("tag","动画结束");
//                    rl_sending.setVisibility(View.GONE);
//                    rl_send_ok.setVisibility(View.VISIBLE);
//                    Random rand = new Random();
//                    int temp = rand.nextInt(5000) + 5000;
//                    send_num.setText("发布成功！已推荐给 " + temp +"位同行营销圈同行");
//                    //发送事件去更新
//                    EventBus.getDefault().post(new SendOkCircleEvent());
//                    removeTempMsg();
//
//                    new Handler().postDelayed(() -> {
//                        hideState();
//                    },2000);
//                }
//
//                //重新恢复状态
//                isAnimPause = false;
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


    private void hideState() {
//        ll_circle_send.setVisibility(View.GONE);
//        rl_send_ok.setVisibility(View.GONE);
//        progressBar.setProgress(0);
//        progressBar.setMax(100);
    }




    //把整体数据转移到Fragment中
    private void sendPicToQiuNiu() {

//        sendData();

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


        //添加话题
        if (requestCode == REQUEST_SELECT_TOPIC_CODE && resultCode == RESULT_OK) {
            ll_topic.setVisibility(View.VISIBLE);

            topicName = data.getExtras().getString("topicName");
            topicId = data.getExtras().getString("topicId");

            select_topic_text.setText("#" + topicName + "  " +  topicId );
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


        //设置话题
        if(!TextUtils.isEmpty(topicId)){
            mTempMsgBean.setTopicId(topicId);
            mTempMsgBean.setTopicName(topicName);
        }else{
            mTempMsgBean.setTopicId("");
            mTempMsgBean.setTopicName("");
        }

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


        if(null != mNewsDetailBean){
            mTempMsgBean.setArticle_id(mNewsDetailBean.getAid());
            mTempMsgBean.setArticle_title(mNewsDetailBean.getTitle());
            mTempMsgBean.setArticle_img(mNewsDetailBean.getPic());
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

        mTempMsgBean.setTopicName(topicName);
        mTempMsgBean.setTopicId(topicId);


        mTempMsgBean.setArticle_id(articleId);
        mTempMsgBean.setArticle_title(articleTitle);
        mTempMsgBean.setArticle_img(articleImg);
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
                            message.obj = status;
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

            if(!TextUtils.isEmpty(mTempMsgBean.getTopicId())){
                topicId = mTempMsgBean.getTopicId();
                topicName = mTempMsgBean.getTopicName();
                ll_topic.setVisibility(View.VISIBLE);
                select_topic_text.setText("#" + mTempMsgBean.getTopicName());
            }


            if(!TextUtils.isEmpty(mTempMsgBean.getArticle_id())){
                articleId = mTempMsgBean.getArticle_id();
                articleTitle = mTempMsgBean.getArticle_title();
                articleImg = mTempMsgBean.getArticle_img();

                acticle_part.setVisibility(View.VISIBLE);
                acticle_title.setText(mTempMsgBean.getArticle_title());
                ImageUtil.loadByDefaultHead(this,mTempMsgBean.getArticle_img(),acticle_img);

                make.setImageResource(R.mipmap.icon_pic_make_false);
                link.setImageResource(R.mipmap.icon_link_false);
                make.setEnabled(false);
                link.setEnabled(false);
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

    @Override
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
