package com.example.fcmchatapplication;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Optional;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsersProfileActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView_Users;
    private FirebaseAuth mAuth;
    private TextView textView_Name;
    private TextView textView_Email;
    private ImageView imageView_user;
    private String TAG=UsersProfileActivity.class.getSimpleName();
    private LocationTrack locationTrack;
    private CurrentAddressDecode currentAddressDecode;
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DatabaseReference databaseUsers;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private List<FCMRegisterUsers> fcmRegisterUsersList;
    private String userEmail;
    @Override
    protected int getContentView() {
        return R.layout.activity_users_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
        
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
         locationTrack=new LocationTrack(UsersProfileActivity.this);
         sharedPreferenceUtil= new SharedPreferenceUtil(getApplicationContext());
         fcmRegisterUsersList= new ArrayList<>();
        listView_Users.setOnItemClickListener(this);

         try {
            String mUsername =user.getDisplayName().trim();
             if(!TextUtils.isEmpty(mUsername)){
                 textView_Name.setVisibility(View.VISIBLE);
                 textView_Name.setText(mUsername);
                 String mUserprofileUrl = user.getPhotoUrl().toString();
                 Glide.with(getApplicationContext()).load(mUserprofileUrl).thumbnail(0.5f).into(imageView_user);
             }
             else{
                 imageView_user.setImageDrawable(ContextCompat.getDrawable(UsersProfileActivity.this, R.drawable.user));
                 textView_Name.setVisibility(View.GONE);
             }
              String mUserEmail=user.getEmail();
             userEmail=mUserEmail;
              textView_Email.setText(mUserEmail);
              saveUsersDB(mUsername,mUserEmail);
        } catch(Exception e) {
            Log.e(TAG, "Problem fetching user's info from mFirebaseUser, some info may be missing.");
        }

        showRegisterdUsers();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


    private void saveUsersDB(String userName,String userEmail){
        currentAddressDecode = new CurrentAddressDecode();
        boolean isConnected = ConnectivityReceiver.isConnected();
          if(isConnected){
              if (locationTrack.canGetLocation()) {
                  double longitude = locationTrack.getLongitude();
                  double latitude = locationTrack.getLatitude();
                  if(latitude!=0.0 && longitude !=0.0){
                      currentAddressDecode.getAddressFromLocation(latitude, longitude, UsersProfileActivity.this, new GeocoderHandler());
                      String currentLocation=sharedPreferenceUtil.loadPrefString(getResources().getString(R.string.pref_user_location));
                      Log.d(TAG,"User Profile Location "+currentLocation);
                      String id=sharedPreferenceUtil.loadPrefString(getResources().getString(R.string.pref_user_Id));
                      String createdDate= sdf.format(new Date());
                      if(id==null){
                          id=databaseUsers.push().getKey();
                      }
                      FCMRegisterUsers fcmRegisterUsers= new FCMRegisterUsers(id ,userName,userEmail,currentLocation,createdDate);
                      databaseUsers.child(id).setValue(fcmRegisterUsers);
                      sharedPreferenceUtil.savePrefString(getResources().getString(R.string.pref_user_Id),id);
                  }
              } else {
                  locationTrack.showSettingsAlert();
              }
              }
    }


    private void showRegisterdUsers(){
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 fcmRegisterUsersList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    FCMRegisterUsers fcmRegisterUsers=postSnapshot.getValue(FCMRegisterUsers.class);
                    fcmRegisterUsersList.add(fcmRegisterUsers);
                }
                UsersProfileAdapter usersProfileAdapter= new UsersProfileAdapter(UsersProfileActivity.this,fcmRegisterUsersList);
                listView_Users.setAdapter(usersProfileAdapter);
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
            startActivity(intent_Chat);
        }


    }
}
