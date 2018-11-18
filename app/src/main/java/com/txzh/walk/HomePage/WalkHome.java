package com.txzh.walk.HomePage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.txzh.walk.Adapter.FragmentAdapter;
import com.txzh.walk.BroadcastReceiver.NetworkChangeReceiver;
import com.txzh.walk.Fragment.GroupFragment;
import com.txzh.walk.Fragment.MapFragment;
import com.txzh.walk.Fragment.NewsFragment;
import com.txzh.walk.Fragment.PersonalFragment;
import com.txzh.walk.MyViewPager.MyViewPager;
import com.txzh.walk.R;

import java.util.ArrayList;
import java.util.List;

public class WalkHome extends AppCompatActivity implements View.OnClickListener {

    public static Context context;

    public GroupFragment groupFragment;                             //群组fragmnet
    public MapFragment mapFragment;                                 //地图fragmnet
    public NewsFragment newsFragment;                               //消息fragmnet
    public PersonalFragment personalFragment;                     //个人中心fragmnet
    private MyViewPager viewPager;
    private List<Fragment> fragments;

    private TextView tv_map_walk_home,tv_group_walk_home,tv_news_walk_home,tv_personal_walk_home;       //地图、群组、消息、我的主界面屏幕下方的四个TextView

    private NetworkChangeReceiver networkChangeReceiver;//监听网络状态
    public static boolean openGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                                       //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_home);
        context=getApplicationContext();
        judgePermission();
        init();                                                                                 //实例化对象、监听、添加fragment到列表

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);

        viewPager = (MyViewPager) findViewById(R.id.viewpager);
        viewPager.setScanScroll(false);                                                         //禁止页面滑动
        viewPager.setAdapter(fragmentAdapter);                                                  //滑动页面绑定适配器


        Intent intent = getIntent();
        int id = intent.getIntExtra("flag", 0);
        if (id==1) {
            //fragment的切换采用的是viewpage的形式,然后1是指底部第2个Fragment
            viewPager.setCurrentItem(1);
            openGroup = true;
        }if (id==0) {
            //fragment的切换采用的是viewpage的形式,然后1是指底部第2个Fragment
            viewPager.setCurrentItem(0);
        }


    }


    //实例化对象、监听、添加fragment到列表
    protected void init(){

        fragments = new ArrayList<>();

        tv_map_walk_home = (TextView)findViewById(R.id.tv_map_walk_home);
        tv_group_walk_home = (TextView)findViewById(R.id.tv_group_walk_home);
        tv_news_walk_home = (TextView)findViewById(R.id.tv_news_walk_home);
        tv_personal_walk_home = (TextView)findViewById(R.id.tv_personal_walk_home);

        tv_map_walk_home.setOnClickListener(this);
        tv_group_walk_home.setOnClickListener(this);
        tv_news_walk_home.setOnClickListener(this);
        tv_personal_walk_home.setOnClickListener(this);

        mapFragment = new MapFragment();
        groupFragment = new GroupFragment();
        newsFragment = new NewsFragment();
        personalFragment = new PersonalFragment();

        fragments.add(mapFragment);
        fragments.add(groupFragment);
        fragments.add(newsFragment);
        fragments.add(personalFragment);

    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.tv_map_walk_home:
                viewPager.setCurrentItem(0);
                break;

            case R.id.tv_group_walk_home:
                viewPager.setCurrentItem(1);
                break;

            case R.id.tv_news_walk_home:
                viewPager.setCurrentItem(2);
                break;

            case R.id.tv_personal_walk_home:
                viewPager.setCurrentItem(3);
                break;
        }
    }

    //注册NetworkChanagerReceiver广播
    protected void onResume() {
        if(networkChangeReceiver == null){
            networkChangeReceiver = new NetworkChangeReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver,intentFilter);
        System.out.print("注册");

        super.onResume();
    }

    //销毁NetworkChanagerReceiver广播
    protected void onPause(){
        unregisterReceiver(networkChangeReceiver);
        System.out.print("销毁");
        super.onPause();
    }


//6.0之后要动态获取权限，重要！！！

    protected void judgePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            // sd卡权限
            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
            }

            //手机状态权限
            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);
            }
            //定位权限
            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, locationPermission, 300);
            }

            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};

            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);
            }

            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);
            }

            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);
            }

        }else{
            //doSdCardResult();
        }
    }

}


