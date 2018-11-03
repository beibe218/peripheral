package com.jimmy.data.local;

public final class DbPersistenceContract {
    private DbPersistenceContract() {
    }

    public static final class ItemEntry {
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_MERCHANTID = "merchantid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_RETAILPRICE = "retailprice";
    }

    public static final class ItemClsEntry {
        public static final String TABLE_NAME = "itemcls";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_MERCHANTID = "merchantid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PARENTID = "parentid";
        public static final String COLUMN_NAME_DELETEFLAG = "deleteflag";
        public static final String COLUMN_NAME_TSFLAG = "tsflag";
        public static final String COLUMN_NAME_STOCKFLAG = "stockflag";
        public static final String COLUMN_NAME_CLSTYPE = "clstype";
    }
}
