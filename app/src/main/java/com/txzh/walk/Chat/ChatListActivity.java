package com.txzh.walk.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.adapter.EaseConversationAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAllGroup;
import com.hyphenate.easeui.model.EaseGlobal;
import com.hyphenate.easeui.model.EaseMember;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.txzh.walk.Bean.GroupInfoBean;
import com.txzh.walk.Group.GroupMembers;
import com.txzh.walk.Group.GroupSingleMember;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.txzh.walk.MainActivity.userID;
import static com.txzh.walk.NetWork.NetWorkIP.URL_attainUserInforByHX;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainAllGroupId;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainOneGroupId;


public class ChatListActivity extends FragmentActivity{
    int a=0;
    int b=0;
    List<EaseAllGroup> groupList = new ArrayList<>();            //easeui自定义
    List<EaseMember> memberList = new ArrayList<>();            //easeui自定义

    private EaseConversationListFragment conversationFragment;
    private Boolean ishander = false;
    private Boolean obtainOneGroup = false;
    private Handler handler;
    private Bundle bundle = new Bundle();

    TextView back_chat_list;
    public static void show(Context context) {
        Intent intent = new Intent(context, ChatListActivity.class);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        back_chat_list = (TextView)findViewById(R.id.back_chat_list);
        back_chat_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatListActivity.this.finish();
            }
        });

        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        if(!conversations.isEmpty()){
            List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
            List<EMConversation> list = new ArrayList<EMConversation>();
            for (Pair<Long, EMConversation> sortItem : sortList) {
                list.add(sortItem.second);
            }
            for (EMConversation s:list){
                if(s.getType().name().equals("Chat")){
                    a=a+s.getUnreadMsgCount();
                    obtainChatSingle(s.conversationId());
                }
                if(s.getType().name().equals("GroupChat")){
                    b=b+s.getUnreadMsgCount();
                    obtainOnClickGroupInfo(s.conversationId());
                }
            }

            Log.i("0000000","我是未读消息条数："+ a+"----"+b);
        }
        initView();
    }





    public void initView() {


        conversationFragment = new EaseConversationListFragment();
        conversationFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                if(conversation.getType().name().equals("GroupChat")){
                    ishander = true;
                    obtainOnClickGroupInfo(conversation.conversationId());

                    handler = new Handler(){
                        public void handleMessage(Message msg){
                            if(msg.what==1){
                                if(msg.obj.equals("true")){
                                    Intent intent = new Intent(ChatListActivity.this,ChatGroup.class);
                                    intent.putExtra("groupInfo",bundle);
                                    startActivity(intent);
                                }
                            }
                        }

                    };

        //            startActivity(new Intent(ChatListActivity.this, ChatGroup.class).putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId()));
                }if(conversation.getType().name().equals("Chat")){
                    startActivity(new Intent(ChatListActivity.this, ChatSingle.class).putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId()).putExtra("nickName",conversation.conversationId()));
                }
            }

        });

        getSupportFragmentManager().beginTransaction().add(R.id.list, conversationFragment).commit();

    }

    public void obtainOnClickGroupInfo(String groupHXID){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("userID",userID)
                .add("groupHXID",groupHXID)
                .build();

        Request request = new Request.Builder()
                .url(URL_obtainOneGroupId)
                .post(formBody)
                .build();

        Response response;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    return;
                }
                try {
                    String groupId = null;
                    String groupHXId= null;
                    String groupName = null;
                    String groupHostID= null;
                    String status= null;
                    String grouppPic = null;

                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    obtainOneGroup = Boolean.valueOf(jsonObject.getString("success"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        GroupInfoBean groupInfoBean = new GroupInfoBean();

                        groupId = object.getString("groupID");
                        groupHXId = object.getString("groupHXID");
                        grouppPic = object.getString("groupPic");
                        groupName = object.getString("groupName");
                        groupHostID = object.getString("groupHostID");
                        status = object.getString("status");
                        Log.i("888888881111111111",grouppPic);
//添加群组头像昵称信息
                        EaseAllGroup ep = new EaseAllGroup();           //环信自定义easeui
                        ep.group_hxid = groupHXId;
                        ep.group_headphoto = grouppPic;
                        ep.group_nickname = groupName;
                        URL imageurl = new URL(grouppPic);
                        try {
                            HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            ep.group_bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        groupList.add(ep);
                    }
                    EaseGlobal.groupList = groupList;

                    if(obtainOneGroup){
                        bundle.putString("groupName",groupName);
                        bundle.putString("groupID",groupId);
                        bundle.putString("groupHostID", groupHostID);
                        bundle.putString("groupHXID",groupHXId);

                        if(ishander){
                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            msg.obj ="true";
                            handler.sendMessage(msg);
                            ishander = false;
                        }
                    }

                } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });

    }

    public void obtainChatSingle(final String userName){
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody=new FormBody.Builder()
                .add("userName",userName)
                .build();

        Request request = new Request.Builder()
                .url(URL_attainUserInforByHX)
                .post(formBody)
                .build();

        Response response;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("++++","我是环信请求的是获取所有消息列表个人、昵称：请求失败");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(!response.isSuccessful()){
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        GroupInfoBean groupInfoBean = new GroupInfoBean();

                        String nickName = object.getString("nickName");
                        String headPath = object.getString("headPath");
                        String status = object.getString("status");

                        Log.i("8888888888888222222",headPath);
                        ///添加好友头像昵称信息
                        EaseMember em = new EaseMember();           //环信自定义easeui
                        em.member_hxid = userName;
                        em.member_headphoto = headPath;
                        em.member_nickname = nickName;
                        URL imageurl = new URL(headPath);
                        try {
                            HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            em.bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        memberList.add(em);
                    }
                    //添加自己头像昵称信息
                    EaseMember em2 = new EaseMember();
                    em2.member_hxid = Tools.getAccounts();
                    em2.member_headphoto = Tools.getHeadPhoto();
                    em2.member_nickname = Tools.getNickName();
                    URL imageurl = new URL( Tools.getHeadPhoto());
                    try {
                        HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        em2.bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    memberList.add(em2);
                    EaseGlobal.memberList = memberList;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }













    EMMessageListener messageListener=new EMMessageListener() {
       @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //接收到新的消息
            refreshUIWithMessage();
            /*for (EMMessage message : messages) {
                //                EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());

                //接收并处理扩展消息
                String UserName=message.getStringAttribute("TO_nickName","");
                String UserPic=message.getStringAttribute("TO_headPath","");
                String hxIdFrom=message.getFrom();

                EaseUser easeUser=new EaseUser(hxIdFrom);
                easeUser.setAvatar(UserPic);
                easeUser.setNickname(UserName);




                // in background, do not refresh UI, notify it in notification bar
//                    if(!easeUI.hasForegroundActivies()){
//                        getNotifier().onNewMsg(message);
//                    }
            }*/
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                // refresh conversation list
                if (conversationFragment != null) {
                    conversationFragment.refresh();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }*/



    @Override

    protected void onDestroy() {

        super.onDestroy();

        EMClient.getInstance().chatManager().removeMessageListener(messageListener);

    }



}

