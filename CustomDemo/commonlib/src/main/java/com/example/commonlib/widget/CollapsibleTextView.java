package com.example.commonlib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by douzi on 2017/7/12.
 */

public class CollapsibleTextView extends TextView implements View.OnLayoutChangeListener{

    public static final int LINES_INVALID = -1;
    public static final int COLOR_INVALID = -1;
    int maxLines = LINES_INVALID; // 保存最大行数
    int totalLines = LINES_INVALID; // 总行数，每次设置text时计算更新此变量
    boolean isCollapsed; // 记录展开收起状态
    private int mWidth;
    private CharSequence mText;

    OnCollapseStateChangeListener collapseStateChangeListener;

    private String expendText = "全文";
    private String ellipsisSymbol = "...";
    private String collapseText = "收起";
    private boolean collapsibleByHand = false; // 是否显示收起功能
    private int collapseSwitcherColor = COLOR_INVALID; // 展开、收起文字的颜色
    private int collapseSwitcherColorId = COLOR_INVALID; // 展开、收起文字的颜色

    public CollapsibleTextView(Context context) {
        super(context);
        init(null);
    }

    public CollapsibleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CollapsibleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        maxLines = LINES_INVALID;
        if (attrs != null) {
            String nameSpace = "http://schemas.android.com/apk/res-auto";
            maxLines = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "maxLines", LINES_INVALID);
            String expendText = attrs.getAttributeValue(nameSpace, "expend_text");
            String collapseText = attrs.getAttributeValue(nameSpace, "collapse_text");
            String ellipsisSymbol = attrs.getAttributeValue(nameSpace, "ellipsis_symbol");
            if (!TextUtils.isEmpty(expendText)) {
                this.expendText = expendText;
            }
            if (!TextUtils.isEmpty(collapseText)) {
                this.collapseText = collapseText;
            }
            if (!TextUtils.isEmpty(ellipsisSymbol)) {
                this.ellipsisSymbol = ellipsisSymbol;
            }

