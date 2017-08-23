package com.example.douzi.customdemo.longclick;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by douzi on 2017/7/28.
 */

public class ChildView extends TextView {
    public ChildView(Context context) {
        super(context);
    }

    public ChildView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
