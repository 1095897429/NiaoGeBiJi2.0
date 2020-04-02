package com.vhall.uilibs.watch;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.socks.library.KLog;
import com.vhall.document.DocumentView;
import com.vhall.uilibs.BasePresenter;
import com.vhall.uilibs.R;
import com.vhall.uilibs.event.ChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.vhall.ops.VHOPS.TYPE_SWITCHOFF;

/**
 * Created by zwp on 2019/7/4
 */
public class DocumentFragmentVss extends Fragment implements WatchContract.DocumentViewVss {

    private static final String TAG = "DocumentFragmentVss";
    private RelativeLayout rlContainer;
    private ScrollView scrollView;
    private DocumentView tempView = null;
    private String switchType = "";

    public static DocumentFragment newInstance() {
        DocumentFragment articleFragment = new DocumentFragment();
        return articleFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.document_fragment_vss, null);
        rlContainer = view.findViewById(R.id.rl_doc_container);
        scrollView = view.findViewById(R.id.scrollView);
        if (!TextUtils.isEmpty(switchType)) {
            if (rlContainer != null) {
                if (switchType.equals(TYPE_SWITCHOFF)) {
                    rlContainer.setVisibility(View.GONE);
                } else {
                    rlContainer.setVisibility(View.VISIBLE);
                }
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (tempView != null) {
            refreshView(tempView);
        }
        super.onActivityCreated(savedInstanceState);

    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void refreshView(DocumentView view) {
        /**
         * 文档缩放功能不完善，暂不建议使用
         * view.getSettings().setBuiltInZoomControls(true);
         * view.getSettings().setSupportZoom(true);
         */
        KLog.d("tag","refreshView ");
//        view.getSettings().setDisplayZoomControls(false);
//        view.getSettings().setUseWideViewPort(true);
//        view.getSettings().setLoadWithOverviewMode(true);
//        view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

//        view.scrollTo(0,0);
//        view.setSize(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight());
        if (rlContainer != null) {
            rlContainer.removeAllViews();
            rlContainer.addView(view);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1280*5, ViewGroup.LayoutParams.MATCH_PARENT);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            view.setLayoutParams(params);
//            view.getSettings().setSupportZoom(true);
//            view.getSettings().setBuiltInZoomControls(true);
        } else {
            tempView = view;
        }
    }

    @Override
    public void switchType(String type) {
        switchType = type;
        if (rlContainer != null) {
            if (type.equals(TYPE_SWITCHOFF)) {
                rlContainer.setVisibility(View.GONE);
            } else {
                rlContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setPresenter(BasePresenter presenter) {

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeEvent(ChangeEvent event){

        if (!TextUtils.isEmpty(switchType)) {
            if (rlContainer != null) {
                if (!switchType.equals(TYPE_SWITCHOFF)) {
                    if(getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){

                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
                        lp.height = SizeUtils.dp2px(210f);
                        lp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        rlContainer.setLayoutParams(lp);

                    }else if(getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                        KLog.d("tag","走这里啊1111 ");

                        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
//                        lp2.height = ScreenUtils.getScreenHeight();
//                        lp2.width = ScreenUtils.getScreenWidth();
                        lp2.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                        lp2.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        rlContainer.setLayoutParams(lp2);

//                        //计算文档的高宽比
//                        float radio11 = (float) ((SizeUtils.dp2px(210f) * 1.0 )/ ScreenUtils.getScreenHeight());
//                        KLog.d("tag","竖屏 高宽比 " + radio11);
//
                        float radio22 = (float) ((SizeUtils.dp2px(375f) / (SizeUtils.dp2px(210f) * 1.0 )));
                        KLog.d("tag","竖屏 宽高比 " + radio22);

                        float radio33 = (float) ((SizeUtils.dp2px(667f) / (SizeUtils.dp2px(295f) * 1.0 )));
                        KLog.d("tag","横屏 宽高比 " + radio33);
//
//                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
//                        lp.width = ScreenUtils.getScreenWidth() ;
//                        lp.height = (int) (lp.width / radio33);
//                        KLog.d("tag","height " + lp.height);
//
////                        lp.width = ScreenUtils.getScreenWidth();
//                        rlContainer.setLayoutParams(lp);


//                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
//                        //得到可展示的高度
//                        lp.height = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(60f);
//                        KLog.d("tag","height " + lp.height);
//
//                        lp.width = (int) (lp.height / radio11);
//                        KLog.d("tag","展示的宽度是 " + lp.width + "  原本s横屏的宽度  " + ScreenUtils.getScreenWidth());
////                        lp.width = ScreenUtils.getScreenWidth();
//                        rlContainer.setLayoutParams(lp);


                        //计算文档的高高比
//                        float radio = (float) ((SizeUtils.dp2px(210f) * 1.0 )/ (ScreenUtils.getScreenHeight() - SizeUtils.dp2px(60f)));
//                        KLog.d("tag","高高比 " + radio);
//
//                        //计算文档的比
//                        float radio2 = (float) ((ScreenUtils.getScreenHeight() * 1.0 )/ (ScreenUtils.getScreenWidth()));
//                        KLog.d("tag","宽比 " + radio2);


//                        if(radio >= radio2){
//                            //以小的
//                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
//                            lp.width = ScreenUtils.getScreenWidth();
//                            lp.height = (int) (lp.width / radio2);
//                            rlContainer.setLayoutParams(lp);
//                        }else{
//                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
//                            lp.height = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(60f);
//                            lp.width = (int) (lp.height / radio);
//                            rlContainer.setLayoutParams(lp);
//                        }


                    }
                }
            }
        }

    }

}
