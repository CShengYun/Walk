package com.txzh.walk.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.txzh.walk.R;

public class GroupFragment extends Fragment implements View.OnClickListener {
    protected View view=null;
    private TextView group_tv_search,group_tv_establish_group,group_tv_manage_group,group_tv_add_group;                 //搜索、创建、我管理的群、我加入的群
    private ListView group_lv_manage_group,group_lv_add_group;                                                              //我管理的群ListView；我创建的群ListView


    protected Drawable arrow_right_group_pic = null;                        //关闭群组向右的箭头
    protected Drawable arrow_down_group_pic = null;                         //展开群组向下的箭头
    public static boolean is_onclik_manage_group = true;                    //判断展开、关闭我管理的的群组
    public static boolean is_onclik_add_group = true;                       //判断展开、关闭我加入的的群组

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("我滑动了第二个界面22");
        view = inflater.inflate(R.layout.fragment_group, container, false);

        init();             //实例化和监听控件



        return view;
    }

    //实例化和监听控件
    public void init(){

        //实例化和监听群组搜索、创建
        group_tv_search = (TextView)view.findViewById(R.id.group_tv_establish_group);
        group_tv_establish_group = (TextView)view.findViewById(R.id.group_tv_establish_group);
        group_tv_search.setOnClickListener(this);
        group_tv_establish_group.setOnClickListener(this);

        //实例化和监听我管理的群组、我加入的群组
        group_tv_manage_group = (TextView)view.findViewById(R.id.group_tv_manage_group);
        group_tv_add_group = (TextView)view.findViewById(R.id.group_tv_add_group);
        group_tv_manage_group.setOnClickListener(this);
        group_tv_add_group.setOnClickListener(this);

        //实例化我管理的群组ListView、我加入的群组ListView
        group_lv_manage_group = (ListView)view.findViewById(R.id.group_lv_manage_group);
        group_lv_add_group = (ListView)view.findViewById(R.id.group_lv_add_group);


        //展开、关闭群组的带箭头图片
        arrow_right_group_pic = getResources().getDrawable(R.drawable.group_arrow_down_32dp);
        arrow_right_group_pic.setBounds(0,0,arrow_right_group_pic.getMinimumWidth(),arrow_right_group_pic.getMinimumHeight());
        arrow_down_group_pic = getResources().getDrawable(R.drawable.group_arrow_right_32dp);
        arrow_down_group_pic.setBounds(0,0,arrow_down_group_pic.getMinimumWidth(),arrow_down_group_pic.getMinimumHeight());

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.group_tv_search:                          //群组搜索

                break;


            case R.id.group_tv_establish_group:                 //创建群组

                break;


            case R.id.group_tv_manage_group:                    //我管理的群组
                openCloseManageGroup();                             //打开关闭我管理的群组

                break;


            case R.id.group_tv_add_group:                       //我加入的群组
                openCloseAddGroup();                               //打开关闭我加入的群组

                break;

        }
    }


    public void openCloseGroupPic(){

    }


    //打开关闭我管理的群组
    public void openCloseManageGroup(){
        if(is_onclik_manage_group==true){
            group_tv_manage_group.setCompoundDrawables(arrow_right_group_pic,null,null,null);       //改变箭头方向
            group_lv_manage_group.setVisibility(View.VISIBLE);                                                            //设置ListView可见
            is_onclik_manage_group=!is_onclik_manage_group;
        }else if(is_onclik_manage_group==false){
            group_tv_manage_group.setCompoundDrawables(arrow_down_group_pic,null,null,null);       //改变箭头方向
            group_lv_manage_group.setVisibility(View.GONE);                                                              //设置ListView不可见
            is_onclik_manage_group=!is_onclik_manage_group;
        }
    }


    //打开关闭我加入的群组
    public void openCloseAddGroup(){
        if(is_onclik_add_group==true){
            group_tv_add_group.setCompoundDrawables(arrow_right_group_pic,null,null,null);          //改变箭头方向
            group_lv_add_group.setVisibility(View.VISIBLE);                                                               //设置ListView可见
            is_onclik_add_group=!is_onclik_add_group;
        }else if(is_onclik_add_group==false){
            group_tv_add_group.setCompoundDrawables(arrow_down_group_pic,null,null,null);           //改变箭头方向
            group_lv_add_group.setVisibility(View.GONE);                                                                  //设置ListView不可见
            is_onclik_add_group=!is_onclik_add_group;
        }
    }



}
