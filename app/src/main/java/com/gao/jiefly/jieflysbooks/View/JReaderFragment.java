package com.gao.jiefly.jieflysbooks.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Present.PresentReader;
import com.gao.jiefly.jieflysbooks.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by jiefly on 2016/8/25.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
@SuppressLint("ValidFragment")
public class JReaderFragment extends Fragment implements FragmentReader {
    private static final String TAG = "JReaderFragment";
    Chapter mChapter;
    PresentReader mPresentReader;
    private TextView tvShowContent;
    private TextView tvShowTitle;
    private int textColor;
    private TextView tvShowPresent;
    private TextView tvShowTime;
    private float present;
    private int textSize;
    private ScrollView mScrollView;
    java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

    public JReaderFragment() {
    }

    public JReaderFragment(Chapter chapter, PresentReader reader, int textColor, float present) {
        mChapter = chapter;
        this.textColor = textColor;
        this.present = present;
        mPresentReader = reader;
        textSize = mPresentReader.getReaderTextSize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        tvShowContent = (TextView) view.findViewById(R.id.id_fragment_tv);
        tvShowTitle = (TextView) view.findViewById(R.id.id_fragment_title_tv);
        tvShowTime = (TextView) view.findViewById(R.id.id_fragment_time_tv);
        tvShowPresent = (TextView) view.findViewById(R.id.id_fragment_persent_tv);
        mScrollView = (ScrollView) view.findViewById(R.id.id_fragment_sv);
        return view;
    }

    /*
    * 当该fragment被缓存的时候就加载chapterContent
    * */

    @Override
    public void onResume() {
        super.onResume();
        show();
    }

    protected void show() {
        textSize = mPresentReader.getReaderTextSize();
        showChapter(mChapter);
        tvShowContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvShowContent.getTextSize() + textSize);
        setTextColor(textColor);
        String value = df.format(present) + "%";
        setpresent(value);
        if (textSize != 0)
            setTextSize(textSize);
        String time = mPresentReader.getTime();
        if (time != null)
            setTime(time);
    }

    @Override
    public void showChapter(final Chapter chapter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setTvShowTitle("加载中...");
                Chapter result = mPresentReader.getCompleteChapter(mChapter);
                setTvShowTitle(chapter.getTitle());
                setTvShowContent(result.getContent());
            }
        }).start();
    }


    public void setTvShowTitle(String title) {
        Observable.just(title)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        tvShowTitle.setText(s);
                    }
                });
    }

    public void setTvShowContent(String content) {
        Observable.just(content)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        tvShowContent.setText(s);
                    }
                });
    }


    public void setpresent(String present) {
        Observable.just(present)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s != null)
                            tvShowPresent.setText(s);
                    }
                });
    }

    @Override
    public Chapter getChapter() {
        return null;
    }

    @Override
    public void setTextSize(int size) {
        textSize = size;
        if (tvShowContent != null)
            tvShowContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public void scrollDownToNextPage() {
        int currentY = mScrollView.getScrollY();
        final int height = mScrollView.getHeight() * 19 / 20;
        final int toY = currentY + height;
        Observable.just(toY)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return toY >= height;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mScrollView.scrollTo(0, integer);
                    }
                });
    }


    @Override
    public void addTextSize() {
        textSize++;
        if (tvShowContent != null) {
            tvShowContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvShowContent.getTextSize() + 1);
        }

    }

    @Override
    public void reduceTextSize() {
        textSize--;
        if (tvShowContent != null) {
            float size = tvShowContent.getTextSize();
            tvShowContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            tvShowContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvShowContent.getTextSize() - 1);
            size = tvShowContent.getTextSize();
        }
    }

    @Override
    public void setTextColor(int color) {
        if (color != 0)
            textColor = color;
        if (tvShowPresent == null)
            return;
        if (color != 0) {
            tvShowTitle.setTextColor(color);
            tvShowContent.setTextColor(color);
            tvShowPresent.setTextColor(color);
            tvShowTime.setTextColor(color);
        }
    }

    @Override
    public void setTime(String time) {
        Observable.just(time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s != null && tvShowTime != null)
                            tvShowTime.setText(s);
                    }
                });
    }
}
