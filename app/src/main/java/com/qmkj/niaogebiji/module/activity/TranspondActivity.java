package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:转发帖子
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

    private String mString;
    private int textLength;
    //编辑字数限制
    private int num = 140;

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


    @OnClick({R.id.cancel,R.id.send,
            R.id.part2222})
    public void clicks(View view){
        KeyboardUtils.hideSoftInput(mEditText);
        switch (view.getId()){
            case R.id.part2222:
                KLog.d("tag","跳转到外链");
                UIHelper.toWebViewActivity(this,"http://www.baidu.com");
                break;
            case R.id.send:
                KLog.d("tag","发布");
                ToastUtils.showShort("发布成功");
                EventBus.getDefault().post(new SendOkCircleEvent());
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
            default:
        }
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
