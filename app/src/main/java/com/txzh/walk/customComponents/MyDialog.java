package com.txzh.walk.customComponents;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.txzh.walk.R;


public class MyDialog extends Dialog {

    private Button bt_yes;
    private Button bt_no;
    private ImageButton ibtn_close;
    private TextView tv_title;
    private String title;
    private onNoOnclickListener noOnclickListener;
    private onYesOnclickListener yesOnclickListener;
    private OnClickListener onClickListener;
    private String left,right;

    public MyDialog(Context context,String left,String right) {

        super(context);
        this.left = left;
        this.right = right;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dialog);

        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //点击鼠标返回键不取消动画
        setCancelable(false);
        initView();
        initData(left,right);
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
        //设置关闭按钮
        ibtn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData(String left,String right) {
        //设置按钮的文字
        bt_yes.setText(right);
        bt_no.setText(left);
        tv_title.setText(title);
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        bt_yes = (Button) findViewById(R.id.yes);
        bt_no = (Button) findViewById(R.id.no);
        ibtn_close = findViewById(R.id.ibtn_close);
        tv_title = (TextView) findViewById(R.id.title);
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        void onYesClick();
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }

    public interface  OnClickListener{
        void onClick();
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param onNoOnclickListener
     */
    public void setNoButton(onNoOnclickListener onNoOnclickListener) {
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param onYesOnclickListener
     */
    public void setYesButton(onYesOnclickListener onYesOnclickListener) {
        this.yesOnclickListener = onYesOnclickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

}
