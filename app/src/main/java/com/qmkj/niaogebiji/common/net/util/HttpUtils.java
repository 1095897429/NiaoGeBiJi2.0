package com.qmkj.niaogebiji.common.net.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.socks.library.KLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-12
 * 描述:内部利用AsyncTask来异步请求，用于一些不需要返回结果的情况
 */
public class HttpUtils {
    //单例
    private static HttpUtils mHttpUtils;
    //组合
    private IHttpCallback mIHttpCallback;
    //url
    private String mUrl;
    //json字符串
    private String mJson ;
    //默认类型 和 协议
    private static HTTP_TYPE mType = HTTP_TYPE.GET;
    private static PROTOCOL_TYPE mProtocolType = PROTOCOL_TYPE.HTTP;

    public HttpUtils(String url, String stringParams, IHttpCallback callback) {
        mUrl = url;
        mIHttpCallback = callback;
        mJson = stringParams;
        // 判断是http请求还是https请求
        try {
            URL httpUrl = new URL(mUrl);
            if (httpUrl.getProtocol().toLowerCase().equals("https")) {
                mProtocolType = PROTOCOL_TYPE.HTTPS;
            } else if (httpUrl.getProtocol().toLowerCase().equals("http")) {
                mProtocolType = PROTOCOL_TYPE.HTTP;
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public enum HTTP_TYPE {
        GET, POST
    }

    public enum PROTOCOL_TYPE {
        HTTP, HTTPS
    }

    //接口回调
    public interface IHttpCallback {
        void onResponse(final String result);
    }


    //提供外部调用
    public static HttpUtils getHttpUtil(final String url, final String stringParams, IHttpCallback callback) {

        mHttpUtils = new HttpUtils(url, stringParams, callback);
        return mHttpUtils;
    }

    //get请求
    public void httpGet() {
        mType = HTTP_TYPE.GET;
        if (!mUrl.contains("?")) {
            mUrl = mUrl + "?" + mJson;
        } else if (mUrl.substring(mUrl.length() - 1).equals("?")) {
            mUrl = mUrl + mJson;
        }
        httpAccess();
    }


    private void httpAccess() {
        //并行
        new HttpTask( mIHttpCallback, mType, mProtocolType, mJson)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new String[] { mUrl });
    }




    @SuppressLint("NewApi")
    class HttpTask extends AsyncTask<String, Void, String> {
        private IHttpCallback mIHttpCallback ;
        private HttpUtils.HTTP_TYPE mType ;
        private HttpUtils.PROTOCOL_TYPE mProtocolType;
        private final int CONNECTION_TIMEOUT = 15000;
        private final int READ_TIMEOUT = 15000;
        private String mParams;

        public HttpTask(IHttpCallback callback, HttpUtils.HTTP_TYPE type, HttpUtils.PROTOCOL_TYPE protocolType, String params) {
            super();
            mIHttpCallback = callback;
            mType = type;
            mParams = params;
            mProtocolType = protocolType;
        }

         TrustManager[] xtmArray = new MytmArray[] { new MytmArray() };

        /**
         * 信任所有主机-对于任何证书都不做检查
         */
        private  void trustAllHosts() {
            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, xtmArray, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

         HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> {
             // TODO Auto-generated method stub
             return true;
         };

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            if (urls == null || urls.length == 0) {
                return null;
            }

            String result = "";
            HttpURLConnection httpUrlCon = null;

            try {
                // new  a  url connection
                URL httpUrl = new URL(urls[0]);
                KLog.d("ok", "url to server: " + httpUrl);
                switch (mProtocolType) {
                    case HTTP:
                        httpUrlCon = (HttpURLConnection) httpUrl.openConnection();
                        break;
                    case HTTPS:
                        trustAllHosts();
                        httpUrlCon = (HttpsURLConnection) httpUrl.openConnection();
                        // 不进行主机名确认
                        ((HttpsURLConnection) httpUrlCon).setHostnameVerifier(DO_NOT_VERIFY);
                        break;
                    default:
                        break;
                }

                // set  http  configure
                httpUrlCon.setConnectTimeout(CONNECTION_TIMEOUT);
                //数据传输超时时间，很重要，必须设置。
                httpUrlCon.setReadTimeout(READ_TIMEOUT);
                // 向连接中写入数据
                httpUrlCon.setDoInput(true);
                // 从连接中读取数据
                httpUrlCon.setDoOutput(true);
                // 禁止缓存
                httpUrlCon.setUseCaches(false);
                httpUrlCon.setInstanceFollowRedirects(true);
                httpUrlCon.setRequestProperty("Charset", "UTF-8");

                switch (mType) {
                    case GET:
                        // 设置请求类型为
                        httpUrlCon.setRequestMethod("GET");
                        break;
                    case POST:
                        // 设置请求类型为
                        httpUrlCon.setRequestMethod("POST");
                        //设置特定的属性
                        httpUrlCon.setRequestProperty("Content-Type", "application/json");
                        // 获取输出流
                        DataOutputStream out = new DataOutputStream(httpUrlCon.getOutputStream());
                        // 将要传递的数据写入数据输出流,不要使用out.writeBytes(param); 否则中文时会出错
                        out.write(mParams.getBytes("utf-8"));
                        out.flush(); // 输出缓存
                        out.close(); // 关闭数据输出流
                        break;
                    default:
                        break;

                }

                httpUrlCon.connect();

                //check the result of connection
                KLog.d("返回状态码：",httpUrlCon.getResponseCode());
                if (HttpURLConnection.HTTP_OK == httpUrlCon.getResponseCode()) {
                    BufferedInputStream bis = new BufferedInputStream(httpUrlCon.getInputStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len;
                    byte [] bytes = new byte[1024];
                    //TODO 疑问 baos的flush作用  2019.7.12
                    while ((len = bis.read(bytes)) != -1){
                        baos.write(bytes,0,len);
                        baos.flush();
                    }

                    result = baos.toString();
                    bis.close();
                    baos.close();

                }
            }
            catch (Exception e) {
                e.printStackTrace();
                //如果需要处理超时，可以在这里写
                KLog.d("异常: " ,e.toString());
            }
            finally {
                if (httpUrlCon != null) {
                    httpUrlCon.disconnect(); // 断开连接
                }
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mIHttpCallback.onResponse(result);
        }
    }



    //TODO 关注一波 https证书
     class MytmArray implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // return null;
            return new X509Certificate[] {};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // TODO Auto-generated method stub

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // TODO Auto-generated method stub
            // System.out.println("cert: " + chain[0].toString() + ", authType: "
            // + authType);
        }
    }


}
