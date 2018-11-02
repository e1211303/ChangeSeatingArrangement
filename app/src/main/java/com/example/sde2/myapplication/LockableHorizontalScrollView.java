package com.example.sde2.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.ConcurrentModificationException;
import java.util.jar.Attributes;

public class LockableHorizontalScrollView extends HorizontalScrollView {

    public LockableHorizontalScrollView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    public LockableHorizontalScrollView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }

    public LockableHorizontalScrollView(Context context)
    {
        super(context);
    }

    //スクロールできるか
    private boolean mScrollable = true;

    public void setScrollingEnabled(boolean enabled){
        mScrollable=enabled;
    }

    public boolean isScrollable(){
        return mScrollable;
    }

    //自分のスクロールについて
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // スクロール可
                if (mScrollable) return super.onTouchEvent(ev);
                // スクロール不可
                return mScrollable; // ここでは常にfalse
            default:
                return super.onTouchEvent(ev);
        }
    }

    //子のスクロールについて
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // スクロール可
                if (mScrollable) return super.onInterceptTouchEvent(ev);
                // スクロール不可
                return mScrollable; // ここでは常にfalse
            default:
                return super.onInterceptTouchEvent(ev);
        }
    }
}
