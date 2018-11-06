package com.txzh.walk.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.txzh.walk.Bean.GroupInfoBean;
import com.txzh.walk.R;

import java.util.List;

public class GroupInfoAdapter extends BaseAdapter {
    private List<GroupInfoBean> groupInfoBeanList;
    private Context context;
    ViewHolder viewHolder;


    public GroupInfoAdapter(List<GroupInfoBean> groupInfoBeanList, Context context){
        this.groupInfoBeanList=groupInfoBeanList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return groupInfoBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupInfoBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GroupInfoBean groupInfoBean = groupInfoBeanList.get(position);

        if(convertView==null){
            convertView = View.inflate(context, R.layout.adapter_group_info,null);

            viewHolder = new ViewHolder();
            viewHolder.group_name = (TextView)convertView.findViewById(R.id.group_name_item);
            viewHolder.group_headPath = (ImageView)convertView.findViewById(R.id.group_pic_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.group_name.setText(groupInfoBean.getGroupName());

        Log.i("****","群ID:"+groupInfoBean.getGroupId()+"---"+"群主id："+groupInfoBean.groupHostID+"---"+"群昵称："+groupInfoBean.getGroupName());

        return convertView;
    }

    class ViewHolder{
        TextView group_name;
        ImageView group_headPath;
    }
}
