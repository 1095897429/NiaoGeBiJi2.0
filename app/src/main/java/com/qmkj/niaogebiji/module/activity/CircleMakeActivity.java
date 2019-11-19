package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.socks.library.KLog;

import butterknife.BindView;
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



    private String mString;
    private int textLength;

    private int num = 8;


    @Override
    protected int getLayoutId() {
        getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return R.layout.activity_circle_make;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        RxTextView
                .textChanges(mEditText)
                .subscribe(charSequence -> {
                    //英文单词占1个字符  表情占2个字符 中文占1个字符
                    KLog.d("tag", "accept: " + charSequence.toString() );
                    KLog.d("tag", "长度: " + charSequence.toString().length() );

                    mString = charSequence.toString();
                    textLength = mString.length();
                    //设置光标在最后
                    mEditText.setSelection(mString.length());

                    if(mString.length() > num){
                        KLog.d("tag","超出了");
                        return;
                    }

                    listentext.setText(textLength + " / " + num);

                });
    }
}
