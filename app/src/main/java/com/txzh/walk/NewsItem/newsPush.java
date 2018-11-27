package com.txzh.walk.NewsItem;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.txzh.walk.Adapter.NewsSystemPushAdapter;
import com.txzh.walk.Bean.NewsSystemPushInfoBean;
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

import static com.txzh.walk.HomePage.WalkHome.context;

public class newsPush extends AppCompatActivity implements View.OnClickListener {
    private ListView lv_systemPush;
    private ImageButton ib_systemPushReturn;
    private Handler handler;

    private NewsSystemPushAdapter newsSystemPushAdapter;
    private List<NewsSystemPushInfoBean> newsSystemPushInfoBeanList = new ArrayList<NewsSystemPushInfoBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_push);

        handler = new Handler();

        lv_systemPush = findViewById(R.id.lv_systemPush);
        ib_systemPushReturn = findViewById(R.id.ib_systemPushReturn);
        ib_systemPushReturn.setOnClickListener(this);

        getSystemPush();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_systemPushReturn:
               finish();
                break;
        }
    }

    public void getSystemPush(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("userID", ""+ Tools.getUserID())
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_attainSystem)
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

                        newsSystemPushInfoBeanList.clear();
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
                                NewsSystemPushInfoBean newsSystemPushInfoBean = new NewsSystemPushInfoBean();

                                newsSystemPushInfoBean.setDate(object.getString("date"));
                                newsSystemPushInfoBean.setTheme(object.getString("theme"));
                                newsSystemPushInfoBean.setContent(object.getString("content"));
                                newsSystemPushInfoBean.setStatus(object.getString("status"));

                                newsSystemPushInfoBeanList.add(newsSystemPushInfoBean);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String finalSuccess = success;
                        final String finalMessage = message;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                newsSystemPushAdapter = new NewsSystemPushAdapter(newsSystemPushInfoBeanList,context);
                                lv_systemPush.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                                lv_systemPush.setStackFromBottom(true);
                                lv_systemPush.setAdapter(newsSystemPushAdapter);
                            }
                        });

                    }
                });
            }
        }).start();
    }
}
