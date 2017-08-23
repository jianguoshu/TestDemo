package com.example.douzi.customdemo.toast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.douzi.customdemo.BaseActivity;
import com.example.douzi.customdemo.R;
import com.example.douzi.customdemo.xy.XYActivity;

public class ToastActivity extends BaseActivity {

    public static void startAct(Context context) {
        Intent intent = new Intent(context, ToastActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);

        this.findViewById(R.id.tv_thread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ToastActivity.this, "this is thread", Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }
        });
        this.findViewById(R.id.tv_thread_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ToastActivity.this, "main thread", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
