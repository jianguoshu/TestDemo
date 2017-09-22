package com.example.douzi.customdemo.GridLayoutTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douzi.customdemo.BaseActivity;
import com.example.douzi.customdemo.R;

public class GridLayoutActivity extends BaseActivity {

    public static void startAct(Context context) {
        Intent intent = new Intent(context, GridLayoutActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private android.support.v7.widget.GridLayout mGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout_test);
        mGridLayout = (GridLayout) this.findViewById(R.id.gridlayout);

        getChildView(R.mipmap.creditduihuan, "积分换搜豆呀", mGridLayout, 0, 0);
        addDivider(mGridLayout, 1, 0);
        getChildView(R.mipmap.creditshop, "积分商城", mGridLayout, 2, 0);
        addDivider(mGridLayout, 3, 0);
        getChildView(R.mipmap.creditchoujiang, "抽奖", mGridLayout, 5, 0);
        getChildView(R.mipmap.creditchoujiang, "抽奖", mGridLayout, 0, 1);
        addDivider(mGridLayout, 1, 1);

    }

    private void addDivider(GridLayout gridLayout, int startX, int startY) {
        GridLayout.Spec rowSpec = GridLayout.spec(startY, 1f);
        GridLayout.Spec columnSpec = GridLayout.spec(startX, 1f);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        layoutParams.width = 0;
        layoutParams.height = 0;
        gridLayout.addView(new View(this), layoutParams);
    }

    private View getChildView(int iconId, final String name, GridLayout gridLayout, int startX, int startY) {
        GridLayout.Spec rowSpec = GridLayout.spec(startY);
        GridLayout.Spec columnSpec = GridLayout.spec(startX);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);

        View childView = LayoutInflater.from(this).inflate(R.layout.item_gridlayout, null);
        ImageView icon = (ImageView) childView.findViewById(R.id.iv);
        icon.setImageResource(iconId);
        final TextView tvName = (TextView) childView.findViewById(R.id.tv);
        tvName.setText(name);
        gridLayout.addView(childView, layoutParams);
        childView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GridLayoutActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });
        return childView;
    }
}
