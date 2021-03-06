package com.qmkj.niaogebiji.common.net.base;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonParseException;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.socks.library.KLog;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/***
 * 把整体数据送进来检查一下，然后再送出去
 * @param <T>
 */
public abstract class BaseObserver<T extends HttpResponse> extends DisposableObserver<T> {


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onNext(T response) {

        if("200".equals(response.getReturn_code())){
            onSuccess(response);
        }else{
            //TODO 2020.1.13 账号登录问题，去登录界面
            // -- 修改把下面代码隐藏了 3.11放到用户请求处,测试发现在进入用户信息界面，uid不正确，会提示账号不存在，紧接着跳转到login，显然不正确！！
            //您当前未登录，先去登录吧！    1008 账号不存在
//            if("2003".equals(response.getReturn_code()) || "1008".equals(response.getReturn_code())){
//                UIHelper.toLoginActivity(BaseApp.getApplication());
//            }

            onHintError(response.getReturn_code(),response.getReturn_msg());
        }

    }

    @Override
    public void onError(Throwable t) {
        KLog.d("错误❎的信息：" + t.getMessage());

        if(t instanceof HttpException){
            //   HTTP错误
            onException(ExceptionReason.BAD_NETWORK);
        }else if(t instanceof ConnectException
                || t instanceof UnknownHostException){
            //  连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        }else if(t instanceof InterruptedException){
            //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if(t instanceof JsonParseException
                || t instanceof JSONException
                || t instanceof ParseException){
            //  解析错误
            onException(ExceptionReason.PARSE_ERROR);
        }else if(t instanceof SocketException){
            //  服务器响应超时
            onException(ExceptionReason.RESPONSE_TIMEOUT);
        }else{
            //未知错误
            onException(ExceptionReason.UNKNOWN_ERROR);
        }
    }


    public void onException(ExceptionReason reason) {
        switch (reason) {
            case CONNECT_ERROR:
                onNetFail("连接超时");
                break;

            case CONNECT_TIMEOUT:
                KLog.d("连接错误");
                onNetFail("连接错误");
                break;

            case BAD_NETWORK:
                KLog.d("HTTP错误");
                onNetFail("HTTP错误");
                break;

            case PARSE_ERROR:
                KLog.d("解析错误 不给予 toast 提示");
                onNetFail("解析错误");
                //TODO 如果解析错误，那么就给空值

                break;

            case UNKNOWN_ERROR:
                KLog.d("未知错误 不给予 toast 提示");
                onNetFail("未知错误");

            default:
                break;
        }
    }

    /** --------------------- 枚举 -------------------- */

    public enum ExceptionReason {
        /** 解析数据失败 */
        PARSE_ERROR,
        /** 网络问题 */
        BAD_NETWORK,
        /** 连接错误 */
        CONNECT_ERROR,
        /** 连接超时 */
        CONNECT_TIMEOUT,
        /** 响应超时 */
        RESPONSE_TIMEOUT,
        /** 未知错误 */
        UNKNOWN_ERROR,
    }


    @Override
    public void onComplete() {

    }

    //自定义抽象方法 成功 提示错误 链接错误
    public abstract void onSuccess(T t);

    //如果code 不是200 ,则toast显示
    public void onHintError(String return_code, String errorMes){
        ToastUtils.showShort(errorMes);
    }

    //网络请求过程错误 toast提示
    public void onNetFail(String msg){
        if(!"未知错误".equals(msg) &&
            !"解析错误".equals(msg)){
             ToastUtils.showShort(msg);
        }

    }




}
