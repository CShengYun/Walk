package com.txzh.walk.Group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.Adapter.GroupMemberInfoAdapter;
import com.txzh.walk.Bean.GroupMemberInfoBean;
import com.txzh.walk.Bean.GroupMemberLocationBean;
import com.txzh.walk.HomePage.WalkHome;
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

import static com.txzh.walk.Fragment.MapFragment.groupMemberLocationBeanList;
import static com.txzh.walk.Group.GroupDescrible.isObtainGroupDescrible;
import static com.txzh.walk.HomePage.WalkHome.context;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainGroupInfo;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainGroupMember;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainGroupPositionInfo;

public class GroupMembers extends AppCompatActivity implements View.OnClickListener {
    //返回、群昵称、添加群成员、群员定位、群简介
    private TextView back_group_member,group_name_group_member,add_group_member,location_group_member,group_describle_group_member;                           //群昵称
    private ListView listview_group_member;                              //群成员ListVew
    private GroupMemberInfoAdapter groupMemberInfoAdapter;                //群成员适配器

    private Bundle bundleReceive,bundleSend = new Bundle();                                                  //接收传过来的数据
    private String groupName,groupID,groupHostID;                                      //保存传过来的数据
    private String isObationGroupMemberLocation;


    public static List<GroupMemberInfoBean> groupMemberInfoBeanList = new ArrayList<GroupMemberInfoBean>();
    public static boolean isLocation=false;                                 //是否获取到群成员位置，显示在地图上

    private String isObtainGroupMember="";                                      //判断是否获取所有群成员
    private Intent intent;                                                      //开启群成员acticity
    private int groupMemberCount = 0,groupManCount = 0,groupWomanCount = 0;           //群成员、男、女数量
    private String groupDescrible = "",groupAnnouncement = "";
    protected Handler handler;
    public GroupMembers(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                           //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        requestGroupMember();                                                       //发送请求获取群组成员
        init();                                                                     //实例化对象
        Log.i("8888","我是移除群成员回调结果");
    }

