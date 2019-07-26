package com.example.lit.smartap_20180111.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.lit.smartap_20180111.DaidaigouSqliteHelper;
import com.example.lit.smartap_20180111.MainActivity;
import com.example.lit.smartap_20180111.Mview.AddList;
import com.example.lit.smartap_20180111.R;
import com.example.lit.smartap_20180111.Utils;

import java.util.ArrayList;
import java.util.List;

public class BuyedListRecycleAdpter extends RecyclerView.Adapter<BuyedListRecycleAdpter.MyViewHolder> {
    private String TAG="BuyedListRecycleAdpter";
    private Cursor mcursor,listcursor;
    private MainActivity mcontext;
    private List<Boolean> signlist;
    private int selcetPosition;
    private DaidaigouSqliteHelper daidaigouSqliteHelper;

    public BuyedListRecycleAdpter(Cursor cursor, MainActivity mcontext){
        mcursor=cursor;
        this.mcontext=mcontext;
        selcetPosition=-1;
        daidaigouSqliteHelper=new DaidaigouSqliteHelper(mcontext);
        initSignList();

    }

    Cursor getlistcursor(){
        return listcursor;
    }

    private void initSignList(){
        if (mcursor!=null) {
            int num = getItemCount();
            signlist = new ArrayList<Boolean>();
            for (int i = 0; i < num; i++) {
                signlist.add(true);
            }
            /*
            if (selcetPosition > -1 && num >= selcetPosition && num > 0) {
                signlist.set(selcetPosition, true);
            }else {
                selcetPosition = -1;
            }
            */
        }
    }

    void changeCursor(Cursor cursor){
        Log.i(TAG, "changeCursor: ");
        this.mcursor=cursor;
        initSignList();
        notifyDataSetChanged();
    }

    private Cursor getListcursor(int position){
        Log.i(TAG, "getListcursor: "+DaidaigouSqliteHelper.getCursor(mcursor)+" position:"+position);
        mcursor.moveToPosition(position);
        String customer ="";
        try {
             int index=mcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.Customer);
             customer = mcursor.getString(index);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (customer ==null){
            String id = mcursor.getString(mcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
            ContentValues contentValues = new ContentValues();
            contentValues.put(DaidaigouSqliteHelper.ID,id);
            contentValues.put(DaidaigouSqliteHelper.Customer,"null");
            daidaigouSqliteHelper.upDatainfo(contentValues);
            customer="null";
        }
        return daidaigouSqliteHelper.getgoodsandnumberlistforbuyed(customer);
    }

    @Override
    public int getItemCount() {
        return mcursor.getCount();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(mcontext).inflate(
                        R.layout.recycle_buyedlist,parent,false)
        );
    }

