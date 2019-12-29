package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.common.utils.TimeAppUtils;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.TestNewBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:测一测详情
 * 1.（time *  题目数 ）+ "分数比较 " + "标题"
 */
public class TestDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.iv_right)
    ImageView iv_right;


    @BindView(R.id.test_grade)
    ImageView test_grade;


    @BindView(R.id.test_name)
    TextView test_name;

    @BindView(R.id.test_des)
    TextView test_des;

    @BindView(R.id.test_count)
    TextView test_count;

    @BindView(R.id.summary)
    TextView summary;

    private String mins;



    private SchoolBean.SchoolTest mSchoolTest;

    private ExecutorService mExecutorService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_detail;
    }

    @Override
    protected void initView() {
        mExecutorService = Executors.newFixedThreadPool(2);
        mSchoolTest = (SchoolBean.SchoolTest) getIntent().getExtras().getSerializable("bean");

        tv_title.setText("测试详情");
        tv_title.setTextColor(getResources().getColor(R.color.white));
        iv_back.setImageResource(R.mipmap.icon_test_detail_back);
        iv_right.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(mSchoolTest.getTime()) && !TextUtils.isEmpty(mSchoolTest.getQuestion_num())){
            long result = Long.parseLong(mSchoolTest.getTime()) * Long.parseLong(mSchoolTest.getQuestion_num());
            mins  = TimeAppUtils.convertSecToTimeString(result);
            KLog.d("tag",mins);
        }



        getData();
    }

    private void getData() {

        ImageUtil.load(this,mSchoolTest.getIcon(),test_grade);

        test_name.setText(mSchoolTest.getTitle());

        test_des.setText(mSchoolTest.getDesc());

        test_count.setText(StringUtil.formatPeopleNum(mSchoolTest.getNum()) + "人已测");

        if(!TextUtils.isEmpty(mSchoolTest.getComment())){
            summary.setText(Html.fromHtml(mSchoolTest.getComment()));
        }
    }


    @OnClick({R.id.iv_back,R.id.toTest,R.id.iv_right})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_right:
                MobclickAgentUtils.onEvent(UmengEvent.academy_testdetail_starttest_share_2_0_0);
                showShareDialog();
                break;
            case R.id.toTest:
                MobclickAgentUtils.onEvent(UmengEvent.academy_testdetail_starttest_2_0_0);
                getTestQuestions();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    List<TestNewBean> list = new ArrayList<>();
    private void getTestQuestions() {
        Map<String,String> map = new HashMap<>();
        map.put("test_cate_id",mSchoolTest.getId() + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getTestQuestions(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<TestNewBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<TestNewBean>> httpResponse) {
                        KLog.d("tag",httpResponse.getReturn_data());
                        list = httpResponse.getReturn_data();
                        setData();
                    }
                });
    }

    private void setData() {
        if(!list.isEmpty()){
            UIHelper.toTestLauchActivity(this, (ArrayList<TestNewBean>) list,mSchoolTest);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 ){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
    }

    /** --------------------------------- 分享  ---------------------------------*/

    ShareBean bean = new ShareBean();
    Bitmap bitmap =  null;

    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setSharelinkView();
        alertDialog.setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position){
                case 0:
                    mExecutorService.submit(() -> {
                        bitmap = StringUtil.getBitmap(mSchoolTest.getIcon());
                        mHandler.sendEmptyMessage(0x111);
                    });

                    break;
                case 1:
                    mExecutorService.submit(() -> {
                        bitmap = StringUtil.getBitmap(mSchoolTest.getIcon());
                        mHandler.sendEmptyMessage(0x112);
                    });
                    break;
                case 2:
                        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                        ToastUtils.showShort("链接复制成功！");
                        StringUtil.copyLink(mSchoolTest.getTitle() + "\n" +  mSchoolTest.getShare_url());

                    break;
                default:
            }
        });
        alertDialog.show();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            bean.setBitmap(bitmap);
            bean.setImg(mSchoolTest.getIcon());
            bean.setLink(mSchoolTest.getShare_url());
            bean.setTitle("测一测：" + mSchoolTest.getTitle());
            bean.setContent(mins + "看看你能否成为合格的" + mSchoolTest.getTitle());
            if(msg.what == 0x111){
                bean.setShareType("circle_link");
            }else{
                bean.setShareType("weixin_link");
            }
            StringUtil.shareWxByWeb(TestDetailActivity.this,bean);

        }
    };


}
