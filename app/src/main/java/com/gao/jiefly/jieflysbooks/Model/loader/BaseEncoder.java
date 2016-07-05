package com.gao.jiefly.jieflysbooks.Model.loader;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jiefly on 2016/6/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BaseEncoder implements Encoder {

    @Override
    public Chapter encode(String url, FileInputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            content.append(line).append("\n");
        }
        Chapter chapter = new Chapter(url);
        chapter.setContent(content.toString());
        return chapter;
    }
}
