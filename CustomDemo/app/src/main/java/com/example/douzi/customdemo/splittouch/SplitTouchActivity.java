/**
 windowEnableSplitTouch：用在主题上；是否允许将多个手指的事件分别传递给多个window；
 splitMotionEvents：用在布局上；是否将多个手指组合事件拆分成单个事件分别传递给targetView；

 事件传递：
 一个parentView接收到第2、3...个手指的事件时，收到的pointerup、pointerdown、move事件是所有手指事件的组合事件：如果一个手指的事件能够确定targetView则将该手指的事件拆分出来形成一个单一事件传递给targetChildView；继续拆分其他手指的事件；如果没有拆分完毕，则剩余的组合事件会传递给第1个手指的targetView—targetChildViewOne，此时targetViewOne收到的事件是组合事件，是一个新的parentView，继续拆分、分发事件到childView

 关于事件的标识：
         actionIndex：每个手指所属事件的角标，是随时变化的，但是是从0开始连续的
         pointerId：每个手指所属事件的id，手指离开前不管传递到哪个View都不会变化，从0开始但不是连续的;
 */


package com.example.douzi.customdemo.splittouch;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.douzi.customdemo.R;

public class SplitTouchActivity extends Activity {

    private SplitTouchFilter splitTouchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_touch);

        splitTouchFilter = new SplitTouchFilter();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (splitTouchFilter.isNeedFilter(ev)) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}
