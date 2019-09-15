package com.example.fcmchatapplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.fcmchatapplication.adapter.UsersProfileAdapter;
import com.example.fcmchatapplication.model.CurrentAddressDecode;
import com.example.fcmchatapplication.model.FCMRegisterUsers;
import com.example.fcmchatapplication.model.LocationTrack;
import com.example.fcmchatapplication.util.ConnectivityReceiver;
import com.example.fcmchatapplication.util.SharedPreferenceUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsersProfileActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView_Users;
    private FirebaseAuth mAuth;
    private TextView textView_Name;
    private TextView textView_Email;
    private ImageView imageView_user;
    private String TAG=UsersProfileActivity.class.getSimpleName();
    private CurrentAddressDecode currentAddressDecode;
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DatabaseReference databaseUsers;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private List<FCMRegisterUsers> fcmRegisterUsersList;
    private ProgressBar progressBar_Profile;
    List<String> subScribeList= new ArrayList<>();
    private String userName=null;
    private String userEmail=null;
    private  String mUserprofileUrl=null;
    private List<String> allSubscribeList;
    private double latitude=0.0;
    private  double longitude=0.0;
    private ValueEventListener mUsersProfileReferenceListener;

    @Override
    protected int getContentView() {
        return R.layout.activity_users_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
         super.onViewReady(savedInstanceState, intent);
         showBackArrow();
         listView_Users=(ListView)findViewById(R.id.lv_users);
         mAuth=FirebaseAuth.getInstance();
         imageView_user =(ImageView) findViewById(R.id.imageView);
         textView_Name = (TextView)findViewById(R.id.textViewName);
         textView_Email = (TextView)findViewById(R.id.textViewEmail);
         FirebaseUser user=mAuth.getCurrentUser();
         databaseUsers = FirebaseDatabase.getInstance().getReference("fcmRegisterUsers");
         sharedPreferenceUtil= new SharedPreferenceUtil(getApplicationContext());
         fcmRegisterUsersList= new ArrayList<>();
         allSubscribeList= new ArrayList<>();
         listView_Users.setOnItemClickListener(this);
         progressBar_Profile=(ProgressBar)findViewById(R.id.prog_profile);
         subScribeList.add(getResources().getString(R.string.notification_entertainment));


        try {
              userName =user.getDisplayName().trim();
            if(!TextUtils.isEmpty(userName) && userName!=null){
                textView_Name.setVisibility(View.VISIBLE);
                textView_Name.setText(userName);
                mUserprofileUrl = user.getPhotoUrl().toString();
                Glide.with(getApplicationContext()).load(mUserprofileUrl).thumbnail(0.5f).into(imageView_user);
            }
            else{
                imageView_user.setImageDrawable(ContextCompat.getDrawable(UsersProfileActivity.this, R.drawable.user));
                textView_Name.setVisibility(View.GONE);
            }
            userEmail=user.getEmail();
            textView_Email.setText(userEmail);
            sharedPreferenceUtil.savePrefString(getResources().getString(R.string.pref_user_email),userEmail);
        } catch(Exception e) {
            Log.e(TAG, "Problem fetching user's info from mFirebaseUser, some info may be missing.");
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( UsersProfileActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e(TAG,"newToken"+ newToken);
                saveUsersDB(userName,userEmail,mUserprofileUrl,newToken);

            }
        });
        showRegisterdUsers();
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private void saveUsersDB(String muserName,String muserEmail,String mProfileUrl,String deviceToken){
        List<Double> lngList=getLatlong();
         if(lngList.size()>0){
             latitude=lngList.get(0);
             longitude=lngList.get(1);
         }
         String createdDate= sdf.format(new Date());
        String id=muserEmail.replaceAll("[^a-zA-Z0-9]", "");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference=firebaseDatabase.getReference().child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()){
                     for (DataSnapshot d: dataSnapshot.getChildren()){
                         Log.d(TAG,"All Key value "+d.getKey());
                         HashMap<String,Object> result= new HashMap<>();
                         result.put("createDate",createdDate);
                         result.put("latitude",latitude);
                         result.put("longitude",longitude);
                         result.put("userName",muserName);
                         result.put("profileURL",mProfileUrl);
                         reference.child(String.valueOf(d.getKey())).updateChildren(result);  //update according to keys
                         Log.d(TAG,"Single Key value "+d.getKey());
                     }
                 }
                 else {

                     FCMRegisterUsers fcmRegisterUsers=new FCMRegisterUsers(id, muserName, muserEmail,
                             deviceToken,latitude,longitude, createdDate, mProfileUrl,subScribeList);
                     databaseUsers.child(id).setValue(fcmRegisterUsers);
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG,"Error updating DB"+databaseError.getMessage());
             }
         });

    }
    private void showRegisterdUsers(){
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 fcmRegisterUsersList.clear();
                 allSubscribeList.clear();
                 progressBar_Profile.setVisibility(View.VISIBLE);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FCMRegisterUsers fcmRegisterUsers=postSnapshot.getValue(FCMRegisterUsers.class);
                    fcmRegisterUsersList.add(fcmRegisterUsers);
                    allSubscribeList.addAll(fcmRegisterUsers.getSubscribeList());
                }
                UsersProfileAdapter usersProfileAdapter= new UsersProfileAdapter(UsersProfileActivity.this,fcmRegisterUsersList);
                listView_Users.setAdapter(usersProfileAdapter);
                progressBar_Profile.setVisibility(View.GONE);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
       FCMRegisterUsers fcmRegisterUsers=fcmRegisterUsersList.get(position);
       String emailID= fcmRegisterUsers.getUserEmail();
       if(emailID.equals(userEmail)){
           Toast.makeText(getApplicationContext(),"This is Your Account please choose Different User",Toast.LENGTH_SHORT).show();
    }
        else {
            Intent intent_Chat= new Intent(UsersProfileActivity.this,UsersChatActivity.class);
            intent_Chat.putExtra(getResources().getString(R.string.const_chat_user),emailID);
            intent_Chat.putExtra(getResources().getString(R.string.current_user_latitude),latitude);
            intent_Chat.putExtra(getResources().getString(R.string.current_user_longitude),longitude);
            intent_Chat.putExtra(getResources().getString(R.string.chat_user_latitude),fcmRegisterUsers.getLatitude());
            intent_Chat.putExtra(getResources().getString(R.string.chat_user_longitude),fcmRegisterUsers.getLongitude());
            startActivity(intent_Chat);


        }


    }
}
