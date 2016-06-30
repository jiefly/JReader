package com.gao.jiefly.jieflysbooks.Model.loader;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import java.io.OutputStream;

/**
 * Created by jiefly on 2016/6/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public interface Decoder {
    OutputStream decode(Chapter chapter);
}
