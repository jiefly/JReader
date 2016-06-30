package com.gao.jiefly.jieflysbooks;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.loader.ChapterLoader;
import com.gao.jiefly.jieflysbooks.View.FragmentReaderContainer;

import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Book book = new Book();
        book.setBookNewTopicUrl("http://www.uctxt.com/book/1/1269/4662467.html");
        FragmentReaderContainer frc = new FragmentReaderContainer();
        frc.setBook(book);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.id_test_fragment,frc);
        transaction.commit();

    }
}
