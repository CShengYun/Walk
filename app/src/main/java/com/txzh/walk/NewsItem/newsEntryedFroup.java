package com.txzh.walk.NewsItem;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.txzh.walk.Adapter.NewsEntryedFroupAdapter;
import com.txzh.walk.Bean.NewsEntryedFroupInfoBean;
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

public class newsEntryedFroup extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_entryedFroupReturn;
    private ListView lv_entryedFroup;

    private NewsEntryedFroupAdapter newsEntryedFroupAdapter;
    private List<NewsEntryedFroupInfoBean> newsEntryedFroupInfoBeanList = new ArrayList<NewsEntryedFroupInfoBean>();

    protected static final int issuccess = 1;
    protected static final int isfail = 2;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            if(msg.what == issuccess){
                newsEntryedFroupAdapter = new NewsEntryedFroupAdapter((List<NewsEntryedFroupInfoBean>) msg.obj,getApplicationContext());
                lv_entryedFroup.setAdapter(newsEntryedFroupAdapter);
            }else if(msg.what == isfail){

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_entryed_froup);

        ib_entryedFroupReturn = findViewById(R.id.ib_entryedFroupReturn);
        ib_entryedFroupReturn.setOnClickListener(this);
        lv_entryedFroup = findViewById(R.id.lv_entryedFroup);

        getNewsEntryedFroup();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_entryedFroupReturn:
                finish();
                break;
        }
    }

    public void getNewsEntryedFroup(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("userID", ""+ Tools.getUserID())
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_attainAddUser)
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

                        newsEntryedFroupInfoBeanList.clear();
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
                                NewsEntryedFroupInfoBean newsEntryedFroupInfoBean = new NewsEntryedFroupInfoBean();

                                newsEntryedFroupInfoBean.setDate(object.getString("date"));
                                newsEntryedFroupInfoBean.setGroupName(object.getString("groupName"));
                                newsEntryedFroupInfoBean.setStatus(object.getString("status"));
                                newsEntryedFroupInfoBean.setGroupID(object.getString("groupID"));

                                newsEntryedFroupInfoBeanList.add(newsEntryedFroupInfoBean);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(success.equals("true")){
                            Message msg = new Message();
                            msg.what = issuccess;
                            msg.obj = newsEntryedFroupInfoBeanList;
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
}
