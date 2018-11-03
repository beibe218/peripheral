package com.jimmy.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "local.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ITEM_ENTRIES =
            "CREATE TABLE " + DbPersistenceContract.ItemEntry.TABLE_NAME + " (" +
                    DbPersistenceContract.ItemEntry.COLUMN_NAME_ID + TEXT_TYPE + " PRIMARY KEY," +
                    DbPersistenceContract.ItemEntry.COLUMN_NAME_MERCHANTID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemEntry.COLUMN_NAME_RETAILPRICE + DOUBLE_TYPE +
                    " )";

    public static final String SQL_CREATE_ITEMCLS_ENTRIES =
            "CREATE TABLE " + DbPersistenceContract.ItemClsEntry.TABLE_NAME + " (" +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_ID + TEXT_TYPE + " PRIMARY KEY," +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_MERCHANTID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_PARENTID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_DELETEFLAG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_TSFLAG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_STOCKFLAG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.ItemClsEntry.COLUMN_NAME_CLSTYPE + TEXT_TYPE +
                    " )";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "sql create item entries " + SQL_CREATE_ITEM_ENTRIES);
        Log.d(TAG, "sql create itemcls entries " + SQL_CREATE_ITEMCLS_ENTRIES);
        db.execSQL(SQL_CREATE_ITEM_ENTRIES);
        db.execSQL(SQL_CREATE_ITEMCLS_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
