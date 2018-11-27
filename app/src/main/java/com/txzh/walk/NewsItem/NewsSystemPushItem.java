package com.txzh.walk.NewsItem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.txzh.walk.R;

public class NewsSystemPushItem extends AppCompatActivity {
    private TextView tv_itemTheme;
    private TextView tv_itemData;
    private TextView tv_itemContent;

    private ImageButton ib_systemPushitemReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_system_push_item);

        tv_itemTheme = findViewById(R.id.tv_itemTheme);
        tv_itemData = findViewById(R.id.tv_itemData);
        tv_itemContent = findViewById(R.id.tv_itemContent);

        ib_systemPushitemReturn = findViewById(R.id.ib_systemPushitemReturn);
        ib_systemPushitemReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String itemTheme = intent.getStringExtra("theme");
        String itemData = intent.getStringExtra("data");
        String itemContent = intent.getStringExtra("content");

        tv_itemTheme.setText("主题:"+itemTheme);
        tv_itemData.setText("时间:"+itemData);
        tv_itemContent.setText("内容:"+itemContent);
    }
}