    float calcSellPrices(final Cursor cursor){
        float prices=0;
        if (cursor.moveToFirst()){
            for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                float num = cursor.getFloat(cursor.getColumnIndex(DaidaigouSqliteHelper.goodsNumber));
                float price = cursor.getFloat(cursor.getColumnIndex(DaidaigouSqliteHelper.sellingPrice));
                prices += Utils.mul(price,num);
            }
        }
        return prices;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BuyedListRecycleAdpter.BuyedListCursorAdatper buyedListCursorAdatper;
        if (!mcursor.moveToPosition(position)){
            return;
        }
        try {
            listcursor=getListcursor(position);
        }catch (Exception e){
            listcursor=null;
            Log.e(TAG, "onBindViewHolder: error:"+e,null );
        }
        Log.i(TAG, "onBindViewHolder: "+"start bind View pos "+position);
        try{
            String customer = mcursor.getString(mcursor.
                    getColumnIndexOrThrow(DaidaigouSqliteHelper.Customer));
            holder.recycle_buyedlist_customer.setText(customer);
            holder.recycle_buyedlist_goodsnum.setText(mcursor.getString(mcursor.
                    getColumnIndexOrThrow(DaidaigouSqliteHelper.SumgoodsNumber)));
            float prices = calcSellPrices(listcursor);
            holder.recycle_buyedlist_allprice.setText(String.valueOf(prices));
            holder.recycle_buyedlist_imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getAdapterPosition();
                    if(signlist.get(pos)){
                        signlist.set(pos,false);
                    }else {
                        signlist.set(pos,true);
                    }
                    /*
                    //记录展开位置，单个展开使用
                    if (selcetPosition!=pos && selcetPosition!=-1){
                        signlist.set(selcetPosition,false);
                    }
                    selcetPosition=pos;
                    */
                    //更新界面,适配器更新显示，数据无变化
                    notifyDataSetChanged();
                }
            });
            holder.recycle_buyedlist_allfinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listcursor=getListcursor(holder.getAdapterPosition());
                    if (listcursor.moveToFirst()){
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                do {
                                    Long rowid=listcursor.getLong(listcursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
                                    daidaigouSqliteHelper.upDataPayed(rowid,"1");
                                    String s= Long.toString(System.currentTimeMillis());
                                    daidaigouSqliteHelper.upDataSended(rowid,"1",s);
                                }
                                while (listcursor.moveToNext());
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        mcursor = daidaigouSqliteHelper.getcustomerlistforbuyed();
                        notifyDataSetChanged();
                        //daidaiGou.listCursorUpdate();
                    }
                }
            });
        }catch (Exception e){
            Log.e(TAG, "onBindViewHolder: error:"+e,null );
        }
        try {
            if (signlist.get(position)){
                holder.recycle_buyedlist_customerlist.setVisibility(View.VISIBLE);
            }else {
                holder.recycle_buyedlist_customerlist.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Log.e(TAG, "onBindViewHolder: recycle_buyedlist_customerlist error:"+e,null );
        }

        try {
            String[] from = new String[]{
                    DaidaigouSqliteHelper.goodsName,
                    DaidaigouSqliteHelper.goodsNumber,
                    DaidaigouSqliteHelper.remarks,
                    DaidaigouSqliteHelper.sellingPrice
            };
            int[] to = new int[]{
                    R.id.itforpayed_goodsname,
                    R.id.itforpayed_goodsnumber,
                    R.id.itforpayed_remarks,
                    R.id.itforpayed_Sellprice
            };
            if (listcursor != null) {
                buyedListCursorAdatper = new BuyedListCursorAdatper(mcontext,
                        R.layout.item_list_forbuyed,
                        listcursor,
                        from,
                        to,
                        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,
                        R.id.checkBox_payed,
                        R.id.checkBox_sended
                );
                //手动计算listView的高度，setLayoutParams 设置高度
                View itemview = buyedListCursorAdatper.getView(0, null, holder.recycle_buyedlist_customerlist);
                itemview.measure(0, 0);
                int totalHeight = itemview.getMeasuredHeight() * buyedListCursorAdatper.getCount();
                ViewGroup.LayoutParams layoutParams = holder.recycle_buyedlist_customerlist.getLayoutParams();
                layoutParams.height = totalHeight + holder.recycle_buyedlist_customerlist.getDividerHeight() * buyedListCursorAdatper.getCount();
                holder.recycle_buyedlist_customerlist.setLayoutParams(layoutParams);
                holder.recycle_buyedlist_customerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent Myintent=new Intent();
                        Myintent.setClass(mcontext, AddList.class);
                        listcursor.moveToPosition(position);
                        Myintent.putExtra("ThevaluestoEdit", id);
                        mcontext.startActivity(Myintent);
                    }
                });
                holder.recycle_buyedlist_customerlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                        new AlertDialog.Builder(mcontext).setTitle("警告")
                                .setMessage("未购买，确认？")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i(TAG, "onClick: id = "+id);
                                        daidaigouSqliteHelper.upDataBuyed(id,"0");
                                        //daidaiGou.listCursorUpdate();
                                    }
                                })
                                .setNegativeButton("否",null)
                                .show();
                        return true;
                    }
                });
                holder.recycle_buyedlist_customerlist.setAdapter(buyedListCursorAdatper);
            } else {
                holder.recycle_buyedlist_customerlist.setAdapter(null);
            }
        }catch (Exception e ){
            Log.e(TAG, "onBindViewHolder: buyedListCursorAdatper error:"+e,null );
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView recycle_buyedlist_customer,recycle_buyedlist_goodsnum,
                recycle_buyedlist_allprice;
        ImageButton recycle_buyedlist_imageButton;
        Button recycle_buyedlist_allfinish;
        //LinearLayout linearLayout_hide;
        ListView recycle_buyedlist_customerlist;

        private MyViewHolder(View view){
            super(view);
            recycle_buyedlist_customer=view.findViewById(R.id.recycle_buyed_text1);
            recycle_buyedlist_goodsnum=view.findViewById(R.id.recycle_buyed_text2);
            recycle_buyedlist_imageButton=view.findViewById(R.id.recycle_buyedlist_imageButton);
            recycle_buyedlist_allfinish=view.findViewById(R.id.recycle_buyedlist_allfinish);
            recycle_buyedlist_allprice=view.findViewById(R.id.recycle_buyed_text3);
            //linearLayout_hide=view.findViewById(R.id.linearLayout3);
            recycle_buyedlist_customerlist=view.findViewById(R.id.recycle_buyedlist_list);
            recycle_buyedlist_customerlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return false;
                }
            });
        }
    }
    class BuyedListCursorAdatper extends SimpleCursorAdapter {
        int checkbox1;
        int checkbox2;
        Cursor mCursor;
        BuyedListCursorAdatper(Context context, int layout, Cursor c, String[] from,
                               int[] to, int flags , int checkbox1, int checkbox2){
            super(context, layout, c, from, to,flags);
            this.checkbox1=checkbox1;
            this.checkbox2=checkbox2;
            mCursor=c;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view= super.getView(position, convertView, parent);
            try {
                CheckBox checkBox1 = view.findViewById(checkbox1);
                CheckBox checkBox2 = view.findViewById(checkbox2);
                checkBox1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mCursor.moveToPosition(position);
                            Long rowId = mCursor.getLong(mCursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
                            Cursor cursor = daidaigouSqliteHelper.searchID(Long.toString(rowId));
                            if (cursor.moveToFirst() && cursor.getCount() == 1) {
                                int index = cursor.getInt(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.payed));
                                if (index == 0) {
                                    daidaigouSqliteHelper.upDataPayed(rowId, "1");
                                } else if (index == 1) {
                                    daidaigouSqliteHelper.upDataPayed(rowId, "0");
                                }
                            }
                            //notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, "checkbox1 onClick: error:" + e, null);
                        }

                    }
                });
                checkBox2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mCursor.moveToPosition(position);
                            Long rowId = mCursor.getLong(mCursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.ID));
                            Cursor cursor = daidaigouSqliteHelper.searchID(Long.toString(rowId));
                            if (cursor.moveToFirst() && cursor.getCount() == 1) {
                                int index = cursor.getInt(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.sended));
                                if (index == 0) {
                                    daidaigouSqliteHelper.upDataSended(rowId, "1", Long.toString(System.currentTimeMillis()));
                                } else if (index == 1) {
                                    daidaigouSqliteHelper.upDataSended(rowId, "0", Long.toString(System.currentTimeMillis()));
                                }
                            }
                            //notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, "checkbox2 onClick: error:" + e, null);
                        }
                    }
                });
                mCursor.moveToPosition(position);
                int indexpayed = mCursor.getInt(mCursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.payed));
                if (indexpayed == 1) {
                    checkBox1.setChecked(true);
                } else if (indexpayed == 0) {
                    checkBox1.setChecked(false);
                }
                int indexsended = mCursor.getInt(mCursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.sended));
                if (indexsended == 1) {
                    checkBox2.setChecked(true);
                } else if (indexsended == 0) {
                    checkBox2.setChecked(false);
                }
            }catch (Exception e){
                Log.e(TAG, "getView: error:"+e,null );
            }
            return view;
        }
    }
}
