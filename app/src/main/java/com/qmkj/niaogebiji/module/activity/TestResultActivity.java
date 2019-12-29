package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.common.utils.TimeAppUtils;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-25
 * 描述:测试结果
 */
public class TestResultActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.iv_right)
    ImageView iv_right;

    @BindView(R.id.icon_tag)
    ImageView icon_tag;



    @BindView(R.id.test_grade)
    TextView test_grade;

    @BindView(R.id.content)
    TextView content;


    private SchoolBean.SchoolTest mSchoolTest;

    private String hege = "不合格";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_result;
    }

    @Override
    protected void initView() {
        mExecutorService = Executors.newFixedThreadPool(2);
        mSchoolTest = (SchoolBean.SchoolTest) getIntent().getExtras().getSerializable("bean");
        tv_title.setText(mSchoolTest.getTitle());

        content.setText("恭喜你获得鸟哥笔记认证『" + mSchoolTest.getTitle() + "』称号。特授予你" + mSchoolTest.getTitle() +
                   "认证徽章，已帮你自动佩戴上" );

        ImageUtil.load(this,mSchoolTest.getIcon(),icon_tag);

        if(!TextUtils.isEmpty(mSchoolTest.getTime()) && !TextUtils.isEmpty(mSchoolTest.getQuestion_num())){
            long result = Long.parseLong(mSchoolTest.getTime()) * Long.parseLong(mSchoolTest.getQuestion_num());
            mins  = TimeAppUtils.convertSecToTimeString(result);
            KLog.d("tag",mins);
        }

        if(mSchoolTest.getRecord() !=  null){
            //表示参加过测试
            if(1 == mSchoolTest.getRecord().getIs_tested()){
                getHeGe(mSchoolTest.getRecord().getScore(),mSchoolTest.getPass_score());
            }else{
                test_grade.setText(mSchoolTest.getMyScore());
                getHeGe(mSchoolTest.getMyScore(),mSchoolTest.getPass_score());
            }
            test_grade.setText(mSchoolTest.getRecord().getScore());
        }else{
            test_grade.setText(mSchoolTest.getMyScore());
            getHeGe(mSchoolTest.getMyScore(),mSchoolTest.getPass_score());
        }



        tv_title.setTextColor(getResources().getColor(R.color.white));
        iv_back.setImageResource(R.mipmap.icon_test_detail_back);
        iv_right.setVisibility(View.VISIBLE);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        test_grade.setTypeface(typeface);

        if(mSchoolTest.getRecord()!= null && mSchoolTest.getRecord().getScore()!= null){
            test_grade.setText(mSchoolTest.getRecord().getScore());
        }else{
            test_grade.setText(mSchoolTest.getMyScore());
        }

    }

    private void getHeGe(String score,String passScore) {
        int score_int = Integer.parseInt(score);
        int passScore_int = Integer.parseInt(passScore);
        if(score_int >= passScore_int){
            hege = "我是合格的";
            if(score_int == 100){
                hege = "我是优秀的";
            }
        }
    }


    @OnClick({R.id.toTake,R.id.toShare,
            R.id.iv_back,R.id.iv_right
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
            case R.id.toShare:
                MobclickAgentUtils.onEvent(UmengEvent.academy_testdetail_score_share_2_0_0);

                showShareDialog();
                break;
            case R.id.toTake:
                break;
            default:
        }
    }


    /** --------------------------------- 分享  ---------------------------------*/
    private String mins;
    ShareBean bean = new ShareBean();
    Bitmap bitmap =  null;
    private ExecutorService mExecutorService;

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
            bean.setTitle(hege + mSchoolTest.getTitle());
            bean.setContent(test_grade.getText().toString() + "分" + "通过鸟哥笔记认证"  + mSchoolTest.getTitle() + "测试，你也来试试");
            if(msg.what == 0x111){
                bean.setShareType("circle_link");
            }else{
                bean.setShareType("weixin_link");
            }
            StringUtil.shareWxByWeb(TestResultActivity.this,bean);

        }
    };




}
