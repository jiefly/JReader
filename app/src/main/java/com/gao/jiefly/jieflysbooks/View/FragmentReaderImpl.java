package com.gao.jiefly.jieflysbooks.View;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.R;

import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class FragmentReaderImpl extends Fragment implements FragmentReader {
    private TextView tvShowContent;
    private TextView tvShowTitle;
    private TextView tvShowTime;
    private TextView tvShowPersent;
    private ScrollView svWrapper;
    private Chapter mChapter;
    private static final int ONE_MINUTES = 60000;

    public void setChapterSize(int chapterSize) {
        this.chapterSize = chapterSize;
    }
    private float testSize;
    private int chapterSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        tvShowContent = (TextView) view.findViewById(R.id.id_fragment_tv);
        tvShowTitle = (TextView) view.findViewById(R.id.id_fragment_title_tv);
        tvShowTime = (TextView) view.findViewById(R.id.id_fragment_time_tv);
        svWrapper = (ScrollView) view.findViewById(R.id.id_fragment_sv);
        tvShowPersent = (TextView) view.findViewById(R.id.id_fragment_persent_tv);
        setTextColor(getResources().getColor(((JieReader)getActivity()).getTextColorId()));
        handler.post(runnable);
        return view;
    }

    @Override
    public void showChapter(Chapter chapter) {
            Observable.just(chapter)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Chapter>() {
                        @Override
                        public void onCompleted() {
                            }

                        @Override
                        public void onError(Throwable e) {
//                            Log.e("FragmentReader", e.getMessage());
                        }

                        @Override
                        public void onNext(Chapter chapter) {
                            mChapter = chapter;
                            svWrapper.scrollTo(0, 0);
                            tvShowContent.setText(chapter.getContent());
                            tvShowTitle.setText(chapter.getTitle());
//                            Log.e("fragment", "content:" + mChapter.getContent());
                            tvShowPersent.setText(df.format((float) (chapter.getIndex() + 1) * 100 / (float) chapterSize) + "%");
                        }
                    });

    }

    java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

    @Override
    public Chapter getChapter() {
        if (mChapter != null)
            return mChapter;
        return null;
    }

    @Override
    public void addTextSize() {
        tvShowContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,tvShowContent.getTextSize() + 1);
    }

    @Override
    public void reduceTextSize() {
        tvShowContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,tvShowContent.getTextSize() - 1);
    }

    @Override
    public void setTextColor(int color) {
        if (tvShowPersent == null)
            return;
        if (color != 0){
            tvShowTitle.setTextColor(color);
            tvShowContent.setTextColor(color);
            tvShowPersent.setTextColor(color);
            tvShowTime.setTextColor(color);
        }
    }

    Date mDate = new Date();
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {

                handler.postDelayed(this, ONE_MINUTES);
                mDate.setTime(System.currentTimeMillis());
                foMateTime();
                tvShowTime.setText(mHour + ":" + mMinutes);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception...");
            }
        }
    };

    private void foMateTime() {
        if (mDate.getHours() > 9)
            mHour = String.valueOf(mDate.getHours());
        else
            mHour = "0" + mDate.getHours();
        if (mDate.getMinutes() > 9)
            mMinutes = String.valueOf(mDate.getMinutes());
        else
            mMinutes = "0" + mDate.getMinutes();
    }

    String mHour;
    String mMinutes;
}
