package com.qmkj.niaogebiji.common.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.socks.library.KLog;

import java.io.IOException;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-05
 * 描述:
 */



public class MediaService extends Service {

    private static final String TAG = "MediaService";
    private MyBinder mBinder = new MyBinder();
    //标记当前歌曲的序号
    private int i = 0;


    public MediaPlayer mMediaPlayer;
    private Mythred mythred;


    public MediaService() {
        KLog.d("tag"," service的构造函数 ");
        if(null == mMediaPlayer){
            mMediaPlayer = new MediaPlayer();
            // 设置音量，参数分别表示左右声道声音大小，取值范围为0~1
            mMediaPlayer.setVolume(0.5f, 0.5f);
            // 设置是否循环播放
            mMediaPlayer.setLooping(false);
        }
    }

    public void prepare(String url){
        iniMediaPlayerFile(url);
    }


    @Override
    public void onCreate() {
        KLog.d("tag"," onCreate ");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {

        public MediaService getInstance() {
            return MediaService.this;
        }

        /**
         * 播放音乐
         */
        public void playMusic() {
            if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.start();

                mythred = new Mythred();
                mythred.start();

                isPauseOrEnding = false;
            }
        }

        /**
         * 暂停播放
         */
        public void pauseMusic() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.pause();
                isPauseOrEnding = true;
            }
        }

        /**
         * 关闭播放器
         */
        public void closeMedia() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                isPauseOrEnding = true;
                 KLog.e("tag","关闭播放器");
                if(null != mOnCloseListener){
                    mOnCloseListener.onClose();
                }
            }
        }


        /**
         * 获取歌曲长度 1
         **/
        public int getProgress() {
            return mMediaPlayer.getDuration();
        }

        /**
         * 获取播放位置 2
         */
        public int getPlayPosition() {

            return mMediaPlayer.getCurrentPosition();
        }
        /**
         * 播放指定位置
         */
        public void seekToPositon(int msec) {
            mMediaPlayer.seekTo(msec);
        }

        /**
         * 是否播放
         */
        public boolean isPlaying(){
            return mMediaPlayer.isPlaying();
        }
   }


    /**
     * 添加file文件到MediaPlayer对象并且准备播放音频
     */
    private void iniMediaPlayerFile(String  url) {
        //获取文件路径
        try {
            //此处的两个方法需要捕获IO异常
            //设置音频文件到MediaPlayer对象中
            if(null == mMediaPlayer){
                mMediaPlayer = new MediaPlayer();
                // 设置音量，参数分别表示左右声道声音大小，取值范围为0~1
                mMediaPlayer.setVolume(0.5f, 0.5f);
                // 设置是否循环播放
                mMediaPlayer.setLooping(false);
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            //让MediaPlayer对象准备
            mMediaPlayer.prepareAsync();
            initListener();
            Log.d("tag", "设置资源，准备阶段成功，开始播放");
        } catch (IOException e) {
            Log.d("tag", "设置资源，准备阶段出错");
            e.printStackTrace();
        }
    }


    private void initListener() {
        //播放出错监听
        mMediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
            Log.d("tag", "OnError - Error code: " + what + " Extra code: " + extra);
            return false;
        });
        //播放完成监听
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            KLog.e("tag","播放完成监听");
            //TODO 12.24 出现的问题是：音频播放完，进度条还没有走到头 -- 延缓状态设置  isPauseOrEnding = true;
            SystemClock.sleep(500);
            isPauseOrEnding = true;
            if(null != mOnEndListener){
                mOnEndListener.onEnd();
            }
        });
        //网络流媒体缓冲监听
        mMediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> {
            // i 0~100
            Log.d("tag:", "缓存进度" + i + "%");
        });

        //准备Prepared完成监听
        mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
            //获得音乐总时长
            int lengthoftime = mediaPlayer.getDuration();
            KLog.d("tag","获得音乐总时长 " + lengthoftime);

            if(null != mOnStartListener){
                mOnStartListener.onStart(lengthoftime);
            }

        });


        //进度调整完成SeekComplete监听，主要是配合seekTo(int)方法
        mMediaPlayer.setOnSeekCompleteListener(mediaPlayer -> {
            KLog.d("tag","seekbar移动监听，seekcomplete");
        });
    }


    boolean isPauseOrEnding = false;

    class Mythred extends Thread {
        @Override
        public void run() {
                //得到当前音乐的播放位置
                while (true){
                    if(isPauseOrEnding){
                        break;
                    }
                    int currentPosition = mBinder.getPlayPosition();
                    Log.i("tag", "currentPosition " + currentPosition);
                    if(null != onProgressListener){
                        onProgressListener.onProgress(currentPosition);
                    }
                    //让进度条每一秒向前移动
                    SystemClock.sleep(1000);
                }

        }
    }


    public interface OnProgressListener {
        void onProgress(int progress);
    }

    /**
     * 更新进度的回调接口
     */
    private OnProgressListener onProgressListener;
    /**
     * 注册回调接口的方法，供外部调用
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }


    public interface OnEndListener {
        void onEnd();
    }

    /**
     * 完成结束回调
     */
    private OnEndListener mOnEndListener;


    public void setOnEndListener(OnEndListener onEndListener) {
        mOnEndListener = onEndListener;
    }



    public interface OnStartListener {
        void onStart(int totalLength);
    }
    /**
     * 开始回调
     */
    private OnStartListener mOnStartListener;

    public void setOnStartListener(OnStartListener onStartListener) {
        mOnStartListener = onStartListener;
    }


    public interface OnCloseListener {
        void onClose();
    }

    /**
     * 关闭资源回调
     */
    private OnCloseListener mOnCloseListener;


    public void setOnCloseListener(OnCloseListener onCloseListener) {
        mOnCloseListener = onCloseListener;
    }
}
