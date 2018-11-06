package com.txzh.walk.Register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.txzh.walk.MainActivity;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;

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

public class RegisteredUI extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_Return;
    private Button btn_Registered;
    private EditText et_accounts,et_password,et_confirmPassword;
    private String phoneNumber;
    private String accounts,password,confirmPassword;
    private Intent intent;
    private Handler handler;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_ui);

        init();

    }

    private void init(){
        ib_Return = findViewById(R.id.ib_Return);
        ib_Return.setOnClickListener(this);
        btn_Registered = findViewById(R.id.btn_Registered);
        btn_Registered.setOnClickListener(this);
        et_accounts = findViewById(R.id.et_accounts);
        et_password = findViewById(R.id.et_password);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        handler = new Handler();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_Return:
                //Toast.makeText(this, "aaaaa", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, RetrievePassword.class);
                startActivity(intent);
                break;
            case  R.id.btn_Registered:
                phoneNumber = RetrievePassword.et_PhoneNumber.getText().toString().trim();
                accounts = et_accounts.getText().toString().trim();
                password = et_password.getText().toString().trim();
                confirmPassword = et_confirmPassword.getText().toString().trim();
                if(judAccounts()){
                    userInformation(accounts,password,phoneNumber);
                }

                break;
        }
    }

    //检查账号和密码格式是否正确
    private boolean judAccounts(){
        if(!accounts.isEmpty() && accounts.length()<11){
            String str = stringFilter(accounts);
            if(accounts.equals(str)){
                if(password.length()>5 && password.length()<11){
                    if(password.equals(confirmPassword)){
                        return true;
                    }else {
                        Toast.makeText(this, "两个密码不一致", Toast.LENGTH_SHORT).show();
                        return false;
                    }
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

    //注册帐号
    private void userInformation(final String accounts,final String password,final String phoneNumber){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("userName",accounts)
                        .add("password",password)
                        .add("phone",phoneNumber)
                        .build();
                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_register)
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

                        JSONObject object = null;
                        String success = null;
                        String message = null;
                        try {
                            object = new JSONObject(response.body().string());
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
                                //Toast.makeText(RetrievePassword.this, ""+b, Toast.LENGTH_SHORT).show();

                                    if("true".equals(finalSuccess)){
                                        Toast.makeText(RegisteredUI.this, ""+ finalSuccess, Toast.LENGTH_SHORT).show();
                                        intent = new Intent(RegisteredUI.this, MainActivity.class);
                                        startActivity(intent);
                                        Tools.setAccounts(accounts);
                                    }else {
                                        Toast.makeText(RegisteredUI.this, ""+ finalMessage, Toast.LENGTH_SHORT).show();
                                    }

                                    //Log.i("bbbb","success:"+success+"message:"+message);
                            }
                        });
                    }
                });
             }
        }).start();

    }

}
