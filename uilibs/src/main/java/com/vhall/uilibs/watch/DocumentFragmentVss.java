package com.vhall.uilibs.watch;

import android.content.pm.ActivityInfo;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
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


    @Override
    public void refreshView(DocumentView view) {
        /**
         * 文档缩放功能不完善，暂不建议使用
         * view.getSettings().setBuiltInZoomControls(true);
         * view.getSettings().setSupportZoom(true);
         */
        if (rlContainer != null) {
            rlContainer.removeAllViews();
            rlContainer.addView(view);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1280*5, ViewGroup.LayoutParams.MATCH_PARENT);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            view.setLayoutParams(params);
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
                        lp.width = SizeUtils.dp2px(210f);
                        rlContainer.setLayoutParams(lp);

                    }else if(getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){

                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
                        //这个横屏的宽度
                        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        lp.width = ScreenUtils.getScreenWidth();
                        rlContainer.setLayoutParams(lp);

                        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                        //这个横屏的宽度
                        lp2.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        lp2.width = ScreenUtils.getScreenWidth();
                        rlContainer.setLayoutParams(lp2);
                    }
                }
            }
        }

    }

}
