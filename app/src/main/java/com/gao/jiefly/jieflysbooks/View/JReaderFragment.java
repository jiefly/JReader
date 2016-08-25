package com.gao.jiefly.jieflysbooks.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Present.PresentReader;
import com.gao.jiefly.jieflysbooks.R;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jiefly on 2016/8/25.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class JReaderFragment extends Fragment implements FragmentReader {
    private static final String TAG = "JReaderFragment";
    Chapter mChapter;
    PresentReader mPresentReader;
    private TextView tvShowContent;
    private TextView tvShowTitle;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public JReaderFragment() {

    }

    public JReaderFragment(Chapter chapter, PresentReader reader) {
        mChapter = chapter;
        mPresentReader = reader;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        tvShowContent = (TextView) view.findViewById(R.id.id_fragment_tv);
        tvShowTitle = (TextView) view.findViewById(R.id.id_fragment_title_tv);
        return view;
    }

    /*
    * 当该fragment被缓存的时候就加载chapterContent
    * */

    @Override
    public void onResume() {
        super.onResume();
        if (mTimer == null)
            mTimer = new Timer();
        if (mTimerTask == null)
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mPresentReader.showProgressDialog();
                }
            };
        showChapter(mChapter);
        Log.e(TAG, "onresume");
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

    @Override
    public Chapter getChapter() {
        return null;
    }

    @Override
    public void addTextSize() {

    }

    @Override
    public void reduceTextSize() {

    }

    @Override
    public void setTextColor(int color) {

    }
}
