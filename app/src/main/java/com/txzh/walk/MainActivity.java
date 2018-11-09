package com.txzh.walk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.HomePage.WalkHome;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.Register.RetrievePassword;
import com.txzh.walk.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int BAIDU_READ_PHONE_STATE =10;
    protected Typeface typeface;
    private Intent intent;
    public static TextView tv_AppName,tv_forget_password,tv_registered_account;
    private Button btn_login;
    public static int id;
    private EditText et_accounts,et_password;
    private String accounts,password;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);  //去掉标题栏
        setContentView(R.layout.activity_main);
        showContacts();
        init();

    }

    public void init(){
        //设置字体样式
        typeface = Typeface.createFromAsset(getAssets(),"fonts/font1.ttf");
        tv_AppName = findViewById(R.id.tv_AppName);
        tv_AppName.setTypeface(typeface);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        tv_forget_password = findViewById(R.id.tv_forget_password);
        tv_forget_password.setOnClickListener(this);
        tv_registered_account = findViewById(R.id.tv_registered_account);
        tv_registered_account.setOnClickListener(this);
        et_accounts = findViewById(R.id.et_accounts);
        et_password = findViewById(R.id.et_password);
        handler = new Handler();
    }


    @Override
    public void onClick(View view) {
        id = view.getId();
        switch (id ){
            case R.id.btn_login:
                intent = new Intent(MainActivity.this, WalkHome.class);
                startActivity(intent);
                accounts = et_accounts.getText().toString().trim();
                password = et_password.getText().toString().trim();
                if(judAccounts()){
                    Login(accounts,password);
                }
                break;
            case R.id.tv_forget_password:
                //Toast.makeText(this, "bbbb", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, RetrievePassword.class);
                startActivity(intent);
                break;
            case R.id.tv_registered_account:
                //Toast.makeText(this, "cccc", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, RetrievePassword.class);
                startActivity(intent);
                break;
        }
    }

    //检查账号和密码格式是否正确
    private boolean judAccounts(){
        if(!accounts.isEmpty() && accounts.length()<11){
            String str = stringFilter(accounts);
            if(accounts.equals(str)){
                if(password.length()>5 && password.length()<11){
                        return true;
                }else {
                    Toast.makeText(this, "密码不少于6位，不多于10位", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this, "用户名只能包含字母、数字和汉字", Toast.LENGTH_SHORT).show();
                return false;
            }

        }else {
            Toast.makeText(this, "用户名格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //检查帐号是否包含非法字符
    private String stringFilter(String str){
        String account = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(account);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    //登录
    private void Login(final String accounts,final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userName",accounts)
                        .add("password",password)
                        .build();
                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_login)
                        .post(formBody)
                        .build();
                Response response;
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if(!response.isSuccessful()){
                            return;
                        }

                        JSONObject jsonObject = null;
                        String success = null;
                        String message =null;
                        String [] data = null;
                        try {
                            jsonObject = new JSONObject(response.body().string());
                            success = jsonObject.getString("success");
                            message = jsonObject.getString("message");
                            Tools.setAccounts(accounts);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object = (JSONObject)jsonArray.get(i);
                                Tools.setNickName(object.getString("nickName"));
                                Tools.setPhongNumber(object.getString("phone"));
                                Tools.setSex(object.getString("sex"));
                                Tools.setHeadPhoto(object.getString("headPath"));
                                Tools.setUserID(object.getInt("userID"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String finalSuccess = success;
                        final String finalMessage = message;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(RetrievePassword.this, ""+b, Toast.LENGTH_SHORT).show();


                                    Log.i("bbbb","我是success："+ finalSuccess);

                                        if("true".equals(finalSuccess)){
                                            Toast.makeText(MainActivity.this, ""+ finalSuccess, Toast.LENGTH_SHORT).show();
                                            intent = new Intent(MainActivity.this, WalkHome.class);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(MainActivity.this, ""+ finalMessage, Toast.LENGTH_SHORT).show();
                                        }
                            }
                        });

                    }
                });
            }
        }).start();
    }



    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);
        }
    }

    //退出程序
    protected void onNewIntent(Intent intent1){
        super.onNewIntent(intent1);
        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent1.getFlags()) != 0) {
            finish();
        }
    }


}
