package com.qmkj.niaogebiji.module.activity;

import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.widget.SecurityCodeView;
import com.socks.library.KLog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:验证码
 */
public class VertifyCodeActivity extends BaseActivity {

    @BindView(R.id.tv_text)
    TextView tv_text;

    @BindView(R.id.edit_security_code)
    SecurityCodeView editText;

    String inputContent;

    Disposable disposable;

    //倒计时60秒
    public static int COUNT = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vertifycode;
    }

    @Override
    protected void initView() {

        initRxTime();

        editText.setInputCompleteListener(new SecurityCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                inputContent = editText.getEditContent();
                KLog.d("tag","请输入验证码 : " + inputContent);
            }

            @Override
            public void deleteContent(boolean isDelete) {
                KLog.d("tag","请输入验证码");
            }
        });

        tv_text.setOnClickListener(view -> {
            initRxTime();
        });
    }


    private void initRxTime() {

        int time = SPUtils.getInstance().getInt("lastTime");
        int count;
        if(time == -1){
            //第一次进入
            count = COUNT;
            tv_text.setEnabled(true);
        }else if(time == 0){
            //倒计时完成了
            count = COUNT;
            tv_text.setEnabled(true);
        }else{
            //倒计时没结束进入
            count = time;
            tv_text.setEnabled(false);
        }


        //参数依次为：从0开始，发送次数是9次 ，0秒延时,每隔1秒发射,主线程中
        disposable = Observable.intervalRange(0,count + 1,0,1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    //接收到消息，这里需要判空，因为3秒倒计时中间如果页面结束了，会造成找不到 tvAdCountDown
                    if (tv_text != null) {
                        tv_text.setText("重新发送 " + (count - aLong) +  " 秒");
                        SPUtils.getInstance().put("lastTime",(int) (count - aLong));
                        tv_text.setEnabled(false);
                    }
                }).doOnComplete(() -> {
                    tv_text.setEnabled(true);
                    KLog.d("tag","完成之后跳转到主页面");
                    SPUtils.getInstance().put("lastTime",COUNT);
                    tv_text.setText("获取验证码");
                })
                .subscribe();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != disposable){
            disposable.dispose();
        }
    }
}
