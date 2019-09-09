package com.example.fcmchatapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fcmchatapplication.R;
import com.example.fcmchatapplication.model.FCMRegisterUsers;

import java.util.List;

public class UsersProfileAdapter extends ArrayAdapter<FCMRegisterUsers> {
    private List<FCMRegisterUsers> fcmRegisterUsersList;
    private Context context;

    private static class ViewHolder {
        TextView textView_userName;
        TextView textView_userEmail;
        TextView textView_userDate;
        TextView textView_userAddress;
    }

    public UsersProfileAdapter(Context context, List<FCMRegisterUsers> fcmRegisterUsersList) {
        super(context, R.layout.custom_users_profiles, fcmRegisterUsersList);
        this.context=context;
        this.fcmRegisterUsersList=fcmRegisterUsersList;
    }

    @Override
    public int getCount() {
        return fcmRegisterUsersList.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FCMRegisterUsers fcmRegisterUsers= fcmRegisterUsersList.get(position);
        ViewHolder fcmUsersViewHolder;
        if(convertView==null){
            fcmUsersViewHolder= new ViewHolder();
            LayoutInflater layoutInflater= LayoutInflater.from(context);
            convertView= layoutInflater.inflate(R.layout.custom_users_profiles,parent,false);
            fcmUsersViewHolder.textView_userName=(TextView)convertView.findViewById(R.id.txt_userName);
            fcmUsersViewHolder.textView_userEmail=(TextView)convertView.findViewById(R.id.txt_email);
            fcmUsersViewHolder.textView_userDate=(TextView)convertView.findViewById(R.id.txt_date);
            fcmUsersViewHolder.textView_userAddress=(TextView)convertView.findViewById(R.id.txt_address);
        }
        else {
            fcmUsersViewHolder=(ViewHolder)convertView.getTag();
        }

        fcmUsersViewHolder.textView_userEmail.setText(fcmRegisterUsers.getUserEmail());
        fcmUsersViewHolder.textView_userDate.setText("Last Seen: "+fcmRegisterUsers.getCreateDate());

        if(fcmRegisterUsers.getUserName()!=null && !TextUtils.isEmpty(fcmRegisterUsers.getUserName())){
            fcmUsersViewHolder.textView_userName.setVisibility(View.VISIBLE);
            fcmUsersViewHolder.textView_userName.setTextColor(Color.BLACK);
            fcmUsersViewHolder.textView_userName.setText(fcmRegisterUsers.getUserName());
        }else {
            fcmUsersViewHolder.textView_userName.setVisibility(View.GONE);
        }


        if(fcmRegisterUsers.getUserAddress()!=null){
            fcmUsersViewHolder.textView_userAddress.setVisibility(View.VISIBLE);
            fcmUsersViewHolder.textView_userAddress.setText(fcmRegisterUsers.getUserAddress());
        }
        else{
            fcmUsersViewHolder.textView_userAddress.setVisibility(View.GONE);
        }
        convertView.setTag(fcmUsersViewHolder);
        return convertView;
    }


}