    //实例化对象、接收数据
    public void init(){

        back_group_member = (TextView)findViewById(R.id.tv_back_group_member);
        group_name_group_member = (TextView)findViewById(R.id.tv_group_name_group_member);
        add_group_member = (TextView)findViewById(R.id.tv_add_group_member);
        location_group_member = (TextView)findViewById(R.id.tv_location_group_member);
        group_describle_group_member = (TextView)findViewById(R.id.tv_group_describle_group_member);

        back_group_member.setOnClickListener(this);
        add_group_member.setOnClickListener(this);
        location_group_member.setOnClickListener(this);
        group_describle_group_member.setOnClickListener(this);

        group_name_group_member.setText(groupName);                            //设置群组昵称

        listview_group_member = (ListView)findViewById(R.id.listview_group_member);
        handler = new Handler(){
            public void handleMessage(Message msg) {
                if(msg.what==11){
                    groupMemberInfoAdapter = new GroupMemberInfoAdapter(groupMemberInfoBeanList,getApplicationContext());
                    listview_group_member.setAdapter(groupMemberInfoAdapter);

                }
            }
        };
        listview_group_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(bundleSend!=null){
                    bundleSend.clear();
                }

                bundleSend.putString("userID",groupMemberInfoBeanList.get(position).getGroupMemberID());
                intent = new Intent(GroupMembers.this,GroupSingleMember.class);
                intent.putExtra("userIdInfo",bundleSend);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(data!=null){
            if(requestCode==1){
                if(resultCode==1){

                    Log.i("88888","返回数据"+data.getSerializableExtra("userInfo"));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_back_group_member:                                     //返回
                GroupMembers.this.finish();
                break;

            case R.id.tv_add_group_member:                                     //添加群成员
                Intent intent = new Intent(this,addGroupMembers.class);
                bundle.putString("groupID",groupID);
                intent.putExtra("groupIdInfo",bundle);
                startActivity(intent);
                break;

            case R.id.tv_location_group_member:                                 //群员位置
                obtainGroupMemberLocation();
                break;

            case R.id.tv_group_describle_group_member:                            //群简介
                obtainGroupDescrible();
                break;
        }
    }


//发送请求，请求群成员。
    public void requestGroupMember(){

        bundleReceive=getIntent().getBundleExtra("groupInfo");
        groupName = bundleReceive.getString("groupName");
        groupID = bundleReceive.getString("groupID");
        groupHostID = bundleReceive.getString("groupHostID");
        OkHttpClient client = new OkHttpClient();

                FormBody formBody = new FormBody.Builder()
                        .add("groupID",groupID)
                        .build();

                Request request = new Request.Builder()
                        .url(URL_obtainGroupMember)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        obtainGroupMember(response);
                    }
                });
    }

    //异步POST方法获取我管理的和我加入的群成员
    public void obtainGroupMember(final Response response){
        if(!response.isSuccessful()){
            return;
        }
        groupMemberInfoBeanList.clear();
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            isObtainGroupMember = jsonObject.getString("success");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                GroupMemberInfoBean groupMemberInfoBean = new GroupMemberInfoBean();

                String userID = object.getString("userID");
                String headPath = object.getString("headPath");
                String nickName = object.getString("nickName");
                String sex = object.getString("sex");
                String status = object.getString("status");

                groupMemberInfoBean.setGroupMemberID(object.getString("userID"));
                groupMemberInfoBean.setGroupMemberHeadPath(object.getString("headPath"));
                groupMemberInfoBean.setGroupMemberUserName(object.getString("userName"));
                groupMemberInfoBean.setGroupMemberNiceName(object.getString("nickName"));
                groupMemberInfoBean.setGroupMemberSex(object.getString("sex"));
                groupMemberInfoBean.setGroupMemberStatue(object.getString("status"));

                groupMemberInfoBeanList.add(groupMemberInfoBean);

                Message msg = new Message();
                msg.what = 11;
                handler.sendMessage(msg);


                groupMemberCount++;
                if(object.getString("sex").equals("男")){
                    groupManCount++;
                }else if(object.getString("sex").equals("女")){
                    groupWomanCount++;
                }
                Log.i("******","我是管理群的群成员："+userID+"----"+nickName+"----"+sex+"----"+isObtainGroupMember+jsonObject.toString());
            }
            if(isObtainGroupMember.equals("true")){

            }else if(isObtainGroupMember.equals("false")) {
                Toast.makeText(context,"服务器繁忙，请重新点击加载！",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context,"当前网络状况不佳，请重新点击加载！",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



//获取群成员位置
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
                        isLocation = true;
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


//获取群简介
    private void obtainGroupDescrible(){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("groupID",groupID)
                .build();

        final Request request = new Request.Builder()
                .url(URL_obtainGroupInfo)
                .post(formBody)
                .build();
        Response response;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.i("@@@@","请求失败。");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    return;
                }
                try {
                    JSONObject jsonObject  = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    isObtainGroupDescrible = Boolean.valueOf(jsonObject.getString("success"));
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        groupDescrible = object.getString("groupDescribe");
                        groupAnnouncement = object.getString("groupAnnouncement");
                    }
                    if(isObtainGroupDescrible){
                        if(bundleSend!=null){
                            bundleSend.clear();
                        }
                        bundleSend.putString("groupMemberCount",groupMemberCount+"");
                        bundleSend.putString("groupManCount",groupManCount+"");
                        bundleSend.putString("groupWomanCount",groupWomanCount+"");
                        bundleSend.putString("groupName",groupName);
                        bundleSend.putString("groupID",groupID);
                        bundleSend.putString("groupHostID",groupHostID);
                        bundleSend.putString("groupDescrible",groupDescrible);
                        bundleSend.putString("groupAnnouncement",groupAnnouncement);
                        intent = new Intent(GroupMembers.this,GroupDescrible.class);
                        intent.putExtra("groupDescrible",bundleSend);
                        startActivity(intent);
                    }else if(!isObtainGroupDescrible) {
                        Toast.makeText(context,"服务器繁忙,获取失败，请重新点击加载！",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"当前网络状况不佳，请重新点击加载！",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }











}
