package com.qmkj.niaogebiji.module.activity;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.socks.library.KLog;

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

    @BindView(R.id.test_grade)
    TextView test_grade;

    private SchoolBean.SchoolTest mSchoolTest;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_result;
    }

    @Override
    protected void initView() {

        mSchoolTest = (SchoolBean.SchoolTest) getIntent().getExtras().getSerializable("bean");
        tv_title.setText(mSchoolTest.getTitle());


        tv_title.setTextColor(getResources().getColor(R.color.white));
        iv_back.setImageResource(R.mipmap.icon_test_detail_back);
        iv_right.setVisibility(View.VISIBLE);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        test_grade.setTypeface(typeface);

        test_grade.setText(mSchoolTest.getRecord().getScore());
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
                showShareDialog();
                break;
            case R.id.toTake:
                break;
            default:
        }
    }


    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setShareDynamicView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    KLog.d("tag", "朋友圈 是张图片");
                    WxShareBean bean = new WxShareBean();
                    shareWxCircleByWeb(bean);
                    break;
                case 1:
                    KLog.d("tag", "朋友 是链接");
                    WxShareBean bean2 = new WxShareBean();
                    shareWxByWeb(bean2);
                    break;
                case 4:
                    KLog.d("tag", "转发到动态");
                    UIHelper.toTranspondActivity(this);
                    //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                    overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                    break;
                default:
            }
        });
        alertDialog.show();
    }




}
