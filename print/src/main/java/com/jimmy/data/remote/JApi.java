package com.jimmy.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JApi {

    private final JService jService;

    public JApi() {
        jService = new Retrofit.Builder()
                .baseUrl("http://www.yingqianpos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build()
                .create(JService.class);
    }

    public Call<ResponseBody> getItems(String merchantId, String branchId, int pageSize, String maxFlowId, String hashCode) {
        return jService.getItems(merchantId, branchId, pageSize, maxFlowId, hashCode);
    }
}
