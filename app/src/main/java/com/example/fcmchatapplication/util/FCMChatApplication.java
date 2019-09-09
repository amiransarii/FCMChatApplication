package com.example.fcmchatapplication.util;

import android.app.Application;

public class FCMChatApplication extends Application {
    private static FCMChatApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    public static synchronized FCMChatApplication getInstance(){
        return mInstance;
    }
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener=listener;
    }

}
