package com.txzh.walk.Adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.Bean.GroupAddInfoBean;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;
import com.txzh.walk.customComponents.CircleImageView;

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

public class GroupAddMembersInfoAdapter extends BaseAdapter  {
    private List<GroupAddInfoBean> groupAddInfoBeanList;
    private Context context;
    private String groupID;
    private ViewHolder viewHolder;
    private Handler handler;

    public GroupAddMembersInfoAdapter(List<GroupAddInfoBean> groupAddInfoBeanList,Context context,String groupID){
        this.groupAddInfoBeanList = groupAddInfoBeanList;
        this.context = context;
        this.groupID = groupID;
    }
    @Override
    public int getCount() {
        return groupAddInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return groupAddInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final GroupAddInfoBean groupAddInfoBean = groupAddInfoBeanList.get(i);
        if(view == null){
            view = View.inflate(context, R.layout.adapter_addgroupmembers_info,null);
            viewHolder = new ViewHolder();
            handler = new Handler();
            viewHolder.civ_groupHeadPortrait = view.findViewById(R.id.civ_groupHeadPortrait);
            viewHolder.tv_groupName = view.findViewById(R.id.tv_groupName);
            viewHolder.tv_userAccounts = view.findViewById(R.id.tv_userAccounts);
            viewHolder.btn_applyForAdmission = view.findViewById(R.id.btn_applyForAdmission);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.civ_groupHeadPortrait.setEnabled(false);
        viewHolder.civ_groupHeadPortrait.setImageUrl(groupAddInfoBean.getUserHead(),R.drawable.headportrait);
        viewHolder.tv_groupName.setText("昵称:"+groupAddInfoBean.getUserNickName());
        viewHolder.tv_userAccounts.setText("帐号:"+groupAddInfoBean.getUserAccounts());
        viewHolder.btn_applyForAdmission.setText("邀请");
        viewHolder.btn_applyForAdmission.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                applyForAdmission(groupAddInfoBean.getUserID());
            }
        });

        return view;
    }

    private void applyForAdmission(final String userID){

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("groupID",groupID)
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_addUser)
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

                        String success = null;
                        String message = null;

                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            success = object.getString("success");
                            message = object.getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String finalSuccess = success;
                        final String finalMessage = message;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if("true".equals(finalSuccess)){
                                    Toast.makeText(context, ""+finalMessage, Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, ""+finalMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).start();

    }

    class ViewHolder{
        CircleImageView civ_groupHeadPortrait;
        TextView tv_groupName;
        TextView tv_userAccounts;
        Button btn_applyForAdmission;
    }
}
