package com.txzh.walk;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseGlobal;
import com.hyphenate.easeui.model.EaseMember;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.txzh.walk.ToolClass.Tools;

import java.util.List;
import java.util.Map;

import static com.hyphenate.easeui.utils.EaseUserUtils.getUserInfo;

public class Walk extends Application {
    EaseUI easeUI;
    EMMessageListener messageListener;
    @Override
    public void onCreate()
    {
        super.onCreate();
        easeUI = EaseUI.getInstance();
        EaseUI.getInstance().init(getApplicationContext(), null);
        EaseUI.getInstance().init(this, initOptions());

        setEaseUIProviders();
    }


    /**
     * 初始化
     * @return
     */
    private EMOptions initOptions() {
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        // options.setAppKey("lzan13#hxsdkdemo");

        EMOptions options = new EMOptions();
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送回执，
        options.setRequireDeliveryAck(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        return options;
    }

    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
    }

    private EaseUser getUserInfo(String username){
        //获取 EaseUser实例, 这里从内存中读取
        //如果你是从服务器中读读取到的，最好在本地进行缓存
        EaseUser user = null;
        //如果用户是本人，就设置自己的头像
        if(username.equals(EMClient.getInstance().getCurrentUser())){
            user=new EaseUser(username);
            if(EaseGlobal.memberList.size()>0){
                for(EaseMember em : EaseGlobal.memberList) {
                    if (em.member_hxid.equals(EMClient.getInstance().getCurrentUser())) {
                        user.setAvatar(em.member_headphoto);
                        Log.i("hhhhhh", "我是Walk头像:" + Tools.getHeadPhoto()+"头像："+em.member_headphoto+"==="+em.member_nickname);
                        user.setNickname(em.member_nickname);
                        break;
                    }
                }
            }else {
                user.setAvatar(Tools.getHeadPhoto());
                Log.i("gggg", "我是Walk头像:" + Tools.getHeadPhoto());
                user.setNickname(Tools.getNickName());
            }

            return user;
        }else {
        //    user.setNickname(Uri.parse(""));
        }


        //如果用户不是你的联系人，则进行初始化
        if(user == null){
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }else {
            if (TextUtils.isEmpty(user.getAvatar())){//如果名字为空，则显示环信号码
                user.setNickname(user.getUsername());
            }
        }
        Log.i("zcb","头像："+user.getAvatar());

        return user;
    }


    /**
     * set global listener
     */


}
