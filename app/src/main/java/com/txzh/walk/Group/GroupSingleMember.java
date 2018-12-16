package com.txzh.walk.Group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.model.EaseGlobal;
import com.hyphenate.easeui.model.EaseMember;
import com.hyphenate.exceptions.HyphenateException;
import com.txzh.walk.Chat.ChatSingle;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;
import com.txzh.walk.customComponents.CircleImageView;
import com.txzh.walk.customComponents.MyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.txzh.walk.NetWork.NetWorkIP.URL_deleteGroupMember;
import static com.txzh.walk.NetWork.NetWorkIP.URL_obtainGroupMemberDetaileData;


public class GroupSingleMember extends AppCompatActivity implements View.OnClickListener {

    public Bitmap bitmap;
    List<EaseMember> memberList = new ArrayList<>();            //easeui自定义

    private ImageButton back_single_member;
    private CircleImageView custom_single_member;
    private TextView nick_name_single_member,account_single_member,phone_single_member,sex_single_member;
    private Button send_news_single_member,remove_group_single_member;
    Intent intent;
    public static Boolean obtainSingleMember = false;

    public static String nickName="",phone="",userName="",sex="",headPath="";
    private Handler handler;
    private Bundle bundleRecive;
    String userID;
    String groupID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                           //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_single_member);
        init();
    }

    public void init(){
        handler = new Handler();
        back_single_member = (ImageButton)findViewById(R.id.back_single_member);
        custom_single_member = (CircleImageView)findViewById(R.id.custom_single_member);
        back_single_member.setOnClickListener(this);

        nick_name_single_member = (TextView)findViewById(R.id.nick_name_single_member);
        account_single_member = (TextView)findViewById(R.id.account_single_member);
        phone_single_member = (TextView)findViewById(R.id.phone_single_member);
        sex_single_member = (TextView)findViewById(R.id.sex_single_member);

        send_news_single_member = (Button)findViewById(R.id.send_news_single_member);
        remove_group_single_member = (Button)findViewById(R.id.remove_group_single_member);
        send_news_single_member.setOnClickListener(this);
        remove_group_single_member.setOnClickListener(this);
    //    EasePreferenceManager.onClearSP();                  //清除缓存
        obtainSingleMemberInfo();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back_single_member:
                GroupSingleMember.this.finish();
                break;

            case R.id.send_news_single_member:

                //参数为要添加的好友的username和添加理由
                try {
                    EMClient.getInstance().contactManager().addContact(userName, "认识");
                    Log.i("hhhhhhh","添加成功"+userName);

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                intent = new Intent(GroupSingleMember.this, ChatSingle.class);
                intent.putExtra("userId",userName);
                intent.putExtra("nickName",nickName);
                intent.putExtra("headPath",headPath);
                startActivity(intent);
                break;

            case R.id.remove_group_single_member:
                Log.i("8888888888","11111111111111我点击了移除群成员。");
                final MyDialog dialog = new MyDialog(this,"否","是");
                dialog.setTitle("移除群成员？");
                dialog.setYesButton(new MyDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {

                        Log.i("8888888888","2222222222222我点击了移除群成员。");
                        removeingleMember();
                        intent = new Intent();
                        intent.putExtra("userInfo","1");
                        setResult(1,intent);
                        finish();

                        dialog.dismiss();
                    }
                });

                dialog.setNoButton(new MyDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        dialog.dismiss();
                    }
                });

                dialog.setOnClickListener(new MyDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }


    public void obtainSingleMemberInfo(){
        bundleRecive = getIntent().getBundleExtra("userIdInfo");
        userID = bundleRecive.getString("userID");

        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("userID",userID)
                .build();

        Request request = new Request.Builder()
                .url(URL_obtainGroupMemberDetaileData)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                JSONObject jsonObject  = null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    obtainSingleMember = Boolean.valueOf(jsonObject.getString("success"));
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        phone = object.getString("phone");
                        nickName = object.getString("nickName");
                        sex = object.getString("sex");
                        headPath = object.getString("headPath");
                        userName = object.getString("userName");


                        //添加好友头像昵称信息
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
Log.i("hhhhhhhhhhhhhhh","个人信息：222222222"+em.bitmap);

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

                    Log.i("hhhhhhhhhhhhhhhh","个人信息：11111111111"+em2.bitmap);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(obtainSingleMember){
                                nick_name_single_member.setText(nickName);
                                phone_single_member.setText(phone);
                                sex_single_member.setText(sex);
                                account_single_member.setText(userName);
                                custom_single_member.setImageUrl(headPath,R.drawable.accounts_icon,null);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void removeingleMember(){
        bundleRecive = getIntent().getBundleExtra("userIdInfo");
        userID = bundleRecive.getString("userID");
        groupID = bundleRecive.getString("groupID");

        Log.i("8888888888","我是点击的userID"+userID+"=="+groupID);
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("userID",userID)
                .add("groupID",groupID)
                .build();

        Request request = new Request.Builder()
                .url(URL_deleteGroupMember)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.i("8888888888","移除群成员"+response.body().string());
            }
        });

    }

}
