package com.example.fcmchatapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.fcmchatapplication.model.NotificationData;
import com.example.fcmchatapplication.util.NotificationUtils;

public class FCMBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

         if(intent.getAction().equals("com.example.fcmchatapplication.Chat")){
             String title = intent.getStringExtra("chatTitle");
             String msg= intent.getStringExtra("chatMessage");
             NotificationData notificationData= new NotificationData();
             notificationData.setTitle(title);
             notificationData.setMessage(msg);
             NotificationUtils notificationUtils= new NotificationUtils(context);
             notificationUtils.displayFcmNotification(notificationData,new Intent());
         }




    }
}
