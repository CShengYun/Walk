package com.txzh.walk.Group;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.Adapter.GroupMemberInfoAdapter;
import com.txzh.walk.Bean.GroupMemberLocationBean;
import com.txzh.walk.HomePage.WalkHome;
import com.txzh.walk.MainActivity;
import com.txzh.walk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.txzh.walk.Fragment.GroupFragment.groupMemberInfoBeanList;
import static com.txzh.walk.HomePage.WalkHome.context;
import static com.txzh.walk.HomePage.WalkHome.isObtainAllGroup;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainGroupPositionInfo;

public class GroupMembers extends AppCompatActivity implements View.OnClickListener {
    //返回、群昵称、添加群成员、群员定位、群简介
    private TextView back_group_member,group_name_group_member,add_group_member,location_group_member,group_describle_group_member;                           //群昵称
    private ListView listview_group_member;                              //群成员ListVew
    private GroupMemberInfoAdapter groupMemberInfoAdapter;                //群成员适配器

    private Bundle bundle;                                                  //接收传过来的数据
    private String groupName,groupID;                                      //保存传过来的数据
    private String isObationGroupMemberLocation;

    private Handler handler;
    public static List<GroupMemberLocationBean> groupMemberLocationBeanList = new ArrayList<GroupMemberLocationBean>();

    public GroupMembers(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                           //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        init();                                                                     //实例化对象
    }

    //实例化对象、接收数据
    public void init(){
        handler = new Handler();

        back_group_member = (TextView)findViewById(R.id.tv_back_group_member);
        group_name_group_member = (TextView)findViewById(R.id.tv_group_name_group_member);
        add_group_member = (TextView)findViewById(R.id.tv_add_group_member);
        location_group_member = (TextView)findViewById(R.id.tv_location_group_member);
        group_describle_group_member = (TextView)findViewById(R.id.tv_group_describle_group_member);

        back_group_member.setOnClickListener(this);
        add_group_member.setOnClickListener(this);
        location_group_member.setOnClickListener(this);
        group_describle_group_member.setOnClickListener(this);


        bundle=getIntent().getBundleExtra("groupNameID");
        groupName = bundle.getString("groupName");
        groupID = bundle.getString("groupID");
        group_name_group_member.setText(groupName);                            //设置群组昵称

        listview_group_member = (ListView)findViewById(R.id.listview_group_member);
        groupMemberInfoAdapter = new GroupMemberInfoAdapter(groupMemberInfoBeanList,getApplicationContext());
        listview_group_member.setAdapter(groupMemberInfoAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_back_group_member:                                     //返回
                break;

            case R.id.tv_add_group_member:                                     //添加群
                break;

            case R.id.tv_location_group_member:                                 //群员位置
                obtainGroupMemberLocation();

                break;

            case R.id.tv_group_describle_group_member:                            //群简介
                break;

        }
    }

    public void obtainGroupMemberLocation(){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("groupID",groupID)
                .build();

        Request request = new Request.Builder()
                .url(URL_obtainGroupPositionInfo)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(!response.isSuccessful()){
                    return;
                }
                groupMemberLocationBeanList.clear();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            isObationGroupMemberLocation = jsonObject.getString("success");

                            for(int i=0;i<jsonArray.length();i++){
                                GroupMemberLocationBean groupMemberLocationBean = new GroupMemberLocationBean();
                                JSONObject object = (JSONObject)jsonArray.get(i);
                                String nickName = object.getString("nickName");
                                String phone = object.getString("phone");
                                String latitude = object.getString("latitude");
                                String longtitude = object.getString("longititude");
                                String userID = object.getString("userID");

                                groupMemberLocationBean.setNickName(object.getString("nickName"));
                                groupMemberLocationBean.setPhone(object.getString("phone"));
                                groupMemberLocationBean.setLatitude(object.getString("latitude"));
                                groupMemberLocationBean.setLongitude(object.getString("longititude"));
                                groupMemberLocationBean.setUserID(object.getString("userID"));

                                groupMemberLocationBean.setHeadPath("R.drawable.p2");

                                groupMemberLocationBeanList.add(groupMemberLocationBean);
                                Log.i("###","我是位置信息："+nickName+"-----"+phone+"-----"+latitude+"-----"+longtitude+"-----"+userID);
                            }

                            if(isObationGroupMemberLocation.equals("true")){
                                Intent intent = new Intent(GroupMembers.this, WalkHome.class);
                                startActivity(intent);
                            }else if(isObationGroupMemberLocation.equals("false")) {
                                Toast.makeText(context,"服务器繁忙，请重新点击加载！",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context,"当前网络状况不佳，请重新点击加载！",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
