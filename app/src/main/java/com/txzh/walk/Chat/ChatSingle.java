package com.txzh.walk.Chat;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.TileOverlayOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;

public class ChatSingle extends FragmentActivity{

    private TextView back_chat_single,title_chat_single;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_single);
        back_chat_single = (TextView)findViewById(R.id.back_chat_single);
        title_chat_single = (TextView)findViewById(R.id.title_chat_single);
        title_chat_single.setText(getIntent().getStringExtra("nickName"));
        back_chat_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        EaseChatFragment chatFragment = new EaseChatFragment();

        chatFragment.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

               message.setAttribute("FROM_nickName", Tools.getNickName());
               message.setAttribute("FROM_headPath", Tools.getHeadPhoto());

               message.setAttribute("TO_nickName", getIntent().getStringExtra("nickName"));
               message.setAttribute("TO_headPath", getIntent().getStringExtra("headPath"));

            }

            @Override
            public void onEnterToChatDetails() {

            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });



        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, getIntent().getStringExtra("userId"));
        chatFragment.setArguments(args);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_chat, chatFragment).commit();


    }



}
