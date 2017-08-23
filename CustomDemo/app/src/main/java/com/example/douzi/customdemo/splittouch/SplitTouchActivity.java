/**
 * windowEnableSplitTouch：用在主题上；是否允许将多个手指的事件分别传递给多个window；
 * splitMotionEvents：用在布局上；是否将多个手指组合事件拆分成单个事件分别传递给targetView；
 * <p>
 * 事件传递：
 * 一个parentView接收到第2、3...个手指的事件时，收到的pointerup、pointerdown、move事件是所有手指事件的组合事件：如果一个手指的事件能够确定targetView则将该手指的事件拆分出来形成一个单一事件传递给targetChildView；继续拆分其他手指的事件；如果没有拆分完毕，则剩余的组合事件会传递给第1个手指的targetView—targetChildViewOne，此时targetViewOne收到的事件是组合事件，是一个新的parentView，继续拆分、分发事件到childView
 * <p>
 * 关于事件的标识：
 *         actionIndex：每个手指所属事件的角标，是随时变化的，但是是从0开始连续的
 *         pointerId：每个手指所属事件的id，手指离开前不管传递到哪个View都不会变化，从0开始但不是连续的;
 */


package com.example.douzi.customdemo.splittouch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.douzi.customdemo.BaseActivity;
import com.example.douzi.customdemo.R;
import com.example.douzi.customdemo.recyclerview.RecyclerViewActivity;

public class SplitTouchActivity extends BaseActivity {

    public static final String TAG = SplitTouchActivity.class.getSimpleName();
    private LogTextView logOne;
    private LogTextView logTwo;
    private LogLinearLayout logOneContainer;
    private LogLinearLayout logTwoContainer;

//    private SplitTouchFilter splitTouchFilter;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, SplitTouchActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_touch);

//        splitTouchFilter = new SplitTouchFilter();

        logOneContainer = (LogLinearLayout) this.findViewById(R.id.ll_log_one);
        logTwoContainer = (LogLinearLayout) this.findViewById(R.id.ll_log_two);

        logOne = (LogTextView) this.findViewById(R.id.tv_log_one);
        logTwo = (LogTextView) this.findViewById(R.id.tv_log_two);

        logOneContainer.setLogAble(false);
        logTwoContainer.setLogAble(false);
        logOne.setLogAble(false);
        logTwo.setLogAble(true);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (splitTouchFilter.isNeedFilter(ev)) {
//            return true;
//        } else {
//            return super.dispatchTouchEvent(ev);
//        }
//    }
}
