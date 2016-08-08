package com.gao.jiefly.jieflysbooks.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataModelListener;
import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.View.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateBookService extends Service implements OnDataModelListener {
    private AdvanceDataModel mAdvanceDataModel;
    private static final int DEFAULT_UPDATE_TIME = 600000;
    private int time;
    private static final long[] VIBRATES = {0, 1000, 1000, 1000};
    private boolean isUpdateBackground = false;
    private List<String> updatedBooks = new ArrayList<>();
    private List<Book> oldBooks;
    private int updateNum;
    private Timer mTimer;
    private TimerTask mTimerTask;
    int delayNum = 0;
    private boolean isNeedUpdate = true;
    private Binder mBinder;

    public UpdateBookService() {
        mAdvanceDataModel = AdvanceDataModel.build(this, this,OnDataModelListener.TYPE_SERVICE_LISTENER);
        mTimer = new Timer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new UpdateBookBinder();
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        if (mUpdateBookThread == null)
//            mUpdateBookThread = new UpdateBookThread();
//        mUpdateBookThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        time = intent.getIntExtra("time", DEFAULT_UPDATE_TIME);
        mTimerTask = new UpdateBookTimeTask();
        mTimer.schedule(mTimerTask, time, time);
        return Service.START_STICKY;
    }

    @Override
    public void onBookAddSuccess(Book book) {

    }

    @Override
    public void onBookAddFailed() {

    }

    @Override
    public void onBookUpdateSuccess(String bookName) {
        updateNum++;
        Log.e("update num", updateNum + "old book size:" + oldBooks.size());
        if (updateNum == oldBooks.size())
            onBookUpdateCompleted();
    }

    @Override
    public void onBookUpdateFailed() {
        isUpdateBackground = false;
        Log.e("UpdateBookService","update book failed");
    }

    @Override
    public void onBookRemoveSuccess() {

    }

    @Override
    public void onChapterLoadSuccess(Chapter chapter) {

    }

    @Override
    public void onBookUpdateCompleted() {

        List<Book> newBooks = mAdvanceDataModel.getBookList();
        for (int i = 0; i < oldBooks.size(); i++) {
            if (newBooks.get(i).getUpdateDate().after(oldBooks.get(i).getUpdateDate())) {
                updatedBooks.add(newBooks.get(i).getBookName());
            }
        }
        if (updatedBooks.size() >= 0) {
            Log.e("completed", "success");
            String title = "有小说更新";
            String content = "没有";
            if (updatedBooks.size() == 1) {
                content = "《" + updatedBooks.get(0) + "》";
            } else if (updatedBooks.size() > 1)
                content = "《" + updatedBooks.get(0) + "》" + "等" + updatedBooks.size() + "小说更新...";

//            ------------------------------------------------------------------------------------
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(title);
            builder.setContentText(content);
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_ALL);
            Intent mIntent = new Intent(getApplicationContext(), Main.class);
            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(mPendingIntent);
            builder.setVibrate(VIBRATES);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1000, builder.build());
//            ------------------------------------------------------------------------------------
            /*RemoteViews remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
            remoteView.setImageViewResource(R.id.id_notification_icon,R.mipmap.ic_launcher);
            remoteView.setTextViewText(R.id.id_notification_title,title);
            remoteView.setTextViewText(R.id.id_notification_content,content);
            Intent intent = new Intent(getApplicationContext(),Main.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat
                    .Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("new message");
            mBuilder.setAutoCancel(true);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setContent(remoteView);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1000, mBuilder.build());*/
        }

        isUpdateBackground = false;
    }

    public class UpdateBookBinder extends Binder {
        public void setIsNeedUpdate(boolean mIsNeedUpdate){
            isNeedUpdate = mIsNeedUpdate;
        }
    }

    class UpdateBookTimeTask extends TimerTask {

        @Override
        public void run() {
            if (!isNeedUpdate)
                return;
            //            如果当前正在更新中则推迟
            if (isUpdateBackground) {
                Log.e("thread", "正在更新，当前更新任务将被取消");
                delayNum++;
                if (delayNum > 5)
                    onBookUpdateFailed();
                return;
            }
            delayNum = 0;
            //                十分钟检查一次更新
            isUpdateBackground = true;
            updateNum = 0;
            oldBooks = mAdvanceDataModel.getBookList();
            mAdvanceDataModel.updateAllBooks();
        }
    }
}
