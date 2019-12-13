package com.qmkj.niaogebiji.module.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.TestNewBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:测一测详情
 */
public class TestDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.iv_right)
    ImageView iv_right;

    @BindView(R.id.part3333_ll)
    LinearLayout part3333_ll;


    @BindView(R.id.test_grade)
    ImageView test_grade;


    @BindView(R.id.test_name)
    TextView test_name;

    @BindView(R.id.test_des)
    TextView test_des;

    @BindView(R.id.test_count)
    TextView test_count;




    private String [] contents = new String[]{"本测试一共有10道题，每道题答题时间为1分钟,测试时间为10分钟。超过答题时间算为答错并自动切换到下一题",
        "每道题分值10分，总分100分。答对得10分，答错不得分。本测试需要达到60分以上才能获得认证徽章",
            "完成全部题目后将看到分数。达到80分以上可以获得鸟哥笔记认证的“初级ASO优化师”徽章。该认证将在圈子与渠道点评工具中展示",
              "未达到分数可以预约在3天之后重考。重考没有次数限制。"};


    private SchoolBean.SchoolTest mSchoolTest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_detail;
    }

    @Override
    protected void initView() {
        mSchoolTest = (SchoolBean.SchoolTest) getIntent().getExtras().getSerializable("bean");

        tv_title.setText("测试详情");
        tv_title.setTextColor(getResources().getColor(R.color.white));
        iv_back.setImageResource(R.mipmap.icon_test_detail_back);
        iv_right.setVisibility(View.VISIBLE);


        getData();
    }

    private void getData() {

        ImageUtil.load(this,mSchoolTest.getIcon(),test_grade);

        test_name.setText(mSchoolTest.getTitle());

        test_des.setText(mSchoolTest.getDesc());

        test_count.setText(mSchoolTest.getNum() +"人已测");

//        TextView textView;
//        LinearLayout.LayoutParams lp;
//        for (int i = 0; i < contents.length; i++) {
//            textView = new TextView(this);
//            textView.setTag(i);
//            textView.setText((i + 1) + " " + contents[i]);
//            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(0,0,0, SizeUtils.dp2px(20));
//            textView.setLayoutParams(lp);
//            part3333_ll.addView(textView);
//        }

    }


    @OnClick({R.id.iv_back,R.id.toTest,R.id.iv_right})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_right:
                showShareDialog();
                break;
            case R.id.toTest:
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

    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setSharelinkView();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position){
                case 0:
                    KLog.d("tag","朋友圈 是张图片");
                    WxShareBean bean = new WxShareBean();
                    shareWxCircleByWeb(bean);
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");
                    WxShareBean bean2 = new WxShareBean();
                    shareWxByWeb(bean2);
                    break;
                case 2:
                        KLog.d("tag","复制链接");
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", "http://www.baidu.com");
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                        ToastUtils.showShort("链接复制成功！");


                    break;
                default:
            }
        });
        alertDialog.show();
    }


}
