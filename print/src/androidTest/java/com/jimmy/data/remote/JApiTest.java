package com.jimmy.data.remote;

import android.util.Log;

import org.junit.Test;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JApiTest {

    private static final String TAG = "JApiTest";

    @Test
    public void testGetItems() {
        JApi jApi = new JApi();
        Call<ResponseBody> call = jApi.getItems("10108481", "888", 300, "", "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String s = response.body().toString();
                Log.d(TAG, s);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                for (int i = 0; i <= 1; i++) {
                    Log.d(TAG, "i = " + i);
                }
                t.printStackTrace();
            }
        });
    }
}