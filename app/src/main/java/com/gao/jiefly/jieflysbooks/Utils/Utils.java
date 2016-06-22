package com.gao.jiefly.jieflysbooks.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    /*
    * 将<br>转换为\n
    * */
    private static final String REGEX_BR = "<br />";
    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /**
     * 定义HTML标签的正则表达式
     */
    private static final String REGEX_HTML = "<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE = "\t|\r";
    /*
    * 定义html &nbsp; 空格
    * */
    private static final String REGEX_SPACE_NBSP = "&nbsp;";
    public static String delHTMLTag(String htmlStr) {
        //      替换<br>
        Pattern p_br = Pattern.compile(REGEX_BR);
        Matcher m_br = p_br.matcher(htmlStr);
        htmlStr = m_br.replaceAll("jieflywantaTab");
        // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");

        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
//        替换&nbsp;为" "
        Pattern p_space_nbsp = Pattern.compile(REGEX_SPACE_NBSP, Pattern.CASE_INSENSITIVE);
        Matcher m_space_nbsp = p_space_nbsp.matcher(htmlStr);
        htmlStr = m_space_nbsp.replaceAll(" ");

        Pattern p = Pattern.compile("jieflywantaTab");
        Matcher m = p.matcher(htmlStr);
        htmlStr = m.replaceAll("\n");
        return htmlStr.trim(); // 返回文本字符串
    }


}
