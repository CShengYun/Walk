package com.txzh.walk.Register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.txzh.walk.MainActivity;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPassword extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private ImageButton ib_Return;
    private Button btn_Finish;
    private EditText et_newPassword,et_newPasswords;
    private String newPassword,newPasswords,phone;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);  //去掉标题栏
        setContentView(R.layout.activity_new_password);

        init();
    }

    public void init(){
        ib_Return = findViewById(R.id.ib_Return);
        ib_Return.setOnClickListener(this);
        btn_Finish = findViewById(R.id.btn_Finish);
        btn_Finish.setOnClickListener(this);
        et_newPassword = findViewById(R.id.et_newPassword);
        et_newPasswords = findViewById(R.id.et_newPasswords);
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
            case  R.id.btn_Finish:
                phone = RetrievePassword.et_PhoneNumber.getText().toString().trim();
                newPassword = et_newPassword.getText().toString().trim();
                newPasswords = et_newPasswords.getText().toString().trim();
               if(checkPassword()){
                   ChangePassword(newPassword,phone);
               }
                break;
        }
    }

    public boolean checkPassword(){
        if(newPassword.length()>5 && newPassword.length()<11){
            if(newPassword.equals(newPasswords)){
                return true;
            }else {
                Toast.makeText(this, "两密码不一致", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(this, "密码不小于6位，不多于10位", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void ChangePassword(final String password,final String phone){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("password",password)
                        .add("phone",phone)
                        .build();
                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_newPassword)
                        .post(formBody)
                        .build();
                final Response response;
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
                                try {
                                    JSONArray array = new JSONArray(response.body().string());

                                    for(int i=0;i<array.length();i++){
                                        JSONObject object = (JSONObject)array.get(i);
                                        String success = object.getString("success");
                                        String message = object.getString("message");
                                        if("true".equals(success)){
                                            Toast.makeText(NewPassword.this, ""+message, Toast.LENGTH_SHORT).show();
                                            intent = new Intent(NewPassword.this, MainActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(NewPassword.this, ""+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
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
