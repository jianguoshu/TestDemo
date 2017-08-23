package com.example.douzi.customdemo.servicetest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.douzi.customdemo.Consts;
import com.example.douzi.customdemo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ServiceLifecycleActivity extends Activity {


    private TextView btnServiceStart;
    private TextView btnServiceStop;
    private TextView btnServiceBind;
    private TextView btnServiceUnBind;
    private Intent serviceIntent;
    private ServiceConnection serviceConnection;
    private TextView btnLogClear;
    private TextView log;
    private TextView btnLogGet;
    private CustomService.CustomBinder serviceBinder;
    private List<LogInfo> logList = new ArrayList<>();
    private boolean isServiceConnected;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, ServiceLifecycleActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);

        serviceIntent = new Intent(this, CustomService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceBinder = (CustomService.CustomBinder) service;
                isServiceConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isServiceConnected = false;
            }
        };

        btnServiceStart = (TextView) this.findViewById(R.id.tv_btn_service_start);
        btnServiceStop = (TextView) this.findViewById(R.id.tv_btn_service_stop);
        btnServiceBind = (TextView) this.findViewById(R.id.tv_btn_service_bind);
        btnServiceUnBind = (TextView) this.findViewById(R.id.tv_btn_service_unbind);
        btnLogClear = (TextView) this.findViewById(R.id.tv_btn_log_clear);
        btnLogGet = (TextView) this.findViewById(R.id.tv_btn_log_get);
        log = (TextView) this.findViewById(R.id.tv_log);

        btnServiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartServiceBtnClicked();
            }
        });
        btnServiceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopServiceBtnClicked();
            }
        });
        btnServiceBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBindServiceBtnClicked();
            }
        });
        btnServiceUnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnBindServiceBtnClicked();
            }
        });

        btnLogClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogClearBtnClicked();
            }
        });
        btnLogGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogGetBtnClicked();
            }
        });
    }

    private void onLogGetBtnClicked() {
        String logStr = "";
        List<LogInfo> logListAll = new ArrayList<>();
        logListAll.addAll(logList);
        if (serviceBinder != null) {
            logListAll.addAll(serviceBinder.getLog());
        }

        Collections.sort(logListAll, new Comparator<LogInfo>() {
            @Override
            public int compare(LogInfo o1, LogInfo o2) {
                return (int) (o1.time - o2.time);
            }
        });

        for (LogInfo info: logListAll) {
            logStr += info.content + "------------------------------" + new SimpleDateFormat("mm:ss:SSS").format(new Date(info.time)) + "\n";
        }

        log.setText(logStr);

    }

    private void onLogClearBtnClicked() {
        log.setText("");
        if (serviceBinder != null) {
            serviceBinder.clearLog();
        }
        logList.clear();
    }

    private void onStartServiceBtnClicked() {
        logList.add(new LogInfo("action : startService", System.currentTimeMillis()));

        Log.i(Consts.TAG_SERVICE_TEST, "action : startService");
        startService(serviceIntent);
    }

    private void onStopServiceBtnClicked() {
        logList.add(new LogInfo("action : stopService", System.currentTimeMillis()));
        Log.i(Consts.TAG_SERVICE_TEST, "action : stopService");
        stopService(serviceIntent);
    }

    private void onBindServiceBtnClicked() {
        Log.i(Consts.TAG_SERVICE_TEST, "action : bindService");
        logList.add(new LogInfo("action : bindService", System.currentTimeMillis()));
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void onUnBindServiceBtnClicked() {
        Log.i(Consts.TAG_SERVICE_TEST, "action : unbindService");
        logList.add(new LogInfo("action : unbindService", System.currentTimeMillis()));
        if (isServiceConnected) {
            unbindService(serviceConnection);
        }
    }
}
