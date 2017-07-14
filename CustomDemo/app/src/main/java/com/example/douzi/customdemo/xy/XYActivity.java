package com.example.douzi.customdemo.xy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.example.douzi.customdemo.BaseActivity;
import com.example.douzi.customdemo.R;
import com.example.douzi.customdemo.recyclerview.RecyclerViewActivity;

public class XYActivity extends BaseActivity {

    public static void startAct(Context context) {
        Intent intent = new Intent(context, XYActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private View one;
    private View two;
    private View three;
    private View threeChild;
    private boolean hasChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xy);


        one = this.findViewById(R.id.tv_one);
        two = this.findViewById(R.id.tv_two);
        three = this.findViewById(R.id.tv_three);
        threeChild = this.findViewById(R.id.tv_three_child);

        this.findViewById(R.id.tv_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XYActivity.this.one.requestLayout();
                XYActivity.this.two.requestLayout();
                XYActivity.this.three.requestLayout();
                XYActivity.this.threeChild.requestLayout();
                threeChild.post(new Runnable() {
                    @Override
                    public void run() {
                        printXYInfo("reset");
                    }
                });
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        changeXY();
        super.onWindowFocusChanged(hasFocus);
    }

    private void changeXY() {
        if (!hasChanged) {
            printXYInfo("default");
            one.offsetLeftAndRight(50); // 修改mLeft、mRight（重新布局后恢复）
            one.offsetTopAndBottom(50); // 修改mTop、mBottom的值（重新布局后恢复）
            two.setTranslationX(50); // 修改TranslationX属性
            two.setTranslationY(50); // 修改TranslationY属性
            three.scrollBy(-50, -50);// 修改mScrollX、mScrollY属性
            printXYInfo("change");
            hasChanged = true;
        }
    }

    private void printXYInfo(String divider) {
        Rect oneLocalVisibleRect = new Rect();
        one.getLocalVisibleRect(oneLocalVisibleRect);
        Rect oneGlobalVisibleRect = new Rect();
        one.getGlobalVisibleRect(oneGlobalVisibleRect);
        int[] oneLocationInWindow = new int[2];
        one.getLocationInWindow(oneLocationInWindow);
        int[] oneLocationOnScreen = new int[2];
        one.getLocationOnScreen(oneLocationOnScreen);
        Rect twoLocalVisibleRect = new Rect();
        two.getLocalVisibleRect(twoLocalVisibleRect);
        Rect twoGlobalVisibleRect = new Rect();
        two.getGlobalVisibleRect(twoGlobalVisibleRect);
        int[] twoLocationInWindow = new int[2];
        two.getLocationInWindow(twoLocationInWindow);
        int[] twoLocationOnScreen = new int[2];
        two.getLocationOnScreen(twoLocationOnScreen);
        Rect threeLocalVisibleRect = new Rect();
        three.getLocalVisibleRect(threeLocalVisibleRect);
        Rect threeGlobalVisibleRect = new Rect();
        three.getGlobalVisibleRect(threeGlobalVisibleRect);
        int[] threeLocationInWindow = new int[2];
        three.getLocationInWindow(threeLocationInWindow);
        int[] threeLocationOnScreen = new int[2];
        three.getLocationOnScreen(threeLocationOnScreen);
        Rect threeChildLocalVisibleRect = new Rect();
        threeChild.getLocalVisibleRect(threeChildLocalVisibleRect);
        Rect threeChildGlobalVisibleRect = new Rect();
        threeChild.getGlobalVisibleRect(threeChildGlobalVisibleRect);
        int[] threeChildLocationInWindow = new int[2];
        threeChild.getLocationInWindow(threeChildLocationInWindow);
        int[] threeChildLocationOnScreen = new int[2];
        threeChild.getLocationOnScreen(threeChildLocationOnScreen);
    }
}
