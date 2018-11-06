package com.txzh.walk.Register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.txzh.walk.BroadcastReceiver.NetworkChangeReceiver;
import com.txzh.walk.MainActivity;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RetrievePassword extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private ImageButton ib_Return;
    private Button btn_VerificationCode,btn_Next;
    public static EditText et_PhoneNumber;
    private EditText et_VerificationCode;
    private String PhoneNumber;
    private String CordNumber;
    private EventHandler eventHandler;
    public static boolean flag = true;
    private NetworkChangeReceiver networkChangeReceiver;//监听网络状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SMSSDK.initSDK(this, "2858593230ed4","6f1e6ea3e7d977d4ac85bcd7dd651ad1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);

        init();

        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg=handler.obtainMessage(0x00);
                msg.arg1=event;
                msg.arg2=result;
                msg.obj=data;
                handler.sendMessage(msg);
            }
        };

        SMSSDK.registerEventHandler(eventHandler);
    }

    protected void init(){
        ib_Return = findViewById(R.id.ib_Return);
        ib_Return.setOnClickListener(this);
        btn_VerificationCode = findViewById(R.id.btn_VerificationCode);
        btn_VerificationCode.setOnClickListener(this);
        btn_Next = findViewById(R.id.btn_Next);
        btn_Next.setOnClickListener(this);
        et_PhoneNumber = findViewById(R.id.et_PhoneNumber);
        et_VerificationCode = findViewById(R.id.et_VerificationCode);
    }

    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    //注册NetworkChanagerReceiver广播
    protected void onResume() {
        if(networkChangeReceiver == null){
            networkChangeReceiver = new NetworkChangeReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver,intentFilter);
        System.out.print("注册");

        super.onResume();
    }

    //销毁NetworkChanagerReceiver广播
    protected void onPause(){
        unregisterReceiver(networkChangeReceiver);
        System.out.print("销毁");
        super.onPause();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0x00:
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;

                    if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        if(result == SMSSDK.RESULT_COMPLETE){
                            boolean smart = (Boolean)data;
                            if(smart){
                                Toast.makeText(RetrievePassword.this, "该手机号已注册", Toast.LENGTH_SHORT).show();
                                et_PhoneNumber.requestFocus();
                                return;
                            }
                        }
                    }
                    if(result == SMSSDK.RESULT_COMPLETE){
                        if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                            if(MainActivity.id == R.id.tv_registered_account){
                                intent = new Intent(RetrievePassword.this,RegisteredUI.class);
                                startActivity(intent);
                            }else if(MainActivity.id == R.id.tv_forget_password){
                                intent = new Intent(RetrievePassword.this,NewPassword.class);
                                startActivity(intent);
                            }

                        }
                    }

                    break;
                case 0x01:
                    btn_VerificationCode.setText("重新发送("+msg.arg1+")");
                    break;
                case 0x02:
                    btn_VerificationCode.setText("获取验证码");
                    btn_VerificationCode.setClickable(true);
                }


        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_Return:
                //Toast.makeText(this, "aaaaa", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case  R.id.btn_VerificationCode:
                if(flag){
                    checkPhone(et_PhoneNumber.getText().toString().trim());
                }else {
                    Toast.makeText(this, "请连接网络", Toast.LENGTH_SHORT).show();
                }

                break;

            case  R.id.btn_Next:
                if (MainActivity.id == R.id.tv_forget_password){
                    intent = new Intent(this,NewPassword.class);
                    startActivity(intent);
                }else if(MainActivity.id == R.id.tv_registered_account){
                    judCord();
                    //intent = new Intent(RetrievePassword.this,RegisteredUI.class);
                    //startActivity(intent);
                }

                break;
        }
    }

    //校验电话号码
    private boolean jubPhone(){
        if(TextUtils.isEmpty(et_PhoneNumber.getText().toString().trim())){
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
            et_PhoneNumber.requestFocus();
            return false;
        }else if(et_PhoneNumber.getText().toString().trim().length()!=11){
            Toast.makeText(this, "号码数不正确", Toast.LENGTH_SHORT).show();
            et_PhoneNumber.requestFocus();
        }else {
            PhoneNumber = et_PhoneNumber.getText().toString().trim();
            String num = "[1][3578]\\d{9}";
            if(PhoneNumber.matches(num)){
                return true;
            }else{
                Toast.makeText(this, "号码不正确", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    //校验验证码
    private void judCord(){
        jubPhone();
        if(TextUtils.isEmpty(et_VerificationCode.getText().toString().trim())){
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            et_VerificationCode.requestFocus();
        }else if(et_VerificationCode.getText().toString().trim().length()!=4){
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            et_VerificationCode.requestFocus();
        }else  {
            CordNumber = et_VerificationCode.getText().toString().trim();
            SMSSDK.submitVerificationCode("86",PhoneNumber,CordNumber);
        }

    }

    //获取验证码
    private void getCord(){
        //Toast.makeText(this, "bbbbb", Toast.LENGTH_SHORT).show();
        if(jubPhone()){
            SMSSDK.getVerificationCode("86",et_PhoneNumber.getText().toString().trim());
            //Toast.makeText(this, ""+et_PhoneNumber.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            et_VerificationCode.requestFocus();
            btn_VerificationCode.setClickable(false);

            //开启线程去更新button的text
            new Thread(){
                public void run(){
                    int totalTime = 60;
                    for(int i = 0;i<totalTime;i++){
                        Message message = handler.obtainMessage(0x01);
                        message.arg1 = totalTime - i;
                        handler.sendMessage(message);
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(0x02);
                }
            }.start();
        }
    }

    //检查电话号码是否被注册过
    private void checkPhone(final String phone){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = null;
                if (MainActivity.id == R.id.tv_registered_account){
                    URL = NetWorkIP.URL_checkPhone;
                }else if(MainActivity.id == R.id.tv_forget_password){
                    URL = NetWorkIP.URL_forgetPassword;
                }
                OkHttpClient client = new OkHttpClient();
                RequestBody fromBody = new FormBody.Builder()
                        .add("phone",phone)
                        .build();
                Request request = new Request.Builder()
                        .url(URL)
                        .post(fromBody)
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
                            Toast.makeText(RetrievePassword.this, "无响应", Toast.LENGTH_SHORT).show();
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
                                    //JSONArray array = new JSONArray(response.body().string());

                                    if("true".equals(finalSuccess)){
                                        getCord();
                                        Log.i("cccc","1"+ finalSuccess);
                                    }else {
                                        if(MainActivity.id == R.id.tv_registered_account){
                                            Toast.makeText(RetrievePassword.this, ""+ finalMessage, Toast.LENGTH_SHORT).show();
                                            Log.i("cccc","2"+ finalSuccess);
                                            Log.i("cccc","3"+ finalMessage);
                                        }else if(MainActivity.id == R.id.tv_forget_password){
                                            Toast.makeText(RetrievePassword.this, "手机号验证失败", Toast.LENGTH_SHORT).show();
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
