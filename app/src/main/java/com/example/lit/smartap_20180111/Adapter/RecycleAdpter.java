package com.example.lit.smartap_20180111.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.lit.smartap_20180111.DaidaigouSqliteHelper;
import com.example.lit.smartap_20180111.MainActivity;
import com.example.lit.smartap_20180111.Mview.AddList;
import com.example.lit.smartap_20180111.R;
import com.example.lit.smartap_20180111.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class RecycleAdpter extends RecyclerView.Adapter<RecycleAdpter.MyViewHolder> {
    private String TAG="RecycleAdpter";
    private Cursor cursor_Customer,cursor_Category,listcursor,mcursor;
    private MainActivity mcontext;
    private HashMap<String, Boolean> hashmap_Category,hashmap_Customer;
    private DaidaigouSqliteHelper sqliteHelper;
    private int mFlag;
    public RecycleAdpter(MainActivity mcontext, int flag){
        sqliteHelper = DaidaigouSqliteHelper.getInstance(mcontext);
        this.mcontext=mcontext;
        mFlag = flag;
        initCursor();
        inithashmap();
    }

    private void initCursor(){
        cursor_Category = sqliteHelper.getshoplist_goodsCategory();
        cursor_Customer = sqliteHelper.getshoplist_Customer();
        switch (mFlag){
            case 0: mcursor = cursor_Category;break;
            case 1: mcursor = cursor_Customer;break;
            default: throw new NullPointerException("changeCursor中错误的flag");
        }
    }

    public void changeCursor(int flag){
        Log.i(TAG, "changeCursor: cursor:"+" flag:"+flag);
        mFlag = flag;
        initCursor();
        updateSignList();
        notifyDataSetChanged();
    }

    void inithashmap(){
        hashmap_Customer = new HashMap<>();
        SharedPreferences sp0 = mcontext.getSharedPreferences("hashmap_Customer",Context.MODE_PRIVATE);
        //Log.i(TAG, "inithashmap: "+sqliteHelper.toString());
        if (cursor_Customer.moveToFirst()) {
            for (cursor_Customer.moveToFirst(); !cursor_Customer.isAfterLast(); cursor_Customer.moveToNext()) {
                String text="";
                text = cursor_Customer.getString(cursor_Customer.getColumnIndex(DaidaigouSqliteHelper.Customer));
                hashmap_Customer.put(text, false);
                hashmap_Customer.put(text, sp0.getBoolean(text,false));
            }
        }
        SharedPreferences sp1 = mcontext.getSharedPreferences("hashmap_Category",Context.MODE_PRIVATE);
        hashmap_Category = new HashMap<>();
        if (cursor_Category.moveToFirst()) {
            for (cursor_Category.moveToFirst(); !cursor_Category.isAfterLast(); cursor_Category.moveToNext()) {
                String text="";
                text = cursor_Category.getString(cursor_Category.getColumnIndex(DaidaigouSqliteHelper.goodsCategory));
                hashmap_Category.put(text, false);
                hashmap_Category.put(text, sp1.getBoolean(text,false));
            }
        }
    }

    private void editShare(String key,Boolean value){
        SharedPreferences.Editor editor0 = mcontext.getSharedPreferences("hashmap_Category",Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor editor1 = mcontext.getSharedPreferences("hashmap_Customer",Context.MODE_PRIVATE).edit();
        switch (mFlag){
            case 0:
                editor0.putBoolean(key, value);
                editor0.apply();
                break;
            case 1:
                editor1.putBoolean(key, value);
                editor1.apply();
                break;
            default: throw new  NullPointerException ("editShare 错误的flag："+mFlag);
        }
    }

    Map<String, Boolean> getHashmap() {
        switch (mFlag){
            case 0:
                Log.i(TAG, "getHashmap: return 0");
                return hashmap_Category;
            case 1:
                Log.i(TAG, "getHashmap: return 1");
                return hashmap_Customer;
            default: throw new NullPointerException("getHashmap错误的flag:"+mFlag);
        }
    }

    private void updateSignList(){
        Map<String, Boolean> map0 = new HashMap<>(hashmap_Category);
        Map<String, Boolean> map1 = new HashMap<>(hashmap_Customer);
        SharedPreferences.Editor editor0 = mcontext.getSharedPreferences("hashmap_Category",Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor editor1 = mcontext.getSharedPreferences("hashmap_Customer",Context.MODE_PRIVATE).edit();
        inithashmap();
        editor0.clear();
        editor1.clear();
        for (Map.Entry<String,Boolean> entry: map0.entrySet()
        ) {
            String key  = entry.getKey();
            boolean value = map0.get(key);
            if (hashmap_Category.containsKey(key)){
                hashmap_Category.put(key,value);
                editor0.putBoolean(key,value);
                editor0.apply();
            }
        }
        for (Map.Entry<String,Boolean> entry: map1.entrySet()
        ) {
            String key  = entry.getKey();
            boolean value = map1.get(key);
            if (hashmap_Customer.containsKey(key)){
                hashmap_Customer.put(key,value);
                editor1.putBoolean(key,value);
                editor1.apply();
            }
        }
        //更新保存修改后的map
        Log.i(TAG, "updateSignList: hashmap_Customer:"+
                Arrays.toString(hashmap_Customer.entrySet().toArray())+
                "hashmap_Category:"+ Arrays.toString(hashmap_Category.entrySet().toArray()));
    }

    @Override
    public int getItemCount(){
        return mcursor.getCount();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new MyViewHolder(LayoutInflater.from(
                mcontext).inflate(R.layout.recycle_list, parent,
                false));
    }

    interface OnItemClickListener
    {
        public void onItemClick(View view, int postion);
        public void onItemLongClick(View view, int postion);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener (OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    Cursor getListcursorByCategory(int position){
        mcursor.moveToPosition(position);
        String goodscategory = mcursor.getString(mcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.goodsCategory));
        return sqliteHelper.getgoodsnumberlist_category(goodscategory);
    }
    Cursor getListcursorByCustomer(int position){
        mcursor.moveToPosition(position);
        String customer = mcursor.getString(mcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.Customer));
        //出现bug把customr改成了null，所以增加此处代码修改值为“null”
        if (customer == null){
            customer ="null";
            ContentValues values = new ContentValues();
            values.put(DaidaigouSqliteHelper.ID,mcursor.getString(mcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID)));
            values.put(DaidaigouSqliteHelper.Customer,customer);
            sqliteHelper.upDatainfo(values);
        }
        return sqliteHelper.getgoodsnumberlist_customer(customer);
    }

    float calcPrices(final Cursor cursor){
        float prices=0;
        if (cursor.moveToFirst()){
            for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                float num = cursor.getFloat(cursor.getColumnIndex(DaidaigouSqliteHelper.goodsNumber));
                float price = cursor.getFloat(cursor.getColumnIndex(DaidaigouSqliteHelper.purchasePrice));
                prices += Utils.mul(price,num);
            }
        }
        return prices;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position){
        Map<String, Boolean> hashmap =getHashmap();
        Log.i(TAG, "onBindViewHolder: "+"mcount:"+getItemCount()+
                "-start bind View pos "+position+"-list:"+hashmap.size());
        if (!mcursor.moveToPosition(position)) {
            return;
        }
        try {
            if (mFlag == 0) {
                listcursor = getListcursorByCategory(position);
            }else if (mFlag ==1){
                listcursor = getListcursorByCustomer(position);
            }
            Log.i(TAG, "onBindViewHolder: listcursor num:"+listcursor.getCount());
        }catch (Exception e){
            listcursor=null;
            Log.e(TAG, "onBindViewHolder: getListcursorByCategory error:"+e,null );
        }
        try {
            if (mFlag == 0) {
                holder.recycle_text1.setText(mcursor.getString(mcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.goodsCategory)));
            }else if (mFlag ==1){
                holder.recycle_text1.setText(mcursor.getString(mcursor.getColumnIndex(DaidaigouSqliteHelper.Customer)));
            }
            holder.recycle_goodsnum.setText(mcursor.getString(mcursor.getColumnIndexOrThrow("SUM(" + DaidaigouSqliteHelper.goodsNumber + ")")));
            holder.recycle_purchase.setText(String.valueOf(calcPrices(listcursor)));
        }catch (Exception e ){
            Log.e(TAG, "onBindViewHolder: setText error:"+e,null );
        }
        String text = "";
        switch (mFlag){
            case 0:
                text = mcursor.getString(mcursor.getColumnIndex(DaidaigouSqliteHelper.goodsCategory));
                break;
            case 1:
                text = mcursor.getString(mcursor.getColumnIndex(DaidaigouSqliteHelper.Customer));
                break;
        }
        if (hashmap.size()>0) {
            if (hashmap.get(text)) {
                holder.recycle_list_customerlist.setVisibility(View.VISIBLE);
            } else {
                holder.recycle_list_customerlist.setVisibility(View.GONE);
            }
        }
        if (onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getAdapterPosition();
                    onItemClickListener.onItemClick(holder.itemView,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=holder.getAdapterPosition();
                    onItemClickListener.onItemLongClick(holder.itemView,pos);
                    return true;
                }
            });
        }

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position1=holder.getAdapterPosition();
                Map<String, Boolean> hashmap = getHashmap();
                mcursor.moveToPosition(position1);
                String text="";
                switch (mFlag){
                    case 0:
                        text= mcursor.getString(mcursor.getColumnIndex(DaidaigouSqliteHelper.goodsCategory));
                        break;
                    case 1:
                        text = mcursor.getString(mcursor.getColumnIndex(DaidaigouSqliteHelper.Customer));
                        break;
                }
                if(hashmap.get(text)){
                    hashmap.put(text,false);
                    editShare(text,false);
                }else {
                    hashmap.put(text,true);
                    editShare(text,true);
                }
                /*
                //记录展开位置，单个展开使用
                if (selcetPosition!=position && selcetPosition!=-1){
                    signlist.set(selcetPosition,false);
                }

                selcetPosition=position;
                */
                //更新界面,适配器更新显示，数据无变化，不需要回调主程序
                notifyItemChanged(position1);
                //daidaiGou.dataChanged();
            }
        });


        try {
            String[] from = new String[]{
                    DaidaigouSqliteHelper.Customer,
                    DaidaigouSqliteHelper.goodsName,
                    DaidaigouSqliteHelper.goodsNumber,
                    DaidaigouSqliteHelper.remarks,
                    DaidaigouSqliteHelper.purchasePrice
            };
            int[] to = new int[]{
                    R.id.recycle_text2_1,
                    R.id.recycle_text1_1,
                    R.id.recycle_text1_2,
                    R.id.recycle_text2_2,
                    R.id.recycle_text2_3
            };
            if (listcursor.moveToFirst()) {
                ///*
                GoodsListAdapter goodsListAdapter = new GoodsListAdapter(mcontext,
                        R.layout.recycle_list_inside,
                        listcursor,
                        from,
                        to,
                        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,
                        R.id.radioButton
                );
                //*/
                //goodsListAdapter = new GoodsListAdapter(mcontext,)
                //手动计算listView的高度，setLayoutParams 设置高度
                View itemview = goodsListAdapter.getView(0, null, holder.recycle_list_customerlist);
                itemview.measure(0, 0);
                int totalHeight = itemview.getMeasuredHeight() * goodsListAdapter.getCount();
                ViewGroup.LayoutParams layoutParams = holder.recycle_list_customerlist.getLayoutParams();
                layoutParams.height = totalHeight + holder.recycle_list_customerlist.getDividerHeight() * goodsListAdapter.getCount();
                holder.recycle_list_customerlist.setLayoutParams(layoutParams);
                holder.recycle_list_customerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(TAG, "onItemClick: position:"+position+" | id: "+id);
                        Intent Myintent = new Intent();
                        Myintent.setClass(mcontext, AddList.class);
                        listcursor.moveToPosition(position);
                        //int rowid=listcursor.getInt(listcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
                        Myintent.putExtra("ThevaluestoEdit",id);
                        mcontext.startActivity(Myintent);
                    }
                });
                holder.recycle_list_customerlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                        new AlertDialog.Builder(mcontext).setTitle("警告")
                                .setMessage("删除，确认？")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sqliteHelper.deletegoodslist(id);
                                        //daidaiGou.listCursorUpdate();
                                    }
                                })
                                .setNegativeButton("否",null)
                                .show();
                        return true;
                    }
                });
                holder.recycle_list_customerlist.setAdapter(goodsListAdapter);

            } else {
                holder.recycle_list_customerlist.setAdapter(null);
            }
            holder.allbuyedbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(mFlag == 0) {
                            listcursor = getListcursorByCategory(holder.getAdapterPosition());
                        }else if(mFlag == 1){
                            listcursor = getListcursorByCustomer(holder.getAdapterPosition());
                        }
                        if (listcursor.moveToFirst()) {
                            do {
                                Long rowid = listcursor.getLong(listcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
                                sqliteHelper.upDataBuyed(rowid, "1");
                            }
                            while (listcursor.moveToNext());
                            //刷新界面
                            notifyDataSetChanged();
                            //主界面刷新列表
                            //daidaiGou.listCursorUpdate();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onClick:allbuyedbutton error:" + e, null);
                    }
                }
            });
        }catch (Exception e){
            Log.e(TAG, "onBindViewHolder: error:"+e,null );
        }
        Log.i(TAG, "onBindViewHolder: finish");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView recycle_text1,recycle_purchase,recycle_goodsname,recycle_goodsnum;
        ImageButton imageButton;
        Button allbuyedbutton;
        //LinearLayout linearLayout_hide;
        ListView recycle_list_customerlist;

        private MyViewHolder(View view){
            super(view);
            recycle_text1 =view.findViewById(R.id.recycle_text1);
            recycle_purchase=view.findViewById(R.id.recycle_purchase);
            recycle_goodsname=view.findViewById(R.id.recycle_text1_1);
            recycle_goodsnum=view.findViewById(R.id.recycle_goodsnum);
            //linearLayout_hide=view.findViewById(R.id.linearLayout3);
            recycle_list_customerlist=view.findViewById(R.id.recycle_list_customerlist);
            imageButton=view.findViewById(R.id.imageButton);
            recycle_list_customerlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return false;
                }
            });
            allbuyedbutton=view.findViewById(R.id.allbuyedbutton);

        }
    }

    public class GoodsListAdapter extends SimpleCursorAdapter
    {
        //private ArrayList<Integer> selection = new ArrayList<Integer>();
        private int checkbox;
        private Cursor cursor;
        public GoodsListAdapter(Context context, int layout, Cursor c, String[] from,
                                int[] to, int flag, int checkbox)
        {
            super(context, layout,c, from, to,flag);
            this.checkbox = checkbox;
            cursor=c;
        }
        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            //Log.i(TAG, "getView: position:"+position+" convertView:"+convertView);
            RadioButton checkbox = view.findViewById(this.checkbox);
            checkbox.setChecked(false);
            checkbox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(position);
                    Long rowId;
                    try {
                        rowId = cursor.getLong(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
                        Cursor cursor= sqliteHelper.searchID(Long.toString(rowId));
                        if (cursor.getCount()==1) {
                            cursor.moveToFirst();
                            int index = cursor.getInt(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.buyed));
                            if (index == 0) {
                                sqliteHelper.upDataBuyed(rowId, "1");
                            } else if (index == 1) {
                                sqliteHelper.upDataBuyed(rowId, "0");
                            }
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onClick: error:"+e,null );
                    }
                    //更新页面
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }

}
