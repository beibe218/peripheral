package com.jimmy.data.remote;

import android.util.Log;

import com.jimmy.data.db.model.Item;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JApiTest {

    private static final String TAG = "JApiTest";

    @Test
    public void testGetItems() {
        JApi jApi = new JApi();
        Call<HttpResult<Item>> call = jApi.getItems("10108481", "888", 300, "", "");
        call.enqueue(new Callback<HttpResult<Item>>() {
            @Override
            public void onResponse(Call<HttpResult<Item>> call, Response<HttpResult<Item>> response) {
                HttpResult<Item> body = response.body();
                List<Item> items = body.getList();
                for (Item item : items) {
                    Log.d(TAG, item.getName());
                }
            }

            @Override
            public void onFailure(Call<HttpResult<Item>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}