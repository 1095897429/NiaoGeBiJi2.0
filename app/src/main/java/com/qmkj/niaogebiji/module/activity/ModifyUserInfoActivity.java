package com.qmkj.niaogebiji.module.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-23
 * 描述:修改用户信息界面
 */
public class ModifyUserInfoActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.et_input)
    EditText et_input;


    @BindView(R.id.submit)
    TextView submit;


    String type;

    String content;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_userinfo;
    }

    @Override
    protected void initView() {

        content = getIntent().getExtras().getString("content");
        type = getIntent().getExtras().getString("type");
        if("nickname".equals(type)){
            //动态设置长度
            et_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});

            tv_title.setText("昵称");
            if(!TextUtils.isEmpty(content)){
                et_input.setText(content);
                setStatus(true);
            }else{
                et_input.setHint("请输入昵称");
            }

        }else if("profession".equals(type)){
            et_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            tv_title.setText("职业");

            if(!TextUtils.isEmpty(content)){
                et_input.setText(content);
                setStatus(true);
            }else{
                et_input.setHint("请输入职业");
            }
        }else if("profile".equals(type)){
            tv_title.setText("简介");

            if(!TextUtils.isEmpty(content)){
                et_input.setText(content);
                setStatus(true);
            }else{
                et_input.setHint("请输入简介");
            }
        }else if("company".equals(type)){
            et_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            tv_title.setText("公司");

            if(!TextUtils.isEmpty(content)){
                et_input.setText(content);
                setStatus(true);
            }else{
                et_input.setHint("请输入公司名称");
            }
        }



        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();

        if(userInfo != null){
            mNickname = userInfo.getNickname();
            mName = userInfo.getName();
            mGender = userInfo.getGender();
            mPosition = userInfo.getPosition();
            mAvatar_ext = "png";
            mPro_summary = userInfo.getPro_summary() ;
            mBirthday =  userInfo.getBirthday();
        }

        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 500){
                    Log.d("tag","输入的字数过多");
                }


                if("nickname".equals(type)){
                    //英文单词占1个字符  表情占2个字符 中文占1个字符
                    if(s.toString().length() > 8){
                        ToastUtils.showShort("昵称过长");
                        return;
                    }
                }

                if(s.toString().length() == 0){
                    submit.setEnabled(false);
                    submit.setSelected(false);
                    //文字透明度
                    submit.setTextColor(0xCC818386);
                }else{
                    submit.setEnabled(true);
                    submit.setSelected(true);
                    submit.setTextColor(Color.parseColor("#242629"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //TODO 2.17 为了编辑文本中有文字时，发布可用
    private void setStatus(boolean isEnable) {
        if(!isEnable){
            submit.setEnabled(false);
            submit.setSelected(false);
            //文字透明度
            submit.setTextColor(0xCC818386);
        }else{
            submit.setEnabled(true);
            submit.setSelected(true);
            submit.setTextColor(Color.parseColor("#242629"));
        }
    }


    @OnClick({R.id.iv_back,R.id.submit})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.submit:

                if("nickname".equals(type)){
                    mNickname = et_input.getText().toString().trim();
                }else if("profession".equals(type)){
                    mPosition = et_input.getText().toString().trim();
                }else if("profile".equals(type)){
                    mPro_summary = et_input.getText().toString().trim();
                }else if("company".equals(type)){
                    mComany = et_input.getText().toString().trim();
                }
                alterinfo();
                break;
            default:
        }
    }


    /** --------------------------------- 修改用户信息  ---------------------------------*/
    String mComany = "";
    String mNickname = "";
    String mName = "";
    String mGender = "";
    String mPosition = "";
    String mAvatar_base = "";
    String mAvatar_ext = "png";
    String mPro_summary = "" ;
    String mBirthday = "";


    private void alterinfo() {
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(mNickname)){
            map.put("nickname",mNickname);

            //判断是否有表情
//           String lokggg = StringEscapeUtils.escapeJava(mNickname);
//           KLog.d("tag","加密的内容是 " + lokggg);
//           map.put("nickname",lokggg);
        }

        if(!TextUtils.isEmpty(mName)){
            map.put("name",mName);
        }

        if(!TextUtils.isEmpty(mGender)){
            map.put("gender",mGender);
        }

        if(!TextUtils.isEmpty(mPosition)){
            map.put("position",mPosition);
        }

        if(!TextUtils.isEmpty(mAvatar_base)){
            map.put("avatar_base",mAvatar_base);
        }

        if(!TextUtils.isEmpty(mAvatar_ext)){
            map.put("avatar_ext",mAvatar_ext);
        }

        if(!TextUtils.isEmpty(mPro_summary)){
            map.put("pro_summary",mPro_summary);
        }

        if(!TextUtils.isEmpty(mBirthday)){
            map.put("birthday",mBirthday);
        }



        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().alterinfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                    @Override
                    public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {
                        ToastUtils.setGravity(Gravity.CENTER,0,0);
                        ToastUtils.showShort("修改成功");
                        ToastUtils.setGravity(Gravity.BOTTOM,0,0);

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("nickname",mNickname);
                        bundle.putString("profession",mPosition);
                        bundle.putString("profile",mPro_summary);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK,intent);

                        finish();
                    }
                });
    }



}
