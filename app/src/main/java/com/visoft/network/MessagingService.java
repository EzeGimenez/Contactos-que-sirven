package com.visoft.network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.NotificationManagerAdapter;

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
            Toast.makeText(this, "Changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("unreadMessages", true).commit();
        if (!MainActivity.isRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel(Constants.NOTIFICATION_CHAT_CHANNEL_ID,
                                Constants.NOTIFICATION_CHAT_CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_HIGH);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationManagerAdapter.getInstance(this).displayNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else {
            Intent intent = new Intent(MainActivity.RECEIVER_INTENT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
