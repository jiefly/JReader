package com.gao.jiefly.jieflysbooks.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
interface BookService {
    @GET("/{keyWords}")
    Call<String> baiduSearchResult(@Path("keyWords") String keyWords);
    @GET("/{position}/end")
    Call<Observable<String>> ucResult(@Path("position") String position);
}
