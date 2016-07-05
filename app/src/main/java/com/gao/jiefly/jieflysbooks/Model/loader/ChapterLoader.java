package com.gao.jiefly.jieflysbooks.Model.loader;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;

import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.download.BaseHttpURLClient;
import com.gao.jiefly.jieflysbooks.Utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiefly on 2016/6/30.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class ChapterLoader {
    private static final String TAG = "ChapterLoader";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 60;
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 512;
    private static Configuration mConfiguration;
    //    如果没有配置的话，默认开启磁盘缓存，关闭内存缓存
    private boolean isNeedCacheInMemory = true;
    private boolean isNeedCacheInDisk = true;
    private boolean mIsDiskCacheCreated = false;
    //    小说章节内存缓存
    private LruCache<String, Chapter> mMemoryCache;
    //    小说章节磁盘缓存
    private DiskLruCache mDiskCache;

    private Context mContext;

    public static ChapterLoader build(Context context) {
        return new ChapterLoader(context);
    }

    public static ChapterLoader build(Context context, Configuration configuration) {
        mConfiguration = configuration;
        return new ChapterLoader(context);
    }

    //    获取每一章节的具体内容
    public Chapter getChapterLoaderResult(String url) throws IOException {
        Chapter result;
//        尝试从内存中获取
        result = getChapterFromMemoryCache(url);
        if (result != null)
            return result;
//        尝试从磁盘中获取
        result = loadChapterFromDiskCache(url);
        if (result != null) {
            if (isNeedCacheInMemory)
                addChapterToMemoryCache(url, result);
            return result;
        }
//        直接从网络上下载
        result = getChapterFromHttp(url);
        if (isNeedCacheInMemory)
            addChapterToMemoryCache(url, result);
        if (isNeedCacheInDisk)
            addChapterToDiskCache(url, result);
        return result;
    }

    private Chapter getChapterFromHttp(String url) {
        String result = getChapterStringFromHttp(url);
        Chapter chapter = new Chapter(url);
        chapter.setContent(result);
        return chapter;
    }

    private String getChapterStringFromHttp(String url) {
        StringBuilder sb = new StringBuilder();
        String result;
        try {
            result = new BaseHttpURLClient().getWebResourse(new URL(url));
            String tmp = Utils.delHTMLTag(result);
            Pattern p = Pattern.compile("下一章书签([\\w\\W]*)推荐上一章");
            final Matcher m = p.matcher(tmp);
            if (m.find())
                Log.e("jiefly---", m.group(1));
            sb.append(m.group(1));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private ChapterLoader(Context context) {
        mContext = context.getApplicationContext();
//        最大缓存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 2014);
//        内存缓存大小为当前程序最大缓存的八分之一
        int cacheSize = maxMemory / 8;
//        内存缓存
        mMemoryCache = new LruCache<>(cacheSize);
//        获取磁盘缓存文件夹地址
        File diskCacheDir = Utils.getDiskCacheDir(mContext, "book");
        Log.i(TAG, diskCacheDir.getAbsolutePath());
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdir();
        }
        try {
            mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
            mIsDiskCacheCreated = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mConfiguration != null) {
            isNeedCacheInDisk = mConfiguration.isNeedDiskCache();
            isNeedCacheInMemory = mConfiguration.isNeedMemoryCache();
        }
    }

    //    向磁内存缓存中添加内容
    private void addChapterToMemoryCache(String url, Chapter chapter) {
        String key = hashKeyForUrl(url);
        if (mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, chapter);
        }
    }

    //    获取内存缓存中的内容
    private Chapter getChapterFromMemoryCache(String url) {
        String key = hashKeyForUrl(url);
        return mMemoryCache.get(key);
    }

    //    向磁盘缓存中添加内容
    private void addChapterToDiskCache(String url, Chapter chapter) throws IOException {
        String key = hashKeyForUrl(url);
        DiskLruCache.Editor editor = mDiskCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (chapterToStream(chapter, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskCache.flush();
        }
    }

    private boolean chapterToStream(Chapter chapter, OutputStream outputStream) {
        BufferedOutputStream out;
        String result = chapter.getContent();
        out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
        byte[] bytes = result.getBytes();
        try {
            for (byte b : bytes) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //    获取磁盘缓存中的内容
    private Chapter loadChapterFromDiskCache(String url) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load chapter form UI Thread,is's not recommended");
        }
        if (!mIsDiskCacheCreated) {
            return null;
        }
        Chapter chapter = null;
        String key = hashKeyForUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
        if (snapshot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            chapter = new BaseEncoder().encode(url, fileInputStream);
        }
        return chapter;
    }

    private String hashKeyForUrl(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    class Configuration {
        private boolean isNeedMemoryCache;

        public boolean isNeedDiskCache() {
            return isNeedDiskCache;
        }

        public void setNeedDiskCache(boolean needDiskCache) {
            isNeedDiskCache = needDiskCache;
        }

        public boolean isNeedMemoryCache() {
            return isNeedMemoryCache;
        }

        public void setNeedMemoryCache(boolean needMemoryCache) {
            isNeedMemoryCache = needMemoryCache;
        }

        private boolean isNeedDiskCache;

        public Configuration(boolean isNeedMemoryCache, boolean isNeedDiskCache) {
            this.isNeedMemoryCache = isNeedMemoryCache;
            this.isNeedDiskCache = isNeedDiskCache;
        }
    }
}
