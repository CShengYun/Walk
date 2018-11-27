package com.txzh.walk.Chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.EaseGlobal;
import com.hyphenate.easeui.model.EaseMember;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.txzh.walk.Bean.GroupMemberInfoBean;
import com.txzh.walk.Group.GroupMembers;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.txzh.walk.HomePage.WalkHome.context;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainGroupMember;

public class ChatGroup extends FragmentActivity implements View.OnClickListener {

    public Bitmap bitmap;
    List<EaseMember> memberList = new ArrayList<>();

    private TextView back_chat_group,title_chat_group,group_member_chat_group;

    String isObtainGroupMemberHX;

    Bundle bundleReceive;
    String groupName,groupID,groupHostID;
    Boolean isObtainGroupMemberUserName = false;
    Handler handler;
    List<GroupMemberInfoBean> listUserName = new ArrayList<GroupMemberInfoBean>();
    String [] string = new String[200];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        init();
        obtainGroupMemberUserName();

    }


    public void init(){
        back_chat_group = (TextView)findViewById(R.id.back_chat_group);
        title_chat_group = (TextView)findViewById(R.id.title_chat_group);
        group_member_chat_group = (TextView)findViewById(R.id.group_member_chat_group);

        back_chat_group.setOnClickListener(this);
        group_member_chat_group.setOnClickListener(this);

    }


    public void obtainGroupMemberUserName(){

        bundleReceive=getIntent().getBundleExtra("groupInfo");
        groupName = bundleReceive.getString("groupName");
        groupID = bundleReceive.getString("groupID");
        groupHostID = bundleReceive.getString("groupHostID");

        title_chat_group.setText(groupName);

        obtainGroupMemberInfoHX();

        Log.i("hhhhh","我是环信ID:"+bundleReceive.getString("groupHXID"));

        EaseChatFragment chatFragment = new EaseChatFragment();
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, bundleReceive.getString("groupHXID"));
        chatFragment.setArguments(args);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_chat_group, chatFragment).commit();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back_chat_group:
                ChatGroup.this.finish();
                break;

            case R.id.group_member_chat_group:

                Bundle bundle = new Bundle();

                bundle.putString("groupName",bundleReceive.getString("groupName"));
                bundle.putString("groupID",bundleReceive.getString("groupID"));
                bundle.putString("groupHostID", bundleReceive.getString("groupHostID"));
                Intent intent = new Intent(context,GroupMembers.class);
                intent.putExtra("groupInfo",bundle);
                startActivity(intent);

                break;
        }
    }


    public void obtainGroupMemberInfoHX(){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("groupID",groupID)
                .build();

        Request request = new Request.Builder()
                .url(URL_obtainGroupMember)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(!response.isSuccessful()){
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    isObtainGroupMemberHX = jsonObject.getString("success");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        GroupMemberInfoBean groupMemberInfoBean = new GroupMemberInfoBean();

                        String userID = object.getString("userID");
                        String userName = object.getString("userName");
                        String headPath = object.getString("headPath");
                        String nickName = object.getString("nickName");
                        String sex = object.getString("sex");
                        String status = object.getString("status");

                        EaseMember em = new EaseMember();           //环信自定义easeui
                        em.member_hxid = userName;
                        em.member_headphoto = headPath;
                        em.member_nickname = nickName;

                        Log.i("hhhhhhhhhhhh55555555555","我是群组的bitmap:"+returnBitMap(headPath));
                        URL imageurl = new URL(headPath);
                        try {
                            HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);
                            em.bitmap = bitmap;
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Log.i("hhhhhhhhhhhh111111111","我是群组的bitmap:"+em.bitmap);
                        memberList.add(em);
                    }

                    //添加自己头像昵称信息
                    EaseMember em2 = new EaseMember();
                    em2.member_hxid = Tools.getAccounts();
                    em2.member_headphoto = Tools.getHeadPhoto();
                    em2.member_nickname = Tools.getNickName();
                    em2.bitmap = returnBitMap(Tools.getHeadPhoto());
                    Log.i("hhhhhhhhhhhh2222","我是群组的bitmap:"+em2.bitmap);
                    memberList.add(em2);
                    EaseGlobal.memberList = memberList;


                    if(isObtainGroupMemberHX.equals("true")){

                    }else if(isObtainGroupMemberHX.equals("false")) {
                        Toast.makeText(context,"服务器繁忙，请重新点击加载！",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"当前网络状况不佳，请重新点击加载！",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public Bitmap returnBitMap(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;

                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    Log.i("hhhhhhhhhhhh3333333333","我是群组的bitmap:"+bitmap);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.i("hhhhhhhhhhhh444444","我是群组的bitmap:"+bitmap);
        return bitmap;
    }


}
