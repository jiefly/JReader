package com.gao.jiefly.jieflysbooks.Model.loader;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by jiefly on 2016/6/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface Encoder {
    Chapter encode(String url, FileInputStream inputStream) throws IOException;
}
