package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.HeadAlertDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.Base64;
import com.qmkj.niaogebiji.common.utils.FileHelper;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.head_icon)
    CircleImageView head_icon;

    @BindView(R.id.nickname)
    TextView nickname;

    @BindView(R.id.profile_info_text)
    TextView profile_info_text;


    private Uri imageUri;
    public static final int CHOOSE_PHOTO = 1;
    public static final int TAKE_PHOTO = 2;
    public static final int EDIT_PHOTO = 3;
    public static final int COMMON_MODIFY = 4;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        tv_title.setText("设置");
    }


    @OnClick({R.id.iv_back,R.id.change_head})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.change_head:
                showHeadDialog();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    private void showHeadDialog(){
        HeadAlertDialog dialog = new HeadAlertDialog(this).builder();
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
    }



    private void uploadHeadImg() {
        KLog.d("tag","路径是 " + FileHelper.getOutputEditImageFile(this).getPath());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FileHelper.getOutputEditImageFile(this).getPath());
        } catch (FileNotFoundException e) {
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
                            ImageUtil.load(SettingActivity.this,response.getReturn_data().getAvatar(),head_icon);
                            type = 0;
                        }
                    }
                });
    }

}
