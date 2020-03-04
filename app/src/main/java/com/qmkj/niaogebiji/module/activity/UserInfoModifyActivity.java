package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.push.handler.EnableReceiveNotifyMsgHandler;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.HeadAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.Base64;
import com.qmkj.niaogebiji.common.utils.FileHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.NotificationUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020.2.15
 * 描述:用户信息修改界面
 * 1.主要修改用户的职业选择，并没有实名认证
 */
public class UserInfoModifyActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.head_icon)
    CircleImageView head_icon;

    @BindView(R.id.nickname)
    TextView nickname;

    @BindView(R.id.profile_info_text)
    TextView profile_info_text;

    @BindView(R.id.open_push)
    ImageView open_push;

    @BindView(R.id.exit_txt)
    TextView exit_txt;

    @BindView(R.id.push_tx)
    TextView push_tx;

    @BindView(R.id.clean_text)
    TextView clean_text;

    @BindView(R.id.profession_status)
    LinearLayout profession_status;

    @BindView(R.id.profession_name)
    TextView profession_name;

    @BindView(R.id.profession_name_now_text)
    TextView profession_name_now_text;

    @BindView(R.id.company_name_now_text)
    TextView company_name_now_text;

    @BindView(R.id.ll_company)
    LinearLayout ll_company;

    @BindView(R.id.profession_name_now)
    LinearLayout profession_name_now;



    @BindView(R.id.company_name_old)
    LinearLayout company_name_old;

    @BindView(R.id.profession_name_old)
    LinearLayout profession_name_old;

    @BindView(R.id.profession_other)
    LinearLayout profession_other;

    @BindView(R.id.ll_school)
    LinearLayout ll_school;



    private Uri imageUri;
    public static final int CHOOSE_PHOTO = 1;
    public static final int TAKE_PHOTO = 2;
    public static final int EDIT_PHOTO = 3;
    public static final int COMMON_MODIFY = 4;

    private RegisterLoginBean.UserInfo userInfo;


    private boolean isOk;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo_modify;
    }

    @Override
    protected void initView() {


//        if(在职){
//            显示：公司 + 职位
//        }else if(离职){
//            显示：前公司 + 前职位
//        }else if(自由职业){
//            显示：目前从事
//        }else if(学生){
//            显示：学校名
//        } -- 后台需添加字段

        userInfo = StringUtil.getUserInfoBean();

        //职业信息
        getStatePosition();

        if("1".equals(userInfo.getPosition_status())){
            profession_name.setText("在职");
            //当前职位
            if(!TextUtils.isEmpty(userInfo.getPosition())){
                profession_name_now_text.setText(userInfo.getPosition());
            }

            //当前公司
            if(!TextUtils.isEmpty(userInfo.getCompany())){
                company_name_now_text.setText(userInfo.getCompany_name());
            }
        }else if("2".equals(userInfo.getPosition_status())){
            profession_name.setText("离职");
        }else if("3".equals(userInfo.getPosition_status())){
            profession_name.setText("自由职业");
        }else if("4".equals(userInfo.getPosition_status())){
            profession_name.setText("学生");
        }

        //头像
        if(userInfo != null){
            ImageUtil.loadByDefaultHead(mContext,userInfo.getAvatar(),head_icon);
        }

        //昵称
        nickname.setText(userInfo.getNickname());
        //简介
        profile_info_text.setText(userInfo.getPro_summary());




        mNickname = userInfo.getNickname();
        mName = userInfo.getName();
        mGender = userInfo.getGender();
        mPosition = userInfo.getPosition();
        mCompany = userInfo.getCompany();
        mAvatar_ext = "png";
        mPro_summary = userInfo.getPro_summary() ;
        mBirthday =  userInfo.getBirthday();


        tv_title.setText("编辑");

//        boolean isOpen = SPUtils.getInstance().getBoolean("push_open",true);
//        if(isOpen){
//            open_push.setImageResource(R.mipmap.icon_push_open);
//            push_tx.setText("已开启");
//            if(BuildConfig.DEBUG){
//                requestPermission(this);
//            }
//            JPushInterface.resumePush(getApplicationContext());
//        }else{
//            open_push.setImageResource(R.mipmap.icon_push_close);
//            push_tx.setText("已关闭");
//            JPushInterface.stopPush(getApplicationContext());
//
//        }

        Random rand = new Random();
        int i = rand.nextInt(15) + 1;
        clean_text.setText("当前缓存" + i + "M");


        //应用程序接收通知开关是否打开
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
//        if(areNotificationsEnabled){
//            KLog.d("tag","应用程序接收通知开关已打开");
//        }else{
//            KLog.d("tag","应用程序接收通知开关未打开");
//        }


    }

    private void getStatePosition() {
        if("1".equals(userInfo.getPosition_status())){
            ll_company.setVisibility(View.VISIBLE);
            profession_name_now.setVisibility(View.VISIBLE);
        }else if("2".equals(userInfo.getPosition_status())){
            company_name_old.setVisibility(View.VISIBLE);
            profession_name_old.setVisibility(View.VISIBLE);
        }else if("3".equals(userInfo.getPosition_status())){
            profession_other.setVisibility(View.VISIBLE);
        }else if("4".equals(userInfo.getPosition_status())){
            ll_school.setVisibility(View.VISIBLE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否需要开启通知栏功能
            NotificationUtil.OpenNotificationSetting(mContext, new NotificationUtil.OnSettingLitener() {
                @Override
                public void onSetting() {
                    KLog.d("tag","应用程序接收通知开关未打开");
                    open_push.setImageResource(R.mipmap.icon_push_close);
                    push_tx.setText("已关闭");
                    isOk = false;

                }
            },new NotificationUtil.OnNextLitener() {
                @Override
                public void onNext() {
                    isOk = true;
                    KLog.d("tag","应用程序接收通知开关已打开");
                    open_push.setImageResource(R.mipmap.icon_push_open);
                    push_tx.setText("已开启");
                }
            });
        }

    }

    @OnClick({R.id.iv_back,R.id.change_head,
            R.id.exit_ll,
            R.id.open_push,
            R.id.change_cache,
            R.id.change_resetData,
            R.id.change_nickname,
            R.id.profile_info,
            R.id.profession_status,
            R.id.profession_name_now,
            R.id.company_name_now
    })
    public void clicks(View view){
        switch (view.getId()){

            case R.id.company_name_now:

                UIHelper.toModifyUserInfo(this,"company",mCompany);
                break;

            case R.id.profession_name_now:

                UIHelper.toModifyUserInfo(this,"profession",mPosition);
                break;

            case R.id.profession_status:
                getOptionData();
                initOptionPicker();

                break;
            case R.id.profile_info:
                MobclickAgentUtils.onEvent(UmengEvent.i_setting_profile_2_0_0);

                UIHelper.toModifyUserInfo(this,"profile",mPro_summary);
                break;
            case R.id.change_nickname:
                MobclickAgentUtils.onEvent(UmengEvent._setting_nickname_2_0_0);

                UIHelper.toModifyUserInfo(this,"nickname",mNickname);
                break;
            case R.id.change_resetData:
                MobclickAgentUtils.onEvent(UmengEvent.i_setting_reset_2_0_0);

                showReSetDataDialog();
                break;
            case R.id.change_head:
                MobclickAgentUtils.onEvent(UmengEvent.i_setting_icon_2_0_0);
                showHeadDialog();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.exit_ll:

               finish();

                break;
            case R.id.open_push:

                //判断机型


                MobclickAgentUtils.onEvent(UmengEvent.i_setting_push_2_0_0);

                NotificationUtil.gotoSet(BaseApp.getApplication());

//                if(!isOk){
//                    NotificationUtil.gotoSet(BaseApp.getApplication());
//                }else{
//                    boolean isOpen = SPUtils.getInstance().getBoolean("push_open",false);
//                    if(isOpen){
//                        open_push.setImageResource(R.mipmap.icon_push_close);
//                        push_tx.setText("已关闭");
//                        SPUtils.getInstance().put("push_open",false);
//                    }else{
//                        open_push.setImageResource(R.mipmap.icon_push_open);
//                        push_tx.setText("已开启");
//                        SPUtils.getInstance().put("push_open",true);
//                    }
//                }




                break;
            case R.id.change_cache:
                MobclickAgentUtils.onEvent(UmengEvent.i_setting_clear_2_0_0);

                showCacheDialog();

                break;
            default:
        }
    }

    private ArrayList<String> options1Items = new ArrayList<>();
    private OptionsPickerView pvOptions;
    private void getOptionData() {
        options1Items.clear();
        options1Items.add("在职");
        options1Items.add("离职");
        options1Items.add("自由职业");
        options1Items.add("学生");
    }


    private void initOptionPicker() {//条件选择器初始化
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                KLog.d("tag",options1Items.get(options1) + "");
                profession_name.setText(options1Items.get(options1) + "");
            }
        })
                .setTitleText("职业状态")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.LTGRAY)
                .setCancelColor(Color.BLACK)
                .setSubmitColor(Color.BLACK)
                .setTextColorCenter(Color.LTGRAY)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
