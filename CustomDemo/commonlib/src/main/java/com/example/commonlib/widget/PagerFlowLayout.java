package com.example.commonlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by douzi on 2017/7/12.
 */

public class PagerFlowLayout extends FlowLayout {

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;
    private List<ViewWrapper> mPagerViews = new ArrayList<>();
    private ViewCache viewCache = new ViewCache();
    private int mPosition = 0; // 保存child的下一个position，比当前position大1
    private int childViewCount = 0;
    private Adapter mAdapter;
    private PagerObserver mPagerObserver;

    private OnItemClickListener onItemClickListener;
    private boolean needNextPage = false;
    private boolean hasMeasureStart = false;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PagerFlowLayout(Context context) {
        super(context);
    }

    public PagerFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        hasMeasureStart = true;
        if (needNextPage) {
            needNextPage = false;
            nextPageWithoutLayout();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setAdapter(final Adapter adapter) {
        if (mAdapter != null && mPagerObserver != null) {
            mAdapter.unregisterDataSetObserver(mPagerObserver);
        }
        mAdapter = adapter;
        if (mAdapter == null) {
            mPosition = 0;
            childViewCount = 0;
            mPagerViews.clear();
            viewCache.clear();
            removeAllViews();
        } else {
            mPosition = 0;
            mPagerViews.clear();
            viewCache.clear();
            childViewCount = adapter.getCount();
            if (mPagerObserver == null) {
                mPagerObserver = new PagerObserver() {
                    @Override
                    public void onDataSetChanged() {
                        childViewCount = adapter.getCount();
                        mPosition -= mPagerViews.size();
                        checkPosition(mPosition);
                        nextPage();
                    }
                };
            }
            mAdapter.registerDataSetObserver(mPagerObserver);
            nextPage();
        }
    }

    public void nextPage() {
        nextPageWithoutLayout();
        requestLayout();
        invalidate();
    }

    private void nextPageWithoutLayout() {
        if (!hasMeasureStart) {
            needNextPage = true;
            return;
        }

        removeAllViewsInLayout();
        if (mAdapter != null && mPagerViews.size() > 0 && mAdapter.getViewTypeCount() > 0) {
            for (ViewWrapper viewWrapper : mPagerViews) {
                int maxLength = (int) Math.ceil(1.0f * mPagerViews.size() / mAdapter.getViewTypeCount());
                viewCache.cache(viewWrapper.type, viewWrapper.view, maxLength);
            }
        }
        mPagerViews.clear();

        if (childViewCount <= 0 || mAdapter == null) {
            mPosition = 0;
            return;
        }

        FlowLayoutHelper layoutHelper = new FlowLayoutHelper();
        FlowLayoutHelper.MeasureResult result = new FlowLayoutHelper.MeasureResult();
        layoutHelper.preMeasure(this, mWidthMeasureSpec, mHeightMeasureSpec, maxLine, result);

        ViewWrapper child = getChild(checkPosition());
        while (layoutHelper.measure(child.view)) {
            mPagerViews.add(child);
            mPosition++;
            child = getChild(checkPosition());
        }

        layoutHelper.endMeasure();

//        if (result.invalidChildNum > 0) {
//            mPosition -= result.invalidChildNum;
//        }

        View view;
        for (ViewWrapper viewWrapper : mPagerViews) {
            view = viewWrapper.view;
            addViewInLayout(view, -1, view.getLayoutParams());
        }
    }

    private int checkPosition() {
        mPosition = checkPosition(mPosition);
        return mPosition;
    }

    private int checkPosition(int pos) {
        if (childViewCount <= 0) {
            return 0; // 不正常的情况处理，调用此方法时应该保证:childViewCount>0
        }

        if (pos >= childViewCount) {
            pos = pos % childViewCount;
        }

        while (pos < 0) {
            pos += childViewCount;
        }
        return pos;
    }

    private ViewWrapper getChild(final int position) {
        int type = mAdapter.getItemViewType(position);
        View view = mAdapter.getView(position, getConvertView(type));
        if (view.getLayoutParams() == null) {
            view.setLayoutParams(generateDefaultLayoutParams());
        }
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(position);
                }
            }
        });
        return new ViewWrapper(type, view);
    }

    private View getConvertView(int itemViewType) {
        return viewCache.get(itemViewType);
    }

    public static abstract class Adapter {
        private final PagerObservable mDataSetObservable = new PagerObservable();

        public void registerDataSetObserver(PagerObserver observer) {
            mDataSetObservable.registerObserver(observer);
        }

        public void unregisterDataSetObserver(PagerObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
        }

        public boolean hasRegister(PagerObserver observer) {
            return mDataSetObservable.hasRegister(observer);
        }

        public void notifyDataSetChanged() {
            mDataSetObservable.notifyDataSetChanged();
        }

        public abstract int getCount();

        public abstract View getView(int position, View convertView);

        public abstract int getItemViewType(int position);

        public abstract int getViewTypeCount();
    }

    public static class PagerObservable extends android.database.Observable<PagerObserver> {

        public void notifyDataSetChanged() {
            synchronized (mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onDataSetChanged();
                }
            }
        }

        public boolean hasRegister(PagerObserver observer) {
            synchronized (mObservers) {
                return mObservers.contains(observer);
            }
        }
    }


    public interface PagerObserver {
        void onDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public static class ViewCache {
        SparseArray<LinkedList<View>> viewArray = new SparseArray<>();

        public void cache(int type, View view, int maxLength) {
            if (view == null) {
                return;
            }
            LinkedList<View> viewList = viewArray.get(type);
            if (viewList == null) {
                viewList = new LinkedList<>();
                viewArray.put(type, viewList);
            }
            if (viewList.size() < maxLength) {
                viewList.add(view);
            }
        }

        public View get(int type) {
            View view = null;
            LinkedList<View> viewList = viewArray.get(type);
            if (viewList != null) {
                view = viewList.peek();
                viewList.poll();
            }
            return view;
        }

        public void clear() {
            viewArray.clear();
        }
    }

    public static class ViewWrapper {
        int type;
        View view;

        public ViewWrapper(int type, View view) {
            this.type = type;
            this.view = view;
        }
    }

}
