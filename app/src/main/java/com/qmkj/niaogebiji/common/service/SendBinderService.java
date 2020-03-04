package com.qmkj.niaogebiji.common.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SPUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.QINiuTokenBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.xzh.imagepicker.bean.MediaFile;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-04
 * 描述:
 */
public class SendBinderService extends Service {

    private TempMsgBean mTempMsgBean;

    //构建一个对象
    public class MyBinder extends Binder{



        public SendBinderService getService(){
            return SendBinderService.this;
        }

        //③写一个公共方法，用来对data数据赋值。
        public void setData(TempMsgBean mTempMsgBean){
            SendBinderService.this.mTempMsgBean = mTempMsgBean;
            changeData();
        }
    }

    //通过binder实现调用者client与Service之间的通信
    private MyBinder binder = new MyBinder();


    @Override
    public void onCreate() {
        Log.i("DemoLog","TestService -> onCreate, Thread: " + Thread.currentThread().getName());
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DemoLog", "TestService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("DemoLog", "TestService -> onBind, Thread: " + Thread.currentThread().getName());
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("DemoLog", "TestService -> onUnbind, from:" + intent.getStringExtra("from"));
        return false;
    }

    @Override
    public void onDestroy() {
        Log.i("DemoLog", "TestService -> onDestroy, Thread: " + Thread.currentThread().getName());
        super.onDestroy();
    }


    //以下是方法
    private boolean isRequestCancelled;
    public void cancelRequest(){
        isRequestCancelled = true;
    }

    /** --------------------------------- 发布帖子中  ---------------------------------v*/
    private ExecutorService mExecutorService;
    //文件路径
    private String data;
    private String key;
    private boolean isCancelled;
    //七牛上传图片完成计数
    private int uploadTaskCount;
    //图片选择器临时返回数据
    private List<MediaFile> mediaFiles = new ArrayList<>();
    private List<MediaFile> pathList = new ArrayList<>();
    //动态配图，多图链接之间用英文逗号隔开
    private StringBuilder picbycomma = new StringBuilder();
    //界面传递过来的
    private String resultPic = "";
    //构建七牛
    private StringBuilder qiniuPic = new StringBuilder();
    private String lashPic = "";
    String blog = "";
    String blog_images = "";
    String blog_link = "";
    String blog_link_title = "";
    //0原创 1转发
    int blog_type = 0;
    //被转发动态ID，原创为0
    int blog_pid = 0;
    //转发时是否同时评论动态，1是 0否
    int blog_is_comment = 0;
    //文章Id
    int article_id;
    //文字标题
    String article_title = "";
    //文字图片
    String article_image= "";
    //话题id
    String topic_id = "";

    public void changeData() {

        mediaFiles = mTempMsgBean.getImgPath();
        pathList = mTempMsgBean.getImgPath2();
        blog_link  = mTempMsgBean.getLinkurl();
        blog_link_title =  mTempMsgBean.getLinkTitle();

        topic_id = mTempMsgBean.getTopicId();
        //TODO 12.21发现一张图片多次提交 需重新赋值
        picbycomma = new StringBuilder();
        qiniuPic = new StringBuilder();

        if(mediaFiles != null && !mediaFiles.isEmpty()){
            for (int i = 0; i < mediaFiles.size(); i++) {
                picbycomma.append(mediaFiles.get(i).getPath()).append(",");
            }
        }

        if(pathList != null && !pathList.isEmpty()){
            for (int i = 0; i < pathList.size(); i++) {
                picbycomma.append(pathList.get(i).getPath()).append(",");
            }
        }

        //有图则 赋值 resultPic
        if(!TextUtils.isEmpty(picbycomma.toString())){
            KLog.d("tag","picbycomma 值 是：" + picbycomma.toString());
            resultPic =  picbycomma.substring(0,picbycomma.length()  - 1);
            KLog.d("tag","去掉最后一个,显示的值：" + resultPic);
        }else{
            resultPic = "";
        }

        //内容
        blog  = mTempMsgBean.getContent();

        //转发
        blog_is_comment = mTempMsgBean.getBlog_is_comment();
        blog_type = mTempMsgBean.getBlog_type();

    }

    public void sendRequest(){
        //① 如果没有图片，显示属性动画
        KLog.d("tag","resultPic " + resultPic);
        if(TextUtils.isEmpty(resultPic)){
            createBlog();
        }else{
            mExecutorService = Executors.newFixedThreadPool(2);
            getUploadToken();
        }

    }



