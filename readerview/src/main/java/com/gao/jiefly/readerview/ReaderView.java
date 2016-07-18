package com.gao.jiefly.readerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jiefly on 2016/7/18.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class ReaderView extends View {
    private Bitmap currentBitmap;
    private Bitmap prevBitmap;
    private Bitmap nextBitmap;

    private int mWidth;
    private int mHeight;

    private int mCornerX = 0; // 拖拽点对应的页脚
    private int mCornerY = 0;

    //    手指触控点和页脚的中点
    float mMiddleX;
    float mMiddleY;

    float mDegrees;
    float mTouchToCornerDis;

    private boolean mIsRTandLB; // 是否属于右上左下

    PointF mTouch = new PointF(); // 拖拽点

    PointF mBezierStart1 = new PointF(); // 贝塞尔曲线起始点
    PointF mBezierControl1 = new PointF(); // 贝塞尔曲线控制点
    PointF mBeziervertex1 = new PointF(); // 贝塞尔曲线顶点
    PointF mBezierEnd1 = new PointF(); // 贝塞尔曲线结束点

    PointF mBezierStart2 = new PointF(); // 另一条贝塞尔曲线
    PointF mBezierControl2 = new PointF();
    PointF mBeziervertex2 = new PointF();
    PointF mBezierEnd2 = new PointF();

    Path mPath1;
    Path mPath0;

    GradientDrawable mBackShadowDrawableLR;
    GradientDrawable mBackShadowDrawableRL;
    GradientDrawable mFolderShadowDrawableLR;
    GradientDrawable mFolderShadowDrawableRL;
    private float mMaxLength;


    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint;
    Paint paint;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReaderView(Context context, Size screenSize) {
        super(context);
        mWidth = screenSize.getWidth();
        mHeight = screenSize.getHeight();
        mPath0 = new Path();
        mPath1 = new Path();
//        createDrawable();
        // ---------------------------------------
        mBitmap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        currentBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(currentBitmap);
        Paint paint = new Paint();
        canvas.drawColor(Color.YELLOW);
        canvas.drawBitmap(currentBitmap, 0, 0, paint);

        nextBitmap = Bitmap
                .createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(nextBitmap);
        canvas.drawColor(Color.GREEN);
        canvas.drawBitmap(nextBitmap, 0, 0, paint);

//        mPaint = new Paint();
//        mPaint.setStyle(Paint.Style.FILL);
//
//        ColorMatrix cm = new ColorMatrix();
//        float array[] = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
//                0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
//        cm.set(array);
//        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
//        mMatrix = new Matrix();
//        mScroller = new Scroller(getContext());

        mTouch.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
        mTouch.y = 0.01f;
    }

    public ReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void calcPoints() {
        mMiddleX = (mTouch.x + mCornerX) / 2;
        mMiddleY = (mTouch.y + mCornerY) / 2;

        mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);

        mBezierControl1.y = mCornerY;
        mBezierControl2.x = mCornerX;

        mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

        // Log.i("hmg", "mTouchX  " + mTouch.x + "  mTouchY  " + mTouch.y);
        // Log.i("hmg", "mBezierControl1.x  " + mBezierControl1.x
        // + "  mBezierControl1.y  " + mBezierControl1.y);
        // Log.i("hmg", "mBezierControl2.x  " + mBezierControl2.x
        // + "  mBezierControl2.y  " + mBezierControl2.y);

        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2;
        mBezierStart1.y = mCornerY;

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouch.x > 0 && mTouch.x < mWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
                if (mBezierStart1.x < 0)
                    mBezierStart1.x = mWidth - mBezierStart1.x;

                float f1 = Math.abs(mCornerX - mTouch.x);
                float f2 = mWidth * f1 / mBezierStart1.x;
                mTouch.x = Math.abs(mCornerX - f2);

                float f3 = Math.abs(mCornerX - mTouch.x)
                        * Math.abs(mCornerY - mTouch.y) / f1;
                mTouch.y = Math.abs(mCornerY - f3);

                mMiddleX = (mTouch.x + mCornerX) / 2;
                mMiddleY = (mTouch.y + mCornerY) / 2;

                mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                        * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
                mBezierControl1.y = mCornerY;

                mBezierControl2.x = mCornerX;
                mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                        * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
                // Log.i("hmg", "mTouchX --> " + mTouch.x + "  mTouchY-->  "
                // + mTouch.y);
                // Log.i("hmg", "mBezierControl1.x--  " + mBezierControl1.x
                // + "  mBezierControl1.y -- " + mBezierControl1.y);
                // Log.i("hmg", "mBezierControl2.x -- " + mBezierControl2.x
                // + "  mBezierControl2.y -- " + mBezierControl2.y);
                mBezierStart1.x = mBezierControl1.x
                        - (mCornerX - mBezierControl1.x) / 2;
            }
        }
        mBezierStart2.x = mCornerX;
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
                / 2;

        mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
                (mTouch.y - mCornerY));

        mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
                mBezierStart2);
        mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
                mBezierStart2);

        // Log.i("hmg", "mBezierEnd1.x  " + mBezierEnd1.x + "  mBezierEnd1.y  "
        // + mBezierEnd1.y);
        // Log.i("hmg", "mBezierEnd2.x  " + mBezierEnd2.x + "  mBezierEnd2.y  "
        // + mBezierEnd2.y);

		/*
         * mBeziervertex1.x 推导
		 * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 化简等价于
		 * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
		 */
        mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
        mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
        mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
        mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 求解直线P1P2和直线P3P4的交点坐标
     */
    public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
        PointF CrossP = new PointF();
        // 二元函数通式： y=ax+b
        float a1 = (P2.y - P1.y) / (P2.x - P1.x);
        float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

        float a2 = (P4.y - P3.y) / (P4.x - P3.x);
        float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
        CrossP.x = (b2 - b1) / (a1 - a2);
        CrossP.y = a1 * CrossP.x + b1;
        return CrossP;
    }

    /**
     * Author : hmg25 Version: 1.0 Description : 计算拖拽点对应的拖拽脚
     */
    public void calcCornerXY(float x, float y) {
        if (x <= mWidth / 2)
            mCornerX = 0;
        else
            mCornerX = mWidth;
        if (y <= mHeight / 2)
            mCornerY = 0;
        else
            mCornerY = mHeight;
        if ((mCornerX == 0 && mCornerY == mHeight)
                || (mCornerX == mWidth && mCornerY == 0))
            mIsRTandLB = true;
        else
            mIsRTandLB = false;
    }


    private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
        mPath0.reset();
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
                mBezierEnd1.y);
        mPath0.lineTo(mTouch.x, mTouch.y);
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
                mBezierStart2.y);
        mPath0.lineTo(mCornerX, mCornerY);
        mPath0.close();

        canvas.save();
        canvas.clipPath(path, Region.Op.XOR);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
        mPath1.reset();
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.lineTo(mCornerX, mCornerY);
        mPath1.close();

        mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
                - mCornerX, mBezierControl2.y - mCornerY));
        float f5 = mTouchToCornerDis / 4;
        int leftx;
        int rightx;
        GradientDrawable mBackShadowDrawable;
        if (mIsRTandLB) {
            leftx = (int) (mBezierStart1.x - 1);
            rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4 + 1);
            mBackShadowDrawable = mBackShadowDrawableLR;
        } else {
            leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4 - 1);
            rightx = (int) mBezierStart1.x + 1;
            mBackShadowDrawable = mBackShadowDrawableRL;
        }

        Log.i("hmg", "leftx  " + leftx
                + "   rightx  " + rightx);
        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
//        mBackShadowDrawable.setBounds(leftx,
//                (int) mBezierStart1.y, rightx,
//                (int) (mMaxLength + mBezierStart1.y));
//        mBackShadowDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFAAAAAA);
        calcPoints();
        drawCurrentPageArea(mCanvas, currentBitmap, mPath0);
        drawNextPageAreaAndShadow(mCanvas, nextBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mCanvas.drawColor(0xFFAAAAAA);
            mTouch.x = event.getX();
            mTouch.y = event.getY();
            this.postInvalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mCanvas.drawColor(0xFFAAAAAA);
            mTouch.x = event.getX();
            mTouch.y = event.getY();
            calcCornerXY(mTouch.x, mTouch.y);
            this.postInvalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mCanvas.drawColor(0xFFAAAAAA);
            mTouch.x = mCornerX;
            mTouch.y = mCornerY;
            this.postInvalidate();
        }
        // return super.onTouchEvent(event);
        return true;
    }
}
