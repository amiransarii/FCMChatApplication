package com.example.fcmchatapplication;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fcmchatapplication.util.ConnectivityReceiver;
import com.example.fcmchatapplication.util.FCMChatApplication;
import com.example.fcmchatapplication.util.SharedPreferenceUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    protected abstract int getContentView();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;
    private String TAG=BaseActivity.class.getSimpleName();
    private SharedPreferenceUtil sharedPreferenceUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        onViewReady(savedInstanceState, getIntent());
        //get firebase auth instance
        auth=FirebaseAuth.getInstance();
        sharedPreferenceUtil= new SharedPreferenceUtil(this);
        final  FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
         authStateListener= new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
             }
         };
    }

    @CallSuper
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        //To be used by child activities
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    protected boolean isNetworkConnected(){
        boolean isConnected=ConnectivityReceiver.isConnected();
        return isConnected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FCMChatApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                 Intent intent_Setting= new Intent(BaseActivity.this,SettingActivity.class);
                 startActivity(intent_Setting);
                 return  true;
            case R.id.menu_sign_out:
                logOutUser();
                return true;
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    protected void showErrorMessage(String message,int color){
        Snackbar snackbar= Snackbar.make(findViewById(R.id.lnr_sackbar),message,Snackbar.LENGTH_LONG);
        View sbView =snackbar.getView();
        TextView textView=(TextView)sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }


    protected void showBackArrow() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
    }

     protected void logOutUser(){
         auth.signOut();
         Toast.makeText(getApplicationContext(),getResources().getText(R.string.success_logout).toString(),Toast.LENGTH_LONG).show();
         Intent intent_Login= new Intent(BaseActivity.this,LoginActivity.class);
         intent_Login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
         startActivity(intent_Login);
         finish();

     }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    protected  boolean checkAndRequestPermissions(){
        int cameraPermission= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writePermssion=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionRecordAudio=ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded= new ArrayList<>();
        if(cameraPermission!= PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if(writePermssion!=PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(permissionLocation!=PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionRecordAudio!=PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

         if(!listPermissionsNeeded.isEmpty()){
             ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
             return false;
         }
      return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"Permission callback called-------");

        switch (requestCode){
            case REQUEST_ID_MULTIPLE_PERMISSIONS:{
                Map<String,Integer> perms= new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO,PackageManager.PERMISSION_GRANTED);

                if(grantResults.length>0){
                    for (int i=0;i<permissions.length;i++){
                        perms.put(permissions[i],grantResults[i]);
                        // Check for both permissions
                        if(perms.get(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){

                            Log.d(TAG, "sms & location services permission granted");
                            // process the normal flow
                            Intent intent_UserProfile=new Intent(BaseActivity.this,UsersProfileActivity.class);
                            intent_UserProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_UserProfile);
                            finish();
                        }
                        else{
                            Log.d(TAG, "Some permissions are not granted ask again ");

                            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)){
                                showDialogOK("Service Permissions are required for this app",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkAndRequestPermissions();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        finish();
                                                        break;
                                                }
                                            }
                                        });
                            }
                            else{
                                explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            }
                        }
                    }
                }

            }


        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.exampledemo.parsaniahardik.marshmallowpermission")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }


    protected class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString(getResources().getText(R.string.loc_address).toString());
                    break;
                default:
                    locationAddress = null;
            }
            Log.d(TAG,"Base Activity Address "+locationAddress);
            sharedPreferenceUtil.savePrefString(getResources().getString(R.string.pref_user_location),locationAddress);
        }
    }


}
