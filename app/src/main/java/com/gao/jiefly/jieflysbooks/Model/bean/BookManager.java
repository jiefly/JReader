package com.gao.jiefly.jieflysbooks.Model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jiefly on 2016/8/31.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class BookManager implements Parcelable {
    public static final Parcelable.Creator<BookManager> CREATOR = new Parcelable.Creator<BookManager>() {
        @Override
        public BookManager createFromParcel(Parcel source) {
            return new BookManager(source);
        }

        @Override
        public BookManager[] newArray(int size) {
            return new BookManager[size];
        }
    };
    private String name;
    private String author;
    private String lastUpdateStr;
    private Date lastUpdateDate;
    private String type;
    private String[] covers;
    private String statu;
    private boolean isLocal;
    private boolean isCached;
    private boolean hasUpdate;
    private int lastReadIndex;
    private String userChooseResourse;
    private int[] userMarkers;
    private List<Chapter> chapters = new ArrayList<>();
    private Map<String, String> resourse = new HashMap<>();

    private BookManager(Parcel in) {
        name = in.readString();
        author = in.readString();
        lastUpdateStr = in.readString();
        type = in.readString();
        in.readStringArray(covers);
        statu = in.readString();
        isCached = in.readInt() == 1;
        isLocal = in.readInt() == 1;
        hasUpdate = in.readInt() == 1;
        lastReadIndex = in.readInt();
        userChooseResourse = in.readString();
        in.readIntArray(userMarkers);
        in.readList(chapters, Chapter.class.getClassLoader());
        in.readMap(resourse, String.class.getClassLoader());
    }

    public BookManager() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(lastUpdateStr);
        dest.writeString(type);
        dest.writeStringArray(covers);
        dest.writeString(statu);
        dest.writeInt(isCached ? 1 : 0);
        dest.writeInt(isLocal ? 1 : 0);
        dest.writeInt(hasUpdate ? 1 : 0);
        dest.writeInt(lastReadIndex);
        dest.writeString(userChooseResourse);
        dest.writeIntArray(userMarkers);
        dest.writeList(chapters);
        dest.writeMap(resourse);
    }

    public static Creator<BookManager> getCREATOR() {
        return CREATOR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLastUpdateStr() {
        if (lastUpdateStr == null && lastUpdateDate != null)
            lastUpdateStr = lastUpdateDate.toString();
        return lastUpdateStr;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        if (this.lastUpdateDate != null && this.lastUpdateDate.after(lastUpdateDate))
            return;
        this.lastUpdateDate = lastUpdateDate;
    }

    public Date getLastUpdateDate() {
        if (lastUpdateDate == null
                && lastUpdateStr != null)
            lastUpdateDate = string2Date(lastUpdateStr);
        return lastUpdateDate;
    }

    public void setLastUpdateStr(String lastUpdateStr) {
        this.lastUpdateStr = lastUpdateStr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getCovers() {
        return covers;
    }

    public void setCovers(String[] covers) {
        this.covers = covers;
    }

    public void addCover(String cover) {
        if (covers != null) {
            covers[covers.length] = cover;
        }
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public int getLastReadIndex() {
        return lastReadIndex;
    }

    public void setLastReadIndex(int lastReadIndex) {
        this.lastReadIndex = lastReadIndex;
    }

    public String getUserChooseResourse() {
        return userChooseResourse;
    }

    public void setUserChooseResourse(String userChooseResourse) {
        this.userChooseResourse = userChooseResourse;
    }

    public int[] getUserMarkers() {
        return userMarkers;
    }

    public void setUserMarkers(int[] userMarkers) {
        this.userMarkers = userMarkers;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public Map<String, String> getResourse() {
        return resourse;
    }

    public void setResourse(Map<String, String> resourse) {
        this.resourse = resourse;
    }

    public void addResourse(String webName, String webUrl) {
        resourse.put(webName, webUrl);
    }

    private Date string2Date(String time) {
        if (time == null)
            return new Date();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = new Date();
        try {
            date = mSimpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
