package com.jimmy.data.remote;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import com.jimmy.data.db.model.ItemCls;
import com.jimmy.data.local.DbHelper;
import com.jimmy.data.local.DbPersistenceContract;
import com.jimmy.data.remote.api.SyncApi;
import com.jimmy.data.remote.model.HttpResult;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@RunWith(AndroidJUnit4.class)
public class JApiTest {
    private static final String TAG = "JApiTest";
    private Function<Cursor, ItemCls> itemClsFunction;

    private BriteDatabase getBriteDb() {
        DbHelper dbHelper = new DbHelper(InstrumentationRegistry.getTargetContext());
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase briteDb = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        itemClsFunction = this::getItemCls;
        return briteDb;
    }

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
                            saveItemCls(item);
                        }
                    }
                });
    }

    private void saveItemCls(ItemCls cls) {
        ContentValues values = new ContentValues();
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_ID, cls.getId());
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_MERCHANTID, cls.getMerchantId());
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_NAME, cls.getName());
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_PARENTID, cls.getParentId());
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_DELETEFLAG, cls.getDeleteFlag());
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_TSFLAG, cls.gettSFlag());
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_STOCKFLAG, cls.getStockFlag());
        values.put(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_CLSTYPE, cls.getClsType());
        getBriteDb().insert(DbPersistenceContract.ItemClsEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Test
    public void testGetItemCls() {
        String[] projection = {
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_ID,
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_MERCHANTID,
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_NAME,
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_PARENTID,
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_DELETEFLAG,
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_TSFLAG,
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_STOCKFLAG,
                DbPersistenceContract.ItemClsEntry.COLUMN_NAME_CLSTYPE,
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), DbPersistenceContract.ItemClsEntry.TABLE_NAME);
        Flowable<List<ItemCls>> listFlowable = getBriteDb().createQuery(DbPersistenceContract.ItemClsEntry.TABLE_NAME, sql)
                .mapToList(itemClsFunction)
                .toFlowable(BackpressureStrategy.BUFFER);

        listFlowable.subscribe(itemCls -> itemCls.forEach(item -> Log.d(TAG, item.getName() + "," + item.getId())));
    }

    private ItemCls getItemCls(Cursor c) {
        String id = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_ID));
        String merchantId = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_MERCHANTID));
        String name = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_NAME));
        String parentId = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_PARENTID));
        String deleteFlag = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_DELETEFLAG));
        String tsFlag = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_TSFLAG));
        String stockFlag = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_STOCKFLAG));
        String clsType = c.getString(c.getColumnIndexOrThrow(DbPersistenceContract.ItemClsEntry.COLUMN_NAME_CLSTYPE));

        ItemCls cls = new ItemCls();
        cls.setId(id);
        cls.setMerchantId(merchantId);
        cls.setName(name);
        cls.setParentId(parentId);
        cls.setDeleteFlag(deleteFlag);
        cls.settSFlag(tsFlag);
        cls.setStockFlag(stockFlag);
        cls.setClsType(clsType);
        return cls;
    }

}