package com.txzh.walk;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import com.txzh.walk.Register.RegisteredUI;
import com.txzh.walk.Register.RetrievePassword;

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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(RetrievePassword.this, ""+b, Toast.LENGTH_SHORT).show();
                                try {
                                    JSONArray array = new JSONArray(response.body().string());
                                    Log.i("bbbb",""+array);
                                    for(int i=0;i<array.length();i++){
                                        JSONObject object = (JSONObject)array.get(i);
                                        String success = object.getString("success");
                                        String message = object.getString("message");
                                        if("true".equals(success)){
                                            Toast.makeText(MainActivity.this, ""+success, Toast.LENGTH_SHORT).show();
                                            intent = new Intent(MainActivity.this, WalkHome.class);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //Log.i("bbbb","success:"+success+"message:"+message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
