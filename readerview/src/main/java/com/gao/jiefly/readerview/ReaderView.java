package com.gao.jiefly.readerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Scroller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiefly on 2016/7/18.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class ReaderView extends View {
    private Bitmap currentBitmap;
    private Bitmap prevBitmap;
    private Bitmap nextBitmap;
    private Bitmap backgroundBitmap;

    private static final String CURRENT_BITMAP = "current";
    private static final String NEXT_BITMAP = "next";
    private static final String PREV_BITMAP = "prev";


    private Map<String, Bitmap> mBitmaps = new HashMap<>();

    private static final String TAG = "jiefly";
    boolean isPositionCenter = false;
    private int mWidth;
    private int mHeight;

    private Scroller mScroller = new Scroller(getContext());

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

    int[] mBackShadowColors;
    int[] mFrontShadowColors;

    GradientDrawable mBackShadowDrawableLR;
    GradientDrawable mBackShadowDrawableRL;
    GradientDrawable mFolderShadowDrawableLR;
    GradientDrawable mFolderShadowDrawableRL;

    GradientDrawable mFrontShadowDrawableHBT;
    GradientDrawable mFrontShadowDrawableHTB;
    GradientDrawable mFrontShadowDrawableVLR;
    GradientDrawable mFrontShadowDrawableVRL;

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
        mMaxLength = (float) Math.hypot(mWidth, mHeight);
        mPath0 = new Path();
        mPath1 = new Path();
        createDrawable();
        // ---------------------------------------
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(0xFFAAAAAA);

        currentBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(currentBitmap);
        Paint paint = new Paint();
        canvas.drawColor(Color.YELLOW);
        canvas.drawBitmap(currentBitmap, 0, 0, paint);

        nextBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(nextBitmap);
        canvas.drawColor(Color.GREEN);
        canvas.drawBitmap(nextBitmap, 0, 0, paint);

        prevBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(prevBitmap);
        canvas.drawColor(Color.RED);
        canvas.drawBitmap(prevBitmap, 0, 0, paint);

        mBitmaps.put(CURRENT_BITMAP, currentBitmap);
        mBitmaps.put(PREV_BITMAP, prevBitmap);
        mBitmaps.put(NEXT_BITMAP, nextBitmap);

        /*currentBitmap = decodeSampledBitmapFromResource(getResources(),R.drawable.currentbg,mWidth,mHeight);
        Log.e("size",currentBitmap.getWidth()+"-=-=-=-"+currentBitmap.getHeight());
        nextBitmap = decodeSampledBitmapFromResource(getResources(),R.drawable.nextbg,mWidth,mHeight);
        prevBitmap = decodeSampledBitmapFromResource(getResources(),R.drawable.prevbg,mWidth,mHeight);*/


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

    /*
    * 设置翻页事件监听器
    * */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    private OnPageChangeListener mOnPageChangeListener;

    /*
    * 获取降采样之后的bitmap
    * */
    private Bitmap decodeSampledBitmapFromResource(Resources res,
                                                   int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /*
    * 计算图片采样率
    * */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "origin, w= " + width + " h=" + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }

    /*
    * 计算各个点的值
    * */
    private void calcPoints() {
        mMiddleX = (mTouch.x + mCornerX) / 2;
        mMiddleY = (mTouch.y + mCornerY) / 2;

        mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);

        mBezierControl1.y = mCornerY;
        mBezierControl2.x = mCornerX;

        mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2;
        mBezierStart1.y = mCornerY;

        // 当mBezierStart1.x < 0或者mBezierStart1.x > mWidth时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouch.x > 0 && mTouch.x < mWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
                if (mBezierStart1.x < 0) {
                    Log.e("<0", mBezierStart1 + "");
                    mBezierStart1.x = mWidth - mBezierStart1.x;
                }

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

    /*
    * 绘制当前页
    * */
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

    private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap) {
        canvas.save();
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    /*
    * 绘制下一页和下一页的阴影(上一页)
    * */
    private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
        int leftx;
        int rightx;
        GradientDrawable mBackShadowDrawable;
        mPath1.reset();
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.lineTo(mCornerX, mCornerY);
        mPath1.close();

        mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
                - mCornerX, mBezierControl2.y - mCornerY));

        if (mIsRTandLB) {
            leftx = (int) (mBezierStart1.x);
            rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
            mBackShadowDrawable = mBackShadowDrawableLR;
        } else {
            leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
            rightx = (int) mBezierStart1.x;
            mBackShadowDrawable = mBackShadowDrawableRL;
        }


        Log.i("hmg", "leftx  " + leftx
                + "   rightx  " + rightx);


        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
                (int) (mMaxLength + mBezierStart1.y));
        Log.e("jiefly", "mMaxLength:" + mMaxLength);
        mBackShadowDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 创建阴影的GradientDrawable
     */
    private void createDrawable() {
        int[] color = {0x333333, 0xb0333333};
        mFolderShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, color);
        mFolderShadowDrawableRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
