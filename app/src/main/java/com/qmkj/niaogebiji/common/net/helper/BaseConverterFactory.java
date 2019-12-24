package com.qmkj.niaogebiji.common.net.helper;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-23
 * 描述:
 */
public class BaseConverterFactory extends Converter.Factory {

    public static BaseConverterFactory create() {
        return create(MGson.newGson());
    }

    public static BaseConverterFactory create(Gson gson) {
        return new BaseConverterFactory(gson);
    }

    private final Gson gson;

    private BaseConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, java.lang.annotation.Annotation[] parameterAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        return new MyGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          java.lang.annotation.Annotation[] parameterAnnotations, java.lang.annotation.Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
//        return new MyGsonRequestBodyConverter<>(gson, adapter);
        return null;
    }
}
