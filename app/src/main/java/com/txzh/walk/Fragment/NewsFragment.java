package com.txzh.walk.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jauker.widget.BadgeView;
import com.txzh.walk.NewsItem.newsEntryGroup;
import com.txzh.walk.NewsItem.newsEntryResult;
import com.txzh.walk.NewsItem.newsEntryedFroup;
import com.txzh.walk.NewsItem.newsPush;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.NewsCounts;

public class NewsFragment extends Fragment implements View.OnClickListener {
    private TextView tv_charRecord;
    private TextView tv_systemPush;         //系统推送
    private TextView tv_entryGroup;         //申请入群通知
    private TextView tv_entryedFroup;       //群主邀请入群通知
    private TextView tv_entryResult;        //入群结果通知

    private LinearLayout ll_charRecord;
    private LinearLayout ll_systemPush;     //系统推送布局
    private LinearLayout ll_entryGroup;     //申请入群通知布局
    private LinearLayout ll_entryedFroup;   //群主邀请入群通知布局
    private LinearLayout ll_entryResult;    //入群结果通知布局

    private Thread thread;
    boolean isRunning = true;

    private BadgeView systemPush;
    private BadgeView entryGroup;
    private BadgeView entryedFroup;
    private BadgeView entryResult;

    Handler handler;



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("我滑动了第三个界面333");
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        handler = new Handler();

        tv_charRecord = view.findViewById(R.id.tv_charRecord);
        tv_systemPush = view.findViewById(R.id.tv_systemPush);
        tv_entryGroup = view.findViewById(R.id.tv_entryGroup);
        tv_entryedFroup = view.findViewById(R.id.tv_entryedFroup);
        tv_entryResult = view.findViewById(R.id.tv_entryResult);

        ll_charRecord = view.findViewById(R.id.ll_charRecord);
        ll_systemPush = view.findViewById(R.id.ll_systemPush);
        ll_entryGroup = view.findViewById(R.id.ll_entryGroup);
        ll_entryedFroup = view.findViewById(R.id.ll_entryedFroup);
        ll_entryResult = view.findViewById(R.id.ll_entryResult);

        ll_charRecord.setOnClickListener(this);
        ll_systemPush.setOnClickListener(this);
        ll_entryGroup.setOnClickListener(this);
        ll_entryedFroup.setOnClickListener(this);
        ll_entryResult.setOnClickListener(this);

        systemPush = new com.jauker.widget.BadgeView(getActivity());
        systemPush.setTargetView(tv_systemPush);    //设置组件显示数据
        systemPush.setBadgeGravity(Gravity.CENTER);  //位置

        entryGroup = new com.jauker.widget.BadgeView(getActivity());
        entryGroup.setTargetView(tv_entryGroup);    //设置组件显示数据
        entryGroup.setBadgeGravity(Gravity.CENTER);  //位置

        entryedFroup = new com.jauker.widget.BadgeView(getActivity());
        entryedFroup.setTargetView(tv_entryedFroup);    //设置组件显示数据
        entryedFroup.setBadgeGravity(Gravity.CENTER);  //位置

        entryResult = new com.jauker.widget.BadgeView(getActivity());
        entryResult.setTargetView(tv_entryResult);    //设置组件显示数据
        entryResult.setBadgeGravity(Gravity.CENTER);  //位置


        return view;
    }

    public void onStart() {

        super.onStart();

        isRunning = true;
        thread = new Thread(new systemPushThread());
        thread.start();
        Log.i("mmmmmmmmm","线程被运行");
    }

    public void onPause() {

        super.onPause();

        isRunning = false;
        Log.i("mmmmmmmmm","线程被停止");
    }

    private void init(){
        systemPush.setBadgeCount(Integer.parseInt(NewsCounts.getSystemPush().trim()));                //显示的数量

        entryGroup.setBadgeCount(Integer.parseInt(NewsCounts.getEntryGroup().trim()));                //显示的数量


        entryedFroup.setBadgeCount(Integer.parseInt(NewsCounts.getEntryedFroup().trim()));                //显示的数量


        entryResult.setBadgeCount(Integer.parseInt(NewsCounts.getEntryResult().trim()));                //显示的数量


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_charRecord:

                break;
            case R.id.ll_systemPush:
                Intent intent = new Intent(getActivity(), newsPush.class);
                startActivity(intent);
                break;
            case R.id.ll_entryGroup:
                Intent entryGroup = new Intent(getActivity(), newsEntryGroup.class);
                startActivity(entryGroup);
                break;
            case R.id.ll_entryedFroup:
                Intent entryedFroup = new Intent(getActivity(),newsEntryedFroup.class);
                startActivity(entryedFroup);
                break;
            case R.id.ll_entryResult:
                Intent entryResult = new Intent(getActivity(),newsEntryResult.class);
                startActivity(entryResult);
                break;
        }
    }

    class systemPushThread implements Runnable {

        @Override
        public void run() {
            while (isRunning){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                });

                try {
                    Thread.sleep(2*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
