package com.txzh.walk.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.txzh.walk.Bean.NewsEntryGroupInfoBean;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsEntryGroupAdapter extends BaseAdapter {
    private List<NewsEntryGroupInfoBean> newsEntryGroupInfoBeanList;
    private Context context;
    private ViewHolder viewHolder;
    private android.os.Handler handler;

    private String ifSuccess;

    public NewsEntryGroupAdapter(List<NewsEntryGroupInfoBean> newsEntryGroupInfoBeanList, Context context) {
        this.newsEntryGroupInfoBeanList = newsEntryGroupInfoBeanList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return newsEntryGroupInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsEntryGroupInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NewsEntryGroupInfoBean newsEntryGroupInfoBean = newsEntryGroupInfoBeanList.get(i);
        if (view == null) {
            view = View.inflate(context, R.layout.adapter_news_entrygroup_info, null);
            viewHolder = new ViewHolder();
            handler = new Handler();

            viewHolder.tv_applicantName = view.findViewById(R.id.tv_applicantName);
            viewHolder.tv_entryTime = view.findViewById(R.id.tv_entryTime);
            viewHolder.tv_groupOwnerAgree = view.findViewById(R.id.tv_groupOwnerAgree);
            viewHolder.tv_groupToApplyFor = view.findViewById(R.id.tv_groupToApplyFor);
            viewHolder.tv_groupOwnerRefuse = view.findViewById(R.id.tv_groupOwnerRefuse);
            viewHolder.tv_displayResult = view.findViewById(R.id.tv_displayResult);

            viewHolder.ll_groupMasterResponse = view.findViewById(R.id.ll_groupMasterResponse);
            viewHolder.ll_applicationResult = view.findViewById(R.id.ll_applicationResult);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Log.i("qqqqqqqqq", "" + newsEntryGroupInfoBean.getGroupName() + ":" + newsEntryGroupInfoBean.getStatus());
        viewHolder.tv_entryTime.setText(newsEntryGroupInfoBean.getDate());
        viewHolder.tv_applicantName.setText(newsEntryGroupInfoBean.getNickName());
        viewHolder.tv_groupToApplyFor.setText("请求加入" + newsEntryGroupInfoBean.getGroupName() + "群组");
        if (newsEntryGroupInfoBean.getStatus().equals("0") || newsEntryGroupInfoBean.getStatus().equals("10")) {
            viewHolder.ll_groupMasterResponse.setVisibility(View.VISIBLE);
            viewHolder.ll_applicationResult.setVisibility(View.GONE);
        } else if (newsEntryGroupInfoBean.getStatus().equals("1") || newsEntryGroupInfoBean.getStatus().equals("6")) {
            viewHolder.ll_groupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_applicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_displayResult.setText("已同意");
        } else if (newsEntryGroupInfoBean.getStatus().equals("2") || newsEntryGroupInfoBean.getStatus().equals("7")) {
            viewHolder.ll_groupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_applicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_displayResult.setText("已拒绝");
        }

        //设置tag标记
        viewHolder.tv_groupOwnerAgree.setTag(R.id.tv_groupOwnerAgree, i);
        viewHolder.tv_groupOwnerRefuse.setTag(R.id.tv_groupOwnerRefuse, i);
        viewHolder.tv_displayResult.setTag(newsEntryGroupInfoBeanList.get(i));

        //设置监听事件
        viewHolder.tv_groupOwnerAgree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                auditingAddGroup(newsEntryGroupInfoBean.getUserID(), newsEntryGroupInfoBean.getGroupID(), "1");

                viewHolder.ll_groupMasterResponse.setVisibility(View.GONE);
                viewHolder.ll_applicationResult.setVisibility(View.VISIBLE);
                viewHolder.tv_displayResult.setText("已同意");


            }
        });
        viewHolder.tv_groupOwnerRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auditingAddGroup(newsEntryGroupInfoBean.getUserID(), newsEntryGroupInfoBean.getGroupID(), "1");
                viewHolder.ll_groupMasterResponse.setVisibility(View.GONE);
                viewHolder.ll_applicationResult.setVisibility(View.VISIBLE);
                viewHolder.tv_displayResult.setText("已拒绝");
            }
        });

        return view;
    }


    static class ViewHolder {
        TextView tv_entryTime;
        TextView tv_applicantName;
        TextView tv_groupToApplyFor;
        TextView tv_groupOwnerAgree;
        TextView tv_groupOwnerRefuse;
        TextView tv_displayResult;

        LinearLayout ll_groupMasterResponse;
        LinearLayout ll_applicationResult;
    }

    public void auditingAddGroup(final String userID, final String groupID, final String status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("groupID", groupID)
                        .add("status", status)
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
                        if (!response.isSuccessful()) {
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
                        final String finalSuccess = success;
                        final String finalMessage = message;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });

                    }
                });
            }
        }).start();
    }
}
