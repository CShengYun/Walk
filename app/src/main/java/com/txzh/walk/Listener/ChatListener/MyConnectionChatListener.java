package com.txzh.walk.Listener.ChatListener;

import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.util.NetUtils;
import com.txzh.walk.MainActivity;

import static com.txzh.walk.MainActivity.mainContent;

public class MyConnectionChatListener implements EMConnectionListener {
    @Override
    public void onConnected() {
    }
    @Override
    public void onDisconnected(final int error) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if(error == EMError.USER_REMOVED){
                    // 显示帐号已经被移除
                }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                } else {
                    if (NetUtils.hasNetwork(mainContent)){
                        //连接不到聊天服务器
                        Toast.makeText(mainContent,"连接不到聊天服务器！",Toast.LENGTH_SHORT).show();
                    }
                else{
                        Toast.makeText(mainContent,"当前网络不可用！",Toast.LENGTH_SHORT).show();
                    }
                    //当前网络不可用，请检查网络设置
                }
            }
        });
    }
}
