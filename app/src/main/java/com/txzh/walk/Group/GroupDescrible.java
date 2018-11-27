package com.txzh.walk.Group;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.Fragment.GroupFragment;
import com.txzh.walk.HomePage.WalkHome;
import com.txzh.walk.MainActivity;
import com.txzh.walk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.txzh.walk.MainActivity.userID;
import static com.txzh.walk.NetWork.NetWorkIP.URL_disbandGroup;
import static com.txzh.walk.NetWork.NetWorkIP.URL_singOutGroup;

public class GroupDescrible extends AppCompatActivity implements View.OnClickListener {


    private String groupMemberCount = "0",groupManCount = "0",groupWomanCount = "0";           //群成员、男、女数量
    private Bundle bundle;                                                                            //接收传过来的数据
    private String groupName,groupID,groupHostID,groupDescrible="",groupAnnouncement="";                                                               //保存传过来的数据

    public static boolean isObtainGroupDescrible = false;

    TextView group_name_group_describle,group_count_group_describle,group_man_count_group_describle,group_woman_count_group_describle;      //群昵称、群成员总数，男和女各自的数量
    TextView out_group_describle,disband_group_describle,finish_edit_group_describle,back_group_describle;           //退出群、解散群
    TextView group_notice_tv_group_describle,group_describle_tv_group_describle;                //群公告、群描述
    EditText group_notice_et_group_describle,group_describle_et_group_describle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                           //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_describle);
        obtainGroupDescrible();                                     //获取群组简介（群公告、群描述）
    }

    //初始化组件
    public void init(){
        group_name_group_describle = (TextView)findViewById(R.id.group_name_group_describle);
        group_count_group_describle = (TextView)findViewById(R.id.group_count_group_describle);
        group_man_count_group_describle = (TextView)findViewById(R.id.group_man_count_group_describle);
        group_woman_count_group_describle = (TextView)findViewById(R.id.group_woman_count_group_describle);
        group_notice_tv_group_describle = (TextView)findViewById(R.id.group_notice_tv_group_describle);
        group_describle_tv_group_describle = (TextView)findViewById(R.id.group_describle_tv_group_describle);


        out_group_describle = (TextView)findViewById(R.id.out_group_describle);
        disband_group_describle = (TextView)findViewById(R.id.disband_group_describle);
        finish_edit_group_describle = (TextView)findViewById(R.id.finish_edit_group_describle);
        back_group_describle = (TextView)findViewById(R.id.back_group_describle);
        out_group_describle.setOnClickListener(this);
        disband_group_describle.setOnClickListener(this);
        back_group_describle.setOnClickListener(this);

        group_notice_et_group_describle = (EditText)findViewById(R.id.group_notice_et_group_describle);
        group_describle_et_group_describle = (EditText)findViewById(R.id.gorup_describle_et_group_describle);


        if(userID.equals(groupHostID)){
            group_notice_tv_group_describle.setVisibility(View.GONE);
            group_describle_tv_group_describle.setVisibility(View.GONE);
            disband_group_describle.setVisibility(View.VISIBLE);
            disband_group_describle.setText("解散群");
            out_group_describle.setVisibility(View.GONE);
        }else {
            group_notice_tv_group_describle.setVisibility(View.GONE);
            group_describle_tv_group_describle.setVisibility(View.GONE);
            out_group_describle.setVisibility(View.VISIBLE);
            out_group_describle.setText("退出群");
            disband_group_describle.setVisibility(View.GONE);
        }

    }








    public void obtainGroupDescrible(){
        bundle=getIntent().getBundleExtra("groupDescrible");
        groupName = bundle.getString("groupName");
        groupID = bundle.getString("groupID");
        groupHostID = bundle.getString("groupHostID");
        groupMemberCount = bundle.getString("groupMemberCount");
        groupManCount = bundle.getString("groupManCount");
        groupWomanCount = bundle.getString("groupWomanCount");
        groupDescrible = bundle.getString("groupDescrible");
        groupAnnouncement = bundle.getString("groupAnnouncement");

        init();                 //初始化组件

        group_name_group_describle.setText(groupName);
        group_count_group_describle.setText(groupMemberCount);
        group_man_count_group_describle.setText("："+groupManCount);
        group_woman_count_group_describle.setText("："+groupWomanCount);
        group_notice_et_group_describle.setText(groupAnnouncement);
        group_describle_et_group_describle.setText(groupDescrible);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        switch (id){
            case R.id.out_group_describle:                  //退出群
                outdGroup();                                  //退出群请求
                intent = new Intent(GroupDescrible.this, WalkHome.class);
                intent.putExtra("flag",1);
                startActivity(intent);
                GroupDescrible.this.finish();
                Log.i("%%%","我点击了退出群");
                break;


            case R.id.disband_group_describle:              //解散群
                Log.i("%%%","我点击了解散群");
                disbandGroup();                                //解散群请求
                intent = new Intent(GroupDescrible.this, WalkHome.class);
                intent.putExtra("flag",1);
                startActivity(intent);
                GroupDescrible.this.finish();
                break;
            case R.id.back_group_describle:                 //返回键
                GroupDescrible.this.finish();
        }
    }


    public void disbandGroup(){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("groupID",groupID)
                .build();

        Request request = new Request.Builder()
                .url(URL_disbandGroup)
                .post(formBody)
                .build();

        Response response;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("%%%","我解散了群："+response.body().string());
            }
        });
    }

    public void outdGroup(){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("userID",userID)
                .add("groupID",groupID)
                .build();

        Request request = new Request.Builder()
                .url(URL_singOutGroup)
                .post(formBody)
                .build();

        Response response;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("%%%","我退出了群："+response.body().string());
            }
        });
    }
}
