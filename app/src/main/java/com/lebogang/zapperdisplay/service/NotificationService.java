package com.lebogang.zapperdisplay.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.TaskStackBuilder;

import com.lebogang.zapperdisplay.MainActivity;
import com.lebogang.zapperdisplay.R;

public class NotificationService extends Service {

    private NotificationManager nm;
    private int nmID = 100;
    Notification.Builder mBuilder;

    public NotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     */
    private void sendNotification() {
        Log.i("displayNotification()", "notification");

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle("Zapper");
        mBuilder.setContentText("New Data Entries");
        mBuilder.setSmallIcon(R.drawable.ic_import_export_black_24dp);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(false);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pi = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            nm.notify(nmID, mBuilder.build());
        }
    }
}
