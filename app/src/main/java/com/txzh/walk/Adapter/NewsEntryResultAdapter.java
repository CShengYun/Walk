package com.txzh.walk.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.txzh.walk.Bean.NewsEntryResultInfoBean;
import com.txzh.walk.R;

import java.util.List;

public class NewsEntryResultAdapter extends BaseAdapter {
    private List<NewsEntryResultInfoBean> newsEntryResultInfoBeanList;
    private Context context;

    private ViewHolder viewHolder;

    public NewsEntryResultAdapter(List<NewsEntryResultInfoBean> newsEntryResultInfoBeanList, Context context){
        this.newsEntryResultInfoBeanList = newsEntryResultInfoBeanList;
        this.context = context;

    }
    @Override
    public int getCount() {
        return newsEntryResultInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsEntryResultInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NewsEntryResultInfoBean newsEntryResultInfoBean = newsEntryResultInfoBeanList.get(i);
        if(view == null){
            view = View.inflate(context, R.layout.adapter_news_entryresult_info,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_entryResultTime = view.findViewById(R.id.tv_entryResultTime);
            viewHolder.tv_entryResultGroupToApplyFor = view.findViewById(R.id.tv_entryResultGroupToApplyFor);
            viewHolder.tv_entryResultApplicantName = view.findViewById(R.id.tv_entryResultApplicantName);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_entryResultTime.setText(newsEntryResultInfoBean.getDate());
        viewHolder.tv_entryResultApplicantName.setText(newsEntryResultInfoBean.getGroupName()+"群群主");
        if(newsEntryResultInfoBean.getStatus().equals("1") || newsEntryResultInfoBean.getStatus().equals("4")|| newsEntryResultInfoBean.getStatus().equals("6")|| newsEntryResultInfoBean.getStatus().equals("8")){
            viewHolder.tv_entryResultGroupToApplyFor.setText("已同意你的入群申请");
        }else {
            viewHolder.tv_entryResultGroupToApplyFor.setText("已拒绝你的入群申请");
        }

        return view;
    }

    class ViewHolder{
        TextView tv_entryResultTime;
        TextView tv_entryResultApplicantName;
        TextView tv_entryResultGroupToApplyFor;
    }
}
