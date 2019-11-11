package com.qmkj.niaogebiji.common.net.response;


import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-03
 * 描述:处理后台数据
 *
 */
public class HttpResponse<T> extends BaseBean {


    /**
     *       基本上后台返回的数据格式如下：
     * {
     *           "success":true,
                 "code":200,
                 "msg":"成功",
                 "Data":[]
        }
     或

     {
         "success":true,
         "code":200,
         "msg":"成功",
         "Data":{} -- 默认没有数据返回的时候
     }

     T 泛型是在运行时动态的获取传递过来的参数

     */

    // TODO: 2019-08-20 鸟哥笔记
    private String return_code;
    private String return_msg;
    private T return_data;


    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public T getReturn_data() {
        return return_data;
    }

    public void setReturn_data(T return_data) {
        this.return_data = return_data;
    }


}
