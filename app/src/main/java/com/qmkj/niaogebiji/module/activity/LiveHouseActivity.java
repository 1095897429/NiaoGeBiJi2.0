package com.qmkj.niaogebiji.module.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.HotNewsAdapterV2;
import com.qmkj.niaogebiji.module.adapter.LiveChatAdapter;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.socks.library.KLog;
import com.vhall.business.VhallSDK;
import com.vhall.business.data.Survey;
import com.vhall.business.data.WebinarInfo;
import com.vhall.business.data.source.WebinarInfoDataSource;
import com.vhall.uilibs.Param;
import com.vhall.uilibs.chat.ChatFragment;
import com.vhall.uilibs.interactive.InteractiveActivity;
import com.vhall.uilibs.util.ActivityUtils;
import com.vhall.uilibs.util.InvitedDialog;
import com.vhall.uilibs.util.MessageLotteryData;
import com.vhall.uilibs.util.ShowLotteryDialog;
import com.vhall.uilibs.util.SignInDialog;
import com.vhall.uilibs.util.SurveyPopu;
import com.vhall.uilibs.util.SurveyPopuVss;
import com.vhall.uilibs.util.SurveyView;
import com.vhall.uilibs.util.VhallUtil;
import com.vhall.uilibs.util.emoji.InputUser;
import com.vhall.uilibs.util.emoji.InputView;
import com.vhall.uilibs.util.handler.WeakHandler;
import com.vhall.uilibs.watch.DocumentFragment;
import com.vhall.uilibs.watch.DocumentFragmentVss;
import com.vhall.uilibs.watch.WatchContract;
import com.vhall.uilibs.watch.WatchLiveFragment;
import com.vhall.uilibs.watch.WatchLivePresenter;
import com.vhall.uilibs.watch.WatchLivePresenterVss;
import com.vhall.uilibs.watch.WatchPlaybackFragment;
import com.vhall.uilibs.watch.WatchPlaybackPresenter;
import com.vhall.uilibs.watch.WatchPlaybackPresenterVss;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vhall.com.vss.data.ResponseRoomInfo;
import vhall.com.vss.module.room.VssRoomManger;
import vhall.com.vss.module.room.callback.IVssMessageLister;
import vhall.com.vss.module.rtc.VssRtcManger;

import static com.vhall.uilibs.util.SurveyView.EVENT_JS_BACK;
import static com.vhall.uilibs.util.SurveyView.EVENT_PAGE_LOADED;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-30
 * 描述:
 * 1.未开播
 * 2.开播中 -- 先研究
 */
public class LiveHouseActivity extends BaseActivity implements WatchContract.WatchView {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.lv_chat)
    RecyclerView lv_chat;


    LiveChatAdapter mLiveChatAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<String> chatList = new ArrayList<>();


    //直播的参数
    private Param param;
    private int type;
    private Fragment docFragment;
    private FragmentManager fragmentManager;

    public WatchPlaybackFragment playbackFragment;
    public WatchLiveFragment liveFragment;
    public ChatFragment chatFragment;
    public ChatFragment questionFragment;

    private WatchContract.WatchView watchView;

    WatchContract.WatchPresenter mPresenter;

    InputView inputView;
    public int chatEvent = ChatFragment.CHAT_EVENT_CHAT;
    private SurveyPopuVss popuVss;
    private SurveyPopu popu;
    private SignInDialog signInDialog;
    private ShowLotteryDialog lotteryDialog;
    private InvitedDialog invitedDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_livehouse;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void initFirstData() {
        tv_title.setText("管理起航---新人管理者成长");

        param = (Param) getIntent().getSerializableExtra("param");
        type = getIntent().getIntExtra("type", VhallUtil.WATCH_LIVE);
        fragmentManager = getSupportFragmentManager();
        docFragment = fragmentManager.findFragmentById(R.id.contentDoc);
        chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.contentChat);

        watchView = this;

        initWatch(param);


