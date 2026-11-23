package com.example.itinerarysync.UI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "itinerary_sync_channel";
    public static final String CHANNEL_NAME = "ItinerarySync Alerts";

    @Override
    public void onReceive(Context context, Intent intent) {
        String rawTitle = intent.getStringExtra("title");
        String rawMessage = intent.getStringExtra("message");
        final String title = (rawTitle == null || rawTitle.trim().isEmpty()) ? "ItinerarySync Alert" : rawTitle;
        final String message = (rawMessage == null || rawMessage.trim().isEmpty()) ? "You have an itinerary alert scheduled for today!" : rawMessage;
        int notificationId = intent.getIntExtra("notificationId", (int) System.currentTimeMillis());

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Alerts for scheduled vacations and excursions");
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (notificationManager != null) {
            try {
                notificationManager.notify(notificationId, builder.build());
            } catch (SecurityException ignored) {
            }
        }

        Toast.makeText(context, title + ": " + message, Toast.LENGTH_LONG).show();
    }
}
