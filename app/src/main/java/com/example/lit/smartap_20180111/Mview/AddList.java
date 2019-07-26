package com.example.lit.smartap_20180111.Mview;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lit.smartap_20180111.ConfigList;
import com.example.lit.smartap_20180111.DaidaigouSqliteHelper;
import com.example.lit.smartap_20180111.R;
import com.example.lit.smartap_20180111.Utils;
import com.example.lit.smartap_20180111.core.MyContentProvider;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2017/9/15.
 */

public class AddList extends AppCompatActivity {

    private EditText mEditText_Number,mEditText_Remarks,mEditText_Price,mEditText_Selling,
            mEditText_Purchase,mEditText_Rate,mEditText_Profit,mEditText_Address,
            mEditText_AddressName;
    private AutoCompleteTextView mEditText_Customer,mEditText_Name,mEditText_Category;
    private EditText mEditText_query;
    private Button mButton_query,mButton_insert,mButton_add,mButton_reduce,mButton_add2;
    private LinearLayout insert_editCustomer_AutoText,insert_linearlayout_foradd;
    private Cursor mCursor_query;
    private String goods_name,goods_category,goods_number,customer,remarks,goods_price,
            selling_price,purchase_price,profit,sellTime,buyed,payed,exchange_rate,
            address,mobile,address_name;
    private TextView mTextView_profit;
    public DaidaigouSqliteHelper daidaigouSqliteHelper;
    private ListView query_ListView,insert_ListView;
    private String TAG="AddList";
    private Long myid;
    private Cursor mCursor;//游标，查询后返回的对象
    private SimpleCursorAdapter adapter;//ListView的适配器
    int mflag;
    /***
    public AddList(DaidaigouSqliteHelper db){
        this.daidaigouSqliteHelper=db;
    }
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        daidaigouSqliteHelper=DaidaigouSqliteHelper.getInstance(this);
        setContentView(R.layout.addlist);
        findViewsAndSetListener();
        Log.i(TAG, "onCreate: over");
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void findViewsAndSetListener()
    {
        EditText editTexts[]={
                mEditText_Name=(AutoCompleteTextView)this.findViewById(R.id.insert_editName_id),
                //mEditText_Number=(EditText)this.findViewById(R.id.insert_editNumber_id),
                mEditText_Category=(AutoCompleteTextView)this.findViewById(R.id.insert_editCategory_id),
                //mEditText_Customer=(AutoCompleteTextView )this.findViewById(R.id.insert_editCustomer_id),
                mEditText_Remarks=(EditText)this.findViewById(R.id.insert_editRemark_id),
                mEditText_Selling=(EditText)this.findViewById(R.id.insert_sellingPrice_id),
                mEditText_Rate=(EditText)this.findViewById(R.id.insert_exchangeRate_id),
                mEditText_Purchase=(EditText)this.findViewById(R.id.insert_purchasePrice_id),
                //mEditText_query=(EditText)this.findViewById(R.id.query_edit_id)
        };

        mTextView_profit=(TextView)this.findViewById(R.id.textView_profit_value);
        //mButton_query=(Button)this.findViewById(R.id.query_button_id);
        mButton_insert=(Button)this.findViewById(R.id.insert_button_id);
        //mButton_add=(Button)this.findViewById(R.id.button_numberadd) ;
        //mButton_reduce=(Button)this.findViewById(R.id.button_numberreduce);
        mButton_add2 = this.findViewById(R.id.button_add2);
        //insert_editCustomer_AutoText = this.findViewById(R.id.insert_editCustomer_AutoText);
        insert_linearlayout_foradd = findViewById(R.id.insert_linearlayout_foradd);
        insert_linearlayout_foradd.addView(getaddlinearView());
        //mEditText_Number.addTextChangedListener(numberTextChangeListenter);
        mEditText_Selling.addTextChangedListener(sellingpriceTextChangeListenter);
        mEditText_Purchase.addTextChangedListener(purchasepriceTextChangeListenter);
        //mEditText_Name.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.autocomtextview,)));
        Log.i(TAG, "findViewsAndSetListener: name setadapter");
        mEditText_Name.setAdapter(
                new ArrayAdapter<>(getApplicationContext(),
                        R.layout.autocomtextview,
                        search_array(DaidaigouSqliteHelper.goodsName)
                )
        );
        Log.i(TAG, "findViewsAndSetListener: category setadapter");
        mEditText_Category.setAdapter(
                new ArrayAdapter<>(getApplicationContext(),
                        R.layout.autocomtextview,
                        search_array(DaidaigouSqliteHelper.goodsCategory)));
        /*
        mEditText_Customer.setAdapter(
                new ArrayAdapter<>(getApplicationContext(),
                        R.layout.autocomtextview,
                        search_array(DaidaigouSqliteHelper.Customer)));
                        */
        //query_ListView=(ListView)this.findViewById(R.id.query_list_id);
        //insert_ListView=(ListView)this.findViewById(R.id.insert_list_id);