            collapsibleByHand = attrs.getAttributeBooleanValue(nameSpace, "collapsible_by_hand", false);
            collapseSwitcherColor = attrs.getAttributeIntValue(nameSpace, "collapse_switcher_color", COLOR_INVALID);
            if (collapseSwitcherColor == COLOR_INVALID) {
                collapseSwitcherColorId = attrs.getAttributeResourceValue(nameSpace, "collapse_switcher_color", COLOR_INVALID);
            }
        }
        isCollapsed = maxLines != LINES_INVALID;
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        this.maxLines = maxLines;
        isCollapsed = maxLines != LINES_INVALID;
    }

    public static class CustomLinkMovementMethod extends LinkMovementMethod {
        private static CustomLinkMovementMethod sInstance;

        public static MovementMethod getInstance() {
            if (sInstance == null)
                sInstance = new CustomLinkMovementMethod();

            return sInstance;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            boolean handled = false;
            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }

                    handled = true;
                }
            }

            return handled;

        }
    }

    /**
     * 解决点击Spannable区域也会出发TextView的点击事件的问题
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        CharSequence text = getText();
        if (!TextUtils.isEmpty(text) && text instanceof Spannable) {
            MovementMethod movementMethod = getMovementMethod();
            if (movementMethod != null) {
                handled = movementMethod.onTouchEvent(this, (Spannable) text, event);
            }
        }
        return handled || super.onTouchEvent(event);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mText = text;
        CharSequence textNew = processText(text);
        super.setText(textNew, type);
        if (mText != null && textNew instanceof Spannable) {
            setHighlightColor(Color.TRANSPARENT);
            boolean clickable = isClickable();
            boolean longClickable = isLongClickable();
            setMovementMethod(CustomLinkMovementMethod.getInstance());
            //setMovementMethod方法会调用fixFocusableAndClickableSettings()方法，所以在此处重置可点击状态
            setClickable(clickable);
            setLongClickable(longClickable);
        }
    }

    private CharSequence processText(CharSequence text) {
        SpannableStringBuilder resultBuilder = new SpannableStringBuilder();
        int width = mWidth - getPaddingLeft() - getPaddingRight();
        if (width > 0 && mText != null) {
            StaticLayout staticLayout = new StaticLayout(text, 0, text.length(), getPaint(), width,
                    Layout.Alignment.ALIGN_NORMAL, 0, 0, false, TextUtils.TruncateAt.END, 0);
            totalLines = staticLayout.getLineCount();
            if (collapsible()) {
                int start;
                int end;
                CharSequence strLine;
                Paint paint = getPaint();
                if (isCollapsed) {
                    for (int line = 0; line < maxLines; line++) {
                        start = staticLayout.getLineStart(line);
                        end = staticLayout.getLineVisibleEnd(line);
                        strLine = text.subSequence(start, end);
                        if (line < maxLines - 1) { // 每一行结尾加换行符防止行错乱
                            resultBuilder.append(strLine).append("\n");
                        } else { // 最后一行

                            float widthExtra = paint.measureText(expendText);
                            CharSequence newText = TextUtils.ellipsize(strLine, getPaint(), width - widthExtra, TextUtils.TruncateAt.END);
                            resultBuilder.append(newText).append(expendText);
                            ClickableSpan clickableSpan = getClickableSpan();
                            resultBuilder.setSpan(clickableSpan, resultBuilder.length() - expendText.length(), resultBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);


//                            float widthExtra = paint.measureText(expendText + ellipsisSymbol);
//                            int lineEnd = strLine.length();
//                            ImageSpanCursor spanCursor = new ImageSpanCursor(strLine);
//                            spanCursor.last();
//                            while (widthExtra > 0 && lineEnd > 0) {
//                                if (spanCursor.isValid() && spanCursor.start < lineEnd && spanCursor.end >= lineEnd) {
//                                    widthExtra -= spanCursor.width;
//                                    lineEnd -= spanCursor.length;
//                                    spanCursor.pre();
//                                } else {
//                                    float widthCharLast = paint.measureText(String.valueOf(strLine.subSequence(lineEnd - 1, lineEnd)));
//                                    widthExtra -= widthCharLast;
//                                    lineEnd--;
//                                }
//                            }
//                            resultBuilder.append(strLine.subSequence(0, lineEnd));
//                            resultBuilder.append(ellipsisSymbol).append(expendText);
//                            ClickableSpan clickableSpan = getClickableSpan();
//                            resultBuilder.setSpan(clickableSpan, resultBuilder.length() - expendText.length(), resultBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                    }
                } else if (collapsibleByHand) {
                    for (int line = 0; line < totalLines; line++) {
                        start = staticLayout.getLineStart(line);
                        end = staticLayout.getLineVisibleEnd(line);
                        strLine = text.subSequence(start, end);
                        if (line < totalLines - 1) { // 每一行结尾加换行符防止行错乱
                            resultBuilder.append(strLine).append("\n");
                        } else { // 最后一行
                            resultBuilder.append(strLine);
                            float desiredWidth = staticLayout.getDesiredWidth(strLine, getPaint());
                            float widthExtra = paint.measureText(collapseText);
                            if (widthExtra + desiredWidth > width) {
                                resultBuilder.append("\n");
                            }
                            resultBuilder.append(collapseText);
                            ClickableSpan clickableSpan = getClickableSpan();
                            resultBuilder.setSpan(clickableSpan, resultBuilder.length() - collapseText.length(), resultBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                    }
                }
            }
        }
        if (resultBuilder.length() == 0) {
            resultBuilder.append(text).append(" ");
        }
        return resultBuilder;
    }

    @NonNull
    private ClickableSpan getClickableSpan() {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                collapse(!isCollapsed);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                if (collapseSwitcherColor != COLOR_INVALID) {
                    ds.setColor(collapseSwitcherColor);
                } else if (collapseSwitcherColorId != COLOR_INVALID) {
                    ds.setColor(getResources().getColor(collapseSwitcherColorId));
                }
                ds.setUnderlineText(false);
            }
        };
    }

    public boolean collapsible() {
        return mWidth > 0 && maxLines != LINES_INVALID && totalLines > maxLines;
    }


    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setWidth(int width) {
        this.mWidth = width;
        super.setWidth(width);
    }

    public void collapse(boolean collapse) {
        if (isCollapsed != collapse) {
            collapseForce(collapse);
        }
    }

    private void collapseForce(boolean collapse) {
        boolean before = isCollapsed;
        isCollapsed = collapse;
        if (isCollapsed) {
            super.setMaxLines(Integer.MAX_VALUE);
        } else {
            super.setMaxLines(Integer.MAX_VALUE);
        }
        setText(mText);
        if (collapseStateChangeListener != null) {
            collapseStateChangeListener.onCollapseStateChange(before, isCollapsed);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnLayoutChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int widthNew = getWidth();
        if (widthNew != mWidth) {
            mWidth = widthNew;
            collapseForce(isCollapsed);
        }
    }

    public void setExpendText(String expendText) {
        if (!TextUtils.isEmpty(expendText)) {
            this.expendText = expendText;
        }
    }

    public void setEllipsisSymbol(String ellipsisSymbol) {
        if (!TextUtils.isEmpty(expendText)) {
            this.ellipsisSymbol = ellipsisSymbol;
        }
    }

    public void setCollapseText(String collapseText) {
        if (!TextUtils.isEmpty(expendText)) {
            this.collapseText = collapseText;
        }
    }

    public void setCollapsibleByHand(boolean collapsibleByHand) {
        this.collapsibleByHand = collapsibleByHand;
    }

    public void setCollapseSwitcherColor(int collapseSwitcherColor) {
        this.collapseSwitcherColor = collapseSwitcherColor;
    }

    public void setCollapseSwitcherColorId(int collapseSwitcherColorId) {
        this.collapseSwitcherColorId = collapseSwitcherColorId;
    }

    public void setCollapseStateChangeListener(OnCollapseStateChangeListener collapseStateChangeListener) {
        this.collapseStateChangeListener = collapseStateChangeListener;
    }

    public interface OnCollapseStateChangeListener {
        void onCollapseStateChange(boolean before, boolean after);
    }

}
