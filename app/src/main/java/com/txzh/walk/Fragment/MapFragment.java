package com.txzh.walk.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.txzh.walk.Bean.GroupMemberLocationBean;
import com.txzh.walk.Listener.LocationListener.MyLocationListener;
import com.txzh.walk.Listener.SensorListener.MyOrientationListener;
import com.txzh.walk.R;

import java.util.List;

public class MapFragment extends Fragment{
    @SuppressWarnings("unused")
    public MapView mMapView;
    View view;
    LatLng latLng;      //获取经纬度

    public static MyOrientationListener myOrientationListener;
    public static BaiduMap mBaiduMap;//定义地图实例
    public static boolean ifFrist = true;//判断是否是第一次进去


    public LocationClient mLocationClient = null;//定义LocationClient
    public MyLocationListener myListener = new MyLocationListener();//继承BDAbstractLocationListener的class



//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        System.out.println("我滑动了第yi个界面11");
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //声明OrientationListener类
    //    myOrientationListener = new MyOrientationListener(getActivity().getApplicationContext());
        //声明LocationClient类
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.bmapView);

        initLocation();                     //获得自己位置

        return view;
    }

    //获得自己位置
    public void initLocation(){
        mBaiduMap = mMapView.getMap();                  //获取地图实例对象

        mBaiduMap.setMyLocationEnabled(true);                   // 开启定位图层

        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true,null));//设置定位图标是否有箭头

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，设置定位模式，默认高精度//LocationMode.Hight_Accuracy：高精度；//LocationMode. Battery_Saving：低功耗；//LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");//可选，设置返回经纬度坐标类型，默认gcj02//gcj02：国测局坐标；//bd09ll：百度经纬度坐标；//bd09：百度墨卡托坐标；//海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(0);//可选，设置发起定位请求的间隔，int类型，单位ms ;如果设置为0，则代表单次定位，即仅定位一次，默认为0; 如果设置非0，需设置1000ms以上才有效


        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
    //    option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
    //    option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
    //    option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setNeedDeviceDirect(true); //返回的定位结果包含手机机头方向
        option.setPriority(LocationClientOption.GpsFirst); //设置gps优先
        mLocationClient.setLocOption(option);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        // 关闭方向传感器
//        myOrientationListener.stop();
//        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        //调用LocationClient的start()方法，便可发起定位请求
        mLocationClient.start();
        // 开启方向传感器
//        myOrientationListener.start();
//        super.onStart();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {        //核心方法，避免因Fragment跳转导致地图崩溃
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            // if this view is visible to user, start to request user location
     //       mLocationClient.start();
        } else if (isVisibleToUser == false) {
            // if this view is not visible to user, stop to request user
            // location
       //     mLocationClient.stop();
        }
    }





    //自定义人物位置图标
    public void addInfosOverlay(List<GroupMemberLocationBean> groupMemberLocationBeanList)	{
        mBaiduMap.clear();
        latLng = null;
        OverlayOptions overlayOptions = null;
        Marker marker = null;
        for (GroupMemberLocationBean groupMemberLocationBean:groupMemberLocationBeanList){
            // 位置
            latLng = new LatLng(Double.parseDouble(groupMemberLocationBean.getLatitude()), Double.parseDouble(groupMemberLocationBean.getLongitude()));
            // 图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(Integer.parseInt(groupMemberLocationBean.getHeadPath()));//此处设置自己的图标即可
            overlayOptions = new MarkerOptions().position(latLng).icon(bitmap).zIndex(5);
            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));

            Bundle bundle = new Bundle();
            bundle.putSerializable("groupMemberLocationBean", groupMemberLocationBean);
            marker.setExtraInfo(bundle);
        }

        // 将地图移到到最后一个经纬度位置
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(u);
    }


    //显示人物图标监听
    public void addListenerMaker() {

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final GroupMemberLocationBean groupMemberLocationBean = (GroupMemberLocationBean) marker.getExtraInfo().get("groupMemberLocationBean");

                View view = getLayoutInflater().inflate(R.layout.marker, null);
                TextView marker_name = (TextView) view.findViewById(R.id.marker_name);
                TextView marker_phone = (TextView) view.findViewById(R.id.marker_phone);
                TextView marker_address = (TextView) view.findViewById(R.id.marker_address);

                SpannableString titleName = new SpannableString(groupMemberLocationBean.getNickName());
                titleName.setSpan(new ForegroundColorSpan(Color.RED), 0, titleName.length(), 0);
                marker_name.setText(titleName);

                SpannableString textPhone = new SpannableString(groupMemberLocationBean.getPhone());
                textPhone.setSpan(new ForegroundColorSpan(Color.BLUE), 0, textPhone.length(), 0);
                marker_phone.setText(textPhone);

                SpannableString textAddress = new SpannableString(groupMemberLocationBean.userID);
                textAddress.setSpan(new ForegroundColorSpan(Color.BLUE), 0, textAddress.length(), 0);
                marker_address.setText(textAddress);

                latLng = new LatLng(Double.parseDouble(groupMemberLocationBean.getLatitude()), Double.parseDouble(groupMemberLocationBean.getLongitude()));
                InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(view), latLng, -47, new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        mBaiduMap.hideInfoWindow();
                    }
                });

                mBaiduMap.showInfoWindow(infoWindow);



    /*                    Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + info.getPhone());
                        intent.setData(data);
                        startActivity(intent);*/


                return true;
            }
        });

    }
}
