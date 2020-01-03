package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.socks.library.KLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-20
 * 描述:圈子帖发布 添加链接界面
 */
public class CircleMakeAddLinkActivity extends BaseActivity {

    @BindView(R.id.et_input)
    EditText mEditText;

    @BindView(R.id.listentext)
    TextView listentext;

    @BindView(R.id.cancel)
    ImageView cancel;

    @BindView(R.id.addLink)
    TextView addLink;

    @BindView(R.id.link_content)
    TextView link_content;


    @BindView(R.id.part2222)
    LinearLayout part2222;


    private String mString;

    private String content;
    private String title;


    @Override
    protected int getLayoutId() {

        return R.layout.activity_circle_make_addlink;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {

            //测试数据 -- 1.以m开头的并不能跳转 ？？？
//        String url = "https://m.weibo.cn/detail/4452636415281894?display=0&retcode=6102";
//        mEditText.setText(url);


        //Android Q 上获取的方式用下面的
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                //判断系统剪贴板是否有数据 ，并检查是否前缀是http或者https
                content = getContentFromClipBoard();
                KLog.d("tag","剪贴板的内容 " + content);
                if(!TextUtils.isEmpty(content) && isUrlPrefix(content)){
                    part2222.setVisibility(View.VISIBLE);
                    link_content.setText(content);
                }
            }
        });



        KeyboardUtils.showSoftInput(mEditText);

        RxTextView
                .textChanges(mEditText)
                .subscribe(charSequence -> {
                    //英文单词占1个字符  表情占2个字符 中文占1个字符
                    KLog.d("tag", "accept: " + charSequence.toString() );
                    //　trim()是去掉首尾空格
                    mString = charSequence.toString().trim();
                    KLog.d("tag",mString);
                    if(!TextUtils.isEmpty(mString) && mString.length() != 0 && isValidUrl(mString)){
                        addLink.setEnabled(true);
                        addLink.setTextColor(getResources().getColor(R.color.text_first_color));
                        //设置光标在最后
                        mEditText.setSelection(charSequence.toString().length());
                    }else{
                        addLink.setEnabled(false);
                        addLink.setTextColor(Color.parseColor("#CC818386"));
                    }
                });
    }



    @OnClick({R.id.cancel,R.id.addLink,R.id.to_paste
            })
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(mEditText);
        switch (view.getId()){
            case R.id.to_paste:
                mEditText.setText(content);
                part2222.setVisibility(View.GONE);
                break;
            case R.id.addLink:
                KLog.d("tag","添加链接");

                //这里还需检查一下链接
//                if(!isValidUrl(mString)){
//                    ToastUtils.showShort("输入了非法链接");
//                    return;
//                }


                new Thread(() -> {
                    title = getWebTitle(mString);
                    Message message = Message.obtain();
                    mHandler.sendMessage(message);
                }).start();


                break;
            case R.id.cancel:
                finishWithAnim(R.anim.activity_alpha_enter,R.anim.activity_exit_right);
                break;
            default:
        }
    }

    /** --------------------------------- 粘贴  ---------------------------------*/

    private String getContentFromClipBoard() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        if(data != null && data.getItemCount() > 0){
            ClipData.Item item = data.getItemAt(0);
            return item.getText().toString();
        }
        return null;
    }

    private boolean isUrlPrefix(String url){
        return url.startsWith("http://") ||  url.startsWith("https://");
    }


    public static boolean isValidUrl(String urlString){
        URI uri ;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        if(uri.getHost() == null){
            return false;
        }
        if(uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")){
            return true;
        }
        return false;
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

    @SuppressLint("HandlerLeak")
    public  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(this != null){
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("add_link",mString);
                bundle.putString("link_title",title);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finishWithAnim(R.anim.activity_alpha_enter,R.anim.activity_exit_right);
            }
        }
    };
}
