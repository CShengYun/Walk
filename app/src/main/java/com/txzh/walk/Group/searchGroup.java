package com.txzh.walk.Group;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.txzh.walk.Adapter.GroupSearchInfoAdapter;
import com.txzh.walk.Bean.GroupSearchInfoBean;
import com.txzh.walk.NetWork.NetWorkIP;
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
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.txzh.walk.HomePage.WalkHome.context;

public class searchGroup extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_groupReturn;   //返回上一个Activity
    private EditText et_groupName;   //查询的群组名称
    private ImageButton ibtn_searchGroup; //查询按钮
    private String groupName;    //查询的群组名称
    private ListView lv_searchGroup; //群组列表
    private GroupSearchInfoAdapter groupSearchInfoAdapter;  //查询群组列表
    private Handler handler;
    private List<GroupSearchInfoBean> groupSearchInfoBeanList = new ArrayList<GroupSearchInfoBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        init();
    }

    private void init(){
        handler = new Handler();
        ib_groupReturn = findViewById(R.id.ib_groupReturn);
        ib_groupReturn.setOnClickListener(this);
        et_groupName = findViewById(R.id.et_groupName);
        ibtn_searchGroup = findViewById(R.id.ibtn_searchGroup);
        ibtn_searchGroup.setOnClickListener(this);
        lv_searchGroup = findViewById(R.id.lv_searchGroup);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_groupReturn:
                finish();
                break;
            case R.id.ibtn_searchGroup:
                groupName = et_groupName.getText().toString().trim();
                searchGroup(groupName);
                break;
        }

    }

    private void searchGroup(final String keyword){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("keyword",keyword)
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_searchGroup)
                        .post(formBody)
                        .build();
                Response response;
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(!response.isSuccessful()){
                            return;
                        }

                        groupSearchInfoBeanList.clear();

                        JSONObject jsonObject = null;
                        String success = null;
                        String message = null;
                        String[] data = null;
                        try {
                            jsonObject = new JSONObject(response.body().string());
                            success = jsonObject.getString("success");
                            message = jsonObject.getString("message");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = (JSONObject) jsonArray.get(i);
                                GroupSearchInfoBean groupSearchInfoBean = new GroupSearchInfoBean();

                                groupSearchInfoBean.setGroupId(object.getString("groupID"));
                                groupSearchInfoBean.setGroupPic(object.getString("groupPic"));
                                groupSearchInfoBean.setGroupName(object.getString("groupName"));
                                groupSearchInfoBean.setGroupDescribe(object.getString("groupDescribe"));

                                groupSearchInfoBeanList.add(groupSearchInfoBean);


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String finalSuccess = success;
                        final String finalMessage = message;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(RetrievePassword.this, ""+b, Toast.LENGTH_SHORT).show();
                                groupSearchInfoAdapter = new GroupSearchInfoAdapter(groupSearchInfoBeanList,context);
                                lv_searchGroup.setAdapter(groupSearchInfoAdapter);
                            }
                        });
                    }
                });
            }
        }).start();
    }


}
