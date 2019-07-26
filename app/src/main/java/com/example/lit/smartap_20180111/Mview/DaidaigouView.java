package com.example.lit.smartap_20180111.Mview;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lit.smartap_20180111.Adapter.BuyedListRecycleAdpter;
import com.example.lit.smartap_20180111.Adapter.RecycleAdpter;
import com.example.lit.smartap_20180111.DaidaigouSqliteHelper;
import com.example.lit.smartap_20180111.MainActivity;
import com.example.lit.smartap_20180111.R;
import com.example.lit.smartap_20180111.core.MyContentProvider;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DaidaigouView {
    private String TAG = "DaidaigouView";
    private View mainView, myview1, myview2, myview3;
    private ListView listView_record;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MainActivity mContext;
    private LayoutInflater mInflater;
    private EditText editText_startime, editText_endtime, edit_search;
    private TextView mClacProfit;
    private int mYear, mMonth, mDay;
    private Cursor shoplist_Cursor, record_Cursor;
    private SimpleCursorAdapter record_itemListAdapter;
    private List<String> mTitleList = new ArrayList<>();//标题集合
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    public static final int SHOWGOODSNAME = 0;
    public static final int SHOWGOODSBUYED = 1;
    private static final int SHOWRECORD = 2;
    public static final int LISTBYGOODS = 0;
    public static final int LISTBYCUSTOMER = 1;
    private int SHOPLIST_FLAG = 0;//默认为LISTBYGOODS
    AppCompatImageView goodslist_bygoods, goodslist_bycustomer;
    private RecyclerView recyclerView_shop, recycleView_send;
    private RecycleAdpter recycleAdpter;
    private DaidaigouSqliteHelper sqliteHelper;

    public DaidaigouView(MainActivity context) {
        mContext = context;
        mainView = View.inflate(context, R.layout.activity_main, null);
        sqliteHelper = DaidaigouSqliteHelper.getInstance(context);
        initTabLayoutandveiw();
    }

    public View getView() {
        return mainView;
    }

    public void initTabLayoutandveiw() {
        mTabLayout = mainView.findViewById(R.id.app_tablayout);
        mViewPager = mainView.findViewById(R.id.container);
        mTitleList.add(mContext.getResources().getString(R.string.addlist_str));
        mTitleList.add(mContext.getResources().getString(R.string.buyedlist_str));
        mTitleList.add(mContext.getResources().getString(R.string.recordlist_str));
        mInflater = LayoutInflater.from(mContext);
        myview1 = mInflater.inflate(R.layout.getgoodslist_layout, null);
        myview2 = mInflater.inflate(R.layout.getgoodslist_layout, null);
        myview3 = mInflater.inflate(R.layout.record_layout, null);
        mViewList.add(myview1);
        mViewList.add(myview2);
        mViewList.add(myview3);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(myPagerAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
        //初始化 购物清单 界面
        showGoodsNameAdapter();
        //初始化 收款发货 界面
        showBuyedlistAdapter();
        //初始化 记录统计 界面
        showRecordlistAdapter();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case SHOWGOODSNAME:
                        //showGoodsNameAdapter();
                        Log.i(TAG, "onTabSelected: this is goodsname");
                        break;
                    case SHOWGOODSBUYED:
                        //showBuyedlistAdapter();
                        Log.i(TAG, "onTabSelected: this is buyedlist");

                        break;
                    case SHOWRECORD:
                        //showRecordlistAdapter();
                        Log.i(TAG, "onTabSelected: this is recordlist");

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }

    }

    void button_light(int n) {
        Log.i(TAG, "button_light: num:" + n);
        int colorblack = mContext.getResources().getColor(R.color.colorAccent);
        int colorlight = mContext.getResources().getColor(R.color.colorPrimary);
        goodslist_bycustomer = myview1.findViewById(R.id.goodslist_bycustomer);
        goodslist_bygoods = myview1.findViewById(R.id.goodslist_bygoods);
        switch (n) {
            case 0:
                goodslist_bycustomer.setColorFilter(colorlight);
                goodslist_bygoods.setColorFilter(colorblack);
                //customer.setColorFilter(colorblack,PorterDuff.Mode.SRC_ATOP);
                //goods.setColorFilter(colorlight,PorterDuff.Mode.SRC_ATOP);
                break;
            case 1:
                goodslist_bycustomer.setColorFilter(colorblack);
                goodslist_bygoods.setColorFilter(colorlight);
                //customer.setColorFilter(colorlight,PorterDuff.Mode.SRC_ATOP);
                //goods.setColorFilter(colorblack,PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    private void showGoodsNameAdapter() {
        Log.i(TAG, "showGoodsNameAdapter: start");
        //默认按照商品类别排序
        SHOPLIST_FLAG = LISTBYGOODS;
        recyclerView_shop = myview1.findViewById(R.id.recycle_view);
        recyclerView_shop.setLayoutManager(new LinearLayoutManager(mContext));
        recycleAdpter = new RecycleAdpter(mContext, SHOPLIST_FLAG);
        recyclerView_shop.setHasFixedSize(true);
        recyclerView_shop.setAdapter(recycleAdpter);

        //listView_shop=(ListView)myview1.findViewById(R.id.listview_getgoods) ;
        TextView allgoodsPrice = myview1.findViewById(R.id.textView2);
        /*
        Cursor cursor = sqliteHelper.getAllGoodsPrice(),
                null,null,null,null);
        float longallgoodsPrice=0;
        if (cursor.getCount()!=0 && cursor.moveToFirst()){

        do {
            int num=cursor.getInt(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.goodsNumber));
            float prices=cursor.getFloat(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.purchasePrice));
            longallgoodsPrice+=ArithUtil.mul(prices,num);
        }
        while (cursor.moveToNext());
        cursor.close();
        }
        */
        String s = sqliteHelper.getAllGoodsPrice();
        allgoodsPrice.setText(s);
        Button goodslist_addlist = myview1.findViewById(R.id.goodslist_addlist);
        goodslist_bygoods = myview1.findViewById(R.id.goodslist_bygoods);
        goodslist_bycustomer = myview1.findViewById(R.id.goodslist_bycustomer);
        button_light(0);
        goodslist_addlist.setBackgroundResource(R.mipmap.add);
        goodslist_addlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(myview1.getContext())
                        .setTitle("提示").setMessage("批量添加或者单个添加")
                        .setPositiveButton("单个", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Myintent=new Intent();
                                Myintent.setClass(DaidaiGou.this,AddList.class);
                                startActivity(Myintent);
                            }
                        }).setNegativeButton("批量",null);
                builder.show();
                */
                Intent myintent = new Intent();
                myintent.setClass(mContext, AddList.class);
                mContext.startActivity(myintent);
            }
        });

        goodslist_bygoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SHOPLIST_FLAG = LISTBYGOODS;
                button_light(0);
                //goodslist_itemListAdapter=getItemListAdapter(LISTBYGOODS);
                //shoplist_Cursor = sqliteHelper.getshoplist_goodsCategory();
                //Uri uri = MyContentProvider.Query_name.shoplistbygoodsCategory.getUrivalue();
                //shoplist_Cursor = getContentResolver().query(uri,
                //       null,null,null,null);
                //if(shoplist_Cursor!=null && shoplist_Cursor.getCount()>0)
                //{
                //listView_shop.setAdapter(goodslist_itemListAdapter);
                recycleAdpter.changeCursor(LISTBYGOODS);
                //recycleAdpter.notifyDataSetChanged();
                //}
                //else
                {
                    //listView_shop.setAdapter(null);
                    //Toast.makeText(DaidaiGou.this,"null goods",Toast.LENGTH_LONG).show();
                }
            }
        });
        goodslist_bycustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SHOPLIST_FLAG = LISTBYCUSTOMER;
                button_light(1);
                //goodslist_itemListAdapter=getItemListAdapter(LISTBYCUSTOMER);
                Cursor shoplist_Cursor = sqliteHelper.getshoplist_Customer();
                //shoplist_Cursor = getContentResolver().query(MyContentProvider.Query_name.shoplistbyCustomer.getUrivalue(),
                //null,null,null,null);
                if (shoplist_Cursor.moveToFirst()) {
                    //listView_shop.setAdapter(goodslist_itemListAdapter);
                    //recyclerView_shop.setAdapter(shoplist_Cursor);
                    recycleAdpter.changeCursor(LISTBYCUSTOMER);
                    //recycleAdpter.notifyDataSetChanged();
                } else {
                    //listView_shop.setAdapter(null);
                    Toast.makeText(mContext, "null goods", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
        if (SHOPLIST_FLAG==LISTBYGOODS) {
            goodslist_itemListAdapter=getItemListAdapter(LISTBYGOODS);
        }else if(SHOPLIST_FLAG==LISTBYCUSTOMER){
            goodslist_itemListAdapter=getItemListAdapter(LISTBYCUSTOMER);
        }
        if(shoplist_Cursor!=null && shoplist_Cursor.getCount()>0)
        {
            listView_shop.setAdapter(goodslist_itemListAdapter);
            listView_shop.setOnItemClickListener(listVeiwOnItemClickListener);
            listView_shop.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    popupWindowshow(view,position,SHOWGOODSNAME);
                    return true;
                }
            });
        }
        else
        {
            listView_shop.setAdapter(null);
            Toast.makeText(this,"null goods",Toast.LENGTH_LONG).show();
        }
        */
        Log.i(TAG, "showGoodsNameAdapter: finish");
    }

    public void showBuyedlistAdapter() {
        Log.i(TAG, "showBuyedlistAdapter: start");
        recycleView_send = myview2.findViewById(R.id.recycle_view);
        Button goodslist_addlist = (Button) myview2.findViewById(R.id.goodslist_addlist);
        goodslist_bycustomer = myview2.findViewById(R.id.goodslist_bycustomer);
        goodslist_bygoods = myview2.findViewById(R.id.goodslist_bygoods);
        TextView profitView = myview2.findViewById(R.id.textView2);
        profitView.setText(sqliteHelper.getSellingPrice());
        goodslist_addlist.setBackgroundResource(R.mipmap.reduce);
        goodslist_bycustomer.setVisibility(View.INVISIBLE);
        goodslist_bygoods.setVisibility(View.INVISIBLE);
        Cursor sendlist_Cursor = sqliteHelper.getcustomerlistforbuyed();
        BuyedListRecycleAdpter buyedListRecycleAdpter = new BuyedListRecycleAdpter(sendlist_Cursor, mContext);
        recycleView_send.setLayoutManager(new LinearLayoutManager(mContext));
        recycleView_send.setAdapter(buyedListRecycleAdpter);

        goodslist_addlist.setOnClickListener(onClickListenerforReduce);
        Log.i(TAG, "showBuyedlistAdapter: finish");
    }

    View.OnClickListener onClickListenerforReduce = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(myview2.getContext()).setTitle("警告")
                    .setMessage("取消所有购买，确认？")
                    .setPositiveButton("是", getOnClickListenerforReduce)
                    .setNegativeButton("否", null)
                    .show();

        }
    };
    DialogInterface.OnClickListener getOnClickListenerforReduce = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                int row = sqliteHelper.upDataBackBuyed();
                Log.i(TAG, "onClick: getOnClickListenerforReduce: num :" + row);
                //listCursorUpdate();
            } catch (Exception e) {
                Log.e(TAG, "getOnClickListenerforReduce onClick: error:" + e, null);
            }
        }
    };

    public void showRecordlistAdapter() {
        listView_record = myview3.findViewById(R.id.listview_record);
        editText_startime = myview3.findViewById(R.id.edit_starttime);
        editText_endtime = myview3.findViewById(R.id.edit_endtime);
        edit_search = myview3.findViewById(R.id.edittext_forsearch);
        setDateforTime(7);
        mClacProfit = myview3.findViewById(R.id.record_profit);
        editText_startime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, 0,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //getmonth 获取到的月份是从0开始，所以month 要+1
                                editText_startime.setText(String.format("%s-%s-%s", year, month + 1, dayOfMonth));
                            }
                        },
                        mYear,
                        mMonth,
                        mDay);
                datePickerDialog.show();
            }
        });
        editText_endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, 0,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //getmonth 获取到的月份是从0开始，所以month 要+1
                                editText_endtime.setText(String.format("%s-%s-%s", year, month + 1, dayOfMonth));
                            }
                        },
                        mYear,
                        mMonth,
                        mDay);
                datePickerDialog.show();
            }
        });
        get_listview_record(myview3);
        Log.i(TAG, "showRecordlistAdapter: finish");
    }

    //默认设置起止时间,day 表示距今多少天
    private void setDateforTime(int day) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        String endtime = dateFormat.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -day);
        String startime = dateFormat.format(cal.getTime());
        editText_startime.setText(startime);
        editText_endtime.setText(endtime);
    }

    private void getDate() {
        Calendar cal = Calendar.getInstance();
        Long time = cal.getTimeInMillis();
        mYear = cal.get(Calendar.YEAR);       //获取年月日时分秒
        mMonth = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        mDay = cal.get(Calendar.DAY_OF_MONTH);
    }

    //转换成Java.util.Date
    private String ymd_to_date(String ymd, String hms) {
        String dateStr = ymd + " " + hms;
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException pe) {
            return null;
        }
        String time = dateFormat.format(date);
        return time;
    }


    public void get_listview_record(View view) {
        record_Cursor = calctimezonerecords();

        String[] from = new String[]{
                DaidaigouSqliteHelper.goodsName,
                DaidaigouSqliteHelper.Customer,
                DaidaigouSqliteHelper.goodsNumber,
                DaidaigouSqliteHelper.sellingPrice,
                DaidaigouSqliteHelper.purchasePrice,
                DaidaigouSqliteHelper.goodsCategory,
        };
        int[] to = new int[]{
                R.id.record_goodname_id,
                R.id.record_customer_id,
                R.id.record_number_id,
                R.id.record_sellprice_id,
                R.id.record_purchaseprice_id,
                R.id.record_Category_id
        };
        if (record_Cursor != null && record_Cursor.getCount() > 0) {
            record_itemListAdapter = new SimpleCursorAdapter(
                    mContext,
                    R.layout.record_item_list,
                    record_Cursor,
                    from,
                    to,
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            );
            listView_record.setAdapter(record_itemListAdapter);
            listView_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "onItemClick: parent:" + parent + " View:" + view + " position:" + position + " id:" + id);
                }
            });

            listView_record.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    popupWindowshow(view, position, SHOWRECORD);
                    return true;
                }
            });
        } else {
            listView_record.setAdapter(null);
        }
        mClacProfit.setText(calcprofit());
    }

    private String calcprofit()
    {
        String startime=editText_startime.getText().toString();
        String endtime=editText_endtime.getText().toString();
        if (startime.equals("")||endtime.equals("")){
            return null;
        }
        Timestamp timestampstarttime =Timestamp.valueOf(ymd_to_date(startime,"00:00:00"));
        Timestamp timestampendtime = Timestamp.valueOf(ymd_to_date(endtime,"23:59:59"));
        if (timestampendtime.after(timestampstarttime)) {
            String profit= sqliteHelper.getcalcprofit(Long.toString(timestampstarttime.getTime()),Long.toString(timestampendtime.getTime()));
            return profit;
        }
        else {
            return null;
        }
    }

    private Cursor calctimezonerecords() {
        String startime = editText_startime.getText().toString();
        String endtime = editText_endtime.getText().toString();
        String searchText = edit_search.getText().toString();
        //Log.i(TAG, "calctimezonerecords:startime "+startime);
        if (startime.equals("") || endtime.equals("")) {
            return null;
        }
        Timestamp timestampstarttime = Timestamp.valueOf(ymd_to_date(startime, "00:00:00"));
        Log.i(TAG, "calctimezonerecords: starttime:" + Long.toString(timestampstarttime.getTime()));
        Timestamp timestampendtime = Timestamp.valueOf(ymd_to_date(endtime, "23:59:59"));
        Log.i(TAG, "calctimezonerecords: endtime:" + Long.toString(timestampendtime.getTime()));

        Cursor cursor = null;
        try {
            if (timestampendtime.after(timestampstarttime)) {
                cursor = sqliteHelper.getlistfortime(Long.toString(timestampstarttime.getTime()),
                        Long.toString(timestampendtime.getTime()),
                        searchText);
            } else {
                cursor = sqliteHelper.getlistfortime(Long.toString(timestampendtime.getTime()),
                        Long.toString(timestampstarttime.getTime()),
                        searchText);
            }
        } catch (Exception e) {
            Log.e(TAG, "calctimezonerecords: getcursor error:" + e, null);
        }
        /*
        Cursor allCursor=daidaigouSqliteHelper.searchAllData();
        String s;
        StringBuilder stringBuilder=new StringBuilder();
        try {
            if(allCursor.moveToFirst()) {
                do{
                    s = allCursor.getString(allCursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.sellTime));
                    stringBuilder.append(s+"|");
                }while (allCursor.moveToNext());
            }
        }catch (Exception e){
            Log.e(TAG, "calctimezonerecords: "+e,null );
            s=null;
        }
        Log.i(TAG, "calctimezonerecords: cursor:"+stringBuilder.toString());
        */
        return cursor;
    }

    public void popupWindowshow(View view, final int position, int tab) {
        final PopupWindow mPopupWindow;
        View popupView = mInflater.inflate(R.layout.layout_popupwindow, null);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        mPopupWindow.showAsDropDown(view);
        View.OnClickListener mPopupWindowlistener = null;
        Button popup_button_first = popupView.findViewById(R.id.popup_button_first);
        Button popup_button_second = popupView.findViewById(R.id.popup_button_second);
        if (tab == SHOWGOODSNAME) {
            mPopupWindowlistener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoplist_Cursor.moveToPosition(position);
                    Long id = shoplist_Cursor.getLong(shoplist_Cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
                    switch (v.getId()) {
                        case R.id.popup_button_first:
                            sqliteHelper.upDataBuyed(id, "1");
                            break;
                        case R.id.popup_button_second:
                            //daidaigouSqliteHelper.deletegoodslist(id);
                            mContext.getContentResolver().delete(MyContentProvider.Uri_delete
                                    , DaidaigouSqliteHelper.ID + "=" + id, null);
                            break;
                    }
                /*
                String goodsname = shoplist_Cursor.getString(shoplist_Cursor.getColumnIndex(DaidaigouSqliteHelper.goodsName));
                String goodscate = shoplist_Cursor.getString(shoplist_Cursor.getColumnIndex(DaidaigouSqliteHelper.goodsCategory));
                Cursor getgoodsnumberlist_category = daidaigouSqliteHelper.getgoodsnumberlist_category(goodsname, goodscate);
                if  (getgoodsnumberlist_category != null && getgoodsnumberlist_category.moveToFirst()) {
                    do {
                        switch (v.getId()) {
                            case R.id.popup_button_first:
                                daidaigouSqliteHelper.upDataBuyed(getgoodsnumberlist_category.getInt(getgoodsnumberlist_category.getColumnIndex(DaidaigouSqliteHelper.ID)),
                                        "1");
                                break;
                            case R.id.popup_button_second:
                                daidaigouSqliteHelper.deletegoodslist(getgoodsnumberlist_category.getInt(getgoodsnumberlist_category.getColumnIndex(DaidaigouSqliteHelper.ID)));
                                break;
                        }
                    }
                    while (getgoodsnumberlist_category.moveToNext());
                }
                */
                    if (mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                        //listCursorUpdate();
                    }
                }
            };
        }

    }
}
