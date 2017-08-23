package com.example.douzi.customdemo.splittouch;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by douzi on 2017/7/31.
 */

public class LogLinearLayout extends LinearLayout implements LogAble{
    private boolean logAble;

    public LogLinearLayout(Context context) {
        super(context);
    }

    public LogLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LogLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LogLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isLogAble()) {
            Log.i(SplitTouchActivity.TAG, this.toString() + "--dispatchTouchEvent("+ev+")");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean isLogAble() {
        return logAble;
    }

    public void setLogAble(boolean enable) {
        this.logAble = enable;
    }
}
