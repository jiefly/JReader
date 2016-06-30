package com.gao.jiefly.jieflysbooks;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.loader.ChapterLoader;

import java.io.IOException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        try {
            Chapter chapter = ChapterLoader.build(getContext()).getLoaderResult("http://www.uctxt.com/book/1/1269/392326.html");
            Log.d("test",chapter.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}