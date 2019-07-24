package com.example.lit.smartap_20180111;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/9/15.
 */

public class SmartAPSqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smartap_db";// 数据库的名字
    private static final int DATABASEVERSION = 1;// 版本号
    public static final String TABLE_NAME = "smartdevices";// 表名

    private SQLiteDatabase db;// 数据库
    private static final String TAG = "SmartAPSqlHelper";

    // 字段
    public static final String ID = "_id";
    /*BaseInfoFlagsDef
    public static final String transferMode = "db_transferMode";
    public static final String powersave = "db_powersave";
    public static final String scanOneChannel = "db_scanOneChannel";
    public static final String noNeighberReboot = "db_noNeighberReboot";
    public static final String uartcmdWithPwd = "db_uartcmdWithPwd";
    public static final String reserve1 = "db_reserve1";
    public static final String reserve2 = "db_reserve2";
    */
    public static final String baseInfoFlagsDef = "db_baseInfoFlagsDef";

    /*tagBaseInfoDef
    public static final String logicaltype = "db_logicaltype";
    public static final String channel = "db_channel";
    public static final String panid = "db_panid";
    public static final String shortaddr = "db_shortaddr";
    public static final String useraddr="db_useraddr";
    public static final String userpasswd="db_userpasswd";
    public static final String securekey16="db_securekey16";
    public static final String txpower="db_txpower";
    public static final String joinmode="db_joinmode";
    public static final String baudrate="db_baudrate";
    public static final String age="db_age";
    public static final String endevpollrate="db_endevpollrate";
    public static final String topologytime="db_topologytime";//只针对协调器
    public static final String io="db_io";//IO的IO配置信息，共42个字节
    */
    public static final String baseInfoDef = "db_baseInfoDef";
    public  class BaseInfoDef{

    }

    /*-- 以下是不需要存储的，只读信息
    public static final String imageType="db_imageType";
    public static final String deviceType="db_deviceType";
    public static final String macaddr="db_macaddr";
    public static final String parentaddr="db_parentaddr";
    public static final String version="db_version";
    public static final String maxbytes="db_maxbytes";
    public static final String status="db_status";
    */
    public static final String baseInfoReadOnlyDef = "db_baseInfoReadOnlyDef";

    /*
    public static final String mac="db_mac";//外部按键的MAC地址
    public static final String button="db_button";//外部按键的按键号
    public static final String ep="db_ep";//外部按键用于内部绑定时的绑定端点，当端点为0x0f的时候，说明绑定的是本节点按键
    */
    public static final String extBindingTableDef = "db_extBindingTableDef";

    /*
    public static final String rEndpoint="db_rEndpoint";//表示对方端点号，也就是对方在绑定表里的序号
    public static final String rEvt="db_rEvt";
    */
    public static final String bindingrEventDef = "db_bindingrEventDef";

    /*
    public static final String enable="db_enable";//0bit:1表示次绑定使能，可以发送绑定数据，0禁止
    public static final String online="db_online";//1表示对方在线，0表示对方掉线
    public static final String external="db_external";//1表示zigbee外部设备，0表示zigbee内网设备
    public static final String linkage="db_linkage";//1表示联动功能，0表示普通绑定
    public static final String lEvt="db_lEvt";//表示本地应用事件，0-7
    */
    public static final String bindinglEventDef = "db_bindinglEventDef";

    /*
    public static final String mode="db_mode";//触发值，0、1、2；2表示任意触发，发送取反值
    public static final String direction="db_direction";//0表示双向既发也收,1表示只发出不接收,2表示只接收不发送
    public static final String BingdingOptDef_reserve1="db_BingdingOptDef_reserve1";//3-7bit保留,暂时没使用
    */
    public static final String bindingOptDef = "db_bindingOptDef";

    /*
    public static final String tagBingdingTableReqDef_shortaddr="db_tagBingdingTableReqDef_shortaddr";
    public static final String tagBingdingTableReqDef_useraddr="db_tagBingdingTableReqDef_useraddr";
    public static final String endpoint="db_endpoint";
    */
    public static final String bingdingTableReqDef = "db_bingdingTableReqDef";

    /*
    unsigned short  useraddr
    bindingrEventDef  rEvent";//高4位表示对方的应用,低4位表示对方的端点,也就是对方在绑定表里的序号
    bindinglEventDef  lEvent";//高4位表示本地应用
    bindinglOptDef   bindOpt";//绑定选项
    */
    public static final String bindingTableDef = "db_bindingTableDef";

    /*
    public static final String type="db_type";//功能模式,0:定时,1:阀值,2:联动
    public static final String reuse="db_reuse";//重复使用模式,0:单次,1:重复
    public static final String reuse1="db_reuse1";//保留,暂时未使用
    public static final String thresholdOption_enable="db_thresholdOption_enable";//是能位,0:禁止,1:使能
    public static final String done="db_done";//内部使用，此位会被内部更改，外部不需要判断此值
    */
    public static final String thresholdOptionDef = "db_thresholdOptionDef";

    /*
    public static final String event="db_event";//时间
    public static final String sensorIndex="db_sensorIndex";//传感器序号
    */
    public static final String thresholdAttrDef = "db_thresholdAttrDef";

    /*
    public static final String week="db_week";//星期，0-6位表示星期一到星期天，0：表示已设置
    public static final String hour="db_hour";//小时
    public static final String minute="db_minute";//分钟
    */
    public static final String timeEventDef = "db_timeEventDef";

    /*
    public static final String tagThreshold_mode="db_tagThreshold_mode";//执行模式，0：小于，1：等于，2：大于
    unsigned char threshold_H;//阀值高8位
    nsigned char threshold_L;//阀值低8位
    */
    public static final String thresholdDef = "db_thresholdDef";

    /*
   unsigned char mode;//执行模式，0：小于，1：等于，2：大于
   unsigned char reserve;//阀值高8位
   nsigned char threshold_L;//阀值低8位
   */
    public static final String linkingDef = "db_linkingDef";

    /*
    thresholdOptionDef option;
    unsigned char event;//属性，针对开关联动时，事件表示的是端口号；针对阀值时，高4位在传感器节点时，表示传感器序号，低4位表示事件，可以说是一个节点上的子设备，例如一个开关上的灯1、灯2、灯3.当传感器数量大于事件个数时，可以进行映射
    unsigned char todo;//动作，也就是发送给绑定设备的动作命令
    union
    {
        TimerEventDef timer;//针对事件，定时执行某事件
        ThresholdDef threshold;//针对事件,例如传感器的阀值
        LinkingDef linkage;//针对事件，
    }
    */
    public static final String linkageDef = "db_linkageDef";

    /*
    public static final String sceneNum="db_sceneNum";//场景号
    public static final String tagSceneOpt_enable="db_tagSceneOpt_enable";//场景使能
    public static final String tagSceneOpt_reserve="db_tagSceneOpt_reserve";//保留
    */
    public static final String sceneOptDef = "db_sceneOptDef";

    /*
    public static final String tagSceneEvent_evt="db_tagSceneEvent_evt";//场景事件
    public static final String tagSceneEvent_bindingEvt="db_tagSceneEvent_bindingEvt";//场景绑定的事件
    */
    public static final String sceneEventDef = "db_sceneEventDef";

    /*
    public static final String tagSceneDef_opt="db_tagSceneDef_opt";//场景选项
    public static final String tagSceneDef_evt="db_tagSceneDef_evt";//场景事件
    public static final String tagSceneDef_cmd="db_tagSceneDef_cmd";//场景命令
    public static final String tagSceneDef_delaytime="db_tagSceneDef_delaytime";//延时时间，单位为秒
    */
    public static final String sceneDef = "db_sceneDef";

    /*
    sceneOptDef opt";//场景选项
    SceneEventDef evt";//场景事件
    unsigned cmd";//场景命令
    unsigned delaytime";//延时时间，单位为秒
    */
    public static final String sceneQueueDef = "db_sceneQueueDef";

    /*
    public static final String tagTopologyDataDef_shortaddr="db_tagTopologyDataDef_shortaddr";//节点短地址
    public static final String tagTopologyDataDef_useraddr="db_tagTopologyDataDef_useraddr";//用户地址
    public static final String tagTopologyDataDef_type="db_tagTopologyDataDef_type";//节点类型
    public static final String tagTopologyDataDef_parentaddr="db_tagTopologyDataDef_parentaddr";//父节点地址
    public static final String voltage="db_voltage";//电压
    public static final String temperature="db_temperature";//温度
    */
    public static final String topologyDataDef = "db_topologyDataDef";

    public static final String createtime = "db_createtime";
    public static final String updatetime = "db_updatetime";

    private static SmartAPSqliteHelper mInstance;

    public SmartAPSqliteHelper(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASEVERSION);
        // 打开或新建数据库(第一次时创建)获得SQLiteDatabase对象，为了读取和写入数据
        db = this.getWritableDatabase();
        Log.i(TAG, "SmartAPSqliteHelper: create db");
    }

    public SmartAPSqliteHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SmartAPSqliteHelper.class) {
                if (mInstance == null) {
                    mInstance = new SmartAPSqliteHelper(context);
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
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + baseInfoFlagsDef + " TEXT,"
                + baseInfoDef + " TEXT," + baseInfoReadOnlyDef + " TEXT,"
                + extBindingTableDef + " TEXT," + bindingrEventDef + " TEXT,"
                + bindinglEventDef + " TEXT," + bindingOptDef + " TEXT," + bingdingTableReqDef
                + " TEXT," + bindingTableDef + " TEXT," + thresholdOptionDef + " TEXT,"
                + thresholdAttrDef + " TEXT," + timeEventDef + " TEXT," + thresholdDef + " TEXT,"
                + linkingDef + " TEXT," + linkageDef + " TEXT," + sceneOptDef + " TEXT,"
                + sceneEventDef + " TEXT," + sceneDef + " TEXT," + sceneQueueDef + " TEXT,"
                + topologyDataDef + " TEXT," + createtime + " TIMESTAMP," + updatetime + " TIMESTAMP)";
        db.execSQL(sql);
    }

    //更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.i(TAG, " onUpgrade() ");
        // 删除表的SQL
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
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
    public Cursor searchID(String s) {
        Cursor cur;
        String[] columns = {
                ID,
                baseInfoFlagsDef,
                baseInfoDef,
                baseInfoReadOnlyDef,
                extBindingTableDef,
                bindingrEventDef,
                bindinglEventDef,
                bindingOptDef,
                bingdingTableReqDef,
                bindingTableDef,
                thresholdOptionDef,
                thresholdAttrDef,
                timeEventDef,
                thresholdDef,
                linkingDef,
                linkageDef,
                sceneOptDef,
                sceneEventDef,
                sceneDef,
                sceneQueueDef,
                topologyDataDef,
                createtime,
                updatetime
        };
        String searchselection = ID + "= ?";
        String[] searchselectionargs = {s};
        String groupBy = null;
        String havingstr = null;
        String orderBy = null;
        cur = db.query(TABLE_NAME, columns, searchselection, searchselectionargs, groupBy, havingstr, orderBy);
        return cur;
    }

    //   ，返回Cursor对象(按照id的升序排列)
    public Cursor searchAllData() {

        //asc是升序desc为降序（默认为asc）
        Cursor temp = db.query(TABLE_NAME, null, null, null, null, null, SmartAPSqliteHelper.ID + " ASC");
        Log.i(TAG, " searchAllData: " + temp);
        return temp;
    }

    //插入数据
    public void insertData(String baseInfoFlagsDef,
                           String baseInfoDef,
                           String baseInfoReadOnlyDef,
                           String extBindingTableDef,
                           String bindingrEventDef,
                           String bindinglEventDef,
                           String bindingOptDef,
                           String bingdingTableReqDef,
                           String bindingTableDef,
                           String thresholdOptionDef,
                           String thresholdAttrDef,
                           String timeEventDef,
                           String thresholdDef,
                           String linkingDef,
                           String linkageDef,
                           String sceneOptDef,
                           String sceneEventDef,
                           String sceneDef,
                           String sceneQueueDef,
                           String topologyDataDef
    ) {
        Log.i(TAG, "insertData: start");
        ContentValues values = new ContentValues();
        //Log.i(TAG, "insertData: value=contentvalues");
        values.put(SmartAPSqliteHelper.baseInfoFlagsDef, baseInfoFlagsDef);
        Log.i(TAG, "insertData: insert goodscategory" + baseInfoFlagsDef);
        values.put(SmartAPSqliteHelper.baseInfoDef, baseInfoDef);
        values.put(SmartAPSqliteHelper.baseInfoReadOnlyDef, baseInfoReadOnlyDef);
        values.put(SmartAPSqliteHelper.extBindingTableDef, extBindingTableDef);
        values.put(SmartAPSqliteHelper.bindingrEventDef, bindingrEventDef);
        values.put(SmartAPSqliteHelper.bindinglEventDef, bindinglEventDef);
        values.put(SmartAPSqliteHelper.bindingOptDef, bindingOptDef);
        values.put(SmartAPSqliteHelper.bingdingTableReqDef, bingdingTableReqDef);
        values.put(SmartAPSqliteHelper.bindingTableDef, bindingTableDef);
        values.put(SmartAPSqliteHelper.thresholdOptionDef, thresholdOptionDef);
        values.put(SmartAPSqliteHelper.thresholdAttrDef, thresholdAttrDef);
        values.put(SmartAPSqliteHelper.timeEventDef, timeEventDef);
        values.put(SmartAPSqliteHelper.thresholdDef, thresholdDef);
        values.put(SmartAPSqliteHelper.linkingDef, linkingDef);
        values.put(SmartAPSqliteHelper.linkageDef, linkageDef);
        values.put(SmartAPSqliteHelper.sceneOptDef, sceneOptDef);
        values.put(SmartAPSqliteHelper.sceneEventDef, sceneEventDef);
        values.put(SmartAPSqliteHelper.sceneDef, sceneDef);
        values.put(SmartAPSqliteHelper.sceneQueueDef, sceneQueueDef);
        values.put(SmartAPSqliteHelper.topologyDataDef, topologyDataDef);
        long row = db.insert(TABLE_NAME, null, values);
        Log.i(TAG, "insertData row=" + row);

    }


    //获取设备列表
    public Cursor getdevicelist() {
        Cursor cur;
        String[] columns = {
                ID,
                baseInfoReadOnlyDef
        };
        String searchselection = null;
        String[] searchselectionargs = null;
        String groupBy = baseInfoReadOnlyDef;
        String havingstr = null;
        String orderBy = null;
        cur = db.query(TABLE_NAME, columns, searchselection, searchselectionargs, groupBy, havingstr, orderBy);
        return cur;
    }

    /*
    //购物清单，按照商品名分类
    public Cursor getshoplist_goodsnamer()
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsCategory,
                "SUM("+goodsNumber+")",
                goodsName,
                "MAX("+purchasePrice+")",
        };
        String searchselection=buyed+"= ?";
        String[] searchselectionargs={"0"};
        String groupBy=goodsName+","+goodsCategory;
        String havingstr=null;
        String orderBy=goodsCategory;
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }
    //查询未能完成购买的商品下 客户列表
    public Cursor getgoodsnumberlist(String cus,String gocate)
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsCategory,
                Customer,
                goodsNumber,
                remarks,
                purchasePrice
        };
        String searchselection=goodsName+"= ?"+" AND "+goodsCategory+"= ?"+" AND "+buyed+"= ?";
        String[] searchselectionargs={cus,gocate,"0"};
        String groupBy=null;
        String havingstr=null;
        String orderBy=null;
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }

    //查询完成购买的商品客户列表
    public Cursor getgoodsandnumberlistforbuyed()
    {
        Cursor cur;
        String[] columns={
                ID,
                goodsCategory,
                goodsName,
                Customer,
                goodsNumber,
                sellingPrice,
                payed,
                sended
        };
        String searchselection=buyed+"= ?"+" AND "+sended+"= ?";
        String[] searchselectionargs={"1","0"};
        String groupBy=null;
        String havingstr=null;
        String orderBy=Customer;
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }

    public Cursor getlistfortime(String starttime,String endtime)
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
        };
        String searchselection=sended+"= ?"+" AND "+sellTime+">= ?" +" AND "+sellTime+"<= ?";
        String[] searchselectionargs={"1",starttime,endtime};
        String groupBy=null;
        String havingstr=null;
        String orderBy=goodsName;
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
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
        cur =db.query(TABLE_NAME,columns,searchselection,searchselectionargs,groupBy,havingstr,orderBy);
        return cur;
    }

    //统计制定时间段内利润
    public String getcalcprofit(String starttime,String endtime)
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
            profit =cur.getString(cur.getColumnIndexOrThrow("SUM("+SmartAPSqliteHelper.profit+")"));
        }
        return profit;
    }

    //通过ID查询所有信息，并返回ContentValues

    public ContentValues valueSpecificId(int id)
    {
        ContentValues values=new ContentValues();
        Cursor cur;
        String[] columns={
                "*"
        };
        String searchselection=ID+"= ?";
        String[] searchselectionargs={Integer.toString(id)};
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
        cursor=db.query(TABLE_NAME,columns,null,null,Customer,null,null);
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
        cursor=db.query(TABLE_NAME,columns,null,null,goodsName,null,null);
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
        cursor=db.query(TABLE_NAME,columns,null,null,goodsCategory,null,null);
        return cursor;
    }

    //查询指定的信息
    public Cursor searchSpecific(String s,String tablename)
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
    public void upDateinfo(int id,
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
        values.put(SmartAPSqliteHelper.goodsName, goodname);
        values.put(SmartAPSqliteHelper.goodsNumber, number);
        values.put(SmartAPSqliteHelper.Customer, customer);
        values.put(SmartAPSqliteHelper.goodsCategory,goodsCategory);
        values.put(SmartAPSqliteHelper.remarks,remarks);
        values.put(SmartAPSqliteHelper.sellingPrice,sellingPrice);
        values.put(SmartAPSqliteHelper.purchasePrice,purchasePrice);
        values.put(SmartAPSqliteHelper.exchangeRate,exchangeRate);
        values.put(SmartAPSqliteHelper.profit,profit);
        values.put(SmartAPSqliteHelper.sellTime,sellTime);
        values.put(SmartAPSqliteHelper.buyed,buyed);
        values.put(SmartAPSqliteHelper.payed,payed);
        String whereClause=SmartAPSqliteHelper.ID+" = ? ";
        String whereArgs[]={Integer.toString(id)};
        int rowaffected =db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.i(TAG, "upDateinfo()  rowaffected="+rowaffected);
    }

    //修改购买属性
    public int upDataBuyed(int id,
                            String buyed)
    {
        ContentValues values=new ContentValues();
        values.put(SmartAPSqliteHelper.buyed,buyed);
        String whereClause=SmartAPSqliteHelper.ID+" = ? ";
        String whereArgs[]={Integer.toString(id)};
        int rowaffected =db.update(TABLE_NAME, values, whereClause, whereArgs);
        return rowaffected;
    }
    //修改付款属性
    public int upDataPayed(int id,String payed)
    {
        ContentValues values=new ContentValues();
        values.put(SmartAPSqliteHelper.payed,payed);
        String whereClause=SmartAPSqliteHelper.ID+" = ? ";
        String whereArgs[]={Integer.toString(id)};
        int rowaffected =db.update(TABLE_NAME, values, whereClause, whereArgs);
        return rowaffected;
    }

    //修改发货属性
    public int upDataSended(int id,String sended)
    {
        ContentValues values=new ContentValues();
        values.put(SmartAPSqliteHelper.sended,sended);
        String whereClause=SmartAPSqliteHelper.ID+" = ? ";
        String whereArgs[]={Integer.toString(id)};
        int rowaffected =db.update(TABLE_NAME, values, whereClause, whereArgs);
        return rowaffected;
    }

    //删除数据
    public int deletegoodslist(int id)
    {
        int rowaffected =db.delete(TABLE_NAME, SmartAPSqliteHelper.ID+"="+id, null);
        return rowaffected;

    }
    //判断是否存在该学生的信息
    public Cursor isHaveThisStu(int number)
    {
        String[] columns={
                SmartAPSqliteHelper.ID,
                SmartAPSqliteHelper.goodsName,
                SmartAPSqliteHelper.goodsCategory,
                SmartAPSqliteHelper.Customer
        };

        Cursor cur=db.query(TABLE_NAME, columns,  SmartAPSqliteHelper.goodsCategory+"="+number, null, null, null, null);
        Log.i("isHaveThisStu()", " cur.getCount()="+cur.getCount());
        return cur;
    }
    */
}
