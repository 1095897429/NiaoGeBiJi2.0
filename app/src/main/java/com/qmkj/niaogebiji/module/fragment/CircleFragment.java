package com.qmkj.niaogebiji.module.fragment;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class CircleFragment extends BaseLazyFragment {
    public static final String TAG = "CircleFragment";



    public static CircleFragment getInstance() {
        return new CircleFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle;
    }


    @Override
    protected void initView() {



    }

    //点击切换fragement会调用
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause

        }else{
            //resume
        }
    }














    @Override
    public void initData() {

    }




//    @OnClick({R.id.sousou, R.id.qiandao, R.id.news_part, R.id.home_news_flipper})
//    public void clicks(View view){
//        switch (view.getId()){
//            case R.id.sousou:
//                MobclickAgentUtils.onEvent(UmengEvent.index_search_icon);
//                UIHelper.toSearchActivity(getActivity());
//                break;
//            case R.id.qiandao:
//                //TODO 判断登录
//                boolean is_login = SPUtils.getInstance().getBoolean(Constant.IS_LOGIN);
//                if(is_login){
//                    MobclickAgentUtils.onEvent(UmengEvent.index_point_in_nologin);
//                    UIHelper.toFeatherNewActivity(getActivity());
//                }else{
//                    MobclickAgentUtils.onEvent(UmengEvent.index_point_in_login);
//                    UIHelper.toLoginActivity(getActivity(),"");
//                }
//
//                break;
//            case R.id.news_part:
//
//                break;
//
//            default:
//        }
//    }

}
