package com.example.commonlib.widget;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by douzi on 2017/7/12.
 */

public class FlowLayoutHelper {

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    private int maxWidthAllLine;        // 所有行的最大宽度
    private int heightUsed;        // 子view占用的height
    private FlowLayout mTarget;
    private MeasureResult mMeasureResult;
    private boolean isLintStart;
    private int lineCur;        // 记录当前计算到的line num
    private int maxLine;

    private void reset() {
        maxWidthAllLine = 0;
        heightUsed = 0;
        lineCur = 1;
        isLintStart = true;
    }

    public void preMeasure(FlowLayout target, int widthMeasureSpec, int heightMeasureSpec, int maxLine, MeasureResult result) {
        mTarget = target;
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        this.maxLine = maxLine;
        if (result != null) {
            mMeasureResult = result;
            mMeasureResult.reset();
        } else {
            mMeasureResult = new MeasureResult();
        }
        reset();
    }

    public MeasureResult endMeasure() {


        if (isLintStart) {
            mMeasureResult.measuredWidth = maxWidthAllLine;
            mMeasureResult.measureHeight = heightUsed;
        } else {
            Line line = mMeasureResult.lines.get(mMeasureResult.lineNum);
            mMeasureResult.measuredWidth = Math.max(maxWidthAllLine, line.width);
            mMeasureResult.measureHeight = heightUsed + line.height;
        }
        return mMeasureResult;
    }

    public boolean measure(View child) {
        if (outLines(lineCur, maxLine)) {// 超出最大行数限制，不再measure
            return false;
        }

        int maxWidth = View.MeasureSpec.getSize(mWidthMeasureSpec) - mTarget.getPaddingLeft() - mTarget.getPaddingRight();

        if (child.getVisibility() == View.GONE) {
            mMeasureResult.validChildNum++;
            mMeasureResult.lineNum = lineCur;
            Line line = mMeasureResult.lines.get(lineCur);
            if (line != null) {
                line.childNum++;
            }
            return true;
        }
        mTarget.measureChildWithMargins(child, mWidthMeasureSpec, 0, mHeightMeasureSpec, 0);
        FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) child.getLayoutParams();
        int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
        int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

        if (isLintStart) { // 新的一行开始处
            maxWidthAllLine = Math.max(maxWidthAllLine, childWidth);
            mMeasureResult.validChildNum++;
            mMeasureResult.lineNum = lineCur;
            Line line = new Line();
            line.childNum = 1;
            line.width = childWidth;
            line.height = childHeight;
            mMeasureResult.lines.put(lineCur, line);
            if (maxWidth - childWidth <= 0) { // 一个child占满一行--new line
                heightUsed += childHeight;
                isLintStart = true;
                lineCur++;
            } else { // 一行中第一个child
                isLintStart = false;
            }
        } else {// 一行中已有child
            Line line = mMeasureResult.lines.get(lineCur);
            if (maxWidth - line.width - childWidth == 0) { // child占满了剩余的空间--new line
                maxWidthAllLine = Math.max(maxWidthAllLine, maxWidth);
                mMeasureResult.validChildNum++;
                mMeasureResult.lineNum = lineCur;
                line.childNum++;
                line.width = maxWidth;
                line.height = Math.max(line.height, childHeight);

                heightUsed += line.height;
                isLintStart = true;
                lineCur++;
            } else if (maxWidth - line.width - childWidth > 0) { // child未占满剩余空间，continue
                maxWidthAllLine = Math.max(maxWidthAllLine, line.width);
                mMeasureResult.validChildNum++;
                mMeasureResult.lineNum = lineCur;
                line.childNum++;
                line.width += childWidth;
                line.height = Math.max(line.height, childHeight);

                isLintStart = false;
            } else { //  剩余空间不能完整显示child
                if (mTarget.needNewLine(maxWidth, line.width, childWidth)) { // 需要新开一行--new line
                    maxWidthAllLine = Math.max(maxWidthAllLine, line.width);
                    heightUsed += line.height;

                    mMeasureResult.lineNum = lineCur;
                    lineCur++;

                    if (outLines(lineCur, maxLine)) { // 新的一行超出最大行数限制，结束measure；并清除下一行中view造成的影响
                        mMeasureResult.invalidChildNum++;
                        isLintStart = true;
                        return false;
                    } else {
                        mMeasureResult.validChildNum++;
                        mMeasureResult.lineNum = lineCur;
                        line = new Line();
                        line.childNum = 1;
                        line.width = childWidth;
                        line.height = childHeight;
                        mMeasureResult.lines.put(lineCur, line);

                        isLintStart = false;
                    }

                } else { // 压缩child填充剩余空间
                    mTarget.measureChildWithMargins(child, mWidthMeasureSpec, line.width, mHeightMeasureSpec, heightUsed);
                    lp = (FlowLayout.LayoutParams) child.getLayoutParams();
                    childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                    childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    maxWidthAllLine = Math.max(maxWidthAllLine, line.width);
                    mMeasureResult.validChildNum++;
                    mMeasureResult.lineNum = lineCur;
                    line.childNum++;
                    line.width += childWidth;
                    line.height = Math.max(line.height, childHeight);

                    heightUsed += line.height;
                    isLintStart = true;
                    lineCur++;
                }
            }
        }
        return true;
    }

    private boolean outLines(int lineCur, int maxLine) {
        return lineCur > maxLine;
    }

    public static class MeasureResult {
        int measuredWidth = 0;
        int measureHeight = 0;
        int validChildNum = 0;
        int invalidChildNum = 0;
        int lineNum = 0;
        SparseArray<Line> lines = new SparseArray<>();

        public void reset() {
            measuredWidth = 0;
            measureHeight = 0;
            validChildNum = 0;
            invalidChildNum = 0;
            lineNum = 0;
            lines.clear();
        }

        @Override
        public String toString() {
            return "MeasureResult{" +
                    "measuredWidth=" + measuredWidth +
                    ", measureHeight=" + measureHeight +
                    ", validChildNum=" + validChildNum +
                    ", invalidChildNum=" + invalidChildNum +
                    ", lineNum=" + lineNum +
                    ", lines=" + lines +
                    '}';
        }
    }

    public static class Line {
        int childNum = 0;
        int width = 0;
        int height = 0;

        @Override
        public String toString() {
            return "Line{" +
                    "childNum=" + childNum +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

}
