package com.example.sde2.myapplication;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;


public class ObservableScrollView extends ScrollView {

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    private ScrollViewListener mScrollViewListener = null;
    private int lasty=0;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defs) {
        super(context, attrs, defs);
    }

    public void setOnScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mScrollViewListener != null&&y!=lasty) {
            mScrollViewListener.onScrollChanged(this,x, y, oldx, oldy);
            lasty=y;
        }
    }
}
