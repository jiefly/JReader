package com.gao.jiefly.jieflysbooks.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class FragmentReaderImpl extends Fragment implements FragmentReader {
    private TextView tvShowContent;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        tvShowContent = (TextView) view.findViewById(R.id.id_fragment_tv);
        return view;
    }

    @Override
    public void showContent(String content) {
        if (tvShowContent != null){
            Observable.just(content)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            tvShowContent.setText(s);
                        }
                    });
        }
    }

    @Override
    public void showNextContent(String nextContent) {

    }

    @Override
    public void showPrevContent(String prevContent) {

    }
}