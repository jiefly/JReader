package com.gao.jiefly.readerview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jiefly on 2016/7/20.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Text2Bitmap {
    private static final int DEFAULT_TEXT_SIZE = 40;
    private static final int DEFAULT_WORDE_MARGINH = 10;
    private static final int DEFAULT_TEXT_LINE_MATGINV = 10;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    //    所需图像宽度
    private int mBitmapWidth;
    //    所需图像高度
    private int mBitmapHeight;
    //    文字大小
    private int mTextSize;
    //    文字高度
    private int mTextHeight;
    //    行数
    private int mTextLines;
    //    字数
    private int mWordsInLine = 26;
    //    字距
    private int mWordMarginH;
    //    行距
    private int mTextLineMarginV;
    //    字体颜色
    private int mTextColor;
    //    图像背景
    private Bitmap mBitmapBackground;
    //    所需要转换的文本（小说的话最好以章节为单位）
    private BufferedReader mReader;
    //    生成的Bitmap
    private List<Bitmap> mBitmaps = new LinkedList<>();
    //    用于写字的paint
    private Paint textPaint;
    //    是否到最后一张图片
    private boolean isLastBitmap = false;

    private Canvas mCanvas;
    private char[] chapter = new char[1024];

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Text2Bitmap(Bitmap bitmapBackground, InputStream inputStream, Size size) {
        mBitmapWidth = size.getWidth();
        mBitmapHeight = size.getHeight();
        mBitmapBackground = bitmapBackground;
        checkAndChangeBitmapSize();
        mReader = new BufferedReader(new InputStreamReader(inputStream));
        init();
    }

    private void init() {
        mBitmaps.add(mBitmapBackground.copy(Bitmap.Config.ARGB_8888, true));
        mCanvas = new Canvas(mBitmaps.get(0));
        mCanvas.save();
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(DEFAULT_TEXT_COLOR);
        textPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextLineMarginV = DEFAULT_TEXT_LINE_MATGINV;
        mWordMarginH = DEFAULT_WORDE_MARGINH;
        mTextSize = DEFAULT_TEXT_SIZE;
        mWordsInLine = (mBitmapWidth - 2 * mWordMarginH) / mTextSize;
    }


    private void checkAndChangeBitmapSize() {
        int width = mBitmapBackground.getWidth();
        int height = mBitmapBackground.getHeight();

        if (height == mBitmapHeight && width == mBitmapWidth)
            return;

//        背景图尺寸大于所需要的图片，对背景图进行裁剪
        if (height > mBitmapHeight && width > mBitmapWidth) {
            mBitmapBackground = Bitmap.createBitmap(mBitmapBackground, (width - mBitmapWidth) / 2, (height - mBitmapHeight) / 2, mBitmapWidth, mBitmapHeight);
        } else {
//            背景图小于所需要的图片，对背景图先放大再裁剪
            Matrix matrix = calScalSize(width, height, mBitmapWidth, mBitmapHeight);
            mBitmapBackground = Bitmap.createBitmap(mBitmapBackground, (width - mBitmapWidth) / 2, (height - mBitmapHeight) / 2, mBitmapWidth, mBitmapHeight, matrix, false);
        }
    }

    private Matrix calScalSize(int dstWidth, int dstHeight, int srcWidth, int srcHeight) {
        int wScale = srcWidth / dstWidth;
        int hScale = srcHeight / dstHeight;
        int scale = Math.max(wScale, hScale);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return matrix;
    }


    public List<Bitmap> getBitmaps() throws IOException {
        calTextLines();
        String line;
        int y = mTextHeight;
        while ((line = mReader.readLine()) != null) {
            int length = line.length();
//            对于一句太长的话进行分割
            if (length > mWordsInLine) {
                //            一句话有几行
                int x = (int) Math.ceil(length / mWordsInLine);
                for (int i = 0; i <= x; i++) {
                    int start = i * mWordsInLine;
                    int end = (i + 1) * mWordsInLine > length ? length : (i + 1) * mWordsInLine;
                    String s = line.substring(start, end);
                    mCanvas.drawText(s, mWordMarginH, y, textPaint);
                    y += mTextHeight;
//                判断一段话是否超过了一页的内容
                    if (y > mBitmapHeight - mTextHeight / 2 - 2 * mTextLineMarginV) {
                        y = mTextHeight;
                        mBitmaps.add(mBitmapBackground.copy(Bitmap.Config.ARGB_8888, true));
                        mCanvas = new Canvas(mBitmaps.get(mBitmaps.size() - 1));
                    }
                }
                continue;
            } else {
                mCanvas.drawText(line, mWordMarginH, y, textPaint);
                y += mTextHeight;
            }
            if (y > mBitmapHeight - mTextHeight / 2 - 2 * mTextLineMarginV) {
                mBitmaps.add(mBitmapBackground.copy(Bitmap.Config.ARGB_8888, true));
                mCanvas = new Canvas(mBitmaps.get(mBitmaps.size() - 1));
                break;
            }
        }
        Log.e("bitmap num", mBitmaps.size() + "");
        return mBitmaps;
    }

    private void calTextLines() {
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        mTextHeight = (int) (metrics.descent - metrics.ascent + 2 + mTextLineMarginV);
        mTextLines = (int) Math.ceil(mBitmapHeight / (mTextHeight));
    }
}
