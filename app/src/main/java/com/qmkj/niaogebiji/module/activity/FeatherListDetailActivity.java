package com.qmkj.niaogebiji.module.activity;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.LocationExchangeDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.ExchageDetailBean;
import com.qmkj.niaogebiji.module.bean.FeatherProductBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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
 * 创建时间 2019-12-19
 * 描述:商品详情页
 */
public class FeatherListDetailActivity  extends BaseActivity {

    @BindView(R.id.title)
    TextView title ;

    @BindView(R.id.content)
    TextView  content ;

    @BindView(R.id.num_feather)
    TextView  num_feather ;


    @BindView(R.id.image)
    ImageView image ;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_feather_item_new;
    }

    @Override
    protected void initView() {
        product_id = getIntent().getStringExtra("product_id");

        getItmeDetailt();
    }


    /** --------------------------------- 商品明细  ---------------------------------*/
    private FeatherProductBean mFeatherProductBean;
    private List<FeatherProductBean.Productean> mProducteanList;
    private FeatherProductBean.Productean mProductean;


    //商品id
    private String product_id ;

    private void getItmeDetailt() {

        Map<String,String> map = new HashMap<>();
        map.put("id",product_id);
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getItmeDetailt(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FeatherProductBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<FeatherProductBean> response) {
                        KLog.e("tag",response.getReturn_code());
                        mFeatherProductBean = response.getReturn_data();
                        if(null != mFeatherProductBean){
                            mProducteanList = mFeatherProductBean.getList();
                            if(null != mProducteanList){
                                setInfoData();
                            }
                        }
                    }

                });
    }

    private void setInfoData() {
        mProductean = mProducteanList.get(0);
        if(null != mProductean){
            ImageUtil.load(this,mProductean.getImg_list(),image);

            title.setText(mProductean.getTitle());
            content.setText(mProductean.getContent());

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
            num_feather.setTypeface(typeface);

            num_feather.setText(mProductean.getPoint());
        }

    }




    @OnClick({R.id.iv_back,R.id.buy})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.buy:
                if(null != mProductean){
                    mProductId = mProductean.getId();
                    showExchangeDialog();
                }

                break;
            default:
        }
    }

    /** --------------------------------- 商品兑换 逻辑  ---------------------------------*/

    private void showExchangeDialog() {

        if(null != mProductean){
            final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
            iosAlertDialog.setPositiveButton("兑换", v -> {
                compareLogic();
            }).setNegativeButton("再想想", v -> {

            }).setMsg("确认消耗 " + mProductean.getPoint() +"羽毛兑换商品？").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }

    }


    private void compareLogic() {

        if(null != mProductean){
            String needPoint = mProductean.getPoint();

            if(StringUtil.getUserInfoBean() ==  null){
                return;
            }

            //金币数判断
            String myPoint = StringUtil.getUserInfoBean().getPoint();
            KLog.d("tag","我的金币数为 " + myPoint);
            if(!TextUtils.isEmpty(needPoint) && !TextUtils.isEmpty(myPoint)){
                long needPointLong = Long.parseLong(needPoint);
                long myPointLong = Long.parseLong(myPoint);
                if(needPointLong > myPointLong){

                    showDownErrorDialog();
                    return;
                }
            }

            //判断点击商品的类型
            if(null != mProductean){
                if("2".equals(mProductean.getCat())
                        || "4".equals(mProductean.getCat())){
                    //实体礼品
                    showLocationExchangeDialog();
                }else{
                    //虚拟礼品
                    exchangePoint(null);
                }
            }

        }

    }



    private void showLocationExchangeDialog() {

        final LocationExchangeDialog iosAlertDialog = new LocationExchangeDialog(this).builder();
        iosAlertDialog.setOnDialogItemClickListener((position, bean) -> {
            if (position == 1) {
                exchangePoint(bean);
            }
        });
        iosAlertDialog.show();
    }


    private void showDownErrorDialog() {

        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("赚羽毛", v -> {
            UIHelper.toFeatherNewActivity(this);
        }).setNegativeButton("再想想", v -> {

        }).setMsg("当前羽毛不足哦~").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }




    /** --------------------------------- 商品兑换  ---------------------------------*/
    //商品的id
    private String mProductId;
    //类型：1-线上资料，2-实体礼品，3-优惠券，4-流量 -- 2的时候弹出填写地址的对话框
    // 当兑换商品cat=2 以下参数必填
    private String mName = "周亮";
    private String mMobile = "18616541823";
    private String mAddress = "上海市静安区谈家桥路4号201室";


    private ExchageDetailBean mExchageDetailBean;

    private void exchangePoint(LocationExchangeDialog.CallBean bean) {

        Map<String,String> map = new HashMap<>();
        map.put("id",mProductId);
        if(null != bean){
            map.put("name",bean.name);
            map.put("mobile",bean.phone);
            map.put("address",bean.location);
        }else{
            map.put("name","");
            map.put("mobile","");
            map.put("address","");
        }

        String result = RetrofitHelper.commonParam(map);


        RetrofitHelper.getApiService().exchangePoint(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ExchageDetailBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<ExchageDetailBean> response) {
                        KLog.e("tag",response.getReturn_code());
                        if(null != mProductean){
                            FeatherProductBean.Productean tempPro = mProductean;
                            tempPro.setHas_exch(1);
                            //TODO 是否设置文字
                            mExchageDetailBean = response.getReturn_data();
                            if(null != mExchageDetailBean){
                                showExchangeSuccessDialog();
                            }
                        }
                    }

                });
    }


    private void showExchangeSuccessDialog() {
        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
        ToastUtils.showShort("兑换成功");
        UIHelper.toExchangeDetailActivity(this,mExchageDetailBean,"feather","");
    }




}
