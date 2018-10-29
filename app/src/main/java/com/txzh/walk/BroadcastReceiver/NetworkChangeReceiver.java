package com.txzh.walk.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.txzh.walk.Register.RetrievePassword;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.print("网络状态发生变化");
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
            //获取ConnectivityManager对象
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetwokInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(wifiNetworkInfo.isConnected() && dataNetwokInfo.isConnected()){
                Toast.makeText(context, "WIFI已连接，移动数据已连接", Toast.LENGTH_SHORT).show();
            }else if(wifiNetworkInfo.isConnected() && !dataNetwokInfo.isConnected()){
                Toast.makeText(context, "WIFI已连接，移动数据已断开", Toast.LENGTH_SHORT).show();
            }else if(!wifiNetworkInfo.isConnected() && dataNetwokInfo.isConnected()){
                Toast.makeText(context, "WIFI已断开，移动数据已连接", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "WIFI已断开，移动数据已断开", Toast.LENGTH_SHORT).show();
            }
        }else {
            System.out.print("API level 大23");

            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网路连接的信息
            Network[] networks = connectivityManager.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for(int i = 0;i<networks.length;i++){
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networks[i]);
                sb.append(networkInfo.getTypeName()+"connect is"+networkInfo.isConnected());
            }
            if(sb.toString().isEmpty()){
                RetrievePassword.flag = false;
                Toast.makeText(context, "当前无网络连接", Toast.LENGTH_SHORT).show();
            }else if(sb.toString().length() != 0){
                RetrievePassword.flag = true;
                Toast.makeText(context, "网络已连接", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(context, ""+sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
