package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.PicPathHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.xzh.imagepicker.ImagePicker;
import com.xzh.imagepicker.bean.MediaFile;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:转发帖子
 *  1. 图片 + 文本 显示
 *
 *  2. 点击发布时 -- 添加动态效果
 *
 *
 *
 *  1.有图片的话 显示图片的第一张和 文本
 *  2.没有图片，显示作者头像 和 文本
 */
public class TranspondActivity extends BaseActivity {

    @BindView(R.id.checkbox)
    CheckBox mCheckbox;


    @BindView(R.id.et_input)
    EditText mEditText;

    @BindView(R.id.send)
    TextView send;

    @BindView(R.id.listentext)
    TextView listentext;

    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.acticle_title)
    TextView acticle_title;

    @BindView(R.id.acticle_part)
    LinearLayout acticle_part;


    //文本内容
    private String mString;
    private int textLength;
    //编辑字数限制
    private int num = 140;


    private CircleBean mCircleBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transpond;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

        KeyboardUtils.showSoftInput(mEditText);

        RxTextView
                .textChanges(mEditText)
                .subscribe(charSequence -> {
                    //英文单词占1个字符  表情占2个字符 中文占1个字符
                    KLog.d("tag", "accept: " + charSequence.toString() );
                    //　trim()是去掉首尾空格
                    mString = charSequence.toString().trim();
                    if(!TextUtils.isEmpty(mString) && mString.length() != 0){
                        send.setEnabled(true);
                        send.setTextColor(getResources().getColor(R.color.text_first_color));
                        //设置光标在最后
                        mEditText.setSelection(charSequence.toString().length());

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

        RxCompoundButton.checkedChanges(mCheckbox).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    KLog.d("tag","选中了");
                } else {
                    KLog.d("tag","没选中");
                }
            }
        });

    }


    @Override
    public void initData() {
        mCircleBean = (CircleBean) getIntent().getExtras().getSerializable("circle");

        //只判断img字段 没有的话是转发，传头像即可；如果有，就用图片的第一张
        if(mCircleBean.getImages() != null && !mCircleBean.getImages().isEmpty()){
            ImageUtil.load(this,mCircleBean.getImages().get(0),logo);
        }else if(mCircleBean.getUser_info() != null){
            ImageUtil.load(this,mCircleBean.getUser_info().getAvatar(),logo);
        }else{
            ImageUtil.load(this, StringUtil.getUserInfoBean().getAvatar(),logo);
        }

        acticle_title.setText(mCircleBean.getBlog());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //添加话题
        if (requestCode == REQUEST_SELECT_TOPIC_CODE && resultCode == RESULT_OK) {
            ll_topic.setVisibility(View.VISIBLE);

            topicName = data.getExtras().getString("topicName");
            topicId = data.getExtras().getString("topicId");
            select_topic_text.setText("#" + topicName + "  " +  topicId );
        }
    }





    //话题
    private String topicId = "";
    private String topicName = "";
    public static final int REQUEST_SELECT_TOPIC_CODE = 4;

    @BindView(R.id.ll_topic)
    LinearLayout ll_topic;

    @BindView(R.id.select_topic_text)
    TextView select_topic_text;

    @OnClick({R.id.cancel,R.id.send,
            R.id.acticle_part,
            R.id.topic_delete,
            R.id.topic,R.id.ll_topic
    })
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(mEditText);
        switch (view.getId()){
            case R.id.ll_topic:
                UIHelper.toTopicDetailActivity(this,topicId);
                break;
            case R.id.topic_delete:

                ll_topic.setVisibility(View.GONE);
                topicId = "";
                topicName = "";
                break;
            case R.id.topic:
                UIHelper.toTopicSelectivity(this,topicId);
                break;

            case R.id.acticle_part:
//                UIHelper.toCommentDetailActivity(this,mCircleBean.getId(),mCircleBean.getSlefPosition());
                UIHelper.toCommentDetailActivity(this,mCircleBean.getId());
                break;
            case R.id.send:
                if(mString.length() > num){
                    ToastUtils.showShort("内容最多输入140字");
                    return;
                }

                //同时评论
                if(mCheckbox.isChecked()){
                    blog_is_comment = 1;
                }
                //转发
                blog_type = 1;


//                createBlog();


                //判断网络是否连接 检测链接的网络能否上网[子线程中]
                if(!NetworkUtils.isConnected() ){
                    ToastUtils.showShort("无网络连接");
                    return;
                }

//                if(! NetworkUtils.isAvailable()){
//                    ToastUtils.showShort("当前网络不可用");
//                    return;
//                }




                sendToService();


                break;
            case R.id.cancel:
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


        if(!tempList.isEmpty()){
            BaseActivity.maxSendProgress = tempList.size() * 100;
        }else{
            BaseActivity.maxSendProgress = 100;
        }


        toSendBlogByService(mTempMsgBean);

        sendPicToQiuNiu();
    }


    //把整体数据转移到Fragment中
    private void sendPicToQiuNiu() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("key",mTempMsgBean);
        intent.putExtras(bundle);
        setResult(200,intent);
        finish();
    }


    public void toSendBlogByService(TempMsgBean tempMsgBean){
        mTempMsgBean = tempMsgBean;

        HomeActivityV2.mSendBinder.setData(tempMsgBean);

        //启动
        HomeActivityV2.mService.sendRequest();
    }



    //发送的数据
    private TempMsgBean mTempMsgBean;
    private void sendData() {
        if(null == mTempMsgBean){
            mTempMsgBean = new TempMsgBean();
        }

        mTempMsgBean.setContent(mEditText.getText().toString().trim());
        //12.26 link 在点击删除按钮时清空了
        mTempMsgBean.setLinkTitle("");
        mTempMsgBean.setLinkurl("");
        mTempMsgBean.setImgPath(null);
        mTempMsgBean.setImgPath2(null);
        mTempMsgBean.setTopicName(topicName);
        mTempMsgBean.setTopicId(topicId);

        //转发 + 评论
        mTempMsgBean.setBlog_is_comment(blog_is_comment);
        mTempMsgBean.setBlog_type(blog_type);
        mTempMsgBean.setPid(mCircleBean.getId());
    }




    String blog = "";
    String blog_images = "";
    String blog_link = "";
    String blog_link_title = "";
    //0原创 1转发 动态传递1
    int blog_type = 1;
    //被转发动态ID，原创为0
    String blog_pid = "";
    //转发时是否同时评论动态，1是 0否
    int blog_is_comment = 0;
    //文章Id
    String article_id = "";
    //文字标题
    String article_title = "";
    //文字图片
    String article_image= "";
    //话题id
    String topic_id = "";

    private void createBlog(){
        blog = mString;
        Map<String,String> map = new HashMap<>();
        map.put("blog",blog + "");
        map.put("images", "");
        map.put("link","");
        map.put("link_title","");
        map.put("type",blog_type + "");//TODO 必转
        map.put("pid",mCircleBean.getId() + "");//TODO 必转
        map.put("is_comment",blog_is_comment + "");//TODO 必转
        map.put("article_id", article_id + "");
        map.put("article_title", article_title + "");
        map.put("article_image",article_image + "");
        map.put("topic_id",topic_id + "");
        String result =  RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("转发成功");
                        EventBus.getDefault().post(new SendOkCircleEvent());
                        finish();
                    }
                });
    }



    /** --------------------------------- 点击空白区域 自动隐藏软键盘  ---------------------------------*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super .onTouchEvent(event);
    }
}
