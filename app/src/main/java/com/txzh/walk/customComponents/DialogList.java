package com.txzh.walk.customComponents;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

import com.txzh.walk.R;

public class DialogList extends Dialog {
    private LinearLayout ll_groupLocated;
    private LinearLayout ll_groupResume;

    private Context context;

    private GroupLocatedClick groupLocatedClick;
    private GroupResumeClick groupResumeClick;

    public DialogList(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialoglist);


        ll_groupLocated = (LinearLayout)findViewById(R.id.ll_groupLocated);
        ll_groupResume = (LinearLayout)findViewById(R.id.ll_groupResume);

        ll_groupLocated.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(groupLocatedClick != null){
                    groupLocatedClick.onGroupLocated();
                }
            }
        });

        ll_groupResume.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //Toast.makeText( context, "我是简介", Toast.LENGTH_SHORT).show();
                if(groupResumeClick != null){
                    groupResumeClick.onGroupResume();
                }
            }
        });
    }

    public interface GroupLocatedClick{
        void onGroupLocated();
    }

    public interface GroupResumeClick{
        void onGroupResume();
    }

    public void setOnGroupLocated(GroupLocatedClick groupLocatedClick){
        this.groupLocatedClick = groupLocatedClick;
    }

    public void setOnGroupResume(GroupResumeClick groupResumeClick){
        this.groupResumeClick = groupResumeClick;
    }
}
