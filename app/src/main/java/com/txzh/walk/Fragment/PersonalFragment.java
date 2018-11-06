package com.txzh.walk.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.txzh.walk.Function.personalData;
import com.txzh.walk.MainActivity;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.Tools;
import com.txzh.walk.customComponents.BottomAnimDialog;

public class PersonalFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tv_personalData;   //个人资料
    private TextView tv_quit;           //退出
    private TextView tv_accounts;       //帐号
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("我滑动了第四个界面444");
        view = inflater.inflate(R.layout.fragment_personal, container, false);

        init();

        return view;
    }

    private void init(){
        tv_personalData = view.findViewById(R.id.tv_personalData);
        tv_personalData.setOnClickListener(this);
        tv_quit = view.findViewById(R.id.tv_quit);
        tv_quit.setOnClickListener(this);
        tv_accounts = view.findViewById(R.id.tv_accounts);
        tv_accounts.setText(Tools.getAccounts());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_personalData:
                final Intent intent = new Intent(getActivity(),personalData.class);
                startActivity(intent);
                break;
            case R.id.tv_quit:
                final BottomAnimDialog dialog =new BottomAnimDialog(getActivity(),"退出登录","关闭");
                dialog.setClickListener(new BottomAnimDialog.BottonAnimDialogListener() {
                    @Override
                    public void onItem1Listener() {
                        //返回首页
                        Intent intent1 = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent1);
                    }

                    @Override
                    public void onItem2Listener() {
                        //退出程序
                        Intent intent1 = new Intent();
                        intent1.setClass(getActivity(),MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        getActivity().finish();
                    }
                });
                dialog.show();

                break;
        }
    }

}
