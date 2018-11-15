package com.txzh.walk.Fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.Adapter.GroupInfoAdapter;
import com.txzh.walk.Bean.GroupInfoBean;
import com.txzh.walk.Bean.GroupMemberInfoBean;
import com.txzh.walk.Group.CreateGroup;
import com.txzh.walk.Group.GroupMembers;
import com.txzh.walk.Group.searchGroup;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;

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
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.txzh.walk.HomePage.WalkHome.context;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainAllGroupId;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainGroupMember;

public class GroupFragment extends Fragment implements View.OnClickListener {
    protected View view=null;
    private TextView group_tv_search,group_tv_create_group,group_tv_manage_group,group_tv_add_group;                 //搜索、创建、我管理的群、我加入的群
    private ListView group_lv_manage_group,group_lv_add_group;                                                              //我管理的群ListView；我创建的群ListView


    protected Drawable arrow_right_group_pic = null;                        //关闭群组向右的箭头
    protected Drawable arrow_down_group_pic = null;                         //展开群组向下的箭头

    public static List<GroupInfoBean> manageGroupInfoBeanList = new ArrayList<GroupInfoBean>();                       //我管理群组信息列表
    public static List<GroupInfoBean> addGroupInfoBeanList = new ArrayList<GroupInfoBean>();                       //我加入的组信息列表
    public static boolean isObtainAllGroup=false;


    public static boolean is_onclik_manage_group = true;                    //判断展开、关闭我管理的的群组
    public static boolean is_onclik_add_group = true;                       //判断展开、关闭我加入的的群组

