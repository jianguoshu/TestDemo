package com.example.douzi.customdemo;

import android.os.Bundle;
import android.view.View;

import com.example.douzi.customdemo.GridLayoutTest.GridLayoutActivity;
import com.example.douzi.customdemo.recyclerview.RecyclerViewActivity;
import com.example.douzi.customdemo.servicetest.ServiceLifecycleActivity;
import com.example.douzi.customdemo.splittouch.SplitTouchActivity;
import com.example.douzi.customdemo.toast.ToastActivity;
import com.example.douzi.customdemo.xy.XYActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.tv_btn_xy).setOnClickListener(this);
        this.findViewById(R.id.tv_btn_recycler).setOnClickListener(this);
        this.findViewById(R.id.tv_btn_toast).setOnClickListener(this);
        this.findViewById(R.id.tv_btn_split_touch).setOnClickListener(this);
        this.findViewById(R.id.tv_btn_service_lifecycle).setOnClickListener(this);
        this.findViewById(R.id.tv_btn_gridlayout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_xy:
                XYActivity.startAct(this);
                break;
            case R.id.tv_btn_recycler:
                RecyclerViewActivity.startAct(this);
                break;
            case R.id.tv_btn_toast:
                ToastActivity.startAct(this);
                break;
            case R.id.tv_btn_split_touch:
                SplitTouchActivity.startAct(this);
                break;
            case R.id.tv_btn_service_lifecycle:
                ServiceLifecycleActivity.startAct(this);
                break;
            case R.id.tv_btn_gridlayout:
                GridLayoutActivity.startAct(this);
                break;
        }
    }
}
