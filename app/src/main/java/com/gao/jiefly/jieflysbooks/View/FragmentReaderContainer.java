package com.gao.jiefly.jieflysbooks.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gao.jiefly.jieflysbooks.Model.BaseDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
@SuppressLint("ValidFragment")
public class FragmentReaderContainer extends Fragment implements ViewPager.OnPageChangeListener {
    //    FragmentReaderImpl[] mFragments = new FragmentReaderImpl[3];
    List<FragmentReaderImpl> mFragmentReaderList;
    Book mBook;
    BaseDataModel mDataModel;
    Observable mObservable = null;
    ViewPager viewPager;
    {
        mFragmentReaderList = new ArrayList<>();
        mFragmentReaderList.add(new FragmentReaderImpl());
        mFragmentReaderList.add(new FragmentReaderImpl());
        mFragmentReaderList.add(new FragmentReaderImpl());
    }


    public void setBook(Book book) {
        mBook = book;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_container, container, false);
         viewPager = (ViewPager) view.findViewById(R.id.id_fragment_view_pager);
        viewPager.setAdapter(new CustomFragmentPagerAdapter(getChildFragmentManager()));
        viewPager.addOnPageChangeListener(this);

        return view;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(final int position) {
        Observable.just(mBook.getBookNewTopicUrl())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (mDataModel == null)
                            mDataModel = new BaseDataModel(null);
                        return mDataModel.getBookChapter(s);
                    }
                }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("jiefly",e.getMessage());
            }

            @Override
            public void onNext(String s) {
                mFragmentReaderList.get(position).showContent(s);
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentReaderList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentReaderList.size();
        }
    }
}
