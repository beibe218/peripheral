package com.jimmy.data.remote;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String baseUrl = "http://www.yingqianpos.com/";
    private final Retrofit retrofit;

    public ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
    }

    public <S> S createService(Class<S> serviceClass) {
        return this.retrofit.create(serviceClass);
    }
}