//        initLiveChat();
    }

    public void initWatch(Param params) {
        String customeId = Build.BOARD + Build.DEVICE + Build.SERIAL;
        String customNickname = Build.BRAND + "手机用户";
        int watchType;
        if (type == VhallUtil.WATCH_LIVE) {
            watchType = WebinarInfo.LIVE;
        } else {
            watchType = WebinarInfo.VIDEO;
        }
        Log.d("tag","直播间id " + params.watchId);
        VhallSDK.initWatch(params.watchId, customeId, customNickname, params.key, watchType, new WebinarInfoDataSource.LoadWebinarInfoCallback() {
            @Override
            public void onWebinarInfoLoaded(String jsonStr, WebinarInfo webinarInfo) {

                KLog.e("tag"," == 初始化房间信息成功 == ");
                param.webinar_id = webinarInfo.webinar_id;

                //敏感词过滤信息，发送聊天、评论通用
                if (webinarInfo.filters != null && webinarInfo.filters.size() > 0) {
                    param.filters.clear();
                    param.filters.addAll(webinarInfo.filters);
                }
                KLog.e("tag"," == vss_room_id " + webinarInfo.vss_room_id);
                if (!TextUtils.isEmpty(webinarInfo.vss_room_id) && !TextUtils.isEmpty(webinarInfo.vss_room_id)) {
                    param.vssRoomId = webinarInfo.vss_room_id;
                    param.vssToken = webinarInfo.vss_token;
                    param.join_id = webinarInfo.join_id;
                    if (docFragment == null) {
                        docFragment = new DocumentFragmentVss();
                        fragmentManager.beginTransaction().add(R.id.contentDoc, docFragment).commit();
                    }
                } else {
                    if (docFragment == null) {
                        docFragment = new DocumentFragment();
                        fragmentManager.beginTransaction().add(R.id.contentDoc, docFragment).commit();
                    }
                }
                if (liveFragment == null && type == VhallUtil.WATCH_LIVE) {
                    //直播间，公告信息
                    if (webinarInfo.notice != null && !TextUtils.isEmpty(webinarInfo.notice.content)) {
                        param.noticeContent = webinarInfo.notice.content;
                    }
                    liveFragment = WatchLiveFragment.newInstance();
                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                            liveFragment, R.id.contentVideo);
                    if (!TextUtils.isEmpty(webinarInfo.vss_room_id)) {
                        new WatchLivePresenterVss(liveFragment, (WatchContract.DocumentViewVss) docFragment, chatFragment, questionFragment, watchView, param);
                    } else {
                        new WatchLivePresenter(liveFragment, (WatchContract.DocumentView) docFragment, chatFragment, questionFragment, watchView, param, webinarInfo);
                    }
                } else if (playbackFragment == null && type == VhallUtil.WATCH_PLAYBACK) {
                    playbackFragment = WatchPlaybackFragment.newInstance();
                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                            playbackFragment, R.id.contentVideo);
                    if (!TextUtils.isEmpty(webinarInfo.vss_room_id)) {
                        new WatchPlaybackPresenterVss(playbackFragment, (WatchContract.DocumentViewVss) docFragment, chatFragment, watchView, param);
                    } else {
                        new WatchPlaybackPresenter(playbackFragment, (WatchContract.DocumentView) docFragment, chatFragment, watchView, param, webinarInfo);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                showToast(errorMsg);
                if (liveFragment == null && type == VhallUtil.WATCH_LIVE) {
                    liveFragment = WatchLiveFragment.newInstance();
                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                            liveFragment, R.id.contentVideo);
                    docFragment = new DocumentFragment();
                    fragmentManager.beginTransaction().replace(R.id.contentDoc, (Fragment) docFragment).commit();
                    new WatchLivePresenter(liveFragment, (WatchContract.DocumentView) docFragment, chatFragment, questionFragment, watchView, param, null);
                }
                if (playbackFragment == null && type == VhallUtil.WATCH_PLAYBACK) {
                    playbackFragment = WatchPlaybackFragment.newInstance();
                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                            playbackFragment, R.id.contentVideo);
                    docFragment = new DocumentFragment();
                    fragmentManager.beginTransaction().replace(R.id.contentDoc, (Fragment) docFragment).commit();
                    new WatchPlaybackPresenter(playbackFragment, (WatchContract.DocumentView) docFragment, chatFragment, watchView, param, null);
                }
            }
        });
    }

    @Override
    public void showChatView(boolean isShowEmoji, InputUser user, int contentLengthLimit) {
        if (contentLengthLimit > 0) {
            inputView.setLimitNo(contentLengthLimit);
        }
        inputView.show(isShowEmoji, user);
    }

    @Override
    public void showSignIn(String signId, int startTime) {
        if (signInDialog == null) {
            signInDialog = new SignInDialog(this);
        }
        signInDialog.setSignInId(signId);
        signInDialog.setCountDownTime(startTime);
        signInDialog.setOnSignInClickListener(new SignInDialog.OnSignInClickListener() {
            @Override
            public void signIn(String signId) {
                mPresenter.signIn(signId);
            }
        });
        signInDialog.show();
    }

    @Override
    public void dismissSignIn() {
        if (signInDialog != null) {
            signInDialog.dismiss();
        }
    }

    @Override
    public void showSurvey(String url, String title) {
        if (popuVss == null) {
            popuVss = new SurveyPopuVss(this);
            popuVss.setListener(new SurveyView.EventListener() {
                @Override
                public void onEvent(int eventCode, final String eventMsg) {
                    switch (eventCode) {
                        case EVENT_PAGE_LOADED:
                            //页面加载完成
                            break;
                        case EVENT_JS_BACK:
                            //数据回调
                            new WeakHandler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    popuVss.dismiss();
                                    // mPresenter.submitSurvey(eventMsg);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        popuVss.loadView(url, title);
        popuVss.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.NO_GRAVITY, 0, 0);
    }

    @Override
    public void showSurvey(Survey survey) {
        if (popu == null) {
            popu = new SurveyPopu(this);
            popu.setOnSubmitClickListener(new SurveyPopu.OnSubmitClickListener() {
                @Override
                public void onSubmitClick(Survey survey1, String result) {
                    mPresenter.submitSurvey(survey1, result);
                }
            });
        }
        popu.setSurvey(survey);
        popu.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.NO_GRAVITY, 0, 0);
    }

    @Override
    public void dismissSurvey() {
        if (popuVss != null) {
            popuVss.dismiss();
        }
        if (popu != null) {
            popu.dismiss();
        }
    }

    @Override
    public int changeOrientation() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        return getRequestedOrientation();
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showLottery(MessageLotteryData lotteryData) {
        if (lotteryDialog == null) {
            lotteryDialog = new ShowLotteryDialog(this);
        }
        lotteryDialog.setMessageInfo(lotteryData);
        lotteryDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        lotteryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        lotteryDialog.show();
    }

    @Override
    public void enterInteractive() { // 进入互动房间
        Intent intent = new Intent(this, InteractiveActivity.class);
        intent.putExtra("param", param);
        startActivity(intent);
    }

    @Override
    public void refreshHand(int second) {

    }

    @Override
    public void showInvited() {
        if (invitedDialog == null) {
            invitedDialog = new InvitedDialog(this);
            invitedDialog.setPositiveOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //同意上麦
                    mPresenter.replyInvite(1);
                    enterInteractive();
                    invitedDialog.dismiss();
                    //发送同意上麦信息
                }
            });
            invitedDialog.setNegativeOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.replyInvite(2);
                    invitedDialog.dismiss();
                    //发送拒绝上麦信息
                }
            });
        }
        invitedDialog.setRefuseInviteListener(new InvitedDialog.RefuseInviteListener() {
            @Override
            public void refuseInvite() {
                mPresenter.replyInvite(2);
            }
        });
        invitedDialog.show();
    }

    @Override
    public void setOnlineNum(int onlineNum) {
        KLog.d("tag","在线人数：" + onlineNum);
    }


    @Override
    public void showNotice(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        KLog.d("tag","通知打开：" + content);
    }

    @Override
    public void dismissNotice() {
        KLog.d("tag","通知关闭：");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        inputView.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            changeOrientation();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        if (null != inputView) {
            inputView.dismiss();
        }
        super.onUserLeaveHint();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VssRtcManger.leaveRoom();
        VssRoomManger.leaveRoom();


//        if (upnpService != null) {
//            upnpService.getRegistry().removeListener(registryListener);
//        }
//        this.unbindService(serviceConnection);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initLiveChat() {

        for (int i = 0; i < 10; i++) {
            chatList.add(i + "");
        }

        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        lv_chat.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mLiveChatAdapter = new LiveChatAdapter(chatList);
        lv_chat.setAdapter(mLiveChatAdapter);
        //解决数据加载不完
        lv_chat.setNestedScrollingEnabled(true);
        lv_chat.setHasFixedSize(true);
    }


    @OnClick({R.id.iv_back,
            R.id.iv_right_1,
    })
    public void clicks(View view){
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right_1:
                NewsDetailBean m = new NewsDetailBean();
                StringUtil.showShareDialog(LiveHouseActivity.this,m);
                break;
            default:
        }
    }

    @Override
    public void setPresenter(WatchContract.WatchPresenter presenter) {
        mPresenter = presenter;
    }
}