//        mFolderShadowDrawableRL.setBounds(0,0,100,100);
        mFolderShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, color);
        mFolderShadowDrawableLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowColors = new int[]{0xff111111, 0x111111};
        mBackShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowColors = new int[]{0x80111111, 0x111111};
        mFrontShadowDrawableVLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
        mFrontShadowDrawableVLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFrontShadowDrawableVRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
        mFrontShadowDrawableVRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHTB = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
        mFrontShadowDrawableHTB
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHBT = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
        mFrontShadowDrawableHBT
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    /**
     * 绘制当前页翻起的阴影
     */
    public void drawCurrentPageShadow(Canvas canvas) {
        int mShadowWidth = Math.min(35, (int) (mTouchToCornerDis / 6));

        double degree;
        if (mIsRTandLB) {
            degree = Math.PI
                    / 4
                    - Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x - mBezierControl1.x);
        } else {
            degree = Math.PI
                    / 4
                    - Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x - mBezierControl1.x);
        }
        // 翻起页阴影顶点与touch点的距离
        double d1 = (float) mShadowWidth * 1.414 * Math.cos(degree);
        double d2 = (float) mShadowWidth * 1.414 * Math.sin(degree);
        float x = (float) (mTouch.x + d1);
        float y;

        if (mIsRTandLB) {
            y = (float) (mTouch.y + d2);
        } else {
            y = (float) (mTouch.y - d2);
        }

        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.close();
        float rotateDegrees;
        canvas.save();

        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        int leftx;
        int rightx;
        GradientDrawable mCurrentPageShadow;
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl1.x);
            rightx = (int) mBezierControl1.x + mShadowWidth;
            mCurrentPageShadow = mFrontShadowDrawableVLR;
        } else {
            leftx = (int) (mBezierControl1.x - mShadowWidth);
            rightx = (int) mBezierControl1.x + 2;
            mCurrentPageShadow = mFrontShadowDrawableVRL;
        }

        rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
                - mBezierControl1.x, mBezierControl1.y - mTouch.y));
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
        mCurrentPageShadow.setBounds(leftx,
                (int) (mBezierControl1.y - mMaxLength), rightx,
                (int) (mBezierControl1.y));
        mCurrentPageShadow.draw(canvas);
        canvas.restore();

        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.close();
        canvas.save();
        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl2.y);
            rightx = (int) (mBezierControl2.y + mShadowWidth);
            mCurrentPageShadow = mFrontShadowDrawableHTB;
        } else {
            leftx = (int) (mBezierControl2.y - mShadowWidth);
            rightx = (int) (mBezierControl2.y + 2);
            mCurrentPageShadow = mFrontShadowDrawableHBT;
        }
        rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
                - mTouch.y, mBezierControl2.x - mTouch.x));
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
        float temp;
        if (mBezierControl2.y < 0)
            temp = mBezierControl2.y - mHeight;
        else
            temp = mBezierControl2.y;

        int hmg = (int) Math.hypot(mBezierControl2.x, temp);
        if (hmg > mMaxLength)
            mCurrentPageShadow
                    .setBounds((int) (mBezierControl2.x - mShadowWidth) - hmg, leftx,
                            (int) (mBezierControl2.x + mMaxLength) - hmg,
                            rightx);
        else
            mCurrentPageShadow.setBounds(
                    (int) (mBezierControl2.x - mMaxLength), leftx,
                    (int) (mBezierControl2.x), rightx);

        // Log.i("hmg", "mBezierControl2.x   " + mBezierControl2.x
        // + "  mBezierControl2.y  " + mBezierControl2.y);
        mCurrentPageShadow.draw(canvas);
        canvas.restore();
    }

    private boolean isNextPage = false;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFAAAAAA);

        if (mTouch.x < mWidth - 1 && mTouch.x > 1) {
            calcPoints();
            if (mBitmaps.get(CURRENT_BITMAP) != null)
                drawCurrentPageArea(canvas, mBitmaps.get(CURRENT_BITMAP), mPath0);
            if (isNextPage) {
                if (mBitmaps.get(NEXT_BITMAP) != null)
                    drawNextPageAreaAndShadow(canvas, mBitmaps.get(NEXT_BITMAP));
            } else {
                if (mBitmaps.get(PREV_BITMAP) != null)
                    drawNextPageAreaAndShadow(canvas, mBitmaps.get(PREV_BITMAP));
            }
            if (mBitmaps.get(CURRENT_BITMAP) != null)
                drawCurrentPageShadow(canvas);
        } else {
            if (mBitmaps.get(CURRENT_BITMAP) != null)
                drawCurrentPageArea(canvas, mBitmaps.get(CURRENT_BITMAP));
            else
                drawCurrentPageArea(canvas, backgroundBitmap);
        }
    }

    /*
    * 抬手后的动画
    * */
    public void startAnimation(int delayMillis) {
        int dx, dy;
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (isPositionCenter) {
            if (isNextPage) {
                dx = (int) (-mTouch.x);
                dy = (int) (mHeight - mTouch.y);
            } else {
                dx = (int) (mWidth - mTouch.x);
                dy = (int) (mHeight - mTouch.y);
            }
        } else {
            if (isNextPage) {
                if (!mIsRTandLB) {
                    dx = (int) (-mTouch.x);
                    dy = (int) (mHeight - mTouch.y);
                } else {
                    dx = (int) (-mTouch.x);
                    dy = (int) (-mTouch.y);
                }
            } else {
                if (!mIsRTandLB) {
                    dx = (int) (mWidth - mTouch.x);
                    dy = (int) (-mTouch.y);
                } else {
                    dx = (int) (mWidth - mTouch.x);
                    dy = (int) (mHeight - mTouch.y);
                    Log.e("dx,dy", dx + "-=-=-" + dy);
                }
            }

        }
//        if (mCornerX > 0) {
//            dx = -(int) (mWidth + mTouch.x);
//        } else {
//            dx = (int) (mWidth - mTouch.x + mWidth);
//        }
//        if (mCornerY > 0) {
//            dy = (int) (mHeight - mTouch.y);
//        } else {
//            dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
//        }
        mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
                delayMillis);
        isAnimFinish = false;
    }

    /*
    * 取消动画
    * */
    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            isAnimFinish = true;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mTouch.x = x;
            mTouch.y = y;
            Log.e("touch", x + "-=-" + y + "offset:" + (float) mScroller.timePassed() / 1200f);

