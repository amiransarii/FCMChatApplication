package com.example.fcmchatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fcmchatapplication.adapter.UsersProfileAdapter;
import com.example.fcmchatapplication.model.FCMRegisterUsers;
import com.example.fcmchatapplication.util.SharedPreferenceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView_Setting;
    private ArrayAdapter<String> arrayAdapter;
    private String[] settingItems=new String[]{};
    private FirebaseUser mFirebaseUser;
    private CheckBox checkBox_Entertainment;
    private CheckBox checkBox_Education;
    private CheckBox checkBox_Career;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private String email;
    private  List<String> subscribeList= new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private Set<String> setSubscribe = new HashSet<String>();



    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        showBackArrow();
        sharedPreferenceUtil= new SharedPreferenceUtil(this);
        listView_Setting=(ListView)findViewById(R.id.lv_setting);
        settingItems=getResources().getStringArray(R.array.setting_array);
        arrayAdapter= new ArrayAdapter<String>(this, R.layout.list_view_setting, R.id.textView, settingItems);
        checkBox_Entertainment=(CheckBox)findViewById(R.id.chkEntertainment);
        checkBox_Education=(CheckBox)findViewById(R.id.chkEduction);
        checkBox_Career=(CheckBox)findViewById(R.id.chkCareer);
        checkBox_Entertainment.setOnClickListener(this);
        checkBox_Education.setOnClickListener(this);
        checkBox_Career.setOnClickListener(this);

        listView_Setting.setAdapter(arrayAdapter);
        listView_Setting.setOnItemClickListener(this);
        mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        email=sharedPreferenceUtil.loadPrefString(getResources().getString(R.string.pref_user_email)).replaceAll("[^a-zA-Z0-9]", "");

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference().child("fcmRegisterUsers");
        Set<String> allSubscribeList=sharedPreferenceUtil.loadStringSet(getResources().getString(R.string.pref_subscribe_list));

         if(allSubscribeList !=null && allSubscribeList.size()>0){
             showSubscribeList(allSubscribeList);

         }

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
         switch (position){
             case 0:
                 updateEmailAddress();
                 break;
             case 1:
                 updatePassword();
                 break;
             case 2:
                 deleteAccount();
                 break;
         }

    }

    private void updateEmailAddress() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final  View dialogView= inflater.inflate(R.layout.layout_update_email,null);
        builder.setView(dialogView);
        final EditText editText_Email=(EditText)dialogView.findViewById(R.id.updateEmail_edit);
        final Button button_UpdateEmail=(Button)dialogView.findViewById(R.id.updateEmail_btn);
        final ProgressBar updateEmail_ProgressBar=(ProgressBar)dialogView.findViewById(R.id.updateEmail_progressBar);
        final TextView textView_UpdateEmailCancel=(TextView) dialogView.findViewById(R.id.updateEmail_cancel);
        final AlertDialog dialog = builder.create();
        button_UpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail=editText_Email.getText().toString().trim();

                if(TextUtils.isEmpty(newEmail)){
                    showErrorMessage(getResources().getText(R.string.email).toString(), Color.RED);
                    return;
                }
                updateEmail_ProgressBar.setVisibility(View.VISIBLE);
                mFirebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            showErrorMessage(getResources().getText(R.string.fail_updateEmail).toString()+" "+task.getException(),Color.RED);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.update_email_success).toString(),Toast.LENGTH_LONG).show();
                        }
                        updateEmail_ProgressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    }

                });

            }
        });
        dialog.show();

        textView_UpdateEmailCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    private void  updatePassword() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final  View dialogView= inflater.inflate(R.layout.layout_update_password,null);
        builder.setView(dialogView);
        final EditText editText_Pass=(EditText)dialogView.findViewById(R.id.updatepass_edit);
        final Button button_UpdatePass=(Button)dialogView.findViewById(R.id.updatePass_btn);
        final ProgressBar updatePass_ProgressBar=(ProgressBar)dialogView.findViewById(R.id.updatePass_progressBar);
        final TextView textView_UpdatePassCancel=(TextView) dialogView.findViewById(R.id.updatePass_cancel);
        final AlertDialog dialog = builder.create();
        button_UpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPass=editText_Pass.getText().toString().trim();

                if(TextUtils.isEmpty(newPass)){
                    showErrorMessage(getResources().getText(R.string.email).toString(), Color.RED);
                    return;
                }
                else if(newPass.length()<6){
                    showErrorMessage(getResources().getText(R.string.pass_short).toString(),Color.RED);
                }
                updatePass_ProgressBar.setVisibility(View.VISIBLE);
                mFirebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            showErrorMessage(getResources().getText(R.string.fail_update_pass).toString()+" "+task.getException(),Color.RED);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),getResources().getText(R.string.success_update_pass).toString(),Toast.LENGTH_LONG).show();
                        }
                        updatePass_ProgressBar.setVisibility(View.GONE);
                    }

                });

            }
        });
        dialog.show();

        textView_UpdatePassCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


    }

    private void  deleteAccount() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_delete_account, null);
        builder.setView(dialogView);
        final Button button_DeletAcc = (Button) dialogView.findViewById(R.id.delete_btn);
        final ProgressBar delete_ProgressBar = (ProgressBar) dialogView.findViewById(R.id.delete_progressBar);
        final TextView textView_DeleteCancel = (TextView) dialogView.findViewById(R.id.delete_cancel);
        final AlertDialog dialog = builder.create();
       button_DeletAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_ProgressBar.setVisibility(View.VISIBLE);
                if(mFirebaseUser!=null){
                       mFirebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(!task.isSuccessful()){
                                   showErrorMessage(getResources().getText(R.string.fail_delete).toString()+" "+task.getException(),Color.RED);
                               }
                               else{
                                   Toast.makeText(getApplicationContext(),getResources().getText(R.string.success_delete).toString(),Toast.LENGTH_LONG).show();
                               }
                              delete_ProgressBar.setVisibility(View.GONE);
                               dialog.dismiss();
                           }
                       });
                   }
            }
        });
        dialog.show();

        textView_DeleteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });




    }

    @Override
    public void onClick(View view) {
        String str="";
        boolean checked = ((CheckBox) view).isChecked();
         switch (view.getId()){
             case R.id.chkEntertainment:
                 str=getResources().getString(R.string.notification_entertainment);
                  if(checked){
                      subscribeList.add(str);
                      FirebaseMessaging.getInstance().subscribeToTopic(str);
                  }else {
                       if(subscribeList.contains(str)){
                           subscribeList.remove(str);
                           FirebaseMessaging.getInstance().unsubscribeFromTopic(str);
                       }
                  }
                 break;
             case R.id.chkEduction:
                 str=getResources().getString(R.string.notification_eduction);
                 if(checked){
                     subscribeList.add(str);
                     FirebaseMessaging.getInstance().subscribeToTopic(str);
                 }else {
                     if(subscribeList.contains(str)){
                         subscribeList.remove(str);
                         FirebaseMessaging.getInstance().unsubscribeFromTopic(str);
                     }
                 }
                 break;
             case R.id.chkCareer:
                 str=getResources().getString(R.string.notification_career);
                 if(checked){
                     subscribeList.add(str);
                     FirebaseMessaging.getInstance().subscribeToTopic(str);
                 }else {
                     if(subscribeList.contains(str)){
                         subscribeList.remove(str);
                         FirebaseMessaging.getInstance().unsubscribeFromTopic(str);
                     }
                 }
                 break;
         }
                if( checked){
                    Toast.makeText(getApplicationContext(),"You have successfully Subscribed "+str,Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"You have successfully UnSubscribed "+str,Toast.LENGTH_SHORT).show();
                }

         //update the value
        reference.child(email).child("subscribeList").setValue(subscribeList);

         setSubscribe.addAll(subscribeList);
        sharedPreferenceUtil.saveStringSet(getResources().getString(R.string.pref_subscribe_list),setSubscribe);
    }

    //without changing real time
    private void showSubscribeList(Set<String> allSubList) {
        Toast.makeText(getApplicationContext(), "All SubList" + allSubList, Toast.LENGTH_SHORT).show();

        for (String sub : allSubList) {
            if (sub.equals(getResources().getString(R.string.notification_entertainment))) {
                checkBox_Entertainment.setChecked(true);
            }

            if (sub.equals(getResources().getString(R.string.notification_eduction))) {
                checkBox_Education.setChecked(true);
            }

            if (sub.equals(getResources().getString(R.string.notification_career))) {
                checkBox_Career.setChecked(true);
            }
        }
    }
       /* allSubList.forEach(sub->{
                       if(sub.equals(getResources().getString(R.string.notification_entertainment))){
                           checkBox_Entertainment.setChecked(true);
                       }

                       if(sub.equals(getResources().getString(R.string.notification_eduction))){
                           checkBox_Education.setChecked(true);
                       }

                       if (sub.equals(getResources().getString(R.string.notification_career))){
                           checkBox_Career.setChecked(true);
                       }

                  });
                }
*/



}
