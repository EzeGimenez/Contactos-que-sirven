package com.visoft.network.MainPageChats;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.visoft.network.MainActivity;
import com.visoft.network.R;
import com.visoft.network.Util.Constants;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference ds = FirebaseDatabase.getInstance().getReference();
            ds.child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                    .child(user.getUid())
                    .child("instanceID").setValue(s);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("unreadMessages", true).commit();

        Log.e("AAAAAA", "isRunningv" + MainActivity.isRunning);
        if (!MainActivity.isRunning && !SpecificChatActivity.isRunning) {
            createNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
        } else {
            Intent intent = new Intent(MainActivity.RECEIVER_INTENT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void createNotification(String body, String title) {
        NotificationManager notifManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final int NOTIFY_ID = 1002;

        String name = Constants.NOTIFICATION_CHAT_CHANNEL_NAME;
        String id = Constants.NOTIFICATION_CHAT_CHANNEL_ID; // The user-visible name of the channel.
        String description = "New Message"; // The user-visible description of the channel.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setContentTitle(title)  // required
                .setSmallIcon(R.drawable.arrow_back) // required
                .setContentText(body)  // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(body);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setShowBadge(true);
                mChannel.setLightColor(Color.GREEN);
                notifManager.createNotificationChannel(mChannel);
            }

        } else {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
}
