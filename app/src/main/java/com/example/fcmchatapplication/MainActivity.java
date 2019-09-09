package com.example.fcmchatapplication;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class MainActivity extends BaseActivity  {
    private Handler mHandler;
    private  int SPLASH_TIME_OUT = 3000;
    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, final Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        mHandler= new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent_Login= new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent_Login);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

}
