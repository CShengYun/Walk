package com.txzh.walk.Group;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.txzh.walk.R;

public class CreateGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                                       //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
    }
}