    public GroupInfoAdapter manageGroupAdapter;                                  //群组适配器
    private Bundle bundle = new Bundle();                                                      //传送数据
    private Intent intent = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("我滑动了第二个界面22");
        view = inflater.inflate(R.layout.fragment_group, container, false);
        obtainAllGroup(1+"");              //获取所有群组信息
        init();             //实例化和监听控件
        GroupListListener();
        return view;
    }

    //实例化和监听控件
    public void init(){

        //实例化和监听群组搜索、创建
        group_tv_search = (TextView)view.findViewById(R.id.group_tv_search);
        group_tv_create_group = (TextView)view.findViewById(R.id.group_tv_create_group);
        group_tv_search.setOnClickListener(this);
        group_tv_create_group.setOnClickListener(this);

        //实例化和监听我管理的群组、我加入的群组
        group_tv_manage_group = (TextView)view.findViewById(R.id.group_tv_manage_group);
        group_tv_add_group = (TextView)view.findViewById(R.id.group_tv_add_group);
        group_tv_manage_group.setOnClickListener(this);
        group_tv_add_group.setOnClickListener(this);

        //实例化我管理的群组ListView、我加入的群组ListView
        group_lv_manage_group = (ListView)view.findViewById(R.id.group_lv_manage_group);
        group_lv_add_group = (ListView)view.findViewById(R.id.group_lv_add_group);


        //展开、关闭群组的带箭头图片
        arrow_down_group_pic = getResources().getDrawable(R.drawable.group_arrow_down_32dp);
        arrow_down_group_pic.setBounds(0,0,arrow_down_group_pic.getMinimumWidth(),arrow_down_group_pic.getMinimumHeight());
        arrow_right_group_pic = getResources().getDrawable(R.drawable.group_arrow_right_32dp);
        arrow_right_group_pic.setBounds(0,0,arrow_right_group_pic.getMinimumWidth(),arrow_right_group_pic.getMinimumHeight());

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.group_tv_search:                          //群组搜索
                Intent intents = new Intent(getActivity(),searchGroup.class);
                startActivity(intents);
                break;


            case R.id.group_tv_create_group:                 //创建群组
                Intent intent = new Intent(getActivity().getApplicationContext(), CreateGroup.class);
                startActivity(intent);
                break;


            case R.id.group_tv_manage_group:                    //我管理的群组
                if(isObtainAllGroup){                              //判断是否得到所有群组
                    openCloseManageGroup();                             //打开关闭我管理的群组
                    manageGroupAdapter = new GroupInfoAdapter(manageGroupInfoBeanList,context);
                    group_lv_manage_group.setAdapter(manageGroupAdapter);
                }else if(!isObtainAllGroup) {
                    Toast.makeText(context,"服务器繁忙，请重新点击加载！",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"当前网络状况不佳，请重新点击加载！",Toast.LENGTH_SHORT).show();
                }
              break;


            case R.id.group_tv_add_group:                       //我加入的群组

                if(isObtainAllGroup){                               //判断是否得到所有群组
                    openCloseAddGroup();                               //打开关闭我加入的群组
                    manageGroupAdapter = new GroupInfoAdapter(addGroupInfoBeanList,context);
                    group_lv_add_group.setAdapter(manageGroupAdapter);
                }else if(!isObtainAllGroup) {
                    Toast.makeText(context,"服务器繁忙，请重新点击加载！",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"当前网络状况不佳，请重新点击加载！",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    //打开关闭我管理的群组
    public void openCloseManageGroup(){
        if(is_onclik_manage_group==true){
            group_tv_manage_group.setCompoundDrawables(arrow_down_group_pic,null,null,null);       //改变箭头方向
            group_lv_manage_group.setVisibility(View.VISIBLE);                                                            //设置ListView可见
            is_onclik_manage_group=!is_onclik_manage_group;
        }else if(is_onclik_manage_group==false){
            group_tv_manage_group.setCompoundDrawables(arrow_right_group_pic,null,null,null);       //改变箭头方向
            group_lv_manage_group.setVisibility(View.GONE);                                                              //设置ListView不可见
            is_onclik_manage_group=!is_onclik_manage_group;
        }
    }


    //打开关闭我加入的群组
    public void openCloseAddGroup(){
        if(is_onclik_add_group==true){
            group_tv_add_group.setCompoundDrawables(arrow_down_group_pic,null,null,null);          //改变箭头方向
            group_lv_add_group.setVisibility(View.VISIBLE);                                                               //设置ListView可见
            is_onclik_add_group=!is_onclik_add_group;
        }else if(is_onclik_add_group==false){
            group_tv_add_group.setCompoundDrawables(arrow_right_group_pic,null,null,null);           //改变箭头方向
            group_lv_add_group.setVisibility(View.GONE);                                                                  //设置ListView不可见
            is_onclik_add_group=!is_onclik_add_group;
        }
    }

    public void GroupListListener(){
        group_lv_manage_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                bundle.clear();
                bundle.putString("groupName",manageGroupInfoBeanList.get(position).getGroupName());
                bundle.putString("groupID",manageGroupInfoBeanList.get(position).getGroupId());
                bundle.putString("groupHostID", manageGroupInfoBeanList.get(position).getGroupHostID());
                intent = new Intent(context,GroupMembers.class);
                intent.putExtra("groupInfo",bundle);
                startActivity(intent);
            }
        });

        group_lv_add_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bundle.clear();
                bundle.putString("groupName",addGroupInfoBeanList.get(position).getGroupName());
                bundle.putString("groupID",addGroupInfoBeanList.get(position).getGroupId());
                bundle.putString("groupHostID", addGroupInfoBeanList.get(position).getGroupHostID());

                intent = new Intent(context,GroupMembers.class);
                intent.putExtra("groupInfo",bundle);
                startActivity(intent);

            }
        });
    }




    //获取所有群组信息
    public void obtainAllGroup(final String userID){
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody=new FormBody.Builder()
                .add("userID",""+userID)
                .build();

        Request request = new Request.Builder()
                .url(URL_obtainAllGroupId)
                .post(formBody)
                .build();

        Response response;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("++++","我请求的是获取所有群组：请求失败");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(!response.isSuccessful()){
                    return;
                }
                manageGroupInfoBeanList.clear();
                addGroupInfoBeanList.clear();

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    isObtainAllGroup = Boolean.valueOf(jsonObject.getString("success"));
                    Log.i("----","我是判断success111111111111111："+isObtainAllGroup+jsonObject.getString("success"));
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        GroupInfoBean groupInfoBean = new GroupInfoBean();

                        String groupId = object.getString("groupID");
                        String groupName = object.getString("groupName");
                        String groupHostID = object.getString("groupHostID");
                        String status = object.getString("status");

                        groupInfoBean.setGroupId(object.getString("groupID"));
                        groupInfoBean.setGroupName(object.getString("groupName"));
                        groupInfoBean.setGroupHostID(object.getString("groupHostID"));
                        groupInfoBean.setStatus(object.getString("status"));

                        if((userID).equals(object.getString("groupHostID"))){
                            manageGroupInfoBeanList.add(groupInfoBean);
                        }else {
                            addGroupInfoBeanList.add(groupInfoBean);
                        }
                        Log.i("++++","我是JSONArray："+"群ID:"+groupId+"-------"+groupName+"-------"+"群主id："+groupHostID+"-------"+status);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
