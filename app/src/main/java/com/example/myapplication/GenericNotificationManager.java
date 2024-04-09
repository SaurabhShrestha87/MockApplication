package com.example.myapplication;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;



import java.util.Random;


public class GenericNotificationManager {
    public static NotificationManager notificationManager;


    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(context.getResources().getString(R.string.custom_notification_channel_id), context.getResources().getString(R.string.custom_notification_channel_name), importance);
            channel.setDescription(context.getResources().getString(R.string.custom_notification_channel_description));
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static void handleGenericNotification(Context context, NotificationModel notification){
        createNotificationChannel(context);
        if(notification != null) {
            PendingIntent pendingIntent;
            pendingIntent = getPendingIntentForModel(context, notification);
            if(pendingIntent != null)
            {
                createNotification(context, notification, pendingIntent,  R.mipmap.ic_launcher);
            }

        }

    }

    private static Intent getIntentForHome(Context context, NotificationModel notification) {
        if (notification != null && context != null) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra("long",notification.longitude);
            notificationIntent.putExtra("lat",notification.latitude);
            notificationIntent.putExtra("isCustomLocation",notification.isCustomLocation);
            notificationIntent.putExtra("isNewTrip",notification.isNewTrip);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            return notificationIntent;
        }
        return null;
    }










    static PendingIntent getPendingIntentForModel(Context context, NotificationModel notification) {
        Intent intent = null;
            intent=  getIntentForHome(context, notification);


        int requestCode = new Random().nextInt(1000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getActivity(context, requestCode, intent,  PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT );
        }else {
            return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }


    private static void notifyNotification(int notificationId, NotificationCompat.Builder notificationBuilder) {
        try {
            if (notificationManager != null) {
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        } catch (Exception e) {

        }
    }

    protected static void createNotification(Context context, NotificationModel notification, PendingIntent pendingIntent, int appSmallIconId) {
        int notificationId = new Random().nextInt(60000);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = "MockApplication";
        String body ="MockApplication";
        if(notification.restart) {
            body = "Restarting...";
        }
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(body))
            return;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, context.getResources().getString(R.string.custom_notification_channel_id))
                        //   .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon))
                        .setSmallIcon(appSmallIconId)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }




}

