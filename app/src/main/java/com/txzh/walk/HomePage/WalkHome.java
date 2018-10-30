package com.txzh.walk.HomePage;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.Adapter.FragmentAdapter;
import com.txzh.walk.Fragment.GroupFragment;
import com.txzh.walk.Fragment.MapFragment;
import com.txzh.walk.Fragment.NewsFragment;
import com.txzh.walk.Fragment.PersonalFragment;
import com.txzh.walk.MyViewPager.MyViewPager;
import com.txzh.walk.R;

import java.util.ArrayList;
import java.util.List;

public class WalkHome extends AppCompatActivity implements View.OnClickListener {

    public GroupFragment groupFragment;
    public MapFragment mapFragment;
    public NewsFragment newsFragment;
    public PersonalFragment personalFragment;
    private MyViewPager viewPager;
    private List<Fragment> fragments;

    private TextView tv_map_walk_home,tv_group_walk_home,tv_news_walk_home,tv_personal_walk_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                                       //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_home);

        init();                                                                                 //实例化对象、监听、添加fragment到列表

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);

        viewPager = (MyViewPager) findViewById(R.id.viewpager);
        viewPager.setScanScroll(false);                                                         //禁止页面滑动
        viewPager.setAdapter(fragmentAdapter);                                                  //滑动页面绑定适配器

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
                Toast.makeText(this,"00000",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_group_walk_home:
                viewPager.setCurrentItem(1);
                Toast.makeText(this,"11111",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_news_walk_home:
                viewPager.setCurrentItem(2);
                Toast.makeText(this,"22222",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_personal_walk_home:
                viewPager.setCurrentItem(3);
                Toast.makeText(this,"33333",Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
