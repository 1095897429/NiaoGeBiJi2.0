package com.qmkj.niaogebiji.common.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.ExchageDetailBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:商品兑换详情页(第一个界面)
 */
public class ExchangeDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.part1111)
    LinearLayout part1111;

    @BindView(R.id.part2222)
    LinearLayout part2222;

    @BindView(R.id.image11)
    ImageView image11;

    @BindView(R.id.image22)
    ImageView image22;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.txt_title_22)
    TextView txt_title_22;

    @BindView(R.id.txt_msg)
    TextView txt_msg;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.phone)
    TextView phone;


    @BindView(R.id.location)
    TextView location;


    private ExchageDetailBean mExchageDetailBean;
    private ExchageDetailBean.ExchageDetail mDetail;
    private String from;
    //收支明细id
    private String catid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_exchange_detail;
    }

    @Override
    protected void initView() {
        from = getIntent().getExtras().getString("from");
        catid = getIntent().getExtras().getString("catid");
        //收支界面跳入
        if("income".equals(from)){
            tv_title.setText("兑换详情");
            exchDetail();
        }else{
            mExchageDetailBean = (ExchageDetailBean) getIntent().getExtras().getSerializable("bean");
            tv_title.setText("兑换成功");
            setData(mExchageDetailBean);
        }

    }

    private void setData(ExchageDetailBean temp) {
        mDetail = temp.getList();
        if( mDetail != null){
            if("2".equals(mDetail.getCat()) || "4".equals(mDetail.getCat())){
                part2222.setVisibility(View.VISIBLE);
                part1111.setVisibility(View.GONE);
                txt_title_22.setText(mDetail.getTitle());
                ImageUtil.load(this,mDetail.getImg_list(),image22);
                name.setText(mDetail.getName());
                phone.setText(mDetail.getMobile());
                location.setText(mDetail.getAddress());
            }else{
                part2222.setVisibility(View.GONE);
                part1111.setVisibility(View.VISIBLE);
                txt_title.setText(mDetail.getTitle());
                ImageUtil.load(this,mDetail.getImg_list(),image11);
                txt_msg.setText(mDetail.getLink());
            }
        }
    }


    @OnClick({R.id.copy_link,R.id.iv_back,R.id.lookdetail,R.id.lookdetail2})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.copy_link:
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", txt_msg.getText().toString().trim());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                ToastUtils.showShort("复制成功");
                break;

            case R.id.iv_back:
                finish();

                break;
            case R.id.lookdetail:
            case R.id.lookdetail2:
                //TODO 10.14 不通过接口请求
//                UIHelper.toExchangeDetailActivity2(ExchangeDetailActivity.this,mExchageDetailBean,"no_request",null);

                UIHelper.toExchangeAllListActivity(ExchangeDetailActivity.this);
                break;

            default:
        }
    }


    /** --------------------------------- 积分兑换明细  ---------------------------------*/

    private void exchDetail() {
        Map<String,String> map = new HashMap<>();
        map.put("id",catid + "");
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().exchDetail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ExchageDetailBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<ExchageDetailBean> response) {
                        KLog.e("tag",response.getReturn_code());
                        setData(response.getReturn_data());
                    }

                });
    }
}
