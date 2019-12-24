package com.qmkj.niaogebiji.common.net.helper;

import com.chemanman.lib_mgson.MGson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-23
 * 描述:
 */
public class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        //ResponseData中的流只能使用一次，我们先将流中的数据读出在byte数组中。这个方法中已经关闭了ResponseBody,所以不需要再关闭了
        byte[] valueBytes = value.bytes();
        JsonReader jsonReader = null, errorJsonReader = null;
        try {//try中的为正常流程代码
            InputStreamReader charReader = new InputStreamReader(new ByteArrayInputStream(valueBytes), "UTF-8");//后面的charset根据服务端的编码来定
            jsonReader = gson.newJsonReader(charReader);//原来是使用value.charStream
            T obj = adapter.read(jsonReader);


//            if (obj instanceof HttpResponse) {
//                if (((HttpResponse) obj).getE_no() == 100007) {
//                    InputStreamReader sevencharReader = new InputStreamReader(new ByteArrayInputStream(valueBytes), "UTF-8");//后面的charset根据服务端的编码来定
//                    jsonReader = gson.newJsonReader(sevencharReader);
//                    UserBanMyResult myResult = MGson.newGson().fromJson(jsonReader, UserBanMyResult.class);
//                    return (T) myResult;
//                }
//            }
            return obj;


        } catch (JsonSyntaxException e) {//当catch到这个错误说明gson解析错误，基本肯定是服务端responseData的问题，此时我们自己解析
            e.printStackTrace();
            InputStreamReader errorReader = new InputStreamReader(new ByteArrayInputStream(valueBytes), "UTF-8");//后面的charset根据服务端的编码来定
            errorJsonReader = gson.newJsonReader(errorReader);
//
//            MyResult myResult = new MyResult();
//            // myResult.getData() = null;//设为null
//
//            errorJsonReader.beginObject();
//            while (errorJsonReader.hasNext()) {
//                String nextName = errorJsonReader.nextName();
//                if ("e_no".equals(nextName)) {
//                    myResult.setE_no(errorJsonReader.nextInt());
//                } else {
//                    errorJsonReader.skipValue();//跳过所有其他的值
//                }
//            }
//            errorJsonReader.endObject();
//
//            return (T) myResult;
            return null;
        } finally {
            if (jsonReader != null)
                jsonReader.close();
            if (errorJsonReader != null)
                errorJsonReader.close();
        }
    }

}
