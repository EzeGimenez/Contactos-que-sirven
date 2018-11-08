package com.visoft.network.Util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.visoft.network.MainActivity;
import com.visoft.network.R;

/**
 * Created by Belal on 12/8/2017.
 */

public class NotificationManagerAdapter {

    private static NotificationManagerAdapter mInstance;
    private Context mCtx;

    private NotificationManagerAdapter(Context context) {
        mCtx = context;
    }

    public static synchronized NotificationManagerAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotificationManagerAdapter(context);
        }
        return mInstance;
    }

    public void displayNotification(String title, String body) {

        Intent intent = new Intent(mCtx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, Constants.NOTIFICATION_CHAT_CHANNEL_ID)
                .setSmallIcon(R.drawable.arrow_back)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(mCtx);

        mNotifyMgr.notify(1, mBuilder.build());

    }

}
