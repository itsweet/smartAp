package com.example.lit.smartap_20180111;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.lit.smartap_20180111.Structure.Demo_RecyclerView;
import com.example.lit.smartap_20180111.data.ConnectStatus;
import com.example.lit.smartap_20180111.data.DataMQTT;
import com.example.lit.smartap_20180111.data.IOT_CMD;
import com.example.lit.smartap_20180111.data.MqttSenderListener;

import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private String TAG="MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private LayoutInflater mInflater;
    private TabLayout mTabLayout;
    private List<String> mTitleList=new ArrayList<>();//标题集合
    private View myview1,myview2,myview3,myview4;//页卡视图
    private Toolbar toolbar;
    private View mainView,signinView;//登录页面和主程序页面
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private Cursor mCursor;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private MyPagerAdapter myPagerAdapter;
    private SmartAPSqliteHelper database;
    private SendTool sendTool;
    private TextView mtextView,toolbar_text;
    private PopupWindow popupWindow;
    private RecyclerView recyclerView;
    private MyRecyclerAdpter recyclerAdater;
    private List<String> mdata;
    private int selcetPosition;
    private boolean islogin; //为true 表示登录，为false 表示注册
    private ConnectStatus onlineStatus;
    private boolean isheartbeat = false ; //启动心跳的标志位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater=LayoutInflater.from(this);
        mainView=mInflater.inflate(R.layout.activity_main,null);
        //setContentView(mainView);
        signinView=mInflater.inflate(R.layout.layout_signin,null);
        database=new SmartAPSqliteHelper(this);
        //wifiAdapter.setBroadcastAdress();
        sendTool=new SendTool(this);
        MqttSenderListener<DataMQTT> listener = new MqttSenderListener<DataMQTT>() {
            @Override
            public void statusChange(ConnectStatus status) {
                switch (status){
                    case DISCONNECT:
                        setOnlineStatus(R.string.offline);
                        break;
                    case CONNECTING:
                        setOnlineStatus(R.string.linking);
                        break;
                    case CONNECTED:
                        setOnlineStatus("");
                        break;
                }
            }

            @Override
            public void onSuccess(DataMQTT data) {
                IOT_CMD iot_cmd = data.getCmd();
                switch (iot_cmd){
                    case Symmetric_key:
                        if (!islogin) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    verification_codeDialog("");
                                }
                            });
                        }else {
                            sendTool.interactive(IOT_CMD.Login);
                            //sendTool.signin_action(ConfigList.getUsername(),ConfigList.getUserpwd(),
                            //      null,signinListener);
                        }
                        break;
                    case Login:
                        Log.i(TAG, "Login onSuccess: ");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //sendTool.searchFamily_action("905699025",searchFamily_listener);
                                popupwindowdismiss();
                                startMainActivity();
                            }
                        });
                        break;
                }
            }

            @Override
            public void onFail(int status,final String msg) {
                if (msg.equals(MainActivity.this.getResources().getString(R.string.imei_err))){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            verification_codeDialog(null);
                        }
                    });
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "connectServerListener: fail "+msg);
                        popupwindowdismiss();
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                    }
                });
            }
        } ;
        sendTool.setListener(listener);
        //sendTool.recThread.start();
        popupWindow=new PopupWindow(mInflater.inflate(R.layout.popup_wait,null),
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //popupWindow.setTouchable(true);
        //popupWindow.setOutsideTouchable(true);
        //初始化登录界面
        init_signin();
        initMainActivity();
        //startMainActivity();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        sendTool.onDestroy();
    }

    void setOnlineStatus(final String s){
        handler.post(new Runnable() {
            @Override
            public void run() {
                toolbar_text.setText(s);
            }
        });
    }

    void setOnlineStatus(final int id){
        handler.post(new Runnable() {
            @Override
            public void run() {
                toolbar_text.setText(id);
            }
        });
    }

    /*
    synchronized void setLogin(ConnectStatus status){
        switch (!status) {
            this.toolbar_text.setText(R.string.accout_offline);
        }
    }
    */

    protected void initMainActivity(){
        toolbar = (Toolbar) mainView.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_name));
        toolbar_text = mainView.findViewById(R.id.toolbar_text);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = mainView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = mainView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        mTabLayout=mainView.findViewById(R.id.tablayout);
        mViewPager=mainView.findViewById(R.id.container);
        initdata();
        initTabView();
        Log.i(TAG, "initMainActivity: finish");
    }

    protected void startMainActivity(){
        //setLogin(true);
        isheartbeat = true;
        //toolbar.setTitle(getResources().getString(R.string.title_name));
        //setSupportActionBar(toolbar);
        setContentView(mainView);
        popupwindowdismiss();
    }

    protected void initdata(){
        mdata=new ArrayList<String>();
        for (int i='A';i<'z';i++){
            mdata.add(""+(char)i);
        }
    }

    public void initTabView(){
        //mTabLayout.setVisibility(View.INVISIBLE);
        mTitleList.add(this.getResources().getString(R.string.tab_recentdevice));
        mTitleList.add(this.getResources().getString(R.string.tab_listdevice));
        mTitleList.add(this.getResources().getString(R.string.tab_setting));
        mTitleList.add(this.getResources().getString(R.string.tab_4));
        myview1=mInflater.inflate(R.layout.layout_list,null);
        myview2=mInflater.inflate(R.layout.layout_list,null);
        myview3=mInflater.inflate(R.layout.layout_list,null);
        myview4=mInflater.inflate(R.layout.test,null);
        mViewList.add(myview1);
        mViewList.add(myview2);
        mViewList.add(myview3);
        mViewList.add(myview4);
        myPagerAdapter=new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(myPagerAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
        try {
            mTabLayout.getTabAt(0).setIcon(R.drawable.ic_jilu);
            mTabLayout.getTabAt(1).setIcon(R.drawable.ic_device);
            mTabLayout.getTabAt(2).setIcon(R.drawable.ic_setting);
            mTabLayout.getTabAt(3).setIcon(R.drawable.ic_test);
        }catch (NullPointerException e){
            Log.e(TAG, "mTabLayout: setIcon is null:"+e,null );
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int color =getResources().getColor(R.color.colorGreen);
                try {
                    Drawable drawable = tab.getIcon();
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }catch (NullPointerException e){
                    Log.e(TAG, "onTabSelected: setcolorfilter is null:"+e, null);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int color = getResources().getColor(R.color.colorBlack);
                try{
                    tab.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }catch (NullPointerException e){
                    Log.e(TAG, "onTabUnselected: setcolorfilter is null:"+e,null );
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        recyclerView=myview1.findViewById(R.id.recyclerView);
        //Log.i(TAG, "initTabView: recyclerView findView");
        recyclerView.addItemDecoration(new Demo_RecyclerView(this,
                Demo_RecyclerView.VERTICAL_LIST));
        //Log.i(TAG, "initTabView: recycleView additemdecoration");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(linearLayoutManager);
        //Log.i(TAG, "initTabView: recycleView setLayoutManager");
        recyclerAdater=new MyRecyclerAdpter(this,mdata);
        recyclerAdater.setOnItemClickListener(new MyRecyclerAdpter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Toast.makeText(MainActivity.this,"click on "+postion,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int postion) {
                Toast.makeText(MainActivity.this,"longclick on "+postion,
                        Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(recyclerAdater);
        recyclerView.setHasFixedSize(true);
        //Log.i(TAG, "initTabView: recycleView finish");
        mtextView=(TextView)myview4.findViewById(R.id.textView_log);
        mtextView.setMovementMethod(ScrollingMovementMethod.getInstance());

    }

    //初始化登录界面
    public void init_signin(){
        this.setContentView(signinView);
        final EditText editText1=signinView.findViewById(R.id.editView1);
        final EditText editText2=signinView.findViewById(R.id.editView2);
        final CheckBox checkBox=signinView.findViewById(R.id.checkBox_signin);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()){
                    editText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else {
                    editText2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        checkBox.setChecked(true);
        final Button button=signinView.findViewById(R.id.but_signin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String username = editText1.getText().toString();
                    String userpwd = editText2.getText().toString();
                    islogin = true;
                    //button.setClickable(false);
                    if (username.length()<6){
                        Toast.makeText(MainActivity.this,"用户名必须大于6位",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (userpwd.length()<1){
                        Toast.makeText(MainActivity.this,"密码不能为空",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    ConfigList.setUsername(username);
                    ConfigList.setUserpwd(userpwd);
                    //handler.post(new Runnable() {
                        //@Override
                        //public void run() {
                            popupwindowshow();
                        //}
                    //});
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendTool.connect_def();
                            sendTool.connectserver();

                        }
                    }).start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    //初始化注册界面
    public void init_logon(View view){
        View view_logon=mInflater.inflate(R.layout.layout_logon,null);
        final EditText edi_name=view_logon.findViewById(R.id.edi_name);
        final EditText edi_pwd=view_logon.findViewById(R.id.edi_pwd);
        final EditText edi_confirm=view_logon.findViewById(R.id.edi_confirm);
        edi_confirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
        TextView tosignin=view_logon.findViewById(R.id.tosignin);
        tosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_signin();
            }
        });
        Button but_logon=view_logon.findViewById(R.id.but_logon);
        but_logon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edi_name.getText().toString();
                String pwd=edi_pwd.getText().toString();
                String pwd_confirm=edi_confirm.getText().toString();
                if (name.equals("") || pwd.equals("") || pwd_confirm.equals("")){
                    Toast.makeText(MainActivity.this,
                            "账户、密码不可为空",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (!pwd.equals(pwd_confirm)){
                    Toast.makeText(MainActivity.this,
                            "两次密码输入不相同，请重新输入",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                ConfigList.setUsername(name);
                ConfigList.setUserpwd(pwd);
                islogin = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendTool.connect_def();
                        sendTool.connectserver();
                    }
                }).start();
                popupwindowshow();
                //wifiAdapter.write(new DataObject(sendTool.logon(name,pwd),logonCallBack));
            }
        });
        setContentView(view_logon);
    }



    void signout(){
        popupwindowshow();//启动等待界面
        //wifiAdapter.write(new DataObject(sendTool.logout(),logoutCallBack));
        init_signin();
        popupwindowdismiss();
    }


    /*
    public void showmyview1(){
        Log.i(TAG, "showmyview1: start");
        ListView mListView=(ListView)myview1.findViewById(R.id.listView);
        mCursor=database.getdevicelist();
        if (selectPosition!=-1){
            mListView.setSelection(selectPosition);
        }
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i(TAG, "onScrollStateChanged: scrollState"+scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        List<Map<String,Integer>> mListMap=new LinkedList<>();
        while(mCursor.moveToNext()) {
            String string=mCursor.getString(mCursor.getColumnIndexOrThrow(SmartAPSqliteHelper.baseInfoReadOnlyDef));
            BaseInfoReadOnlyDef mdata=new BaseInfoReadOnlyDef(string);
            mListMap.add(mdata.decode());
        }
        try {
            mListView.setAdapter(new SimpleAdapter(
                    MainActivity.this,
                    mListMap,
                    R.layout.layout_list,
                    new String[]{
                            SmartAPSqliteHelper.baseInfoReadOnlyDef
                    },
                    new int[]{
                            R.id.imageView,
                            R.id.devicename,
                            R.id.power,
                            R.id.singnal
                    }
            ));
        }catch (Exception e){
            Log.e(TAG, "showmyview1: setAdapter error:"+e);
        }
    }
    */

    private StringBuffer stringBuffer=new StringBuffer();

    void addText(String string){
        stringBuffer.append(string+"<br />");
        mtextView.setText(Html.fromHtml(stringBuffer.toString()));
        int offset=mtextView.getLineCount()*mtextView.getLineHeight();
        if (offset>mtextView.getHeight()) {
            mtextView.scrollTo(0, offset-mtextView.getHeight());
        }
    }

    //短信验证码校验框
    private void verification_codeDialog(String vcode){
        View view = mInflater.inflate(R.layout.dialog_timer,null);
        final EditText editText = view.findViewById(R.id.dialogtimer_text);
        final Button button_send = view.findViewById(R.id.dialogtimer_send);
        Button button_login = view.findViewById(R.id.dialogtimer_login);
        if (vcode == null){
            vcode = "";
        }
        editText.setFocusable(true);
        editText.setText(vcode);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        button_send.setText("获取验证码");
        final AlertDialog dialog = builder.show();
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTool.interactive(IOT_CMD.Get_verification_code,new SendTool.STforMainListener(){
                    @Override
                    public void onSuccess( final String s){
                        Log.i(TAG, "Get_verification_code onSuccess: "+s);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(s);
                                ConfigList.setVcode(s);
                            }
                        });
                        sendTool.rmSTforMainListener();
                    }

                    @Override
                    public void onFail(final String s){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                            }
                        });
                        sendTool.rmSTforMainListener();
                    }
                });
                new CountDownTimer(30*1000,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        button_send.setEnabled(false);
                        button_send.setText(String.format("重发(%d S)",millisUntilFinished/1000));
                    }

                    @Override
                    public void onFinish() {
                        button_send.setEnabled(true);
                        button_send.setText("重新获取");
                    }
                }.start();
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.length()<6){
                    Toast.makeText(MainActivity.this,"验证码必须大于6位",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!islogin) {
                    try {
                        //String deviceId = tm.getDeviceId();
                        sendTool.registerAccount_action(ConfigList.getUsername(),
                                ConfigList.getUserpwd(), ConfigList.getVcode(),
                                new SendTool.STforMainListener() {
                                    @Override
                                    public void onSuccess(String s) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this,
                                                        "注册成功",
                                                        Toast.LENGTH_LONG)
                                                        .show();
                                                popupwindowdismiss();
                                                init_signin();
                                            }
                                        });
                                    }
                                    @Override
                                    public void onFail(final String s) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this,
                                                        "注册失败:"+s,
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                                popupwindowdismiss();
                                            }
                                        });
                                    }
                                });
                        ConfigList.setVcode(null);
                        dialog.dismiss();
                    } catch (SecurityException e) {
                        Log.e(TAG, "imeiset getDeviceId error: " + e, null);
                    }
                }else {
                    sendTool.signin_action(ConfigList.getUsername(), ConfigList.getUserpwd(),
                            ConfigList.getVcode(),
                            new SendTool.STforMainListener() {
                                @Override
                                public void onSuccess(final String s) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            startMainActivity();
                                        }
                                    });
                                }

                                @Override
                                public void onFail(final String s) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                    ConfigList.setVcode(null);
                    dialog.dismiss();
                }
            }
        });
    }

    private Handler handler=new Handler();

    void popupwindowshow(){
        if (!popupWindow.isShowing()) {
            View decorView = getWindow().getDecorView();
            popupWindow.showAtLocation(decorView, Gravity.CENTER, 0, 0);
        }
    }
    void popupwindowdismiss(){
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }


    public void test(View view){
        Log.i(TAG, "testshow: start");
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.baidu.com");
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(6000);
                    connection.setReadTimeout(6000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    final StringBuilder stringBuilder = new StringBuilder();
                    while (null != (line = reader.readLine()) ){
                        stringBuilder.append(line);
                    }
                    final WebView webView = myview4.findViewById(R.id.test_web);
                    //webView.getSettings().setDefaultTextEncodingName("UTF-8");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadDataWithBaseURL(null,stringBuilder.toString(),"text/html","utf-8",null);
                        }
                    });
                    connection.disconnect();
                }catch (Exception e ){
                    Log.e(TAG, "test: error:"+e,null );
                }
            }
        }).start();
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                //sendTool.connectserver();
            }
        }).start();
        //sendTool.getMqttDefServer();
        /*
        Button button=(Button)findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes={11,22,33,44,55,66};
                wifiAdapter.sendByte(bytes);
            }
        });
        */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = mainView.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Log.i(TAG, "onNavigationItemSelected: nav_camera");
        } else if (id == R.id.nav_gallery) {
            
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Log.i(TAG, "onNavigationItemSelected: nav_share");
        } else if (id == R.id.nav_send) {
            Log.i(TAG, "onNavigationItemSelected: nav_send");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_adddevices) {

            sendTool.searchFamily_action("905699025",searchFamily_listener);
        }
        if (id == R.id.action_logout){
            isheartbeat=false;
            this.setContentView(signinView);

            //init_signin();
        }

        return super.onOptionsItemSelected(item);
    }
    */

    public void AddDevicesView(View view){
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
    class   MyPagerAdapter extends PagerAdapter {
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
}


