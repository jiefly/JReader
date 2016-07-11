package com.gao.jiefly.jieflysbooks.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by jiefly on 2016/7/4.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class CustomViewPager extends ViewPager {
    private boolean isCanScroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void setCanScroll(boolean flag) {
        isCanScroll = flag;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isCanScroll)
            super.scrollTo(x, y);
    }
}
