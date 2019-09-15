package com.example.fcmchatapplication.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.Html;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.example.fcmchatapplication.R;
import com.example.fcmchatapplication.UsersChatActivity;
import com.example.fcmchatapplication.UsersProfileActivity;
import com.example.fcmchatapplication.model.NotificationData;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NotificationUtils {
    private static final String TAG=NotificationUtils.class.getSimpleName();
    private Context context;
    private static final int FCM_NOTIFICATION_ID = 200;
    private static final String FCM_PUSH_NOTIFICATION = "pushNotification";
    private static final String FCM_CHANNEL_ID = "FCMCHATID";
    private static final String FCM_CHANNEL_NAME = "FCMCHATNAME";
    private static final String FCM_URL = "fcmurl";
    private static final String FCM_ACTIVITY = "fcmactivity";
    private Map<String,Class>activityMap= new HashMap<>();

    public NotificationUtils(Context context){
        this.context=context;
        activityMap.put("UsersProfileActivity", UsersProfileActivity.class);
        activityMap.put("UsersChatActivity",UsersChatActivity.class);
    }

        public void displayFcmNotification(NotificationData notificationData,Intent intent){
         String message= notificationData.getMessage();
         String title= notificationData.getTitle();
         String iconUrl=notificationData.getIconUrl();
         String action=notificationData.getAction();
         String destination=notificationData.getActionDestination();
         Bitmap iconBitMap=null;
         if(iconBitMap!=null){
             iconBitMap=getBitmapFromURL(iconUrl);
         }
         final int icon=R.mipmap.ic_launcher;
         PendingIntent pendingIntent;

         if(FCM_URL.equals(action)){
             Intent notificationIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(destination));
             pendingIntent=PendingIntent.getActivity(context,0,notificationIntent,0);
         }
         else if(FCM_ACTIVITY.equals(action) && activityMap.containsKey(destination)){
             intent= new Intent(context,activityMap.get(destination));
             pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

         }
         else {
         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
         pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
         }

         final NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(context,FCM_CHANNEL_ID);
            Notification notification;

            if(iconBitMap==null){
                //When Inbox Style is applied, user can expand the notification
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.addLine(message);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        //.setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                        .setContentText(message)
                        .build();
            }
            else {
                //If Bitmap is created from URL, show big icon
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
                bigPictureStyle.bigPicture(iconBitMap);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(pendingIntent)
                        .setStyle(bigPictureStyle)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                       // .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                        .setContentText(message)
                        .build();
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //All notifications should go through NotificationChannel on Android 26 & above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(FCM_CHANNEL_ID, FCM_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);

            }
            notificationManager.notify(FCM_NOTIFICATION_ID, notification);
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
            wl.acquire(15000);

        }




    private Bitmap getBitmapFromURL(String iconUrl) {
        try {
            URL url = new URL(iconUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Playing notification sound
     */
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