//                        Toast.makeText(UserInfoModifyActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
          pvOptions.setPicker(options1Items);//一级选择器
//        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/

          pvOptions.show();
    }


    private void logout() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().logout(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("用户登出成功");
                        exit();
                    }
                });
    }

    private void resetPersonal() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().resetPersonal(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("重置成功");
                    }
                });
    }


    private void showReSetDataDialog() {
        final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(UserInfoModifyActivity.this).builder();
        iosAlertDialog.setPositiveButton("是的", v -> {
            resetPersonal();
        }).setNegativeButton("取消", v -> {}).setMsg("是否重置个性化数据?\n重置后，可在首页重新选择").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();

    }



    private void showCacheDialog() {
        final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(UserInfoModifyActivity.this).builder();
        iosAlertDialog.setPositiveButton("确定", v -> {
            //清除历史，刷新界面
            clean_text.setText("当前缓存0M");
            SPUtils.getInstance().put("is_cached", true);
            ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
            ToastUtils.showShort("清除成功");
        }).setNegativeButton("取消", v -> {}).setMsg("确认要清空缓存?").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();

    }


    public void showExitDialog(){
        final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(UserInfoModifyActivity.this).builder();
        iosAlertDialog.setPositiveButton("退出", v -> {
            logout();
        }).setNegativeButton("再想想", v -> {}).setMsg("退出当前账号?").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    private void exit() {
        //清数据
        SPUtils.getInstance().put(Constant.IS_LOGIN,false);
        StringUtil.removeUserInfoBean();
        BaseApp.getApplication().exitApp();
        UIHelper.toLoginActivity(this);
    }



    private void showHeadDialog(){
        HeadAlertDialog dialog = new HeadAlertDialog(this).builder();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDialogItemClickListener(position1 -> {
            if(0 == position1){
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    openTakePhoto();
                }
            }else if(1 == position1){
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    openAlbum();
                }
            }
        });
        dialog.show();
    }


    private void openTakePhoto() {
        File outputImage = FileHelper.getOutputHeadImageFile(this);
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,
                    AppUtils.getAppPackageName() + ".fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    public void editPicture(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // 去黑边
        intent.putExtra("scale", true);
        // 去黑边
        intent.putExtra("scaleUpIfNeeded", true);
        // aspectX aspectY 是宽高的比例
        //输出是X方向的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //输出X方向的像素
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileHelper.getOutputEditImageFile(this)));
        //设置为不返回数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, EDIT_PHOTO);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if(permissions[0].equals("android.permission.CAMERA")){
                KLog.d("tag","相机不同意");
                ToastUtils.showShort("需开启相机权限");
            }else if(permissions[0].equals("android.permission.WRITE_EXTERNAL_STORAGE")){
                ToastUtils.showShort("需开启手机存储权限");
            }
        }else{
            if(permissions[0].equals("android.permission.CAMERA")){
                KLog.d("tag","相机已同意");
                openTakePhoto();
            }else if(permissions[0].equals("android.permission.WRITE_EXTERNAL_STORAGE")){
                KLog.d("tag","手机存储已同意");
                openAlbum();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    editPicture(imageUri);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                    }
                    //获取图片的uri
                    Uri uri = data.getData();
                    editPicture(uri);
                }
                break;
            case EDIT_PHOTO:
                if (resultCode == RESULT_OK) {
                    uploadHeadImg();
                }
                break;
            case COMMON_MODIFY:
                if (resultCode == RESULT_OK) {
                    setBackUserInfo(data);
                }
                break;

            default:
                break;
        }
    }


    private void setBackUserInfo(Intent data) {
        nickname.setText(data.getExtras().getString("nickname"));
        mNickname = data.getExtras().getString("nickname");
        profile_info_text.setText(data.getExtras().getString("profile"));
        mPro_summary = data.getExtras().getString("profile");
        profession_name_now_text.setText(data.getExtras().getString("profession"));
        mPosition = data.getExtras().getString("profession");
    }



    private void uploadHeadImg() {
        KLog.d("tag","路径是 " + FileHelper.getOutputEditImageFile(this).getPath());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FileHelper.getOutputEditImageFile(this).getPath());
        } catch (FileNotFoundException e) {
            KLog.d("tag",e.getMessage());
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        String img_data = "data:image/png;base64,"  + Bitmap2StrByBase64(bitmap);
        mAvatar_base = img_data;
        type = 1;
        alterinfo();
    }



    public String Bitmap2StrByBase64(Bitmap bit) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //参数100表示不压缩
            bit.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            byte[] bytes = bos.toByteArray();
            Base64 base64 = new Base64();
            String dataSS = base64.encodeToString(bytes);
            return dataSS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /** --------------------------------- 修改用户信息  ---------------------------------*/
    String mCompany = "";
    String mNickname = "";
    String mName = "";
    String mGender = "1";
    String mPosition = "";
    String mAvatar_base = "";
    String mAvatar_ext = "png";
    String mPro_summary = "没有简介" ;
    String mBirthday = "";
    //当点击修改图片时，type设置1，然后修改后设置归位
    int type = 0;

    private void alterinfo() {
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(mNickname)){
            map.put("nickname",mNickname);
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
                        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                        ToastUtils.showShort("修改成功");
                        if(1 == type){
                            ImageUtil.load(UserInfoModifyActivity.this,response.getReturn_data().getAvatar(),head_icon);
                            type = 0;
                        }
                    }
                });
    }






    /**
     * 华为
     * 设置接收通知消息 | Set up receive notification messages
     * @param enable 是否开启 | enabled or not
     */
    private void setReceiveNotifyMsg(boolean enable){
        KLog.e("tag","enableReceiveNotifyMsg:begin");
        HMSAgent.Push.enableReceiveNotifyMsg(enable, new EnableReceiveNotifyMsgHandler() {
            @Override
            public void onResult(int rst) {
                KLog.e("tag","enableReceiveNotifyMsg:end code=" + rst);
            }
        });
    }


}
