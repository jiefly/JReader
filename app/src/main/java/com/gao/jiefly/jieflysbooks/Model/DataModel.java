package com.gao.jiefly.jieflysbooks.Model;

import rx.Observable;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
interface DataModel {
    void getBookSuscribe(String url);
    Observable<String> getBookTopic(String url);
}
