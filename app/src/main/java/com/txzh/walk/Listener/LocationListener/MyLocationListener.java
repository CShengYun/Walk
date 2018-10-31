package com.txzh.walk.Listener.LocationListener;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import static com.txzh.walk.Fragment.MapFragment.ifFrist;
import static com.txzh.walk.Fragment.MapFragment.mBaiduMap;

public class MyLocationListener extends BDAbstractLocationListener {

    @Override

    public void onReceiveLocation(BDLocation location) {

        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果

        //以下只列举部分获取经纬度相关（常用）的结果信息

        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明



        double latitude = location.getLatitude();    //获取纬度信息

        double longitude = location.getLongitude();    //获取经度信息

        float radius = location.getRadius();    //获取定位精度，默认值为0.0f



        String coorType = location.getCoorType();

        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准



        int errorCode = location.getLocType();

        Log.i("---------", location.getCityCode() + "---" + latitude + "--" + longitude + "----" + coorType + "--" + location.getCountry() + "--" + location.getCity() + "--" + location.getAddrStr());

        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明



          /*  // 构造定位数据

            MyLocationData locData = new MyLocationData.Builder()

                    .accuracy(location.getRadius())

                    // 此处设置开发者获取到的方向信息，顺时针0-360

                    .direction(100).latitude(location.getLatitude())

                    .longitude(location.getLongitude()).build();



            // 设置定位数据

            mBaiduMap.setMyLocationData(locData);*/



        if (ifFrist) {

            LatLng ll = new LatLng(location.getLatitude(),

                    location.getLongitude());

            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);

            // 移动到某经纬度

            mBaiduMap.animateMapStatus(update);

            update = MapStatusUpdateFactory.zoomBy(5f);

            // 放大

            mBaiduMap.animateMapStatus(update);



            ifFrist = false;

        }



        // 显示个人位置图标

        MyLocationData.Builder builder = new MyLocationData.Builder();

        builder.latitude(location.getLatitude());

        builder.longitude(location.getLongitude());

        MyLocationData data = builder.build();

        mBaiduMap.setMyLocationData(data);

    }

}
