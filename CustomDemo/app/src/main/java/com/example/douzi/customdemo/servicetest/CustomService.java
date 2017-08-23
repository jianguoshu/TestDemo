package com.example.douzi.customdemo.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.example.douzi.customdemo.Consts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomService extends Service {

    List<LogInfo> logList = new ArrayList<>();


    public CustomService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logList.add(new LogInfo("onCreate", System.currentTimeMillis()));
        Log.i(Consts.TAG_SERVICE_TEST, "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        logList.add(new LogInfo("onStart", System.currentTimeMillis()));
        Log.i(Consts.TAG_SERVICE_TEST, "onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Consts.TAG_SERVICE_TEST, "onStartCommand");
        logList.add(new LogInfo("onStartCommand", System.currentTimeMillis()));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        logList.add(new LogInfo("onRebind", System.currentTimeMillis()));
        Log.i(Consts.TAG_SERVICE_TEST, "onRebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logList.add(new LogInfo("onDestroy", System.currentTimeMillis()));
        Log.i(Consts.TAG_SERVICE_TEST, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Consts.TAG_SERVICE_TEST, "onBind");
        logList.add(new LogInfo("onBind", System.currentTimeMillis()));
        return new CustomBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(Consts.TAG_SERVICE_TEST, "onUnbind");
        logList.add(new LogInfo("onUnbind", System.currentTimeMillis()));
        return true;
    }

    public class CustomBinder extends Binder {
        public List<LogInfo> getLog() {
            return logList;
        }

        public void clearLog() {
            logList.clear();
        }
    }
}
