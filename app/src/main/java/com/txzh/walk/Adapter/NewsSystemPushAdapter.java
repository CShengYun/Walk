package com.txzh.walk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.txzh.walk.Bean.NewsSystemPushInfoBean;
import com.txzh.walk.NewsItem.NewsSystemPushItem;
import com.txzh.walk.R;

import java.util.List;

public class NewsSystemPushAdapter extends BaseAdapter {
    private List<NewsSystemPushInfoBean> newsSystemPushInfoBeanList;
    private Context context;
    private ViewHolder viewHolder;

    public NewsSystemPushAdapter(List<NewsSystemPushInfoBean> newsSystemPushInfoBeanList, Context context) {
        this.newsSystemPushInfoBeanList = newsSystemPushInfoBeanList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return newsSystemPushInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsSystemPushInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NewsSystemPushInfoBean newsSystemPushInfoBean = newsSystemPushInfoBeanList.get(i);
        if (view == null) {
            view = View.inflate(context, R.layout.adapter_news_systempush_info, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_time = view.findViewById(R.id.tv_time);
            viewHolder.tv_theme = view.findViewById(R.id.tv_theme);
            viewHolder.tv_click = view.findViewById(R.id.tv_click);
            viewHolder.tv_content = view.findViewById(R.id.tv_content);
            viewHolder.ll_allContent = view.findViewById(R.id.ll_allContent);
            viewHolder.ll_allContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, ""+newsSystemPushInfoBean.getContent(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, NewsSystemPushItem.class);
                    intent.putExtra("theme", "" + newsSystemPushInfoBean.getTheme());
                    intent.putExtra("data", "" + newsSystemPushInfoBean.getDate());
                    intent.putExtra("content", newsSystemPushInfoBean.getContent());
                    context.startActivity(intent);
                }
            });

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_time.setText(newsSystemPushInfoBean.getDate());
        viewHolder.tv_theme.setText(newsSystemPushInfoBean.getTheme());
        viewHolder.tv_content.setText(newsSystemPushInfoBean.getContent());
        viewHolder.tv_click.setText("点击查看全文");

        return view;
    }

    class ViewHolder {
        TextView tv_time;
        TextView tv_theme;
        LinearLayout ll_allContent;
        TextView tv_content;
        TextView tv_click;
    }
}
