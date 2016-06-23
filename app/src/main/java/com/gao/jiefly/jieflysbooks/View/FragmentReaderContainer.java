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

import com.gao.jiefly.jieflysbooks.Model.Book;
import com.gao.jiefly.jieflysbooks.Model.DataModelImpl;
import com.gao.jiefly.jieflysbooks.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiefly on 2016/6/23.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
@SuppressLint("ValidFragment")
public class FragmentReaderContainer extends Fragment implements ViewPager.OnPageChangeListener {
    FragmentReaderImpl[] mFragments = new FragmentReaderImpl[3];
    Book mBook;
    DataModelImpl mDataModel;
    Observable mObservable = null;

    @SuppressLint("ValidFragment")
    public FragmentReaderContainer(Book book) {
        mBook = book;
    }

    public FragmentReaderContainer(){
        this(new Book());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_container, container, false);
        FragmentReaderImpl fragmentReader = new FragmentReaderImpl();
        mFragments[0] = fragmentReader;
        mFragments[1] = fragmentReader;
        mFragments[2] = fragmentReader;
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.id_fragment_view_pager);
        viewPager.setAdapter(new CustomFragmentPagerAdapter(getChildFragmentManager(), mFragments));
        viewPager.addOnPageChangeListener(this);
        Observable.just(mBook.getBookNewTopicUrl())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (mDataModel != null)
                            mDataModel = new DataModelImpl(null);
                        assert mDataModel != null;
                        return mDataModel.getBookTopic(s);
                    }
                });
        return view;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        final int mPostion = position;
        if (mObservable != null) {
            mObservable.subscribe(new Action1() {
                @Override
                public void call(Object content) {
                    (mFragments[mPostion]).showContent((String) content);
                }
            });
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
        private FragmentReaderImpl[] mFragments;

        public CustomFragmentPagerAdapter(FragmentManager fm, FragmentReaderImpl[] mFragments) {
            super(fm);
            if (mFragments.length != 3) {
                Log.e("CustomFragmentAdapter", "Fragment size must be 3");
            } else
                this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragments[position % 3];
            Log.e("jiefly","ooo");
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }
}
