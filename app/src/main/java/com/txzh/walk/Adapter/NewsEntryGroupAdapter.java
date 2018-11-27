package com.txzh.walk.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.txzh.walk.Bean.NewsEntryGroupInfoBean;
import com.txzh.walk.R;

import java.util.List;

public class NewsEntryGroupAdapter extends BaseAdapter implements View.OnClickListener {
    private List<NewsEntryGroupInfoBean> newsEntryGroupInfoBeanList;
    private Context context;
    private ViewHolder viewHolder;
    private android.os.Handler handler;
    private InnerItemOnclickListener mListener;

    public NewsEntryGroupAdapter(List<NewsEntryGroupInfoBean> newsEntryGroupInfoBeanList,Context context){
        this.newsEntryGroupInfoBeanList = newsEntryGroupInfoBeanList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return newsEntryGroupInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsEntryGroupInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NewsEntryGroupInfoBean newsEntryGroupInfoBean = newsEntryGroupInfoBeanList.get(i);
        if(view == null){
            view = View.inflate(context, R.layout.adapter_news_entrygroup_info,null);
            viewHolder = new ViewHolder();
           handler = new Handler();

            viewHolder.tv_applicantName = view.findViewById(R.id.tv_applicantName);
            viewHolder.tv_entryTime = view.findViewById(R.id.tv_entryTime);
            viewHolder.tv_groupOwnerAgree = view.findViewById(R.id.tv_groupOwnerAgree);
            viewHolder.tv_groupToApplyFor = view.findViewById(R.id.tv_groupToApplyFor);
            viewHolder.tv_groupOwnerRefuse = view.findViewById(R.id.tv_groupOwnerRefuse);
            viewHolder.tv_displayResult = view.findViewById(R.id.tv_displayResult);

            viewHolder.ll_groupMasterResponse = view.findViewById(R.id.ll_groupMasterResponse);
            viewHolder.ll_applicationResult = view.findViewById(R.id.ll_applicationResult);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        Log.i("qqqqqqqqq",""+newsEntryGroupInfoBean.getGroupName()+":"+newsEntryGroupInfoBean.getStatus());
        viewHolder.tv_entryTime.setText(newsEntryGroupInfoBean.getDate());
        viewHolder.tv_applicantName.setText(newsEntryGroupInfoBean.getNickName());
        viewHolder.tv_groupToApplyFor.setText("请求加入"+newsEntryGroupInfoBean.getGroupName()+"群组");
        if(newsEntryGroupInfoBean.getStatus().equals("0") || newsEntryGroupInfoBean.getStatus().equals("10")){
            viewHolder.ll_groupMasterResponse.setVisibility(View.VISIBLE);
            viewHolder.ll_applicationResult.setVisibility(View.GONE);
        }else if(newsEntryGroupInfoBean.getStatus().equals("1") || newsEntryGroupInfoBean.getStatus().equals("6")){
            viewHolder.ll_groupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_applicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_displayResult.setText("已同意");
        }else if(newsEntryGroupInfoBean.getStatus().equals("2") || newsEntryGroupInfoBean.getStatus().equals("7")){
            viewHolder.ll_groupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_applicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_displayResult.setText("已拒绝");
        }

        viewHolder.tv_groupOwnerAgree.setOnClickListener(this);
        viewHolder.tv_groupOwnerRefuse.setOnClickListener(this);
        viewHolder.tv_groupOwnerAgree.setTag(i);
        viewHolder.tv_groupOwnerRefuse.setTag(i);
        viewHolder.tv_displayResult.setTag(newsEntryGroupInfoBeanList.get(i));

        return view;
    }


    class ViewHolder{
        TextView tv_entryTime;
        TextView tv_applicantName;
        TextView tv_groupToApplyFor;
        TextView tv_groupOwnerAgree;
        TextView tv_groupOwnerRefuse;
        TextView tv_displayResult;

        LinearLayout ll_groupMasterResponse;
        LinearLayout ll_applicationResult;
    }

    public interface InnerItemOnclickListener extends Runnable {
        void itemClick(View v);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){
        this.mListener= listener;
    }

    @Override
    public void onClick(View v) {
        mListener.itemClick(v);
    }

}
