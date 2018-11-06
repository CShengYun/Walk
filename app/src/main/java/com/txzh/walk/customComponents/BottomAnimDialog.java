package com.txzh.walk.customComponents;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.txzh.walk.R;
import com.txzh.walk.ToolClass.DensityUtil;

public class BottomAnimDialog extends Dialog {
    private Context context;
    private final String exit;   //退出帐号
    private final String quit;   //退出应用


    private BottonAnimDialogListener mListener;
    private LinearLayout ll_exit;
    private LinearLayout ll_quit;
    public BottomAnimDialog(Context context, String exit,String quit){
        super(context, R.style.dialog);
        this.context = context;
        this.exit = exit;
        this.quit = exit;

        initView();
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_bottom_anim_dialog, null);

        Window window = this.getWindow();
        if (window != null) { //设置dialog的布局样式 让其位于底部
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.y = DensityUtil.dip2px(context,10); //设置居于底部的距离
            window.setAttributes(lp);
        }

        ll_exit = view.findViewById(R.id.ll_exit);
        ll_quit = view.findViewById(R.id.ll_quit);

        ll_exit.setOnClickListener(new clickListener());
        ll_quit.setOnClickListener(new clickListener());

        setContentView(view);

    }

    public void setClickListener(BottonAnimDialogListener listener) {
        this.mListener = listener;
    }

    public interface BottonAnimDialogListener {
        void onItem1Listener();

        void onItem2Listener();

    }

    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_exit:
                    if (mListener != null) {
                        mListener.onItem1Listener();
                    }
                    break;
                case R.id.ll_quit:
                    if (mListener != null) {
                        mListener.onItem2Listener();
                    }
                    break;
            }
        }
    }

}
