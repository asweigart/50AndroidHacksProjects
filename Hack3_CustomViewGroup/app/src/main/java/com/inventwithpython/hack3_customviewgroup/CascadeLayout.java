package com.inventwithpython.hack3_customviewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CascadeLayout extends ViewGroup {

    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    public CascadeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // NOTE: In this ctor, we set the horizontal and vertical spacing based on the values
        // (if they exist) in attrs. If they don't exist there, the default values in dimens.xml
        // are used.

        // NOTE: R.styleable.CascadeLayout comes from the <declare-styleable name="CascadeLayout">
        // tag in attrs.xml.
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CascadeLayout);

        try {
            // NOTE: It's weird that it is "R.styleable.CascadeLayout_horizontal_spacing" instead
            // of "R.styleable.CascadeLayout.horizontal_spacing". I think there can only be one
            // level underneath R.styleable, so what would be "." is changed to "_".
            // TODO: I don't know if this convention is done elsewhere.
            mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.CascadeLayout_horizontal_spacing,
                    getResources().getDimensionPixelSize(R.dimen.cascade_horizontal_spacing));
            mVerticalSpacing = a.getDimensionPixelSize(R.styleable.CascadeLayout_vertical_spacing,
                    getResources().getDimensionPixelSize(R.dimen.cascade_vertical_spacing));

        } finally {
            // NOTE: The TypedArray object returned by obtainStyledAttributes() needs recycle()
            // called on it. I don't know if it causes a memory leak if this doesn't happen.
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = getPaddingTop();
        int verticalSpacing = mVerticalSpacing;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (lp.verticalSpacing >= 0) {
                verticalSpacing = lp.verticalSpacing;
            }

            width = getPaddingLeft() + mHorizontalSpacing * i;

            lp.x = width;
            lp.y = height;
            width += child.getMeasuredWidth();
            height += verticalSpacing;
        }
        width += getPaddingRight();
        height += getChildAt(getChildCount() - 1).getMeasuredHeight() + getPaddingBottom();
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            // NOTE: layout() assigns size & position values. The parameters are left, top, right, bottom.
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }



    public static class LayoutParams extends ViewGroup.LayoutParams {
        // NOTE: This custom class will hold the x, y position of each child view in this
        // cascade layout view. The ViewGroup.LayoutParams class (which, btw, is where the
        // FILL_PARENT and WRAP_CONTENT constants are defined). The View.getLayoutParams() method
        // returns a ViewGroup.LayoutParams object, which only has width and height info.
        // We extend this class to add x, y info as well.
        int x;
        int y;
        int verticalSpacing;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.CascadeLayout_LayoutParams);
            try {
                verticalSpacing = a.getDimensionPixelSize(R.styleable.CascadeLayout_LayoutParams_layout_vertical_spacing, -1);
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

    }
}