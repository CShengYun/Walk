package com.txzh.walk.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.txzh.walk.Chat.ChatListActivity;
import com.txzh.walk.R;

public class NewsFragment extends Fragment {
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("我滑动了第三个界面333");
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        TextView news_tv_chat_record = (TextView)view.findViewById(R.id.news_tv_chat_record);
        news_tv_chat_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChatListActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
