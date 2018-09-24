package com.jimmy.data.remote;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.jimmy.data.db.model.Item;
import com.jimmy.data.db.model.ItemCls;
import com.jimmy.data.remote.api.SyncApi;
import com.jimmy.data.remote.model.HttpResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@RunWith(AndroidJUnit4.class)
public class JApiTest {
    private static final String TAG = "JApiTest";

    @Test
    public void testGetItemClss() throws Exception {
        ApiManager apiManager = ApiManager.getInstance();
        SyncApi syncApi = apiManager.getSyncApi();
        syncApi.getItemClses("10108481", "888", 300, "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HttpResult<ItemCls>>() {
                    @Override
                    public void accept(HttpResult<ItemCls> itemClsHttpResult) throws Exception {
                        List<ItemCls> itemCls = itemClsHttpResult.getList();
                        for (ItemCls item : itemCls) {
                            Log.d(TAG, item.getName());
                        }
                    }
                });
    }

}