package com.gao.jiefly.jieflysbooks.View;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.gao.jiefly.jieflysbooks.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ScanTxtView extends AppCompatActivity {

    @InjectView(R.id.id_sacn_txt_btn)
    Button mIdSacnTxtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_txt_view);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.id_sacn_txt_btn)
    public void onClick() {
        long time = System.currentTimeMillis();
        walkdir(Environment.getExternalStorageDirectory(),".txt");
        Log.e("scantxt", "扫描时间：" + (System.currentTimeMillis() - time));
    }

    public List<File> walkdir(File dir,String pattern) {
        String pdfPattern = pattern;
        List<File> result = new ArrayList<>();
        File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i],pattern);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
//                        只截取中文开头的文件
                        if (isChinese(listFile[i].getName().charAt(0))) {
                            //Do what ever u want
                            Log.e("scanTxt", "path:" + listFile[i].getAbsolutePath() + "name:" + listFile[i].getName());
                            result.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return result;
//        for (File file: listFile)
//            Log.e("scanTxt","path:"+file.getAbsolutePath()+"name:"+file.getName());
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }
}
