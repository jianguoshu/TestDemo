package com.example.douzi.customdemo.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.douzi.customdemo.BaseActivity;
import com.example.douzi.customdemo.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends BaseActivity {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_NORMAL = 1;

    private RecyclerView mRecyclerView;
    private int mColumnNum = 4;
    private RecyclerView.Adapter mAdapter;
    private List<String> mText;
    private List<Channel> mChannels;
    private GridLayoutManager mGridLayoutManager;
//    private int firstUnSelectedIndex;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, RecyclerViewActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recucler_view);

        initData();

        initViews();
    }

    private void initData() {
        mText = new ArrayList<>();
        mText.add("推荐");
        mText.add("视频");
        mText.add("搞笑");
        mText.add("订阅");
        mText.add("北京");
        mText.add("图片");
        mText.add("财经");
        mText.add("热门");
        mText.add("段子");
        mText.add("要闻");
        mText.add("直播");
        mText.add("情感");
        mText.add("汽车");
        mText.add("科技");
        mText.add("娱乐");
        mText.add("健康");
        mText.add("时尚");
        mText.add("美食");
        mText.add("人文");
        mText.add("旅游");
        mText.add("萌宠");
        mText.add("美女");
        mText.add("知乎");
        mText.add("姿势");
        mText.add("体育");
        mText.add("育儿");
        mText.add("职场");
        mText.add("生活");
        mText.add("教育");
        mText.add("星座");
        mText.add("游戏");
        mText.add("收藏");
        mText.add("风水");
        mText.add("军事");
        mText.add("摄影");

        mChannels = new ArrayList<>();
        int length = mText.size();
        int halfIndex = (int) (1.0f * length / 2);
        boolean isSelected;
        for (int i = 0; i < length; i++) {
            if (i < halfIndex) {
                isSelected = true;
            } else {
                isSelected = false;
            }
            Channel channel = new Channel(mText.get(i), isSelected, i, TYPE_NORMAL);
            mChannels.add(channel);
        }

        mText.add(0, "我的频道");
        mChannels.add(0, new Channel("我的频道", false, 0, TYPE_TITLE));
        int firstUnSelectedIndex = 0;
        for (int i = 0; i < mChannels.size(); i++) {
            Channel channel = mChannels.get(i);
            if (channel.isSelected == false && channel.type == TYPE_NORMAL) {
                firstUnSelectedIndex = i;
                break;
            }
        }
        mText.add(firstUnSelectedIndex, "可添加频道");
        mChannels.add(firstUnSelectedIndex, new Channel("可添加频道", false, 0, TYPE_TITLE));
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);

        mGridLayoutManager = new GridLayoutManager(this, mColumnNum);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mChannels.get(position).type) {
                    case TYPE_TITLE:
                        return mColumnNum;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
//        itemAnimator.setAddDuration(1500);
//        itemAnimator.setChangeDuration(1500);
//        itemAnimator.setMoveDuration(1500);
//        itemAnimator.setRemoveDuration(1500);
        mRecyclerView.setItemAnimator(itemAnimator);
        mAdapter = new RecyclerView.Adapter<ChannelHolder>() {

            @Override
            public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(RecyclerViewActivity.this).inflate(R.layout.item_channel, parent, false);
                ChannelHolder holder = new ChannelHolder(view);
                holder.name = (TextView) view.findViewById(R.id.tv_name);
                return holder;
            }

            @Override
            public void onBindViewHolder(ChannelHolder holder, final int position) {
                if (getItemViewType(position) == TYPE_NORMAL) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Channel channel = mChannels.get(position);
                            String text = mText.get(position);
                            if (channel.isSelected) {
                                channel.isSelected = false;
                                mChannels.remove(channel);
                                mChannels.add(channel);
                                mText.remove(position);
                                mText.add(text);
//                                firstUnSelectedIndex--;
                                mAdapter.notifyItemMoved(position, mChannels.size() - 1);
                            } else {
                                int firstUnSelectedIndex = findFirstUnselectedPositon();
                                if (firstUnSelectedIndex >=0) {
                                    channel.isSelected = true;
                                    mChannels.remove(channel);
                                    mText.remove(position);
                                    mChannels.add(firstUnSelectedIndex, channel);
                                    mText.add(firstUnSelectedIndex, text);
                                    mAdapter.notifyItemMoved(position, firstUnSelectedIndex);
                                }
//                                firstUnSelectedIndex++;
                            }
//                            notifyDataSetChanged();
                        }
                    });
                }
                holder.name.setText(mText.get(position));
            }

            @Override
            public int getItemCount() {
                return mChannels.size();
            }

            @Override
            public int getItemViewType(int position) {
                return mChannels.get(position).type;
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    public static class ChannelHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ChannelHolder(View itemView) {
            super(itemView);
        }
    }

    private int findFirstUnselectedPositon() {
        int length = mChannels.size();
        if (length > 0) {
            for (int i = length - 1; i >=0; i--) {
                Channel channel = mChannels.get(i);
                if (channel.type == TYPE_TITLE) {
                    return i;
                }
            }
        }
        return -1;
    }
}
