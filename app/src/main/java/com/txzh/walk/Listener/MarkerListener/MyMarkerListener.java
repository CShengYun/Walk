package com.txzh.walk.Listener.MarkerListener;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.txzh.walk.Bean.GroupMemberLocationBean;
import com.txzh.walk.R;

import static com.txzh.walk.Fragment.MapFragment.latLng;
import static com.txzh.walk.Fragment.MapFragment.mBaiduMap;
import static com.txzh.walk.HomePage.WalkHome.context;

public class MyMarkerListener implements BaiduMap.OnMarkerClickListener {
    @Override
    public boolean onMarkerClick(Marker marker) {

        final GroupMemberLocationBean groupMemberLocationBean = (GroupMemberLocationBean) marker.getExtraInfo().get("groupMemberLocationBean");

        View view = View.inflate(context,R.layout.marker, null);
        TextView marker_name = (TextView) view.findViewById(R.id.marker_name);
        TextView marker_phone = (TextView) view.findViewById(R.id.marker_phone);
        TextView marker_address = (TextView) view.findViewById(R.id.marker_address);

        SpannableString titleName = new SpannableString(groupMemberLocationBean.getNickName());
        titleName.setSpan(new ForegroundColorSpan(Color.RED), 0, titleName.length(), 0);
        marker_name.setText(titleName);

        SpannableString textPhone = new SpannableString(groupMemberLocationBean.getPhone());
        textPhone.setSpan(new ForegroundColorSpan(Color.BLUE), 0, textPhone.length(), 0);
        marker_phone.setText(textPhone);

        SpannableString textAddress = new SpannableString(groupMemberLocationBean.headPath);
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

        return true;
    }
}
