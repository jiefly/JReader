package com.gao.jiefly.jieflysbooks.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.gao.jiefly.jieflysbooks.Model.AdvanceDataModel;
import com.gao.jiefly.jieflysbooks.Model.bean.Book;
import com.gao.jiefly.jieflysbooks.Model.bean.Chapter;
import com.gao.jiefly.jieflysbooks.Model.listener.OnDataModelListener;
import com.gao.jiefly.jieflysbooks.R;
import com.gao.jiefly.jieflysbooks.View.Main;

import java.util.ArrayList;
import java.util.List;

public class UpdateBookService extends Service implements OnDataModelListener {
    private AdvanceDataModel mAdvanceDataModel;
    private static final int DEFAULT_UPDATE_TIME = 600000;
    private int time;
    private UpdateBookThread mUpdateBookThread;
    private List<String> updatedBooks = new ArrayList<>();
    private List<Book> oldBooks;
    private int updateNum;
    public UpdateBookService() {
        mAdvanceDataModel = AdvanceDataModel.build(this,this);
        mUpdateBookThread = new UpdateBookThread();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        time = intent.getIntExtra("time",DEFAULT_UPDATE_TIME);
        if (mUpdateBookThread == null)
            mUpdateBookThread = new UpdateBookThread();
        if (!mUpdateBookThread.isAlive())
            mUpdateBookThread.start();
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
        Log.e("update num",updateNum+"old book size:"+oldBooks.size());
        if (updateNum == oldBooks.size())
            onBookUpdateCompleted();
    }

    @Override
    public void onBookUpdateFailed() {

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
        for (int i = 0 ; i< oldBooks.size();i++){
            if (newBooks.get(i).getUpdateDate().after(oldBooks.get(i).getUpdateDate())){
                updatedBooks.add(newBooks.get(i).getBookName());
            }
        }
        if (updatedBooks.size() >= 0){
            Log.e("completed","success");
            String title = "有小说更新";
            String content = "没有";
            if (updatedBooks.size() == 1){
                content = "《"+updatedBooks.get(0)+"》";
            }else if (updatedBooks.size() >1)
                content = "《"+updatedBooks.get(0)+"》"+"等"+updatedBooks.size()+"小说更新...";
            RemoteViews remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
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
            mNotificationManager.notify(1000, mBuilder.build());
        }
    }

    public class UpdateBookBinder extends Binder{

    }

    class UpdateBookThread extends Thread{
        @Override
        public void run() {
            //                十分钟检查一次更新
            try {
                updateNum = 0;
                oldBooks = mAdvanceDataModel.getBookList();
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAdvanceDataModel.updateAllBooks();
        }
    }
}
