package com.gao.jiefly.jieflysbooks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.Book;
import com.gao.jiefly.jieflysbooks.Model.CustomDatabaseHelper;
import com.gao.jiefly.jieflysbooks.Model.DataModelImpl;
import com.gao.jiefly.jieflysbooks.Model.onDataStateListener;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements onDataStateListener {

    @InjectView(R.id.id_test)
    TextView mIdTest;
    @InjectView(R.id.button)
    Button mButton;
    @InjectView(R.id.editText)
    EditText mEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

    }

    DataModelImpl dataModel;

    @OnClick(R.id.button)
    public void onClick() {
        String s = mEditText.getText().toString();
        dataModel = new DataModelImpl(this);
        dataModel.getBookSuscribe(s);


        /*new DataModelImpl(new onDataStateListener() {
            @Override
            public void onSuccess(String result) {
                Observable.just(result)
                        .map(new Func1<String, Map<Integer, String>>() {
                            @Override
                            public Map<Integer, String> call(String s) {
                                Pattern p = Pattern.compile("<title>(.*?)</title>");
                                Matcher m = p.matcher(s);
                                if (m.find()) {
                                    Pattern pattern = Pattern.compile(".*?搜索.*?");
                                    Map<Integer, String> result = new HashMap<Integer, String>();
                                    if (pattern.matcher(m.group()).find()) {
//                                        1：搜索结果
//                                        0：直接抵达小说页面
                                        result.put(1, s);
                                    } else {
                                        result.put(0, s);
                                    }
                                    return result;
                                }
                                return null;
                            }
                        })
                        .map(new Func1<Map<Integer, String>, String>() {
                            @Override
                            public String call(Map<Integer, String> integerStringMap) {
                                if (integerStringMap.containsKey(1))
                                    return integerStringMap.get(1);
                               return
                            }
                        })
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                Pattern p = Pattern.compile("<div class=\"list-lastupdate\"(.*?)<li>(.*?)</li>");
                                Matcher m = p.matcher(s);
                                if (m.find())
                                    return m.group(2);
                                return null;
                            }
                        })
                        .map(new Func1<String, Book>() {
                            @Override
                            public Book call(String s) {
                                Pattern p = Pattern.compile("<span class=\"class\">(.*?)</span><span class=\"name\"><a href=\"(.*?)\">(.*?)</a><small> / <a href=\"(.*?)\">(.*?)</a></small></span><span class=\"other\">(.*?)<small>14012K</small><small>(.*?)</small><small>(.*?)</small></span>");
                                Matcher m = p.matcher(s);
                                Book book = new Book();
                                if (m.find()) {
                                    book.setBookStyle(m.group(1));
                                    book.setBookUrl(m.group(2));
                                    book.setBookName(m.group(3));
                                    book.setBookNewTopicUrl(m.group(4));
                                    book.setBookNewTopicTitle(m.group(5));
                                    book.setBookAuthor(m.group(6));
                                    book.setBookLastUpdate(m.group(7));
                                    book.setBookStatu(m.group(8));
                                }
                                return book;
                            }
                        })
                        .map(new Func1<Book, String>() {
                            @Override
                            public String call(Book book) {
                                return book.toString();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                mIdTest.setText(s);
                                Log.e("jielf", s);
                            }
                        });*/
                /*Observable.just(result)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
//                                Pattern p = Pattern.compile("id=\"newscontent\"(.*?)</div>");
                                Pattern p = Pattern.compile("<ul class=\"clrfix\">(.*?)</ul>");
                                Matcher m = p.matcher(s);
                                while (m.find())
                                    return m.toMatchResult().group(1);
                                return s;
                            }
                        })
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String s) {
                                Pattern pattern = Pattern.compile("li>(.*?)</li>");
                                Matcher m = pattern.matcher(s);
                                List<String> list = new ArrayList<>();
                                while (m.find())
                                    list.add(m.group(1)+"\n");
                                Log.e("jiefly", list.size()+"");
                                return Observable.from(list);
                            }
                        })
                        .flatMap(new Func1<String, Observable<Book>>() {
                            @Override
                            public Observable<Book> call(String s) {
                                Pattern pattern = Pattern.compile("<span class=\"class\">(.*?)</span><span class=\"name\"><a href=\"(.*?)\">(.*?)</a><small> <a href=\"(.*?)\">(.*?)</a></small></span><span class=\"other\">(.*?)<small>(.*?)</small><i>(.*?)</i></span>");
                                Matcher m = pattern.matcher(s);
                                List<Book> list = new ArrayList<>();
                                while (m.find()){
                                    Book book = new Book();
                                    book.setBookStyle(m.group(1));
                                    book.setBookUrl(m.group(2));
                                    book.setBookName(m.group(3));
                                    book.setBookNewTopicUrl(m.group(4));
                                    book.setBookNewTopicTitle(m.group(5));
                                    book.setBookAuthor(m.group(6));
                                    list.add(book);
                                }
//                               list.add(m.group(1)+":"+m.group(2)+"\n");
//                                Log.e("jiefly", list.size()+"");
                                return Observable.from(list);
                            }
                        })
                        .subscribe(new Subscriber<Book>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Book book) {
                                mIdTest.append(book.toString()
                                );
                            }
                        });*/
    }

    @Override
    public void onSuccess(Book result) {
        Log.d("jiefly", "success");
        final String s = dataModel.getBookTopic(result.getBookNewTopicUrl());
        CustomDatabaseHelper databaseHelper = new CustomDatabaseHelper(getApplicationContext(),"bookStore.db",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete("Book","author = ?",new String[]{"辰东"});
        /*ContentValues contentValues = new ContentValues();
        contentValues.put("author",result.getBookAuthor());
        contentValues.put("name",result.getBookName());
        contentValues.put("recentTopic",result.getBookNewTopic());
        contentValues.put("recentTopicUrl",result.getBookNewTopicUrl());
        contentValues.put("bookUrl",result.getBookUrl());
        db.insert("Book",null,contentValues);
*/
        /*
        * 查询数据
        * */
        Cursor cursor = db.query("Book",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String author = cursor.getString(cursor.getColumnIndex("author"));
                Log.e("jiefly----db",author);
            }while (cursor.moveToNext());
        }
//        mIdTest.setText(s);
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(s);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mIdTest.setText(s);
                    }
                });
    }

    @Override
    public void onFailed() {

    }

            /*@Override
            public void onFailed() {

            }
        }).getBookSuscribe("黑铁之堡");*/


}

                /*OkHttpClient okHttpClient = new OkHttpClient();
                Response response = null;
                Request request = new Request.Builder().url("http://www.uctxt.com/book/0/763/").build();
                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        System.out.printf(new String(response.body().bytes(),"UTF-8"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                /*try {
                    StringBuilder sb = new StringBuilder();
                    URL url = new URL("http://www.biquge.la/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"gbk"));

                    String lines;
                    while ((lines = bufferedReader.readLine())!=null)
                        sb.append(lines);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
//        new HttpResult();
       /* Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient okHttpClient = new OkHttpClient();
                Response response = null;
                Request request = new Request.Builder().url("http://www.csdn.net/").build();
                try {
                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        subscriber.onNext(new String(response.body().bytes(),"UTF-8"));
                        Log.e("jiiie",new String(response.body().bytes()));
                    }
                    else{
                        subscriber.onError(new Throwable("did not get data"));
                        Log.e("jiiie","no data");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e("jiefly", "complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("jiefly", e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        mIdTest.setText(s);
                        Log.e("jiefly", s);
                    }
                });*/

class WebCon {
    public String getWebCon(String pageURL, String encoding) {
        StringBuffer sb = new StringBuffer();
        GZIPInputStream gzip_in = null;
        byte[] buf = new byte[1024];
        try {
            URL url = new URL(pageURL);
            //获得连接的编码形式！
            URLConnection uc = url.openConnection();
            System.err.println("--------------编码为 ：" + uc.getContentEncoding() + "--------");
            gzip_in = new GZIPInputStream(new BufferedInputStream(
                    url.openStream()));
            int num;
            while ((num = gzip_in.read(buf, 0, buf.length)) != -1) {
                sb.append(new String(buf, encoding));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}


