package com.example.commonlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.commonlib.R;

/**
 * Created by douzi on 2017/7/12.
 */

public class FlowLayout extends ViewGroup {
    public static final String TAG = FlowLayout.class.getSimpleName();
    public static final int HORIZONTAL_GRAVITY_MASK = 0x000F;
    public static final int LEFT = 0x0001;
    public static final int RIGHT = LEFT << 1;
    public static final int CENTER_HORIZONTAL = LEFT << 2;

    public static final int VERTICAL_GRAVITY_MASK = 0x00F0;
    public static final int TOP = 0x0010;
    public static final int BOTTOM = TOP << 1;
    public static final int CENTER_VERTICAL = TOP << 2;

    public static final int CENTER = CENTER_HORIZONTAL | CENTER_VERTICAL;

    private int gravity = LEFT | CENTER_VERTICAL;
    private boolean childFullVisual; // 标示child是否完整显示或者可以压缩宽度
    private float maxBlankWidth; // 标示每行末尾可显示空白的最大尺寸
    protected int maxLine; // 限制显示行数
    private FlowLayoutHelper.MeasureResult measureResult = new FlowLayoutHelper.MeasureResult();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        childFullVisual = ta.getBoolean(R.styleable.FlowLayout_childFullVisual, true);
        maxBlankWidth = ta.getDimension(R.styleable.FlowLayout_maxBlankWidth, 0);
        gravity = ta.getInt(R.styleable.FlowLayout_gravity, LEFT | CENTER_VERTICAL);
        maxLine = ta.getInt(R.styleable.FlowLayout_maxLine, Integer.MAX_VALUE);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        FlowLayoutHelper layoutHelper = new FlowLayoutHelper();
        layoutHelper.preMeasure(this, widthMeasureSpec, heightMeasureSpec, maxLine, measureResult);

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            if (!layoutHelper.measure(getChildAt(i))) {
                break;
            }
        }

        layoutHelper.endMeasure();

        int width = resolveSizeAndState(measureResult.measuredWidth + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);
        int height = resolveSizeAndState(measureResult.measureHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 先分行，计算时child的大小包含margin
     * 排除了自身padding的影响
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        FlowLayoutHelper.MeasureResult result = measureResult;
        if (result.lineNum <= 0) {
            return;
        }

        int widthTotal = r - l - getPaddingLeft() - getPaddingRight(); // 处理padding
        int heightUsed = getPaddingTop(); // 处理padding
        int count = getChildCount();

        int lineNum = 1;
        FlowLayoutHelper.Line line;
        line = result.lines.get(lineNum);
        int childIndexInLine = 0;
        int xStart = 0;
        for (int i = 0; i < count && lineNum <= measureResult.lineNum; i++) {
            if (childIndexInLine == 0) {
                int gravityHorizontal = this.gravity & HORIZONTAL_GRAVITY_MASK;
                int widthValid = widthTotal - line.width;
                switch (gravityHorizontal) {
                    case LEFT:
                        xStart = getPaddingLeft();
                        break;
                    case CENTER_HORIZONTAL:
                        xStart = (int) (widthValid / 2.0f) + getPaddingLeft();
                        break;
                    case RIGHT:
                        xStart = widthValid + getPaddingLeft();
                        break;
                    default:
                        xStart = getPaddingLeft();
                        break;
                }
            }

            View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            layoutView(child, xStart, childWidth, childHeight, heightUsed, line.height + heightUsed);
            xStart += childWidth;

            childIndexInLine++;
            if (childIndexInLine >= line.childNum) {
                lineNum++;
                childIndexInLine = 0;
                heightUsed += line.height;
                line = result.lines.get(lineNum);
            }
        }
    }

    /**
     * 参数的值受margin的影响，需要在此方法中排除margin的影响
     *
     * @param view
     * @param xStart
     * @param width
     * @param height
     * @param yStart
     * @param yEnd
     */
    private void layoutView(View view, int xStart, int width, int height, int yStart, int yEnd) {
        int gravityVertical = this.gravity & VERTICAL_GRAVITY_MASK;

        int l = xStart;
        int r = xStart + width;
        int t;
        int b;

        switch (gravityVertical) {
            case TOP:
                t = yStart;
                b = yStart + height;
                break;
            case CENTER_VERTICAL:
                t = (int) ((yEnd + yStart - height) / 2.0f);
                b = (int) ((yEnd + yStart + height) / 2.0f);
                break;
            case BOTTOM:
                t = yEnd - height;
                b = yEnd;
                break;
            default:
                t = (int) ((yEnd + yStart - height) / 2.0f);
                b = (int) ((yEnd + yStart + height) / 2.0f);
                break;
        }

        final LayoutParams lp = (LayoutParams) view.getLayoutParams();

        // 去除margin
        l += lp.leftMargin;
        t += lp.topMargin;
        r -= lp.rightMargin;
        b -= lp.bottomMargin;

        view.layout(l, t, r, b);
    }

    /**
     * @param total   一行的全部空间
     * @param used    已用的空间
     * @param require 新需要的空间
     * @return
     */
    protected boolean needNewLine(int total, int used, int require) {
        boolean isBlankEnough = total - used - require > 0;
        if (isBlankEnough) {
            return false;
        } else {
            if (childFullVisual) {
                return true;
            } else {
                return total - used <= maxBlankWidth;
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FlowLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new FlowLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p == null) {
            p = generateDefaultLayoutParams();
        }
        if (p instanceof MarginLayoutParams) {
            return new FlowLayout.LayoutParams((MarginLayoutParams) p);
        } else {
            return new FlowLayout.LayoutParams(p);
        }
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    public static int resolveSizeAndState(int size, int measureSpec) {
        int result = size;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case View.MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case View.MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }


    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setChildFullVisual(boolean childFullVisual) {
        this.childFullVisual = childFullVisual;
    }

    public void setMaxBlankWidth(float maxBlankWidth) {
        this.maxBlankWidth = maxBlankWidth;
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
