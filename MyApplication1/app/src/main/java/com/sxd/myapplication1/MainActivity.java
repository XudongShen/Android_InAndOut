package com.sxd.myapplication1;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


import static com.sxd.myapplication1.SpSaveRead.read;
import static com.sxd.myapplication1.SpSaveRead.getCurrentUserName;
import static com.sxd.myapplication1.SpSaveRead.readLocalSetting;
import static com.sxd.myapplication1.SpSaveRead.save;
import static com.sxd.myapplication1.SpSaveRead.saveLocalSetting;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NotificationManager manager;
    private final int NOTIFICATION_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        SpSaveRead.setContext(getApplicationContext());

        manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(!MyConst.login) {
            MyConst.settings_Remember = readLocalSetting("settingsRemember").isEmpty() ? "on" : readLocalSetting("settingsRemember");
            MyConst.UserName = MyConst.settings_Remember.equals("off") ? "No User" : (getCurrentUserName().isEmpty() ? "No User" : getCurrentUserName());
            if(MyConst.UserName.equals("No User")){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Main2Activity.class);
                MainActivity.this.startActivity(intent);
                finish();
            }
        }
        else{
            MyConst.UserName = getCurrentUserName().isEmpty() ? "No User" : getCurrentUserName();
            MyConst.login =false;
        }
        init();
    }

    public void init(){
        InitTask initTask = new InitTask();
        initTask.execute();
    }

    private class InitTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {
            String temp;
            long id;
            ReadAndSave server = new ReadAndSave();
            temp = server.readUnderUser("maxRecordId");
            save("maxRecordId",temp);
            id = temp.isEmpty() ? 0 : Long.parseLong(temp);
            MyConst.maxRecordId = id;
            temp = server.readUnderUser("myPlanDailyPlan");
            save("myPlanDailyPlan",temp);
            temp = server.readUnderUser("myPlanMonthlyPlan");
            save("myPlanMonthlyPlan",temp);
            while(id>0){
                temp = server.readUnderUser("Record"+id);
                save("Record"+id,temp);
                id--;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            homePageInit();
            inAndOutInit();
            myPlanInit();
            settingsInit();

            test("Initial success");
        }
    }

    public void saveOnServer(String title,String content){
        SaveServer saveServer = new SaveServer();
        saveServer.execute(title,content);
    }

    private class SaveServer extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            ReadAndSave server = new ReadAndSave();
            return server.saveUnderUser(strings[0],strings[1]);
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s)
                test("Upload success");

        }
    }

    public void homePageInit(){

        homePageRefresh();

        Button btnHomePageCost = (Button)findViewById(R.id.button_cost);
        Button btnHomePageIncome = (Button)findViewById(R.id.button_income);

        btnHomePageCost.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EditText editTextAmount = (EditText)findViewById(R.id.edittextamount);
                EditText editTextInfo = (EditText)findViewById(R.id.edittextinfo);
                if(!editTextAmount.getText().toString().isEmpty()&&!editTextInfo.getText().toString().isEmpty()&&!MyConst.UserName.equals("No User")) {
                    double amount = Double.parseDouble(editTextAmount.getText().toString());
                    String info = editTextInfo.getText().toString();
                    MyRecord myRecord = new MyRecord(MyRecord.COST, amount, info);
                    myRecord.recordSave();
                    saveOnServer("maxRecordId",""+MyConst.maxRecordId);
                    saveOnServer("Record"+myRecord.getId(),myRecord.getStoredString());
                    homePageRefresh();
                    inAndOutRefresh();
                    editTextAmount.setText("");
                    editTextInfo.setText("");
                }
                else{
                    if(MyConst.UserName.equals("No User")){
                        test("please login first");
                    }
                    else if(editTextAmount.getText().toString().isEmpty()) {
                        test("please enter the amount");
                    }
                    else{
                        test("please enter the information");
                    }
                }
            }
        });

        btnHomePageIncome.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EditText editTextAmount = (EditText)findViewById(R.id.edittextamount);
                EditText editTextInfo = (EditText)findViewById(R.id.edittextinfo);
                if(!editTextAmount.getText().toString().isEmpty()&&!editTextInfo.getText().toString().isEmpty()&&!MyConst.UserName.equals("No User")) {
                    double amount = Double.parseDouble(editTextAmount.getText().toString());
                    String info = editTextInfo.getText().toString();
                    MyRecord myRecord = new MyRecord(MyRecord.INCOME, amount, info);
                    myRecord.recordSave();
                    saveOnServer("maxRecordId",""+MyConst.maxRecordId);
                    saveOnServer("Record"+myRecord.getId(),myRecord.getStoredString());
                    homePageRefresh();
                    inAndOutRefresh();
                    editTextAmount.setText("");
                    editTextInfo.setText("");
                }
                else{
                    if(MyConst.UserName.equals("No User")){
                        test("please login first");
                    }
                    else if(editTextAmount.getText().toString().isEmpty()) {
                        test("please enter the amount");
                    }
                    else{
                        test("please enter the information");
                    }
                }
            }
        });

    }

    public void homePageRefresh(){
        TextView textView1 = (TextView)findViewById(R.id.recentrecord1);
        TextView textView2 = (TextView)findViewById(R.id.recentrecord2);
        TextView textView3 = (TextView)findViewById(R.id.recentrecord3);

        TextView textView4 = (TextView)findViewById(R.id.homepage_today_cost);
        TextView textView5 = (TextView)findViewById(R.id.homepage_month_cost);

        MyConst.myPlanDailyPlan = read("myPlanDailyPlan").isEmpty()? 100 : Integer.parseInt(read("myPlanDailyPlan"));
        MyConst.myPlanMonthlyPlan = read("myPlanMonthlyPlan").isEmpty()? 3000 : Integer.parseInt(read("myPlanMonthlyPlan"));

        double todayCost = 0;
        double monthCost = 0;
        long id = MyConst.maxRecordId;

        if(MyConst.maxRecordId>0) {
            MyRecord myRecord1 = new MyRecord(MyConst.maxRecordId);
            textView1.setText(myRecord1.getDisplayInHomePage());
        }
        if(MyConst.maxRecordId>1){
            MyRecord myRecord2 = new MyRecord(MyConst.maxRecordId-1);
            textView2.setText(myRecord2.getDisplayInHomePage());
        }
        if(MyConst.maxRecordId>2){
            MyRecord myRecord3 = new MyRecord(MyConst.maxRecordId-2);
            textView3.setText(myRecord3.getDisplayInHomePage());
        }

        while(id>0){
            MyRecord myRecord = new MyRecord(id);

            if(MyCalendar.isThisDay(myRecord.getTime())&&myRecord.getCostOrIncome()==MyRecord.COST){
                todayCost+=myRecord.getAmount();
            }
            if(MyCalendar.isThisMonth(myRecord.getTime())&&myRecord.getCostOrIncome()==MyRecord.COST){
                monthCost+=myRecord.getAmount();
            }
            else{
                if(myRecord.getCostOrIncome()==MyRecord.COST)
                    break;
            }
            id--;
        }

        textView4.setText(""+todayCost);
        textView5.setText(""+monthCost);
        textView4.setTextColor(Color.BLACK);
        textView5.setTextColor(Color.BLACK);

        if(todayCost>MyConst.myPlanDailyPlan||monthCost>MyConst.myPlanMonthlyPlan){
            if(MyConst.settings_Attention.equals("on")) {
                manager.cancel(NOTIFICATION_ID);
                sendNotification();
            }
            if(todayCost>MyConst.myPlanDailyPlan)
                textView4.setTextColor(Color.RED);
            if(monthCost>MyConst.myPlanMonthlyPlan)
                textView5.setTextColor(Color.RED);
        }
    }

    @SuppressLint("NewApi")
    private void sendNotification(){

        Notification.Builder builder=new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.icon);
        builder.setTicker("Hey");
        builder.setContentTitle("Warning");
        builder.setContentText("Cost too much");
        builder.setDefaults(Notification.DEFAULT_ALL);
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0, intent,0);
        builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        manager.notify(NOTIFICATION_ID, notification);
    }

    public void inAndOutInit(){
        Spinner spinnerYear = (Spinner)findViewById(R.id.spinnerYear);
        Spinner spinnerMonth = (Spinner)findViewById(R.id.spinnerMonth);
        Spinner spinnerDay = (Spinner)findViewById(R.id.spinnerDay);

        String[] arrYear = new String[6];
        arrYear[0] = "All";
        for(int i = 1;i<6;i++){
            int year = MyCalendar.getCurrentYear() + 1 - i;
            arrYear[i] = "" + year;
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 , arrYear);
        spinnerYear.setAdapter(adapterYear);

        String[] arrMonth = new String[1];
        arrMonth[0] = "All";
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 , arrMonth);
        spinnerMonth.setAdapter(adapterMonth);

        String[] arrDay = new String[1];
        arrDay[0] = "All";
        ArrayAdapter<String> adapterDay = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 , arrDay);
        spinnerDay.setAdapter(adapterDay);

        inAndOutRefresh();

        ShakeListener shakeListener = new ShakeListener(this);
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {

            public void onShake() {
                if(MyConst.spinner_Year!=0&&MyConst.spinner_Month!=0&&MyConst.spinner_Day!=0&&MyConst.current_page==MyConst.INANDOUT){
                    int dayCount = MyCalendar.getDayCountInMonth(MyConst.spinner_Year,MyConst.spinner_Month);
                    if(MyConst.spinner_Day<dayCount){
                        Spinner spinner = (Spinner)findViewById(R.id.spinnerDay);
                        MyConst.spinner_Day++;
                        spinner.setSelection(MyConst.spinner_Day);
                        inAndOutRefresh();
                    }
                }
            }
        });

        spinnerYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MyConst.spinner_Year = (i==0) ? 0 : MyCalendar.getCurrentYear() + 1 - i;
                MyConst.spinner_Year_Changed = true;
                inAndOutRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MyConst.spinner_Month = i;
                MyConst.spinner_Month_Changed = true;
                inAndOutRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDay.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MyConst.spinner_Day = i;
                inAndOutRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void inAndOutRefresh(){
        Spinner spinnerMonth = (Spinner)findViewById(R.id.spinnerMonth);
        Spinner spinnerDay = (Spinner)findViewById(R.id.spinnerDay);
        if(MyConst.spinner_Year_Changed&&MyConst.spinner_Year==0) {
            String[] arrMonth = new String[1];
            arrMonth[0] = "All";
            ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, arrMonth);
            spinnerMonth.setAdapter(adapterMonth);
            MyConst.spinner_Month = 0;
        }
        else if(MyConst.spinner_Year_Changed&&MyConst.spinner_Year!=0){
            String[] arrMonth = new String[13];
            arrMonth[0] = "All";
            for(int i=1;i<=12;i++){
                arrMonth[i] = ""+i;
            }
            ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, arrMonth);
            spinnerMonth.setAdapter(adapterMonth);
            MyConst.spinner_Month = 0;
        }
        if(MyConst.spinner_Year_Changed||MyConst.spinner_Month_Changed) {
            if(MyConst.spinner_Month == 0) {
                String[] arrDay = new String[1];
                arrDay[0] = "All";
                ArrayAdapter<String> adapterDay = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, arrDay);
                spinnerDay.setAdapter(adapterDay);
                MyConst.spinner_Day = 0;
            }
            else{
                int dayCount = MyCalendar.getDayCountInMonth(MyConst.spinner_Year,MyConst.spinner_Month);
                String[] arrDay = new String[dayCount+1];
                arrDay[0] = "All";
                for(int i=1;i<=dayCount;i++){
                    arrDay[i] = ""+i;
                }
                ArrayAdapter<String> adapterDay = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, arrDay);
                spinnerDay.setAdapter(adapterDay);
                MyConst.spinner_Day = 0;
            }
        }

        MyConst.spinner_Year_Changed = false;
        MyConst.spinner_Month_Changed = false;

        ListView listView = (ListView)findViewById(R.id.list);
        ArrayList<String> arrayList = new ArrayList<String>();
        long id = MyConst.maxRecordId;

        while(id>0){
            MyRecord myRecord = new MyRecord(id);
            if(MyConst.spinner_Year == 0){
                arrayList.add(myRecord.getDisplayString());
            }
            else if(MyConst.spinner_Month == 0){
                String chooseYear = ""+MyConst.spinner_Year+'年';
                if(MyCalendar.isChoosenYear(chooseYear,myRecord.getTime())){
                    arrayList.add(myRecord.getDisplayString());
                }
            }
            else if(MyConst.spinner_Day == 0){
                String chooseMonth = ""+MyConst.spinner_Year+'年'+MyConst.spinner_Month+'月';
                if(MyCalendar.isChoosenMonth(chooseMonth,myRecord.getTime())){
                    arrayList.add(myRecord.getDisplayString());
                }
            }
            else{
                String chooseDay = ""+MyConst.spinner_Year+'年'+MyConst.spinner_Month+'月'+((MyConst.spinner_Day<10)? "0"+MyConst.spinner_Day : MyConst.spinner_Day) +'日';
                if(MyCalendar.isChoosenDay(chooseDay,myRecord.getTime())){
                    arrayList.add(myRecord.getDisplayString());
                }
            }

            id--;
        }
        String[] arr = new String[arrayList.size()];
        arrayList.toArray(arr);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listview_item,arr);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    public void myPlanInit(){
        EditText editTextDailyPlan = (EditText)findViewById(R.id.myPlan_dailyPlan);
        EditText editTextMonthlyPlan = (EditText)findViewById(R.id.myPlan_monthlyPlan);
        Button buttonSave = (Button)findViewById(R.id.button_save);
        Button buttonCancel = (Button)findViewById(R.id.button_cancel);

        editTextDailyPlan.setText(""+MyConst.myPlanDailyPlan);
        editTextMonthlyPlan.setText(""+MyConst.myPlanMonthlyPlan);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextDailyPlan = (EditText)findViewById(R.id.myPlan_dailyPlan);
                EditText editTextMonthlyPlan = (EditText)findViewById(R.id.myPlan_monthlyPlan);
                boolean saved = true;

                if(editTextDailyPlan.getText().toString().isEmpty()){
                    test("Please input Daily Plan");
                    saved = false;
                }
                else {
                    MyConst.myPlanDailyPlan = Integer.parseInt(editTextDailyPlan.getText().toString());
                    save("myPlanDailyPlan", "" + MyConst.myPlanDailyPlan);
                    saveOnServer("myPlanDailyPlan", "" + MyConst.myPlanDailyPlan);
                }
                if(editTextMonthlyPlan.getText().toString().isEmpty()){
                    test("Please input Monthly Plan");
                    saved = false;
                }
                else {
                    MyConst.myPlanMonthlyPlan = Integer.parseInt(editTextMonthlyPlan.getText().toString());
                    save("myPlanMonthlyPlan", "" + MyConst.myPlanMonthlyPlan);
                    saveOnServer("myPlanMonthlyPlan", "" + MyConst.myPlanMonthlyPlan);
                }
                if(saved){
                   // test("Commit successful");
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextDailyPlan = (EditText)findViewById(R.id.myPlan_dailyPlan);
                EditText editTextMonthlyPlan = (EditText)findViewById(R.id.myPlan_monthlyPlan);

                editTextDailyPlan.setText(""+MyConst.myPlanDailyPlan);
                editTextMonthlyPlan.setText(""+MyConst.myPlanMonthlyPlan);
            }
        });
    }

    public void settingsInit(){

        CheckBox remember = (CheckBox)findViewById(R.id.checkbox_remember);
        CheckBox attention = (CheckBox)findViewById(R.id.checkbox_add_attention);

        MyConst.settings_Remember = readLocalSetting("settingsRemember").isEmpty() ? "on" : readLocalSetting("settingsRemember");
        MyConst.settings_Attention = readLocalSetting("settingsAttention").isEmpty() ? "on" : readLocalSetting("settingsAttention");

        remember.setChecked(MyConst.settings_Remember.equals("on"));
        attention.setChecked(MyConst.settings_Attention.equals("on"));

        remember.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MyConst.settings_Remember = (MyConst.settings_Remember.equals("off")) ? "on" : "off";
                saveLocalSetting("settingsRemember",MyConst.settings_Remember);
                //saveOnServer("settings_Remember",MyConst.settings_Remember);
            }
        });

        attention.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MyConst.settings_Attention = (MyConst.settings_Attention.equals("off")) ? "on" : "off";
                saveLocalSetting("settingsAttention",MyConst.settings_Attention);
                //saveOnServer("settings_Attention",MyConst.settings_Attention);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        View postpage,newpage;
        int id = item.getItemId();
        Toolbar head = (Toolbar)findViewById(R.id.toolbar);
        newpage = null;
        postpage = null;
        if (id == R.id.nav_homepage) {
            newpage = findViewById(R.id.app_bar_main_id1);
            switch(MyConst.current_page){
                case MyConst.HOMEPAGE:break;
                case MyConst.INANDOUT:postpage = findViewById(R.id.app_bar_main_id2);break;
                case MyConst.MYPLAN:postpage = findViewById(R.id.app_bar_main_id3);break;
                case MyConst.SETTING:postpage = findViewById(R.id.app_bar_main_id4);break;
            }
            MyConst.current_page = MyConst.HOMEPAGE;
            head.setTitle("Home Page");
        } else if (id == R.id.nav_inandout) {
            newpage = findViewById(R.id.app_bar_main_id2);
            switch(MyConst.current_page){
                case MyConst.HOMEPAGE:postpage = findViewById(R.id.app_bar_main_id1);break;
                case MyConst.INANDOUT:break;
                case MyConst.MYPLAN:postpage = findViewById(R.id.app_bar_main_id3);break;
                case MyConst.SETTING:postpage = findViewById(R.id.app_bar_main_id4);break;
            }
            MyConst.current_page = MyConst.INANDOUT;
            head.setTitle("In and Out");
        } else if (id == R.id.nav_myplan) {
            newpage = findViewById(R.id.app_bar_main_id3);
            switch(MyConst.current_page){
                case MyConst.HOMEPAGE:postpage = findViewById(R.id.app_bar_main_id1);break;
                case MyConst.INANDOUT:postpage = findViewById(R.id.app_bar_main_id2);break;
                case MyConst.MYPLAN:break;
                case MyConst.SETTING:postpage = findViewById(R.id.app_bar_main_id4);break;
            }
            MyConst.current_page = MyConst.MYPLAN;
            head.setTitle("My Plan");
        } else if (id == R.id.nav_settings) {
            newpage = findViewById(R.id.app_bar_main_id4);
            switch(MyConst.current_page){
                case MyConst.HOMEPAGE:postpage = findViewById(R.id.app_bar_main_id1);break;
                case MyConst.INANDOUT:postpage = findViewById(R.id.app_bar_main_id2);break;
                case MyConst.MYPLAN:postpage = findViewById(R.id.app_bar_main_id3);break;
                case MyConst.SETTING:break;
            }
            MyConst.current_page = MyConst.SETTING;
            head.setTitle("Settings");
        } else if (id == R.id.nav_person) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Main2Activity.class);
            MainActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_send) {

        }

        if(postpage != null && newpage != null){
            postpage.setVisibility(View.GONE);
            newpage.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void test(String testInfo){
        Toast.makeText(MainActivity.this, testInfo,
                Toast.LENGTH_SHORT).show();
    }
}
