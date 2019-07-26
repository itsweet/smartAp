package com.example.lit.smartap_20180111.core;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lit.smartap_20180111.DaidaigouSqliteHelper;

public class MyContentProvider extends ContentProvider {
    //用于存放并匹配URI标识信息，一般在静态代码块中对信息进行初始化
    private static UriMatcher sUriMatcher;
    //数据库
    private DaidaigouSqliteHelper sqliteHelper;

    //URI
    public static final String INSERT = "daidaigou/insert";
    public static final String DELETE = "daidaigou/delete";
    public static final String UPDATE = "daidaigou/update";
    public static final String QUERY = "daidaigou/query";
    public static final String SCHEME = "content";
    public static final String AUTHORITY = "com.example.lit.daidaigou.provider";
    public static final Uri Uri_insert = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY)
            .path(INSERT).build();
    public static final Uri Uri_delete = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY)
            .path(DELETE).build();
    public static final Uri Uri_update = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY)
            .path(UPDATE).build();
    public static final Uri Uri_query = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY)
            .path(QUERY).build();
    //数据库名
    private static final String TABLE_NAME = DaidaigouSqliteHelper.TABLE_NAME;
    //uri匹配成功的返回码
    public static final int INSERT_CODE = 1000;
    public static final int DELETE_CODE = 1001;
    public static final int UPDATE_CODE = 1002;
    public static final int QUERYALL_CODE = 1003;
    public static final int QUERYONE_CODE = 1004;

    String TAG = "MyContentProvider";
    //public static final String QUERYONE = "daidaigou/#";
    //query 字符串
    /*
    enum Query_name {
        shoplistbyCustomer,
        shoplistbygoodsCategory,
        searchID,
        searchgoodsname,
        searchCategory,
        searchcustomer;

        Query_name(){
            this.queryvalue = this.name();
        }
        String queryvalue;

        String getQueryvalue(){
            return this.queryvalue;
        }

        Uri getUrivalue(){
            return new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(QUERY)
                    .query(queryvalue).build();
        }

    }
    */
    enum Update_name{
        info,
        payed,
        buyed;

        String value;

        Update_name(){
            this.value = this.name();
        }

        Uri getUrivalue(){
            return new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(UPDATE)
                    .query(value).build();
        }
    }

    //静态代码块
    static {
        //NO_MATCH:没有匹配的时候返回状态-1
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, INSERT, INSERT_CODE);
        sUriMatcher.addURI(AUTHORITY, DELETE, DELETE_CODE);
        sUriMatcher.addURI(AUTHORITY, UPDATE, UPDATE_CODE);
        //sUriMatcher.addURI(AUTHORITY, QUERY, Query_shoplistbycustomer);
        sUriMatcher.addURI(AUTHORITY, QUERY, QUERYALL_CODE);
        //sUriMatcher.addURI(AUTHORITY, QUERYONE, QUERYONE_CODE);
        //sUriMatcher.addURI(AUTHORITY, Query_shoplistbycustomer, QUERYALL_CODE);
    }

    @Override
    public boolean onCreate() {
        sqliteHelper = DaidaigouSqliteHelper.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        int count = db.update(DaidaigouSqliteHelper.TABLE_NAME,values,selection,selectionArgs);
        //db.close();
        //String uri_query = uri.getQuery();
        /*
        switch (Update_name.valueOf(uri_query)){
            case info:
                count = sqliteHelper.upDatainfo(values);
                break;
            case buyed:
                count = sqliteHelper.upDataBuyed(values);
                break;
            case payed:
                count = sqliteHelper.upDataPayed(values);
                break;
            default:
                count = 0;
        }
        */
        //notifychange_all();
        getContext().getContentResolver().notifyChange(Uri_query,null);
        return count;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        long id = sqliteHelper.insertData(values);
        //db.close();
        getContext().getContentResolver().notifyChange(Uri_query,null);
        Log.i(TAG, "insert: notifychange:"+Uri_query.toString());
        //notifychange_all();

        return ContentUris.withAppendedId(uri, id);
    }
    /*
    void notifychange_all(){
        try {
            ContentResolver resolver = getContext().getContentResolver();
            for (Query_name qname: Query_name.values()
                 ) {
                resolver.notifyChange(qname.getUrivalue(),null);
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }

    }
    */

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        //int count = sqliteHelper.delete(selection,selectionArgs);
        int count = db.delete(DaidaigouSqliteHelper.TABLE_NAME,selection,selectionArgs);
        //notifychange_all();
        getContext().getContentResolver().notifyChange(Uri_query,null);
        //db.close();
        return count;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "query: uri="+uri.toString());
        String uriQuery = uri.getQuery();
        int code = sUriMatcher.match(uri);
        //Uri queryuri = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(QUERY).build();
        //queryuri.getFragment();
        Uri newuri = ContentUris.withAppendedId(uri, 1);
        long i = ContentUris.parseId(newuri);
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        Cursor cursor;
        if (code==QUERYALL_CODE){
            cursor= sqliteHelper.searchAllData();
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
            return cursor;
            //case QUERYONE_CODE: return
        }else {
            return null;
        }
        /*
        Cursor cursor;
        if (uriQuery == null) {
            cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            //db.close();
            return cursor;
        }
        switch (Query_name.valueOf(uriQuery)) {
            case shoplistbyCustomer:
                cursor = sqliteHelper.getshoplist_Customer();
                break;
            case shoplistbygoodsCategory:
                cursor = sqliteHelper.getshoplist_goodsCategory();
                break;
            case searchID:
                cursor = sqliteHelper.searchID(selectionArgs);
                break;
            case searchgoodsname:
                cursor = sqliteHelper.searchgoodsname();
                break;
            case searchCategory:
                cursor = sqliteHelper.searchCategory();
                break;
            case searchcustomer:
                cursor = sqliteHelper.searchcustomer();
                break;
            default:
                throw new IllegalArgumentException("query匹配失败：" + uri);
        }
        Log.i(TAG, "query: cursor:" + cursor.getCount());
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.i(TAG, "query: set notification");
        return cursor;
        */
        /*
        switch (mathcCode) {
            case Query_shoplistbycustomer:
                Cursor cursor = sqliteHelper.getshoplist_Customer();
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                return cursor;

            case QUERYONE_CODE:
                long parseId = ContentUris.parseId(uri);
                return db.query(TABLE_NAME, projection, "id=?", new String[]{parseId + ""}, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Uri匹配失败：" + uri);
        }
        */
    }
}
