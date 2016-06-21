package com.gao.jiefly.jieflysbooks.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class HttpResult {
    public static final String BASE_RUL = "https://www.baidu.com";
    public String result;

    public HttpResult() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_RUL)
                .addConverterFactory(GsonConverterFactory.create(

                ))
                .build();
        BookService bookService = retrofit.create(BookService.class);
        bookService.ucResult("toplist").enqueue(new Callback<Observable<String>>() {
            @Override
            public void onResponse(Call<Observable<String>> call, Response<Observable<String>> response) {
                response.body().observeOn(Schedulers.io())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String s) {
                                Log.e("jiefly",s);
                            }
                        });
            }

            @Override
            public void onFailure(Call<Observable<String>> call, Throwable t) {
                Log.e("jiefly",t.getMessage());
            }
        });
        /*bookService.baiduSearchResult("s?wd=完美世界")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e("jiefly", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("jiefly",t.getMessage());
                    }
                });*/
    }
}
