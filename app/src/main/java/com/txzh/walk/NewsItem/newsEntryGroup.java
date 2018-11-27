package com.txzh.walk.NewsItem;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.txzh.walk.Adapter.NewsEntryGroupAdapter;
import com.txzh.walk.Adapter.NewsEntryGroupAdapter.InnerItemOnclickListener;
import com.txzh.walk.Bean.NewsEntryGroupInfoBean;
import com.txzh.walk.NetWork.NetWorkIP;
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

public class newsEntryGroup extends AppCompatActivity implements View.OnClickListener,InnerItemOnclickListener{
    ImageButton ib_entryGroupReturn;
    private ListView lv_entryGroup;

    protected static final int issuccess = 1;
    protected static final int isfail = 2;

    private NewsEntryGroupAdapter newsEntryGroupAdapter;
    private List<NewsEntryGroupInfoBean> newsEntryGroupInfoBeanList = new ArrayList<NewsEntryGroupInfoBean>();

    private Handler handler = new Handler(){
      public void handleMessage(android.os.Message msg){
          if(msg.what == issuccess){
              newsEntryGroupAdapter = new NewsEntryGroupAdapter((List<NewsEntryGroupInfoBean>) msg.obj,getApplicationContext());
              newsEntryGroupAdapter.setOnInnerItemOnClickListener(newsEntryGroup.this);
              lv_entryGroup.setAdapter(newsEntryGroupAdapter);
          }else if(msg.what == isfail){

          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_entry_group);


        ib_entryGroupReturn = findViewById(R.id.ib_entryGroupReturn);
        ib_entryGroupReturn.setOnClickListener(this);
        lv_entryGroup = findViewById(R.id.lv_entryGroup);

        getNewsEntryGroup();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_entryGroupReturn:
                finish();
                break;
        }
    }

    public void getNewsEntryGroup(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("userID", ""+ Tools.getUserID())
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_attainEntryApplication)
                        .post(formbody)
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

                        newsEntryGroupInfoBeanList.clear();
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
                                NewsEntryGroupInfoBean newsEntryGroupInfoBean = new NewsEntryGroupInfoBean();

                                newsEntryGroupInfoBean.setDate(object.getString("date"));
                                newsEntryGroupInfoBean.setGroupName(object.getString("groupName"));
                                newsEntryGroupInfoBean.setUserID(object.getString("userID"));
                                newsEntryGroupInfoBean.setStatus(object.getString("status"));
                                newsEntryGroupInfoBean.setNickName(object.getString("nickName"));
                                newsEntryGroupInfoBean.setGroupID(object.getString("groupID"));

                                newsEntryGroupInfoBeanList.add(newsEntryGroupInfoBean);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if("true".equals(success)){
                            Message msg = new Message();
                            msg.what = issuccess;
                            msg.obj = newsEntryGroupInfoBeanList;
                            handler.sendMessage(msg);
                        }else {
                            Message msg = new Message();
                            msg.what = isfail;
                            msg.obj = message;
                            handler.sendMessage(msg);
                        }


                    }
                });
            }
        }).start();
    }

    @Override
    public void itemClick(View v) {
        int i;
        i = (Integer)v.getTag();
        switch (v.getId()){
            case R.id.tv_groupOwnerAgree:
                //Toast.makeText(this, ""+i, Toast.LENGTH_SHORT).show();
                auditingAddGroup(newsEntryGroupInfoBeanList.get(i).getUserID(),newsEntryGroupInfoBeanList.get(i).getGroupID(),"1");
                newsEntryGroupAdapter.notifyDataSetChanged();
                lv_entryGroup.setAdapter(newsEntryGroupAdapter);
                break;
            case R.id.tv_groupOwnerRefuse:
                //Toast.makeText(this, ""+i, Toast.LENGTH_SHORT).show();
                auditingAddGroup(newsEntryGroupInfoBeanList.get(i).getUserID(),newsEntryGroupInfoBeanList.get(i).getGroupID(),"2");
                break;
        }
    }

    @Override
    public void run() {

    }

    public void auditingAddGroup(final String userID,final String groupID,final String status){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("groupID",groupID)
                        .add("status",status)
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_auditingAddGroup)
                        .post(formbody)
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

                        newsEntryGroupInfoBeanList.clear();
                        JSONObject jsonObject = null;
                        String success = null;
                        String message = null;
                        try {
                            jsonObject = new JSONObject(response.body().string());
                            success = jsonObject.getString("success");
                            message = jsonObject.getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        }).start();
    }
}
