package com.gao.jiefly.jieflysbooks.Utils;

import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.loader.BookLoader;
import com.gao.jiefly.jieflysbooks.Model.loader.ChapterLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiefly on 2016/8/11.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class LocalBookSegmentation {
    private LocalBookSegmentation instance = null;
    public static final String TITLE = "第(.*?)章";
    private final List<String> NUM;
    Pattern mPattern;
    Matcher mMatcher;
    StringBuilder sb;
    private String bookName;
    private ChapterLoader mChapterLoader;
    private BookLoader mBookLoader;
    List<Integer> chapterPosition;
    private Book mBook;
    private int wordConut;
    List<Chapter> mChapterList;

    private LocalBookSegmentation() {
        mPattern = Pattern.compile(TITLE);
        NUM = initTestArray();
        sb = new StringBuilder();
        mBook = new Book();
        mChapterList = new ArrayList<>();
        mChapterLoader = ChapterLoader.build(ApplicationLoader.applicationContext);
        mBookLoader = BookLoader.build(ApplicationLoader.applicationContext);
        /*try {
            for (int i = 1 ; i< 900;i++) {
//                mChapterLoader.removeChapter("超凡者游戏" + i);
                Chapter chapter = mChapterLoader.getChapterLoaderResult("超凡者游戏" + i);
                Log.e("chapter", chapter.getContent().substring(0,10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static LocalBookSegmentation build() {
        return new LocalBookSegmentation();
    }

    private List<String> initTestArray() {
        List<String> tmp = new ArrayList<>();
        tmp.add("一");
        tmp.add("二");
        tmp.add("三");
        tmp.add("四");
        tmp.add("五");
        tmp.add("六");
        tmp.add("七");
        tmp.add("八");
        tmp.add("九");
        tmp.add("十");
        tmp.add("零");
        tmp.add("百");
        tmp.add("千");
        tmp.add("万");
        return tmp;
    }


    public boolean LocalBook2CachedBook(File file, String bookName) {
        boolean result = LocalBook2CachedBook(file);
        if (bookName != null)
            this.bookName = bookName;
        mBook.setBookName(bookName);
        return result;
    }

    public boolean LocalBook2CachedBook(File file) {
//        只处理txt文件
        if (!file.getName().endsWith(".txt"))
            return false;
        bookName = file.getName().replace(".txt", "");
        mBook.setBookName(bookName);
        chapterPosition = new LinkedList<>();
        BufferedReader mReader = null;
        try {
            mReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
            String line;
            int position = 0;
            Chapter chapter = null;
            String title = "unkonw";
            while ((line = mReader.readLine()) != null) {
                wordConut += line.length();
                if (isContainChapterTitle(line)) {
                    chapterPosition.add(position);
                    if (chapterPosition.size() > 1) {
                        chapter = new Chapter(title, bookName, chapterPosition.size() - 1, "jieLocal:" + bookName + (chapterPosition.size() - 1));
                        chapter.setContent(sb.toString());
                        mChapterList.add(chapter);
                        mChapterLoader.addChapterToDiskCache(chapter.getUrl(), chapter);
//                    Log.e("getBookChapterPosition", sb.toString());
                        sb.delete(0, sb.length());
                    }
                    title = line;
                }
                sb.append(line).append("\n");
                position++;
            }
//            如果没有检测到任何标题，就将所有文字作为一个章节
            /*if (chapterPosition.size() == 0) {
                mBook.setBookNewTopicTitle(bookName);
                chapter = new Chapter(bookName, bookName, chapterPosition.size() - 1, bookName + (chapterPosition.size() - 1));
                chapter.setContent(sb.toString());
                mChapterList.add(chapter);
                mChapterLoader.addChapterToDiskCache(chapter.getUrl(), chapter);
            } else */
            int y, m, d, h, mi, s;
            Calendar cal = Calendar.getInstance();
            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH) + 1 ;
            d = cal.get(Calendar.DATE);
            h = cal.get(Calendar.HOUR_OF_DAY);
            mi = cal.get(Calendar.MINUTE);
            mBook.setBookLastUpdate(y + "-" + m + "-" + d + " " + (h>9?h:("0"+h)) + ":" + mi);
//            mBook.setBookNewTopicTitle(chapter.getTitle());
//            mBook.setBookNewTopicUrl(chapter.getUrl());
            mBook.setBookTotalWords(wordConut);
            mBook.setChapterList(mChapterList);
            mBook.setCached(true);
            mBook.setBookStatu("完本");
            mBook.setLocal(true);
            mBookLoader.addBookFromLocal(mBook);
            Log.e("getBookChapterPosition", "总字数：" + wordConut);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                mReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean isContainChapterTitle(String line) {
        //                不是以第开头的 判定为不是title
        if (!line.startsWith("第"))
            return false;
        mMatcher = mPattern.matcher(line);
        if (mMatcher.find()) {
            String tmp;
            for (int i = 0; i < mMatcher.groupCount(); i++) {
                tmp = mMatcher.group(i);

//            去除 第 和 章
                tmp = tmp.substring(1, tmp.length() - 1);
                if (!isConsistOfNum(tmp))
                    return false;
            }
            return true;
        }
        return false;
    }

    private boolean isConsistOfNum(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)) && !NUM.contains(s.substring(i, i + 1)))
                return false;
        }
        return true;
    }

    private Chapter LoadChapterFromLocalBook(String bookName, int chapterPosition) {

        return null;
    }

}
