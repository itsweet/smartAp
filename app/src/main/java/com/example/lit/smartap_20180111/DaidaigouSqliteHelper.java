package com.example.lit.smartap_20180111;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lit.smartap_20180111.core.MyContentProvider;

import java.util.Arrays;

/**
 * Created by admin on 2017/9/15.
 */

public class DaidaigouSqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "daidai_db";// 数据库的名字
    private static final int DATABASEVERSION = 1;// 版本号
    public static final String TABLE_NAME = "daidai_table";// 表名
    public static final String CUSTOMER_ADDRESS = "CustomerAddress";// 表名

    private SQLiteDatabase db;// 数据库
    private static final String TAG = "daidaiDataBase";

    // 字段
    public static final String ID = "_id";
    public static final String goodsName = "db_goodsname";
    public static final String goodsCategory = "db_goodscategory";
    public static final String Customer = "db_customer";
    public static final String goodsNumber = "db_goodsnumber";
    public static final String SumgoodsNumber = String.format("SUM(%s)", goodsNumber);
    public static final String remarks="db_remarks";
    public static final String goodsPrice="db_goodsprice";
    public static final String sellingPrice="db_sellingprice";
    public static final String SumsellingPrice= String.format("SUM(%s)", sellingPrice);
    public static final String purchasePrice="db_purchaseprice";
    public static final String exchangeRate="db_exchangerate";
    public static final String profit="db_profit";
    public static final String address="db_address";
    public static final String mobile="db_mobile";
    public static final String addressName="db_address_name";
    public static final String buyed="db_buyed";
    public static final String payed="db_payed";
    public static final String sended="db_sended";
    public static final String sellTime="db_selltime";
    private static DaidaigouSqliteHelper mInstance;
    Context mcontext;

    public DaidaigouSqliteHelper(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASEVERSION);
        // 打开或新建数据库(第一次时创建)获得SQLiteDatabase对象，为了读取和写入数据
        db = this.getWritableDatabase();
        mcontext = context;
        Log.i(TAG, "DaidaigouSqliteHelper: create db");
    }

    public static DaidaigouSqliteHelper getInstance(Context context){
        if (mInstance==null){
            synchronized (DaidaigouSqliteHelper.class){
                if (mInstance==null){
                    mInstance=new DaidaigouSqliteHelper(context);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onCreate()db");
        // 创建表的sql语句
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + goodsName + " TEXT,"
                + goodsCategory + " TEXT," + Customer + " TEXT," + goodsNumber
                + " INTEGER," + remarks + " TEXT," + goodsPrice + " FLOAT,"
                + sellingPrice+" FLOAT," +purchasePrice+" FLOAT,"+exchangeRate
                +" FLOAT,"+profit+" FLOAT,"+buyed+" BIT,"+payed+" BIT,"+sended+" BIT,"
                +sellTime+" TIMESTAMP)";
        String sqlCustomer="CREATE TABLE "+CUSTOMER_ADDRESS+" ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT," +Customer+" TEXT,"+address
                +" TEXT,"+mobile+" INTEGER,"+addressName+" TEXT)";
        db.execSQL(sql);
        //Log.i(TAG, "onCreate: finish exec sql");
        db.execSQL(sqlCustomer);
        //Log.i(TAG, "onCreate: finish exec sqlCustomer");

    }
    //更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.i(TAG, " onUpgrade() ");
        // 删除表的SQL
        //String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        //db.execSQL(sql);
        db.execSQL("DROP TABLE IF EXISTS "+CUSTOMER_ADDRESS);

        onCreate(db);
    }
    //关闭数据库
    @Override
    public synchronized void close() {
        // TODO Auto-generated method stub
        Log.i(TAG, "close()");
        db.close();
        super.close();
    }
    //查询指定ID数据
    public Cursor searchID(String s){
        return searchID(new String[]{s});
    }

    public Cursor searchID(String[] s)
    {
        Cursor cur;
        String[] columns={
                "*"
        };
        String searchselection=ID+"= ?";
        String[] searchselectionargs=s;
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }

    public static String getCursor(Cursor cursor){
        int num = cursor.getColumnCount();
        StringBuffer tostring = new StringBuffer();
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            tostring.append("{");
            for (int i = 0;i<num ;i++) {
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                tostring.append("["+name+"="+value+"]");
            }
            tostring.append("}\n");
        }
        return tostring.toString();
    }

    // 查询所有的数据 ，返回Cursor对象(按照id的升序排列)
    public Cursor searchAllData()
    {
        //asc是升序 ,desc为降序（默认为asc）
        Cursor temp=search(TABLE_NAME, null, null, null, null, null, DaidaigouSqliteHelper.ID );
        Log.i(TAG, " searchAllData: "+temp);
        return temp;
    }
    Cursor searchAll_orderbyCustomer(){
        return search(TABLE_NAME,null,null,null,null,null, DaidaigouSqliteHelper.Customer);
    }

    Cursor searchAll_orderbyCategory(){
        return search(TABLE_NAME,null,null,null,null,null, DaidaigouSqliteHelper.goodsCategory);
    }

    private Cursor temp ;
    synchronized Cursor search(final String table_name , final String[] columns, final String selection, final String[] selectionArgs, final String groupBy, final String having,
                               final String orderBy){
        /*
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask futureTask = new FutureTask(new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                return db.query(TABLE_NAME,null,null,null,null,null,DaidaigouSqliteHelper.Customer);
            }
        });
        executorService.execute(futureTask);
        if (futureTask.isDone()){
        */
        Log.i(TAG, "search: columns:"+ Arrays.deepToString(columns)+" selection:"+selection+" arg"+ Arrays.deepToString(selectionArgs));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            temp = db.query(table_name, columns, selection, selectionArgs, groupBy, having, orderBy + " ASC");
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return temp;
    }
    //插入数据
    public long insertData(String insert_goods_name,
                           String insert_customer,
                           String insert_goods_category,
                           String insert_goods_number,
                           String insert_remarks,
                           String insert_selling_price,
                           String insert_purchase_price,
                           String insert_exchange_rate,
                           String insert_profit,
                           String insert_address,
                           String insert_mobile,
                           String insert_address_name,
                           String insert_selltime
    )
    {
        Log.i(TAG, "insertData: start");
        int insert_buyed=0;
        int insert_payed=0;
        int insert_sended=0;
        ContentValues values=new ContentValues();
        //Log.i(TAG, "insertData: value=contentvalues");
        values.put(DaidaigouSqliteHelper.goodsName, insert_goods_name);
        Log.i(TAG, "insertData: insert goodscategory"+ insert_goods_category);
        values.put(DaidaigouSqliteHelper.goodsCategory, insert_goods_category);
        values.put(DaidaigouSqliteHelper.Customer, insert_customer);
        values.put(DaidaigouSqliteHelper.goodsNumber, insert_goods_number);
        values.put(DaidaigouSqliteHelper.remarks, insert_remarks);
        values.put(DaidaigouSqliteHelper.sellingPrice,insert_selling_price);
        values.put(DaidaigouSqliteHelper.purchasePrice, insert_purchase_price);
        values.put(DaidaigouSqliteHelper.exchangeRate, insert_exchange_rate);
        values.put(DaidaigouSqliteHelper.profit, insert_profit);
        values.put(DaidaigouSqliteHelper.buyed,insert_buyed);
        values.put(DaidaigouSqliteHelper.payed,insert_payed);
        values.put(DaidaigouSqliteHelper.sended,insert_sended);
        values.put(DaidaigouSqliteHelper.sellTime,insert_selltime);
        return insertData(values);
        /*
        ContentValues customeraddress=new ContentValues();
        customeraddress.put(DaidaigouSqliteHelper.Customer,insert_customer);
        customeraddress.put(DaidaigouSqliteHelper.address, insert_address);
        customeraddress.put(DaidaigouSqliteHelper.mobile, insert_mobile);
        customeraddress.put(DaidaigouSqliteHelper.addressName, insert_address_name);
        long row_1=db.insert(CUSTOMER_ADDRESS,null,customeraddress);
        Log.i(TAG, "insertData row1="+row_1);
        */
    }

    public long insertData(ContentValues values){
        long row=db.insert(TABLE_NAME, null, values);
        Log.i(TAG, "insertData row="+row);
        return row;
    }

    //购物清单，按照客户分类
    public Cursor getshoplist_Customer()
    {
        Cursor cur;
        String[] columns={
                ID,
                Customer,
                "SUM("+purchasePrice+")",
                "SUM("+goodsNumber+")",
        };
        String searchselection=buyed+"= ?";
        String[] searchselectionargs={"0"};
        String groupBy=Customer;
        String havingstr=null;
        String orderBy=null;
        try {
            //db = getReadableDatabase();
            cur = search(TABLE_NAME, columns, searchselection, searchselectionargs, groupBy, havingstr, orderBy);
            //db.close();
        }catch (Exception e){
            Log.e(TAG, "getshoplist_Customer: error:"+e,null );
            return null;
        }
        return cur;
    }

    //购物清单，按照分类排序
    public Cursor getshoplist_goodsCategory()
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsCategory,
                "SUM("+purchasePrice+")",
                "SUM("+goodsNumber+")",
        };
        String searchselection=buyed+"= ?";
        String[] searchselectionargs={"0"};
        String groupBy=goodsCategory;
        String havingstr=null;
        String orderBy=goodsCategory;
        try {
            cur = search(TABLE_NAME, columns, searchselection, searchselectionargs, groupBy, havingstr, orderBy);
            //db.close();
        }catch (Exception e){
            cur = null;
            e.printStackTrace();
        }
        return cur;
    }

    //查询未能完成购买的商品下 价格 和 数量列表group by类别
    public Cursor getgoodsnumberlist_category(String goodscategory)
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsCategory,
                goodsName,
                Customer,
                goodsNumber,
                remarks,
                purchasePrice
        };
        String searchselection=goodsCategory+"= ?"+" AND "+buyed+"= ?";
        String[] searchselectionargs={goodscategory,"0"};
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cur = search(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }
    //查询未能完成购买的商品下 价格 和 数量列表group by用户名
    public Cursor getgoodsnumberlist_customer(String customer)
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsCategory,
                goodsName,
                Customer,
                goodsNumber,
                remarks,
                purchasePrice
        };
        String searchselection=Customer+"= ?"+" AND "+buyed+"= ?";
        String[] searchselectionargs={customer,"0"};
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cur = search(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }

    //查询列表用于收款发货界面，以用户名分类排序
    public Cursor getcustomerlistforbuyed(){
        Cursor cursor;
        String[] columns={
                ID,
                Customer,
                SumgoodsNumber,
        };
        String searchselection= String.format("%s= ? AND (%s = ? OR %s= ?)",buyed,sended,payed);
        String[] searchselectionargs={"1","0","0"};
        cursor = db.query(TABLE_NAME, columns, searchselection, searchselectionargs, Customer,
                null, Customer);
        return cursor;
    }

    public Cursor getcustomerallprice(String customer){
        Cursor cursor;
        String[] columns = {
                ID,
                Customer,
                sellingPrice,
                goodsNumber
        };
        String searchselection = String.format("%s= ? AND (%s = ? OR %s= ? )AND %s= ?",buyed,sended,payed,Customer);
        String[] searchselectionargs={"1","0","0",customer};
        cursor = search(TABLE_NAME, columns, searchselection, searchselectionargs, Customer,
                null, Customer);
        return cursor;
    }

    //查询列表用于收款发货界面，特定用户下购买的商品数量列表，
    public Cursor getgoodsandnumberlistforbuyed(String customer)
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsName,
                goodsNumber,
                remarks,
                sellingPrice,
                payed,
                sended
        };
        //String searchselection=buyed+"= ?"+" AND "+sended+"= ?"+" AND "+payed+"= ?";
        String searchselection= String.format("%s= ? AND %s= ? AND (%s= ? OR %s= ?)", DaidaigouSqliteHelper.Customer,buyed,sended,payed);
        String[] searchselectionargs={customer,"1","0","0"};
        String groupBy=null;
        String havingstr=null;
        String orderBy=goodsName;
        cur =search(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }



    public Cursor getlistfortime(String starttime, String endtime, String string)
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsName,
                Customer,
                goodsNumber,
                remarks,
                sellingPrice,
                purchasePrice,
                goodsCategory
        };

        String searchselection=buyed+"= ?"+" AND "+payed+"= ?"+" AND "+sended+"= ?"+" AND "+sellTime+">= ?" +" AND "
                +sellTime+"<= ? "+" AND ("+goodsCategory+" LIKE ? OR "+goodsName
                +" LIKE ? OR "+Customer+" LIKE ?)";
        //String searchselection=String.format("%s=? AND %s=? AND %s=? AND %s >=? AND %s <=? AND (%s LIKE ? OR %s LIKE ? OR %s LIKE ?",
          //      buyed,payed,sended,sellTime,sellTime,goodsCategory,goodsName,Customer);
        String[] searchselectionargs={"1","1","1",starttime,endtime,"%"+string+"%","%"+string+"%","%"+string+"%"};
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,null,null,Customer);
        return cur;
    }
    //
    public Cursor getgoodslistforpayed()
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsCategory,
                goodsName,
                Customer,
                goodsNumber,
                remarks,
        };
        String searchselection=payed+"= ?";
        String[] searchselectionargs={"1"};
        String groupBy=null;
        String havingstr=null;
        String orderBy=Customer;
        cur =search(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }

    //统计已购买并且已付款的货品总价
    public String getSellingPrice(){
        Cursor cursor;
        String[] columns={
                ID,
                goodsNumber,
                sellingPrice,
        };
        //String searchselection=payed+"= 1"+" AND "+buyed+">= 1"+" AND" ;
        String searchselection= String.format("%s = 1 AND %s = 1 AND %s =0",buyed,payed,sended);
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cursor =search(TABLE_NAME,columns,searchselection,null,groupBy,havingstr,orderBy);
        if (cursor.getCount()!=0 && cursor.moveToFirst()){
            float profit=0;
            do {
                int num=cursor.getInt(cursor.getColumnIndexOrThrow(goodsNumber));
                float prices=cursor.getFloat(cursor.getColumnIndexOrThrow(sellingPrice));
                profit+= Utils.mul(prices,num);
            }
            while (cursor.moveToNext());
            return String.valueOf(profit);
        }else {
            //cursor.close();
            return null;
        }
    }
    /*
    Cursor getAllGoodsPrice(){
        Cursor cursor;
        String[] columns={
                ID,
                purchasePrice,
                goodsNumber
        };
        String searchselection=String.format("%s = 0 AND (%s = 0 OR %s =0)",buyed,payed,sended);
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cursor =db.query(TABLE_NAME,columns,searchselection,null,groupBy,havingstr,orderBy);
        return cursor;
    }
    */

    //购物清单界面 统计所有商品进价
    public String getAllGoodsPrice(){
        Cursor cursor;
        String[] columns={
                ID,
                purchasePrice,
                goodsNumber
        };
        String searchselection= String.format("%s = 0 AND (%s = 0 OR %s =0)",buyed,payed,sended);
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cursor =search(TABLE_NAME,columns,searchselection,null,groupBy,havingstr,orderBy);
        if (cursor.getCount()!=0 && cursor.moveToFirst()){
            float allgoodsPrice=0;
            do {
                int num=cursor.getInt(cursor.getColumnIndexOrThrow(goodsNumber));
                float prices=cursor.getFloat(cursor.getColumnIndexOrThrow(purchasePrice));
                allgoodsPrice+= Utils.mul(prices,num);
            }
            while (cursor.moveToNext());
            cursor.close();
            return String.valueOf(allgoodsPrice);
        }else {
            cursor.close();
            return "";
        }
    }


    //统计制定时间段内利润
    public String getcalcprofit(String starttime, String endtime)
    {
        Cursor cur;
        String[] columns={
                ID,
                "SUM("+profit+")",
        };
        String searchselection=sended+"= ?"+" AND "+sellTime+">= ?" +" AND "+sellTime+"<= ?";
        String[] searchselectionargs={"1",starttime,endtime};
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        String profit=null;
        if (cur.getCount()==1) {
            cur.moveToFirst();
            profit =cur.getString(cur.getColumnIndexOrThrow("SUM("+ DaidaigouSqliteHelper.profit+")"));
        }
        cur.close();
        return profit;
    }

    //通过ID查询所有信息，并返回ContentValues

    public ContentValues valueSpecificId(Long id)
    {
        ContentValues values=new ContentValues();
        Cursor cur;
        String[] columns={
                "*"
        };
        String searchselection=ID+"= ?";
        String[] searchselectionargs={Long.toString(id)};
        cur=db.query(TABLE_NAME,columns,searchselection,searchselectionargs,null,null,null);
        if (cur.getCount()==1)
        {
            cur.moveToFirst();
            values.put(ID,cur.getInt(cur.getColumnIndex(ID)));
            String s =cur.getString(cur.getColumnIndex(goodsName));
            values.put(goodsName,s);
            values.put(goodsCategory,cur.getString(cur.getColumnIndex(goodsCategory)));
            values.put(goodsNumber,cur.getString(cur.getColumnIndex(goodsNumber)));
            values.put(Customer,cur.getString(cur.getColumnIndex(Customer)));
            values.put(remarks,cur.getString(cur.getColumnIndex(remarks)));
            values.put(sellingPrice,cur.getString(cur.getColumnIndex(sellingPrice)));
            values.put(purchasePrice,cur.getString(cur.getColumnIndex(purchasePrice)));
            values.put(exchangeRate,cur.getString(cur.getColumnIndex(exchangeRate)));
            values.put(profit,cur.getString(cur.getColumnIndex(profit)));
            values.put(buyed,cur.getString(cur.getColumnIndex(buyed)));
            values.put(payed,cur.getString(cur.getColumnIndex(payed)));
            values.put(sended,cur.getString(cur.getColumnIndex(sended)));
        }
        cur.close();
        return values;
    }

    //查询所有用户名customer
    public Cursor searchcustomer(){
        Cursor cursor=null;
        String[] columns={
                ID,
                Customer
        };
        String[] searchselectionargs={};
        cursor=search(TABLE_NAME,columns,null,null,Customer,null,null);
        return cursor;
    }

    //查询所有商品名goodsname
    public Cursor searchgoodsname(){
        Cursor cursor=null;
        String[] columns={
                ID,
                goodsName
        };
        String[] searchselectionargs={};
        cursor=search(TABLE_NAME,columns,null,null,goodsName,null,null);
        return cursor;
    }
    //查询所有分类Category
    public Cursor searchCategory(){
        Cursor cursor=null;
        String[] columns={
                ID,
                goodsCategory
        };
        String[] searchselectionargs={};
        cursor=search(TABLE_NAME,columns,null,null,goodsCategory,null,null);
        return cursor;
    }

    //查询指定的信息
    public Cursor searchSpecific(String s, String tablename)
    {
        Cursor cur=null;
        String[] columns_goods={
                ID,
                goodsName,
                goodsCategory,
                Customer,
                goodsNumber,
                remarks,
                sellingPrice,
                purchasePrice,
                exchangeRate,
                profit

        };
        String[] columns_customer={
                Customer,
                address,
                mobile,
                addressName
        };
        String searchselection=goodsCategory+" like ? or "+goodsName+" like ? or "+Customer+" like ?";
        String[] searchselectionargs={"%"+s+"%"};
        if (tablename.equals(TABLE_NAME)) {
            cur = db.query(TABLE_NAME, columns_goods, searchselection, searchselectionargs, null, null, null);
        }
        else if (tablename.equals(CUSTOMER_ADDRESS))
        {
            cur =db.query(CUSTOMER_ADDRESS,columns_customer,searchselection,searchselectionargs,null,null,null);
        }
        Log.i("searchSpecific()", " cur.getCount()="+cur);
        return cur;
    }

    //修改数据
    synchronized private int upDatainfo(ContentValues values, String where, String[] whereArgs){
        //String whereClause=DaidaigouSqliteHelper.ID+" = ? ";
        //String[] whereArgs={values.getAsString(DaidaigouSqliteHelper.ID)};
        int rowaffected=mcontext.getContentResolver().update(MyContentProvider.Uri_update
                ,values,where,whereArgs);
        //int rowaffected =db.update(TABLE_NAME, values, whereClause, whereArgs);
        return rowaffected;
    }
    //修改数据  修改单个ID 的数据
    public int upDatainfo(ContentValues values){
        String whereClause= DaidaigouSqliteHelper.ID+" = ? ";
        String[] whereArgs={values.getAsString(DaidaigouSqliteHelper.ID)};
        int rowaffected=upDatainfo(values,whereClause,whereArgs);
        //int rowaffected =db.update(TABLE_NAME, values, whereClause, whereArgs);
        return rowaffected;
    }
    public void upDatainfo(Long id,
                           String goodname,
                           String goodsCategory,
                           String number,
                           String customer,
                           String remarks,
                           String sellingPrice,
                           String purchasePrice,
                           String exchangeRate,
                           String profit,
                           String sellTime,
                           String buyed,
                           String payed
                           )
    {
        ContentValues values=new ContentValues();
        values.put(DaidaigouSqliteHelper.ID,id);
        values.put(DaidaigouSqliteHelper.goodsName, goodname);
        values.put(DaidaigouSqliteHelper.goodsNumber, number);
        values.put(DaidaigouSqliteHelper.Customer, customer);
        values.put(DaidaigouSqliteHelper.goodsCategory,goodsCategory);
        values.put(DaidaigouSqliteHelper.remarks,remarks);
        values.put(DaidaigouSqliteHelper.sellingPrice,sellingPrice);
        values.put(DaidaigouSqliteHelper.purchasePrice,purchasePrice);
        values.put(DaidaigouSqliteHelper.exchangeRate,exchangeRate);
        values.put(DaidaigouSqliteHelper.profit,profit);
        values.put(DaidaigouSqliteHelper.sellTime,sellTime);
        values.put(DaidaigouSqliteHelper.buyed,buyed);
        values.put(DaidaigouSqliteHelper.payed,payed);
        int rowaffected = upDatainfo(values);
        Log.i(TAG, "upDatainfo()  rowaffected="+rowaffected);
    }

    //修改购买属性
    public int upDataBuyed(ContentValues values){
        return upDatainfo(values);
    }

    public int upDataBuyed(Long id,
                           String buyed)
    {
        ContentValues values=new ContentValues();
        //String whereClause=DaidaigouSqliteHelper.ID+" = ? ";
        //String whereArgs[]={Long.toString(id)};
        values.put(DaidaigouSqliteHelper.ID,id);
        values.put(DaidaigouSqliteHelper.buyed,buyed);
        int rowaffected =upDatainfo( values);
        return rowaffected;
    }

    //回退所有购买属性
    public int upDataBackBuyed(){
        ContentValues values=new ContentValues();
        values.put(DaidaigouSqliteHelper.buyed,"0");
        String whereClause= String.format("%s =1 AND (%s =0 OR %s =0)",buyed,sended,payed);
        String whereArgs[]={"1"};
        int rowaffected=upDatainfo(values,whereClause,null);
        return rowaffected;
    }

    int upDataPayed(ContentValues values){
        return db.update(TABLE_NAME,values, DaidaigouSqliteHelper.ID+"="+
                values.getAsString(DaidaigouSqliteHelper.ID),null);
    }

    //修改付款属性
    public int upDataPayed(Long id, String payed)
    {
        ContentValues values=new ContentValues();
        values.put(DaidaigouSqliteHelper.payed,payed);
        String whereClause= DaidaigouSqliteHelper.ID+" = ? ";
        String whereArgs[]={Long.toString(id)};
        int rowaffected =upDatainfo( values, whereClause, whereArgs);
        return rowaffected;
    }

    //修改发货属性并更新时间
    //注意 selltime要主界面程序传进来，不可在数据库使用System.currentTimeMillis获取，两处地方生成的时间不同
    public int upDataSended(Long id, String sended, String sellTime)
    {
        ContentValues values=new ContentValues();
        values.put(DaidaigouSqliteHelper.sended,sended);
        values.put(DaidaigouSqliteHelper.sellTime,sellTime);
        String whereClause= DaidaigouSqliteHelper.ID+" = ? ";
        String whereArgs[]={Long.toString(id)};
        int rowaffected =upDatainfo(values, whereClause, whereArgs);
        return rowaffected;
    }

    //删除数据
    int provider_delete(String selection, String[] selectionArgs){
        return mcontext.getContentResolver().delete(MyContentProvider.Uri_delete,selection,selectionArgs);
    }

    public int deletegoodslist(Long id)
    {
        int rowaffected =provider_delete(DaidaigouSqliteHelper.ID+"="+id, null);
        return rowaffected;

    }

    int deletegoodslist(String id){
        return deletegoodslist(Long.valueOf(id));
    }

    //判断是否存在该学生的信息
    public Cursor isHaveThisStu(int number)
    {
        String[] columns={
                DaidaigouSqliteHelper.ID,
                DaidaigouSqliteHelper.goodsName,
                DaidaigouSqliteHelper.goodsCategory,
                DaidaigouSqliteHelper.Customer
        };

        Cursor cur=db.query(TABLE_NAME, columns,  DaidaigouSqliteHelper.goodsCategory+"="+number, null, null, null, null);
        Log.i("isHaveThisStu()", " cur.getCount()="+cur.getCount());
        return cur;
    }
}
