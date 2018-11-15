package com.txzh.walk.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.txzh.walk.Bean.GroupMemberLocationBean;
import com.txzh.walk.Listener.LocationListener.MyLocationListener;
import com.txzh.walk.R;
import com.txzh.walk.overlayutil.DrivingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import static com.txzh.walk.Group.GroupMembers.isLocation;
import static com.txzh.walk.HomePage.WalkHome.context;
import static com.txzh.walk.Listener.LocationListener.MyLocationListener.la;
import static com.txzh.walk.Listener.LocationListener.MyLocationListener.lo;


public class MapFragment extends Fragment implements OnGetGeoCoderResultListener {
    @SuppressWarnings("unused")
    public static MapView mMapView;
    View view;
    public static LatLng latLng;      //获取经纬度

    public static BaiduMap mBaiduMap;//定义地图实例
    public static boolean ifFrist = true;//判断是否是第一次进去
    public static List<GroupMemberLocationBean> groupMemberLocationBeanList = new ArrayList<GroupMemberLocationBean>();     //群组成员位置信息
    public InfoWindow currentInfoWindow;            //marker点击弹出信息窗口

    public RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    public GeoCoder mCoder = null;                 //把经纬度坐标转化为城市地址

    public OverlayOptions overlayOptions = null;        //地图上的覆盖物


    public static LocationClient mLocationClient = null;//定义LocationClient
    public MyLocationListener myListener = new MyLocationListener();//继承BDAbstractLocationListener的class


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(isLocation){
            mBaiduMap.clear();
            addInfosOverlay(groupMemberLocationBeanList);
            addListenerMaker();
        }
    }


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

        mCoder = GeoCoder.newInstance();            //初始化GeoCoder
        mCoder.setOnGetGeoCodeResultListener(this);

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

    }


    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        //调用LocationClient的start()方法，便可发起定位请求
        mLocationClient.start();

    //    addListenerMaker();
        // 开启方向传感器

    }


    //自定义人物位置图标
    public void addInfosOverlay(List<GroupMemberLocationBean> groupMemberLocationBeanList)	{
        mBaiduMap.clear();
        latLng = null;
        overlayOptions = null;
        Marker marker = null;
        for (GroupMemberLocationBean groupMemberLocationBean:groupMemberLocationBeanList){
            // 位置
            latLng = new LatLng(Double.parseDouble(groupMemberLocationBean.getLatitude()), Double.parseDouble(groupMemberLocationBean.getLongitude()));
            // 图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.p2);//此处设置自己的图标即可
            overlayOptions = new MarkerOptions().position(latLng).icon(bitmap).zIndex(5);
            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
            marker.setVisible(true);
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
                latLng = new LatLng(Double.parseDouble(groupMemberLocationBean.getLatitude()), Double.parseDouble(groupMemberLocationBean.getLongitude()));

                currentInfoWindow = new InfoWindow(getInfoWindoView(marker,groupMemberLocationBean),latLng, -77);
                mBaiduMap.showInfoWindow(currentInfoWindow);
                return true;
            }
        });
    }


    private View getInfoWindoView(Marker marker, final GroupMemberLocationBean groupMemberLocationBean){
        View view = View.inflate(context,R.layout.marker, null);
        TextView marker_name = (TextView) view.findViewById(R.id.marker_name);
        final TextView marker_phone = (TextView) view.findViewById(R.id.marker_phone);
        TextView marker_address = (TextView) view.findViewById(R.id.marker_address);
        final Button marker_call = (Button) view.findViewById(R.id.marker_call);
        final Button marker_destination = (Button)view.findViewById(R.id.marker_destination);

        SpannableString titleName = new SpannableString(groupMemberLocationBean.getNickName());
        titleName.setSpan(new ForegroundColorSpan(Color.RED), 0, titleName.length(), 0);
        marker_name.setText(titleName);

        SpannableString textPhone = new SpannableString(groupMemberLocationBean.getPhone());
        textPhone.setSpan(new ForegroundColorSpan(Color.BLUE), 0, textPhone.length(), 0);
        marker_phone.setText(textPhone);

        SpannableString textAddress = new SpannableString(groupMemberLocationBean.headPath);
        textAddress.setSpan(new ForegroundColorSpan(Color.BLUE), 0, textAddress.length(), 0);
        marker_address.setText(textAddress);

        marker_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("###","我点击了呼叫。");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + marker_phone.getText().toString());
                intent.setData(data);
                startActivity(intent);
            }
        });

        marker_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRoute(groupMemberLocationBean);


            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
      });

        return view;
    }

    public void getRoute(GroupMemberLocationBean groupMemberLocationBean){
        LatLng latLngFrom = new LatLng(la,lo);
        LatLng latLngTo = new LatLng(Double.parseDouble(groupMemberLocationBean.getLatitude()), Double.parseDouble(groupMemberLocationBean.getLongitude()));
        mCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLngTo));

        initPlan();        //规划路线
        PlanNode startNode = PlanNode.withLocation(latLngFrom);
        PlanNode endNode = PlanNode.withLocation(latLngTo);

        mSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));            //驾车路线规划

    }

    private void initPlan() {
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    drivingRouteResult.getSuggestAddrInfo();
                    return;
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    if (drivingRouteResult.getRouteLines().size() >= 1) {
//                        DrivingRouteLine route = drivingRouteResult.getRouteLines().get(0);
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
    //                    overlayOptions = overlay;

                        mBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(drivingRouteResult.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    } else {
                        Log.d("route result", "结果数<0");
                        return;
                    }
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
    }
    ///以下两个方法是把经纬度转化为具体城市的坐标

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;		}
            String  s =  reverseGeoCodeResult.getAddress();

        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();



    }
}
