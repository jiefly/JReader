package com.gao.jiefly.jieflysbooks.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by jiefly on 2016/6/26.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class SlidingMenu extends HorizontalScrollView {
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    //dp
    private int mMenuRightPadding = 50;

    private int mMneuWidth;

   /* private int mLastX = 0;
    private int mLaseY = 0;

    private int mLastInterceptX = 0;
    private int mLastInterceptY = 0;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;*/

    private boolean isFirst = false;

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        //dp -> px
        mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMneuWidth, context.getResources().getDisplayMetrics());

       /* if (mScroller == null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }*/
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!isFirst) {
            isFirst = true;
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMneuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    //    通过设置偏移量，将menu隐藏
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed)
            this.scrollTo(mMneuWidth, 0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                //隐藏在左边的宽度
                int scrollX = getScrollX();
                if (scrollX > mMneuWidth / 2) {
                    this.smoothScrollTo(mMneuWidth, 0);
                } else {
                    this.smoothScrollTo(0, 0);
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }
}
