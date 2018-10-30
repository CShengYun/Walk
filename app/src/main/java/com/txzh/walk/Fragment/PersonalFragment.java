package com.txzh.walk.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.txzh.walk.R;

public class PersonalFragment extends Fragment {
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("我滑动了第四个界面444");
        View view = inflater.inflate(R.layout.fragment_personal, container, false);


        return view;
    }
}