    String qiniuToken;
    private void getUploadToken() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUploadToken(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse<QINiuTokenBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<QINiuTokenBean> response) {
                        qiniuToken = response.getReturn_data().getToken();
                        if(!TextUtils.isEmpty(qiniuToken)){
                            if(!TextUtils.isEmpty(resultPic)){
                                uploadPicToQiNiu();
                            }
                        }
                    }
                });
    }


    UploadManager uploadManager;
    LinkedList<String> linkedList;
    //返回图片所有的集合
    List<String>  tempList;

    private void uploadPicToQiNiu() {
        com.qiniu.android.storage.Configuration config = new com.qiniu.android.storage.Configuration.Builder()
                // 分片上传时，每片的大小。 默认256K
                .chunkSize(512 * 1024)
                // 启用分片上传阀值。默认512K
                .putThreshhold(1024 * 1024)
                // 链接超时。默认10秒
                .connectTimeout(10)
                // 是否使用https上传域名
                .useHttps(true)
                // 服务器响应超时。默认60秒
                .responseTimeout(60)
                .build();

        // 重用uploadManager一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);
        //data = <File对象、或 文件路径、或 字节数组>
        //String key = <指定七牛服务上的文件名，或 null>;
        //String token = <从服务端获取>;
        key = "niaogebiji";
        qiniuToken = qiniuToken.replace("\\s","");
        qiniuToken = qiniuToken.replace("\n","");

        tempList = new ArrayList<>();
        tempList.clear();

        if(null != mediaFiles && !mediaFiles.isEmpty()){
            for (MediaFile fileBean  : mediaFiles) {
                tempList.add(fileBean.getPath());
            }
        }

        if(null != pathList && !pathList.isEmpty()){
            for (MediaFile fileBean  : pathList) {
                tempList.add(fileBean.getPath());
            }
        }

        linkedList = new LinkedList<>();
        linkedList.clear();
        linkedList.addAll(tempList);

        BaseActivity.maxSendProgress = tempList.size() * 100;
        BaseActivity.currentSendProgress = 0;
        uploadPicToQiNiuByOnePic();

    }

    private void uploadPicToQiNiuByOnePic() {

        //查询并移除第一个元素；
        if(linkedList.size() > 0){
            data = linkedList.poll();

            mExecutorService.submit(() -> {
                KLog.d("tag","本地存储的路径是 "  + data);

                //手动设值，取消的话不上传到七牛云上
                if(isRequestCancelled){
                    return;
                }

                uploadManager.put(data, System.currentTimeMillis() + key , qiniuToken, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if(info.isOK()) {
                            KLog.i("tag", key + " Upload Success");
                            //恢复默认值
                            data = "";


                        } else {
                            KLog.i("tag", key + " Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                    }
                },new UploadOptions(null,"image/jpeg",true,upProgressHandler,upCancellationSignal));
            });
        }
    }



    //广播意图
    private Intent intent ;

    UpProgressHandler upProgressHandler = new UpProgressHandler() {
        /**
         * @param key 上传时的upKey；
         * @param percent 上传进度；
         */
        @Override
        public void progress(String key, double percent) {
            KLog.e("tag","percent " + percent);
            intent = new Intent("com.example.communication.RECEIVER");
            intent.putExtra("progress", BaseActivity.currentSendProgress + (int) (percent * 100));
            sendBroadcast(intent);

            if(percent == 1.0){
                Message message = Message.obtain();
                message.what = QI_NIU_UPLOAD_OK;
                handler.sendMessage(message);
                //上传成功一个，就添加到qiniuPic中去
                qiniuPic.append(key).append(",");
                BaseActivity.currentSendProgress += 100;
                KLog.e("tag", "tempProgress最大值  " +  BaseActivity.maxSendProgress);
                KLog.e("tag", "tempProgress " +  BaseActivity.currentSendProgress);
            }

        }
    };




    UpCancellationSignal upCancellationSignal = new UpCancellationSignal() {
        @Override
        public boolean isCancelled() {
            return isCancelled;
        }
    };

    private static final int QI_NIU_UPLOAD_OK = 120;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //七牛上传图片完成计数
                case QI_NIU_UPLOAD_OK:
                    uploadTaskCount++;
                    KLog.e("tag", "上传的个数 " + uploadTaskCount + "");
                    //容器中图片全部上传完成
                    if (uploadTaskCount == tempList.size()) {
                        if(!TextUtils.isEmpty(qiniuPic.toString())){
                            lashPic =  qiniuPic.substring(0,qiniuPic.length()  - 1);
                            KLog.d("tag","构建七牛的图片路径是：" + lashPic);
                        }
                        createBlog();
                        return;
                    }


                    //类似手动点击
                    uploadPicToQiNiuByOnePic();

                    break;
                default:
            }
        }
    };


    private void createBlog(){
        Map<String,String> map = new HashMap<>();
        map.put("blog",blog + "");
        map.put("images",lashPic + "");
        map.put("link",blog_link + "");
        map.put("link_title",blog_link_title + "");
        map.put("type",blog_type + "");
        map.put("pid",blog_pid + "");
        map.put("is_comment",blog_is_comment + "");
        map.put("article_id", article_id + "");
        map.put("article_title", article_title + "");
        map.put("article_image",article_image + "");
        map.put("topic_id",topic_id + "");
        KLog.e("tag"," 发送请求00000  ");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        BaseActivity.sendStatus = BaseActivity.isSendOk;
                        KLog.e("tag","请求成功 " + response.getReturn_code());
                        //针对有图片特殊处理
                        if(TextUtils.isEmpty(resultPic)){
                            KLog.e("tag","发送请求  1111 请求成功  ");
                            //① 发送广播 -- 成功了显示成功进度条
                            intent = new Intent("com.example.communication.RECEIVER");
                            intent.putExtra("no_pic", 1);
                            sendBroadcast(intent);

//                            intent = new Intent("com.example.communication.RECEIVER");
//                            intent.putExtra("error", 1);
//                            sendBroadcast(intent);
                        }else {
                            //① 发送广播  -- 图片
                            intent = new Intent("com.example.communication.RECEIVER");
                            intent.putExtra("pic", 1);
                            sendBroadcast(intent);
                        }

                        cleanData();
                        removeTempMsg();

                    }


                    @Override
                    public void onNetFail(String msg) {
                        //① 发送失败广播
                        intent = new Intent("com.example.communication.RECEIVER");
                        intent.putExtra("error", 1);
                        sendBroadcast(intent);
                    }
                });
    }

    private void cleanData() {
        uploadTaskCount = 0;
        data = "";
        key = "";
        resultPic = "";
        lashPic = "";
        blog_link  = "";
        blog_link_title = "";
        blog_type = 0;
        blog_is_comment = 0;
    }


    public  void removeTempMsg() {
        mTempMsgBean = null;
        SPUtils.getInstance().remove(Constant.TMEP_MSG_INFO);
    }





}
