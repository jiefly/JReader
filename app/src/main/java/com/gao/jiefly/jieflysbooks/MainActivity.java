package com.gao.jiefly.jieflysbooks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.gao.jiefly.jieflysbooks.Model.DataModelImpl;
import com.gao.jiefly.jieflysbooks.Model.onDataStateListener;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.id_test)
    TextView mIdTest;
    @InjectView(R.id.button)
    Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        String search = Utils.UrlEncoder("完美世界");

    }

    @OnClick(R.id.button)
    public void onClick() {
        new DataModelImpl(new onDataStateListener() {
            @Override
            public void onSuccess(String result) {
                Observable.just(result)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                Pattern p = Pattern.compile("id=\"newscontent\"(.*?)</div>");
                                Matcher m = p.matcher(s);
                                while (m.find())
                                    return m.toMatchResult().group(1);
                                return null;
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
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String s) {
                                Pattern pattern = Pattern.compile("span>(.*?)</span>");
                                Matcher m = pattern.matcher(s);
                                List<String> list = new ArrayList<>();
                                while (m.find())
                                    list.add(m.group(1)+"\n");
//                                Log.e("jiefly", list.size()+"");
                                return Observable.from(list);
                            }
                        })
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String s) {
                                mIdTest.append(s+"\n");
                            }
                        });
            }

            @Override
            public void onFailed() {

            }
        }).getDate("");


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

    class WebCon{
        public String getWebCon(String pageURL,String encoding) {
            StringBuffer sb = new StringBuffer();
            GZIPInputStream gzip_in = null;
            byte[] buf = new byte[1024];
            try {
                URL url = new URL(pageURL);
                //获得连接的编码形式！
                URLConnection uc = url.openConnection();
                System.err.println("--------------编码为 ：" + uc.getContentEncoding()+"--------");
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

}
