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
    public static MyLocationData locData;
    public static double lo,la;
    public static float ra;
    public BDLocation location;
    private float mCurrentAccracy;

    @Override
    public void onReceiveLocation(final BDLocation location) {
        StringBuffer sb = new StringBuffer(256);
        String detailAddress="";

        if (location.getLocType() == BDLocation.TypeGpsLocation){                       //GPS定位
            Log.i("----","我是GPS定位："+location.getAddrStr());
            detailAddress=location.getAddrStr();
        }else if(location.getLocType() == BDLocation.TypeNetWorkLocation){            //网络定位
            detailAddress=location.getAddrStr();
            Log.i("----","我是网络定位："+location.getAddrStr());
        }else if (location.getLocType() == BDLocation.TypeOffLineLocation){            //离线定位
            detailAddress=location.getAddrStr();
            Log.i("----","我是离线定位："+location.getAddrStr());
        }
//        List<Poi> list = location.getPoiList();    // POI数据
//        if (list != null) {
//            sb.append("\npoilist size = : ");
//            sb.append(list.size());
//            for (Poi p : list) {
//                sb.append("\npoi= : ");
//                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//            }
//        }

        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果

        //以下只列举部分获取经纬度相关（常用）的结果信息

        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        if(location!=null){
            this.location=location;
//错误代码，如果定位失败会返回这样的经纬度[4.9E-324, 4.9E-324]
            if(62==location.getLocType()||63==location.getLocType()||67==location.getLocType()||(161<location.getLocType() && location.getLocType()<168)){
                this.location=null;
            }
        }



        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f
        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
        // 获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentAccracy).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);

        //判断是否是第一次定位
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

        Log.i("---------", mCurrentAccracy+detailAddress+location.getLocationDescribe()+location.getCityCode() + "---" + latitude + "--" + longitude + "----" + location.getLocType() + "--" + location.getCountry() + "--" + location.getCity() + "--" + location.getAddrStr());


    }


}
