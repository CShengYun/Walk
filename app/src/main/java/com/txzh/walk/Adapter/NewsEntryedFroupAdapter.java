package com.txzh.walk.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.txzh.walk.Bean.NewsEntryedFroupInfoBean;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsEntryedFroupAdapter extends BaseAdapter {
    private List<NewsEntryedFroupInfoBean> newsEntryedFroupInfoBeanList;
    private Context context;
    private ViewHolder viewHolder;
    private android.os.Handler handler;

    public NewsEntryedFroupAdapter(List<NewsEntryedFroupInfoBean> newsEntryedFroupInfoBeanList,Context context){
        this.newsEntryedFroupInfoBeanList = newsEntryedFroupInfoBeanList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return newsEntryedFroupInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsEntryedFroupInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NewsEntryedFroupInfoBean newsEntryedFroupInfoBean = newsEntryedFroupInfoBeanList.get(i);
        if(view == null){
            view = View.inflate(context, R.layout.adapter_news_entryedfroup_info,null);
            viewHolder = new ViewHolder();
            handler = new android.os.Handler();
            viewHolder.tv_entryedTime = view.findViewById(R.id.tv_entryedTime);
            viewHolder.tv_entryedApplicantName = view.findViewById(R.id.tv_entryedApplicantName);
            viewHolder.tv_entryedGroupToApplyFor = view.findViewById(R.id.tv_entryedGroupToApplyFor);
            viewHolder.tv_entryedGroupOwnerAgree = view.findViewById(R.id.tv_entryedGroupOwnerAgree);
            viewHolder.tv_entryedGroupOwnerRefuse = view.findViewById(R.id.tv_entryedGroupOwnerRefuse);
            viewHolder.tv_entryedDisplayResult = view.findViewById(R.id.tv_entryedDisplayResult);

            viewHolder.ll_entryedApplicationResult = view.findViewById(R.id.ll_entryedApplicationResult);
            viewHolder.ll_entryedGroupMasterResponse = view.findViewById(R.id.ll_entryedGroupMasterResponse);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_entryedTime.setText(newsEntryedFroupInfoBean.getDate());
        viewHolder.tv_entryedApplicantName.setText(newsEntryedFroupInfoBean.getGroupName()+"群群主");
        viewHolder.tv_entryedGroupToApplyFor.setText("邀请你加入"+newsEntryedFroupInfoBean.getGroupName()+"群组");

        if(newsEntryedFroupInfoBean.getStatus().equals("3")){
            viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.VISIBLE);
        }else if(newsEntryedFroupInfoBean.getStatus().equals("4") || newsEntryedFroupInfoBean.getStatus().equals("8")){
            viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_entryedApplicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_entryedDisplayResult.setText("已同意");
        }else if(newsEntryedFroupInfoBean.getStatus().equals("5") || newsEntryedFroupInfoBean.getStatus().equals("9")){
            viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_entryedApplicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_entryedDisplayResult.setText("已拒绝");
        }

        //设置监听事件
        viewHolder.tv_entryedGroupOwnerAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuditingAddGroup(""+Tools.getUserID(),newsEntryedFroupInfoBean.getGroupID(),"4");
                viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.GONE);
                viewHolder.ll_entryedApplicationResult.setVisibility(View.VISIBLE);
                viewHolder.tv_entryedDisplayResult.setText("已同意");
            }
        });

        viewHolder.tv_entryedGroupOwnerRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuditingAddGroup(""+Tools.getUserID(),newsEntryedFroupInfoBean.getGroupID(),"5");
                viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.GONE);
                viewHolder.ll_entryedApplicationResult.setVisibility(View.VISIBLE);
                viewHolder.tv_entryedDisplayResult.setText("已拒绝");
            }
        });

        return view;
    }

    class ViewHolder{
        TextView tv_entryedTime;
        TextView tv_entryedApplicantName;
        TextView tv_entryedGroupToApplyFor;
        TextView tv_entryedGroupOwnerAgree;
        TextView tv_entryedGroupOwnerRefuse;
        TextView tv_entryedDisplayResult;

        LinearLayout ll_entryedApplicationResult;
        LinearLayout ll_entryedGroupMasterResponse;
    }

    public void userAuditingAddGroup(final String userID, final String groupID, final String status) {
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
                        .url(NetWorkIP.URL_userAuditingAddGroup)
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

                        newsEntryedFroupInfoBeanList.clear();
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
                        final String [] s={"Sheng"};
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if("true".equals(finalSuccess)){
                                    //群主加人调用此方法
                                    if("1".equals(status)){
                                        EMClient.getInstance().groupManager().asyncAddUsersToGroup("66556443820033", new String[]{s[0]}, new EMCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                Log.i("66666666666666","我添加sheng 成功");
                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }

                                            @Override
                                            public void onProgress(int i, String s) {

                                            }
                                        });
                                    }

                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
