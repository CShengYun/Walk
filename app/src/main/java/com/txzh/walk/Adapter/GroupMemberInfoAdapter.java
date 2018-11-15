package com.txzh.walk.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.txzh.walk.Bean.GroupMemberInfoBean;
import com.txzh.walk.R;
import com.txzh.walk.customComponents.CircleImageView;

import java.util.List;

public class GroupMemberInfoAdapter extends BaseAdapter {

    public Context context;
    public List<GroupMemberInfoBean> groupMemberInfoBeanList;
    ViewHolder viewHolder;

    public GroupMemberInfoAdapter(List<GroupMemberInfoBean> groupMemberInfoBeanList,Context context){
        this.groupMemberInfoBeanList=groupMemberInfoBeanList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return groupMemberInfoBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupMemberInfoBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GroupMemberInfoBean groupMemberInfoBean = groupMemberInfoBeanList.get(position);

        if(convertView==null){
            convertView = View.inflate(context, R.layout.adapter_group_member_info,null);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.group_member_name_item);
            viewHolder.headPath = (CircleImageView) convertView.findViewById(R.id.group_member_pic_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(groupMemberInfoBean.getGroupMemberNiceName());
        return convertView;
    }



    class ViewHolder{
        TextView name;
        CircleImageView headPath;
    }
}
