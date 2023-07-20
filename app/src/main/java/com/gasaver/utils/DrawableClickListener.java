package com.gasaver.utils;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public abstract class DrawableClickListener implements View.OnTouchListener {
    public static final int DRAWABLE_LEFT = 0;
    public static final int DRAWABLE_TOP = 1;
    public static final int DRAWABLE_RIGHT = 2;
    public static final int DRAWABLE_BOTTOM = 3;

    private final int drawableIndex;

    public DrawableClickListener(int index) {
        drawableIndex = index;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        TextView textView = (TextView) view;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (textView.getRight() - textView.getCompoundDrawables()[drawableIndex].getBounds().width())) {
                return onDrawableClick();
            }
        }
        return false;
    }

    public abstract boolean onDrawableClick();
}