//            翻页快动画结束的时候通知model更改数据
            if (mScroller.timePassed() > 0.7 * 1200) {
                if (!isChangingBitmap && isAnimFinish){
                    isChangingBitmap = true;
                    Log.e("jie", isNextPage + "<----isNextPage");
                    if (isNextPage)
                        mOnPageChangeListener.onNextPage(mCurrentPageIndex++);
                    else
                        mOnPageChangeListener.onPrevPage(mCurrentPageIndex--);
                }
            }
            if (mScroller.isFinished())
                isAnimFinish = true;
            postInvalidate();
        }
    }

    private boolean isChangingBitmap = false;
    private int mChapterPageNum;
    private int mCurrentPageIndex = 1;

    /*
    * 设置当前章节的页数
    * */
    public void setChapterPageNum(int num) {
        mChapterPageNum = num;
    }

    /**
     * 求解直线P1P2和直线P3P4的交点坐标
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
     * 计算拖拽点对应的拖拽脚
     */
    public void calcCornerXY(float x, float y) {
        if (x <= mWidth / 2) {
            mCornerX = 0;
            isNextPage = false;
        } else {
            mCornerX = mWidth;
            isNextPage = true;
        }
        if (y <= mHeight / 2)
            mCornerY = 0;
        else
            mCornerY = mHeight;

        mIsRTandLB = (mCornerX == 0 && mCornerY == mHeight) || (mCornerX == mWidth && mCornerY == 0);

    }
    private boolean isAnimFinish = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCanvas.drawColor(0xFFAAAAAA);
                Log.e("position", event.getX() + "--=-=--" + event.getY());
                if (event.getY() > mHeight / 3 && event.getY() < mHeight * 2 / 3) {
                    isPositionCenter = true;
                    mTouch.y = mHeight - 0.01f;
                } else {
                    mTouch.y = event.getY();
                    isPositionCenter = false;
                }
                isNextPage = event.getX() >= mWidth / 2;
                mTouch.x = event.getX();
                calcCornerXY(mTouch.x, mTouch.y);
                Log.e("jjiefluy", "+" + isPositionCenter);
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mCanvas.drawColor(0xFFAAAAAA);
                mTouch.x = event.getX();
                if (isPositionCenter)
                    mTouch.y = mHeight - 0.01f;
                else
                    mTouch.y = event.getY();
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawColor(0xFFAAAAAA);
                abortAnimation();
                startAnimation(1200);
//                mTouch.x = mCornerX;
//                mTouch.y = mCornerY;
                this.postInvalidate();
                break;
        }
        return true;
    }

    public void changeBitmaps(Bitmap bitmap, boolean isTurnNext) {
        if (isTurnNext) {
            mBitmaps.put(PREV_BITMAP, mBitmaps.get(CURRENT_BITMAP));
            mBitmaps.put(CURRENT_BITMAP, mBitmaps.get(NEXT_BITMAP));
            long time = System.currentTimeMillis();
            mBitmaps.put(NEXT_BITMAP, bitmap);
            Log.e("time", System.currentTimeMillis() - time + "");
        } else {
            mBitmaps.put(NEXT_BITMAP, mBitmaps.get(CURRENT_BITMAP));
            mBitmaps.put(CURRENT_BITMAP, mBitmaps.get(PREV_BITMAP));
            mBitmaps.put(PREV_BITMAP, bitmap);
        }
        isChangingBitmap = false;
        postInvalidate();
    }
}
