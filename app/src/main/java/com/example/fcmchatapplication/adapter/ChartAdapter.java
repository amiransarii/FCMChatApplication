package com.example.fcmchatapplication.adapter;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fcmchatapplication.R;
import com.example.fcmchatapplication.UsersChatActivity;
import com.example.fcmchatapplication.model.ChatMessage;

import java.util.List;


public class ChartAdapter extends ArrayAdapter<ChatMessage> {
    private UsersChatActivity activity;
    List<ChatMessage> chatMessageList;

    public ChartAdapter(UsersChatActivity context, List<ChatMessage> chatMessageList) {
        super(context, R.layout.item_in_message, chatMessageList);
        this.activity = context;
        this.chatMessageList= chatMessageList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = getItem(position);
        if (chatMessage.getMessageUserId().equals(activity.getLoggedInUserName()))
            convertView = activity.getLayoutInflater().inflate(R.layout.item_out_message, parent, false);
        else
            convertView = activity.getLayoutInflater().inflate(R.layout.item_in_message, parent, false);

        TextView messageText = (TextView) convertView.findViewById(R.id.message_text);
        TextView messageUser = (TextView) convertView.findViewById(R.id.message_user);
        TextView messageTime = (TextView) convertView.findViewById(R.id.message_time);

        messageText.setText(chatMessage.getMessageText());
        messageUser.setText(chatMessage.getMessageUser());
        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", chatMessage.getMessageTime()));
        return convertView;
    }


    @Override
    public int getCount() {
        return chatMessageList.size();
    }
}
