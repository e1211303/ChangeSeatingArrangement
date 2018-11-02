package com.example.sde2.myapplication;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;


public class ObservableScrollView extends ScrollView {

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    private ScrollViewListener scrollViewListener = null;
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
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null&&y!=lasty) {
            scrollViewListener.onScrollChanged(this,x, y, oldx, oldy);
            lasty=y;
        }
    }
}
