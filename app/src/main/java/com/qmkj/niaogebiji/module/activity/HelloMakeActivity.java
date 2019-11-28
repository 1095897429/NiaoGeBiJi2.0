package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.CirclePicItemAdapter;
import com.qmkj.niaogebiji.module.bean.MulMediaFile;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.widget.GlideLoader;
import com.socks.library.KLog;
import com.xzh.imagepicker.ImagePicker;
import com.xzh.imagepicker.bean.MediaFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-25
 * 描述:打招呼界面
 */
public class HelloMakeActivity extends BaseActivity {

    @BindView(R.id.et_input)
    EditText mEditText;

    @BindView(R.id.listentext)
    TextView listentext;

    @BindView(R.id.cancel)
    TextView cancel;

    @BindView(R.id.send)
    TextView send;

    private String mString;
    private int textLength;
    //编辑字数限制
    private int num = 140;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_hello_make;
    }

    @Override
    public void initData() {
        KeyboardUtils.showSoftInput(mEditText);
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

    }

    @OnClick({R.id.cancel,R.id.send})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(mEditText);
        finishWithAnim(R.anim.activity_alpha_enter,R.anim.activity_exit_bottom);
        switch (view.getId()){
            case R.id.send:
                KLog.d("tag","发布");
                break;
            case R.id.cancel:
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        clicks(cancel);
    }


}
