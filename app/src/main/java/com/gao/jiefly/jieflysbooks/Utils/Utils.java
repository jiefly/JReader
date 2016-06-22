package com.gao.jiefly.jieflysbooks.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by jiefly on 2016/6/21.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class Utils {
//    将文字转码
    public static String UrlEncoder(String value,String charsetName){
        String reslut = null;
        try {
            reslut = URLEncoder.encode(value,charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return reslut;
    }
//    将文字解码
    public static String UrlDecoder(String value){
        String result = null;
        try {
            result = URLDecoder.decode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }




}
