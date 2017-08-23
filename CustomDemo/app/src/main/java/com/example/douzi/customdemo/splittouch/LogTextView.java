package com.example.douzi.customdemo.splittouch;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by douzi on 2017/7/31.
 */

public class LogTextView extends TextView implements LogAble{
    private boolean logAble;

    public LogTextView(Context context) {
        super(context);
    }

    public LogTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LogTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LogTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isLogAble()) {
            Log.i(SplitTouchActivity.TAG, this.toString() + "--onTouchEvent("+event+")");
        }
        return true;
    }

    @Override
    public boolean isLogAble() {
        return logAble;
    }

    public void setLogAble(boolean enable) {
        this.logAble = enable;
    }
}