        //mButton_query.setOnClickListener(queryButton_OnClickListener);
        mButton_insert.setOnClickListener(insertButton_OnClickListener);
        //mButton_add.setOnClickListener(addbutton_OnClickListener);
        //mButton_reduce.setOnClickListener(reducebutton_OnClickListener);

        myid=getIntent().getLongExtra("ThevaluestoEdit",-1);
        mButton_add2 .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myid>-1){
                    return;
                }
                if (check_add()){
                    return;
                }
                //insert_editCustomer_AutoText.addView(linearLayout);
                insert_linearlayout_foradd.addView(getaddlinearView());
            }
        });
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].setOnClickListener(listener);
        }
        //mEditText_query.setOnClickListener(listener);
        //mEditText_query.setOnKeyListener(keyListener);
        //mEditText_Number.setText(R.string.default_number);//初始化number为1
        mEditText_Rate.setText(String.valueOf(ConfigList.getRate())); //初始利率

        //获取传来的id，查询数据然后显示在页面
        if (myid!=-1)
        {
            //ContentValues values= daidaigouSqliteHelper.valueSpecificId(myid);
            //Cursor cursor = getContentResolver().query(MyContentProvider.Query_name.searchID.getUrivalue(),
              //      null,null,new String[] {String.valueOf(myid)},null);
            Cursor cursor = daidaigouSqliteHelper.searchID(String.valueOf(myid));
            cursor.moveToFirst();
            String s= cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.goodsName));
            mEditText_Name.setText(s);
            View view=insert_linearlayout_foradd.getChildAt(0);
            EditText number = view.findViewById(R.id.linear_editNumber_id);
            EditText customer =view.findViewById(R.id.linear_editCustomer_id);
            number.setText(cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.goodsNumber)));
            mEditText_Category.setText(cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.goodsCategory)));
            customer.setText(cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.Customer)));
            mEditText_Remarks.setText(cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.remarks)));
            mEditText_Selling.setText(cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.sellingPrice)));
            mEditText_Rate.setText(cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.exchangeRate)));
            mEditText_Purchase.setText(cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.purchasePrice)));
            buyed=cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.buyed));
            payed=cursor.getString(cursor.getColumnIndex(DaidaigouSqliteHelper.payed));
        }
    }

    View getaddlinearView(){
        View addview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.linearlayout_numberadd,null);
        AutoCompleteTextView autoText = addview.findViewById(R.id.linear_editCustomer_id);
        Log.i(TAG, "getaddlinearView: setadapter");
        autoText.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                R.layout.autocomtextview,
                search_array(DaidaigouSqliteHelper.Customer)));
        autoText.setThreshold(1);
        final EditText autoNumber = addview.findViewById(R.id.linear_editNumber_id);
        autoNumber.setText("1"); //默认数量为1
        autoNumber.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editstart;
            private int editend;
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp=s;
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable s) {
                editstart=autoNumber.getSelectionStart();
                Log.i(TAG, "afterTextChanged: editstart:"+editstart);
                editend=autoNumber.getSelectionEnd();
                Log.i(TAG, "afterTextChanged: editend:"+editend);
                if (temp.length()>3){
                    Toast.makeText(AddList.this,R.string.Toast_numberwarn, Toast.LENGTH_SHORT).show();

                    s.delete(editstart-1,editend);
                    int tempselection=editstart;
                    autoNumber.setText(s);
                    autoNumber.setSelection(tempselection);
                }
                int num =isProfitValid();
                if(num != -1){
                    Log.i(TAG, "afterTextChanged: start calculate profit");
                    calculateProfit(num);
                }

            }
        });
        Button autoButton_add = addview.findViewById(R.id.linear_button_numberadd);
        autoButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoNumber.requestFocus();
                addNumber(autoNumber,true);
            }
        });
        final Button autoButton_reduce = addview.findViewById(R.id.linear_button_numberreduce);
        autoButton_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoNumber.requestFocus();
                addNumber(autoNumber,false);
            }
        });
        return addview;
    }

    boolean check_add(){
        int num = insert_linearlayout_foradd.getChildCount();
        View child = insert_linearlayout_foradd.getChildAt(--num);
        EditText autotext =child.findViewById(R.id.linear_editCustomer_id);
        EditText autonum = child.findViewById(R.id.linear_editNumber_id);
        String s1 = autotext.getText().toString();
        String s2 = autonum.getText().toString();
        return s1.equals("")||s2.equals("");
    }

    View.OnClickListener insertButton_OnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: insertMethed");
            if(!isSaveValid()){
                return;
            }
            insertMethod(getContentValues(getCustomersandNumber()));
        }
    };
    View.OnClickListener addbutton_OnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int editstart=mEditText_Number.getSelectionStart();
            int editend=mEditText_Number.getSelectionEnd();
            //Log.i(TAG, "onClick addbutton: "+editstart+","+editend);
            mEditText_Number.requestFocus();
            
            addNumber(mEditText_Number,true);
            //mEditText_Number.setSelection(editend);
        }
    };
    View.OnClickListener reducebutton_OnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mEditText_Number.requestFocus();
            addNumber(mEditText_Number,false);
        }
    };

    TextWatcher sellingpriceTextChangeListenter=new TextWatcher() {
        private CharSequence temp;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            int num =isProfitValid();
            if (num!=-1){
                calculateProfit(num);
            }
        }
    };
    TextWatcher purchasepriceTextChangeListenter=new TextWatcher() {
        private CharSequence temp;
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            int num = isProfitValid();
            if (num!=-1){calculateProfit(num);}
        }
    };

    String[][] getCustomersandNumber(){
        int num = insert_linearlayout_foradd.getChildCount();
        String[][] list= new String[num][2];
        for (int i = 0;i<num;i++) {
            View child =insert_linearlayout_foradd.getChildAt(i);
            EditText editText1 = child.findViewById(R.id.linear_editCustomer_id);
            EditText editText2 = child.findViewById(R.id.linear_editNumber_id);
            String customer = editText1.getText().toString();
            String number = editText2.getText().toString();
            if (customer.equals("") || number.equals("")) {
                continue;
            }
            list[i][0]=customer;
            list[i][1]=number;
        }
        return list;
    }

    ContentValues[] getContentValues (String[][] customers){
        List<ContentValues> list = new ArrayList<>();
        for (int i = 0; i< customers.length;i++
             ) {
            ContentValues values = new ContentValues();
            values.put(DaidaigouSqliteHelper.buyed,0);
            values.put(DaidaigouSqliteHelper.payed,0);
            values.put(DaidaigouSqliteHelper.sended,0);
            values.put(DaidaigouSqliteHelper.goodsName,goods_name);
            values.put(DaidaigouSqliteHelper.goodsCategory,goods_category);
            values.put(DaidaigouSqliteHelper.goodsNumber,customers[i][1]);
            values.put(DaidaigouSqliteHelper.remarks,remarks);
            values.put(DaidaigouSqliteHelper.sellingPrice,selling_price);
            values.put(DaidaigouSqliteHelper.purchasePrice,purchase_price);
            values.put(DaidaigouSqliteHelper.exchangeRate,exchange_rate);
            values.put(DaidaigouSqliteHelper.profit,profit);
            values.put(DaidaigouSqliteHelper.sellTime,sellTime);
            values.put(DaidaigouSqliteHelper.Customer,customers[i][0]);
            list.add(values);
        }
        return list.toArray(new ContentValues[0]);
    }

    //新增数据
    public void insertMethod(ContentValues[] customers)
    {
        //String im_goodname=goods_name,im_customer=customer,im_goodscategory=goods_category,im_remarks=remarks;
        //int im_goodsnumber=Integer.valueOf(goods_number),im_mobile=Integer.valueOf(mobile);
        //float im_sellingprice=Float.valueOf(selling_price),im_purchaseprice=Float.valueOf(purchase_price),
        //		im_rate=Float.valueOf(exchange_rate),im_profit=Float.valueOf(profit);
        sellTime= String.valueOf( System.currentTimeMillis());
        if (exchange_rate!=null&&exchange_rate!="0") {
            ConfigList.setRate(Double.parseDouble(exchange_rate));
        }
        if (myid==-1)
        //Cursor c=daidaigouSqliteHelper.isHaveThisStu(Integer.valueOf(goods_number));
        {
            Log.i(TAG, "insertdatebase:");
            /***
            daidaigouSqliteHelper.insertData(goods_name, customer, goods_category,
                    goods_number, remarks, selling_price,
                    purchase_price, exchange_rate, profit,
                     sellTime);
            ***/
            for (ContentValues customer:customers
                 ) {
                getContentResolver().insert(MyContentProvider.Uri_insert,customer);
            }
            Log.i(TAG, "finish insertdatebase");
            Toast.makeText(this,"Save OK!", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            /*
            ContentValues values = new ContentValues();
            values.put(DaidaigouSqliteHelper.ID,myid);
            values.put(DaidaigouSqliteHelper.goodsName,goods_name);
            values.put(DaidaigouSqliteHelper.goodsCategory,goods_category);
            values.put(DaidaigouSqliteHelper.goodsNumber,goods_number);
            values.put(DaidaigouSqliteHelper.Customer,customer);
            values.put(DaidaigouSqliteHelper.remarks,remarks);
            values.put(DaidaigouSqliteHelper.sellingPrice,selling_price);
            values.put(DaidaigouSqliteHelper.purchasePrice,purchase_price);
            values.put(DaidaigouSqliteHelper.exchangeRate,exchange_rate);
            values.put(DaidaigouSqliteHelper.profit,profit);
            values.put(DaidaigouSqliteHelper.sellTime,sellTime);
            values.put(DaidaigouSqliteHelper.buyed,buyed);
            values.put(DaidaigouSqliteHelper.payed,payed);
            getContentResolver().update(MyContentProvider.Uri_update,values,null,null);
            */
            LinearLayout child = (LinearLayout) insert_linearlayout_foradd.getChildAt(0);
            TextView textView = child.findViewById(R.id.linear_editCustomer_id);
            customer=textView.getText().toString();
            daidaigouSqliteHelper.upDatainfo(myid,goods_name, goods_category,goods_number,customer,
                    remarks, selling_price, purchase_price, exchange_rate, profit,
                    sellTime,buyed,payed);

            Toast.makeText(this,"Update OK!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //查询所有联系人，并转换成数组返回
    public String[] search_array(String s){
        Cursor cursor =null;
        List<String> list_customer=new ArrayList<String>();
        switch (s) {
            case DaidaigouSqliteHelper.goodsName:
                cursor = daidaigouSqliteHelper.searchgoodsname();
                //cursor = getContentResolver().query(MyContentProvider.Query_name.searchgoodsname.getUrivalue(),
                  //      null,null,null,null);
                if (cursor!=null&&cursor.getCount()>0) {
                    cursor.moveToFirst();
                    do {
                        try {
                            list_customer.add(cursor.getString(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.goodsName)));
                        }catch (Exception e){
                            Toast.makeText(this,"add error"+e, Toast.LENGTH_LONG).show();
                        }
                    } while (cursor.moveToNext());
                }
                else {
                    return new String[]{};
                }
                break;
            case DaidaigouSqliteHelper.goodsCategory:
                cursor=daidaigouSqliteHelper.searchCategory();
                //cursor = getContentResolver().query(MyContentProvider.Query_name.searchCategory.getUrivalue()
                  //      ,null,null,null,null);
                if (cursor!=null&&cursor.getCount()>0) {
                    cursor.moveToFirst();
                    do {
                        try {
                            list_customer.add(cursor.getString(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.goodsCategory)));
                        }catch (Exception e){
                            Toast.makeText(this,"add error"+e, Toast.LENGTH_LONG).show();
                        }
                    } while (cursor.moveToNext());
                }
                else {
                    return new String[]{};
                }
                break;
            case DaidaigouSqliteHelper.Customer:
                cursor=daidaigouSqliteHelper.searchcustomer();
                //cursor = getContentResolver().query(MyContentProvider.Query_name.searchcustomer.getUrivalue(),
                  //      null,null,null,null);
                if (cursor!=null&&cursor.getCount()>0) {
                    cursor.moveToFirst();
                    do {
                        try {
                            list_customer.add(cursor.getString(cursor.getColumnIndexOrThrow(DaidaigouSqliteHelper.Customer)));
                        }catch (Exception e){
                            Toast.makeText(this,"add error"+e, Toast.LENGTH_LONG).show();
                        }
                    } while (cursor.moveToNext());
                }
                else {
                    return new String[]{};
                }
                break;
        }
        String[] array_customer=new String[list_customer.size()];
        list_customer.toArray(array_customer);
        return array_customer;
    }



    //flag =true 表示加，false 表示减
    public void addNumber(EditText number, boolean flag)
    {
        String string_number;
        string_number=number.getText().toString();
        int num;
        if (string_number.equalsIgnoreCase(""))
        {
            num=0;
        }else {
            num= Integer.valueOf(string_number);
        }
        if(flag) {
            num++;
        }else {
            num--;
        }
        string_number= String.valueOf(num);
        number.setText(string_number);
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.insert_editCategory_id:
                    mEditText_Category.setHint("");
                    break;
                case R.id.insert_editName_id:
                    mEditText_Name.setHint("");
                    break;

                default:
                    break;
            }
        }
    };
    //判断profit是否合法计算
    public int isProfitValid(){
        goods_number=null;selling_price=null;exchange_rate=null;purchase_price=null;
        int num = insert_linearlayout_foradd.getChildCount();
        int allnum =0;
        for (int i=0; i<num;i++){
            View view=insert_linearlayout_foradd.getChildAt(i);
            EditText editText =(EditText) view.findViewById(R.id.linear_editNumber_id);
            String number = editText.getText().toString();
            if (!number.equals("")) {
                allnum += Integer.valueOf(number);
            }
        }
        goods_number= String.valueOf(allnum);
        //Log.i(TAG, "isProfitValid: goods_number= "+goods_number);
        selling_price=mEditText_Selling.getText().toString();
        //Log.i(TAG, "isProfitValid: selling_price= "+selling_price);
        exchange_rate=mEditText_Rate.getText().toString();
        //Log.i(TAG, "isProfitValid: exchange_rate= "+exchange_rate);
        purchase_price=mEditText_Purchase.getText().toString();
        //Log.i(TAG, "isProfitValid: purchase_price= "+purchase_price);
        if (goods_number.equalsIgnoreCase("")||selling_price.equalsIgnoreCase("")||
                exchange_rate.equalsIgnoreCase("")||purchase_price.equalsIgnoreCase("")){
            //Log.i(TAG, "isProfitValid: is false");
            return -1;

        }else {
            //Log.i(TAG, "isProfitValid: is true");
            //Until.exchange_rate=exchange_rate;
            return allnum;

        }
    }
    //计算利润profit
    public void calculateProfit(int num){
        //goods_number=mEditText_Number.getText().toString();
        selling_price=mEditText_Selling.getText().toString();
        exchange_rate=mEditText_Rate.getText().toString();
        purchase_price=mEditText_Purchase.getText().toString();
        Log.i(TAG, "calculateProfit: "+num+","+selling_price+","+exchange_rate+","+
                purchase_price+".");
        float goodsnumber= Float.valueOf(num);
        float sellprice= Float.valueOf(selling_price);
        float rate= Float.valueOf(exchange_rate);
        float puchaseprice= Float.valueOf(purchase_price);
        float profitint=Utils.mul(puchaseprice,rate);
        profitint=Utils.sub(sellprice,profitint);
        profitint=Utils.mul(profitint,goodsnumber);
        Log.i(TAG, "calculateProfit: init calculate");
        profit= String.valueOf(profitint);
        Log.i(TAG, "calculateProfit: "+profit);
        mTextView_profit.setText(profit);

    }

    //保存是否合法，检测客户名、数量、商品名
    public boolean  isSaveValid()
    {
        Log.i(TAG, "isSaveValid: start validate");
        goods_name=null;customer=null;
        goods_name=mEditText_Name.getText().toString().trim();
        goods_category=mEditText_Category.getText().toString().trim();
        remarks=mEditText_Remarks.getText().toString().trim();
        selling_price=mEditText_Selling.getText().toString().trim();
        exchange_rate=mEditText_Rate.getText().toString().trim();
        purchase_price=mEditText_Purchase.getText().toString().trim();
        Log.i(TAG, "isSaveValid: goods_name:"+goods_name);
        if (goods_name.equalsIgnoreCase("")){
            Toast.makeText(this,R.string.Toast_checkname, Toast.LENGTH_SHORT).show();
            return false;
        }

        //goods_number=mEditText_Number.getText().toString();
        Log.i(TAG, "isSaveValid: goods_number:"+goods_number);
        if (!check_customerandnumber()){
            Toast.makeText(this,R.string.Toast_checkcustomerandnumber, Toast.LENGTH_SHORT).show();
            return false;
        }
        //goods_category=mEditText_Category.getText().toString().trim();
        /*
        customer=mEditText_Customer.getText().toString().trim();
        Log.i(TAG, "isSaveValid: customer:"+customer);
        if (customer.equalsIgnoreCase("")){
            Toast.makeText(this,R.string.Toast_checkcustomer,Toast.LENGTH_SHORT).show();
            return false;
        }
           */
        Log.i(TAG, "isSaveValid: is true");
        return true;
    }

    boolean check_customerandnumber(){
        int num = insert_linearlayout_foradd.getChildCount();
        for (int i = 0;i<num ;i++){
            View child = insert_linearlayout_foradd.getChildAt(i);
            EditText customer= child.findViewById(R.id.linear_editCustomer_id);
            EditText number = child.findViewById(R.id.linear_editNumber_id);
            String cus = customer.getText().toString();
            String numb = number.getText().toString();
            if (cus.equals("")||numb.equals("")){
                return false;
            }
        }
        return true;
    }

    public void  update()
    {
        //_id=0;
        mEditText_Name.setText("");
        Log.i(TAG,"mEditText_Name"+mEditText_Name.getText().toString());
        mEditText_Number.setText("1");//初始值都是1
        mEditText_Category.setText("");
        mEditText_Purchase.setText("");
        mEditText_Customer.setText("");
        mEditText_Selling.setText("");
        mEditText_Rate.setText(String.valueOf(ConfigList.getRate()));
        setHintText();
        Log.i(TAG, "update: finish sethinttext");
        //mCursor.requery();
        //adapter.notifyDataSetChanged();
        //设置文本编辑区不可以编辑
        /*
        if (Until.isInsert()) {
            mEditText_Name.setEnabled(true);
            mEditText_Number.setEnabled(true);
            mEditText_Category.setEnabled(true);
            mEditText_Customer.setEnabled(true);
        } else {
            mEditText_Name.setEnabled(false);
            mEditText_Number.setEnabled(false);
            mEditText_Category.setEnabled(false);
            mEditText_Customer.setEnabled(false);
        }
        */
    }
    //提示语为空
    public void setHintEmptyText()
    {
        mEditText_Name.setHint("");
        mEditText_Customer.setHint("");
        mEditText_Category.setHint("");

    }
    //设置提示语
    public void setHintText()
    {
        mEditText_Name.setHint(getResources().getString(R.string.add_editName_hint));
        mEditText_Customer.setHint(getResources().getString(R.string.add_editCustomer_hint));
        mEditText_Category.setHint(getResources().getString(R.string.add_editCategory_hint));
    }
}
