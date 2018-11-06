package com.txzh.walk.Group;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.txzh.walk.Adapter.GroupMemberInfoAdapter;
import com.txzh.walk.R;

import static com.txzh.walk.Fragment.GroupFragment.groupMemberInfoBeanList;

public class GroupMember extends AppCompatActivity implements View.OnClickListener {
    //返回、群昵称、添加群成员、群员定位、群简介
    TextView back_group_member,group_name_group_member,add_group_member,location_group_member,group_describle_group_member;                           //群昵称
    ListView listview_group_member;                              //群成员ListVew
    GroupMemberInfoAdapter groupMemberInfoAdapter;                //群成员适配器

    String groupName,groupID;                                     //保存群昵称、群ID


    public GroupMember(String groupName,String groupID){
        this.groupName = groupName;
        this.groupID = groupID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                                       //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);

        init();          //实例化对象
    }

    //实例化对象
    public void init(){
        back_group_member = (TextView)findViewById(R.id.tv_back_group_member);
        group_name_group_member = (TextView)findViewById(R.id.tv_group_name_group_member);
        add_group_member = (TextView)findViewById(R.id.tv_add_group_member);
        location_group_member = (TextView)findViewById(R.id.tv_location_group_member);
        group_describle_group_member = (TextView)findViewById(R.id.tv_group_describle_group_member);

        back_group_member.setOnClickListener(this);
        group_name_group_member.setOnClickListener(this);
        add_group_member.setOnClickListener(this);
        location_group_member.setOnClickListener(this);
        group_describle_group_member.setOnClickListener(this);

        listview_group_member = (ListView)findViewById(R.id.listview_group_member);
        groupMemberInfoAdapter = new GroupMemberInfoAdapter(groupMemberInfoBeanList,getApplicationContext());
        listview_group_member.setAdapter(groupMemberInfoAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_back_group_member:
                break;

            case R.id.tv_group_name_group_member:
                break;

            case R.id.tv_add_group_member:
                break;

            case R.id.tv_location_group_member:
                break;

            case R.id.tv_group_describle_group_member:
                break;

        }
    }
}
