package com.gao.jiefly.jieflysbooks.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.R;

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
    private TextView tvShowInfo;
    private ScrollView svWrapper;
    private Chapter mChapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        tvShowContent = (TextView) view.findViewById(R.id.id_fragment_tv);
        tvShowTitle = (TextView) view.findViewById(R.id.id_fragment_title_tv);
        tvShowInfo = (TextView) view.findViewById(R.id.id_fragment_info_tv);
        svWrapper = (ScrollView) view.findViewById(R.id.id_fragment_sv);
        return view;
    }

    @Override
    public void showChapter(Chapter chapter) {
        if (tvShowContent != null) {
            Observable.just(chapter)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Chapter>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("FragmentReader",e.getMessage());
                        }

                        @Override
                        public void onNext(Chapter chapter) {
                            mChapter = chapter;
                            svWrapper.scrollTo(0, 0);
                            tvShowContent.setText(chapter.getContent());
                            tvShowTitle.setText(chapter.getTitle());
                        }
                    });
        }
    }

    @Override
    public Chapter getChapter() {
        if (mChapter != null)
            return mChapter;
        return null;
    }


}
