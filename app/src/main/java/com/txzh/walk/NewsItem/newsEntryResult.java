package com.txzh.walk.NewsItem;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.txzh.walk.Adapter.NewsEntryResultAdapter;
import com.txzh.walk.Bean.NewsEntryResultInfoBean;
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

public class newsEntryResult extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_entryedResultReturn;
    private ListView lv_entryResult;

    private NewsEntryResultAdapter newsEntryResultAdapter;
    private List<NewsEntryResultInfoBean> newsEntryResultInfoBeanList = new ArrayList<NewsEntryResultInfoBean>();

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_entry_result);

        handler = new Handler();

        ib_entryedResultReturn = findViewById(R.id.ib_entryedResultReturn);
        ib_entryedResultReturn.setOnClickListener(this);
        lv_entryResult = findViewById(R.id.lv_entryResult);

        getNewsEntryResult();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_entryedResultReturn:
                finish();
                break;
        }
    }

    public void getNewsEntryResult(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("userID", ""+ Tools.getUserID())
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_attainEntryResult)
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

                        newsEntryResultInfoBeanList.clear();
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
                                NewsEntryResultInfoBean newsEntryResultInfoBean = new NewsEntryResultInfoBean();

                                newsEntryResultInfoBean.setDate(object.getString("date"));
                                newsEntryResultInfoBean.setGroupName(object.getString("groupName"));
                                newsEntryResultInfoBean.setStatus(object.getString("status"));

                                newsEntryResultInfoBeanList.add(newsEntryResultInfoBean);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String finalSuccess = success;
                        final String finalMessage = message;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(finalSuccess.equals("true")){
                                    newsEntryResultAdapter = new NewsEntryResultAdapter(newsEntryResultInfoBeanList,context);
                                    lv_entryResult.setAdapter(newsEntryResultAdapter);
                                }else {
                                    Toast.makeText(newsEntryResult.this, ""+ finalMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });
            }
        }).start();
    }
}
