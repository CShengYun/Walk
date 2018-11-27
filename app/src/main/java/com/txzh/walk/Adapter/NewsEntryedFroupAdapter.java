package com.txzh.walk.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.txzh.walk.Bean.NewsEntryedFroupInfoBean;
import com.txzh.walk.R;

import java.util.List;

public class NewsEntryedFroupAdapter extends BaseAdapter {
    private List<NewsEntryedFroupInfoBean> newsEntryedFroupInfoBeanList;
    private Context context;
    private ViewHolder viewHolder;

    public NewsEntryedFroupAdapter(List<NewsEntryedFroupInfoBean> newsEntryedFroupInfoBeanList,Context context){
        this.newsEntryedFroupInfoBeanList = newsEntryedFroupInfoBeanList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return newsEntryedFroupInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsEntryedFroupInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NewsEntryedFroupInfoBean newsEntryedFroupInfoBean = newsEntryedFroupInfoBeanList.get(i);
        if(view == null){
            view = View.inflate(context, R.layout.adapter_news_entryedfroup_info,null);
            viewHolder = new ViewHolder();

            viewHolder.tv_entryedTime = view.findViewById(R.id.tv_entryedTime);
            viewHolder.tv_entryedApplicantName = view.findViewById(R.id.tv_entryedApplicantName);
            viewHolder.tv_entryedGroupToApplyFor = view.findViewById(R.id.tv_entryedGroupToApplyFor);
            viewHolder.tv_entryedGroupOwnerAgree = view.findViewById(R.id.tv_entryedGroupOwnerAgree);
            viewHolder.tv_entryedGroupOwnerRefuse = view.findViewById(R.id.tv_entryedGroupOwnerRefuse);
            viewHolder.tv_entryedDisplayResult = view.findViewById(R.id.tv_entryedDisplayResult);

            viewHolder.ll_entryedApplicationResult = view.findViewById(R.id.ll_entryedApplicationResult);
            viewHolder.ll_entryedGroupMasterResponse = view.findViewById(R.id.ll_entryedGroupMasterResponse);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_entryedTime.setText(newsEntryedFroupInfoBean.getDate());
        viewHolder.tv_entryedApplicantName.setText(newsEntryedFroupInfoBean.getGroupName()+"群群主");
        viewHolder.tv_entryedGroupToApplyFor.setText("邀请你加入"+newsEntryedFroupInfoBean.getGroupName()+"群组");

        if(newsEntryedFroupInfoBean.getStatus().equals("3")){
            viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.VISIBLE);
        }else if(newsEntryedFroupInfoBean.getStatus().equals("4") || newsEntryedFroupInfoBean.getStatus().equals("8")){
            viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_entryedApplicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_entryedDisplayResult.setText("已同意");
        }else if(newsEntryedFroupInfoBean.getStatus().equals("5") || newsEntryedFroupInfoBean.getStatus().equals("9")){
            viewHolder.ll_entryedGroupMasterResponse.setVisibility(View.GONE);
            viewHolder.ll_entryedApplicationResult.setVisibility(View.VISIBLE);
            viewHolder.tv_entryedDisplayResult.setText("已拒绝");
        }

        return view;
    }

    class ViewHolder{
        TextView tv_entryedTime;
        TextView tv_entryedApplicantName;
        TextView tv_entryedGroupToApplyFor;
        TextView tv_entryedGroupOwnerAgree;
        TextView tv_entryedGroupOwnerRefuse;
        TextView tv_entryedDisplayResult;

        LinearLayout ll_entryedApplicationResult;
        LinearLayout ll_entryedGroupMasterResponse;
    }
}
