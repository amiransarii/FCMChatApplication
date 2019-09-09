package com.example.fcmchatapplication;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fcmchatapplication.adapter.ChartAdapter;
import com.example.fcmchatapplication.model.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersChatActivity extends BaseActivity {
    private ListView listView;
    private String loggedInUserName = "";
    private List<ChatMessage> chatMessageList;
    private DatabaseReference databaseChats;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String currentUserEmail=null;
    private String chatUserEmail=null;

    @Override
    protected int getContentView() {
        return R.layout.activity_users_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        showBackArrow();
        mAuth = FirebaseAuth.getInstance();
        databaseChats = FirebaseDatabase.getInstance().getReference("chats");
        chatMessageList = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final EditText input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list_of_messages);
        user = mAuth.getCurrentUser();
        Intent chat_intent = getIntent();

        if(chat_intent!=null){
            chatUserEmail=chat_intent.getStringExtra(getResources().getString(R.string.const_chat_user));
        }

        if (user != null) {
            currentUserEmail=user.getEmail();
            showAllOldMessages();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected=isNetworkConnected();
                if(!isConnected){
                    showErrorMessage(getResources().getText(R.string.bad_internet).toString(), Color.RED);
                }
                else{
                    if (input.getText().toString().trim().equals("")) {
                        Toast.makeText(UsersChatActivity.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                    } else {
                        String id = databaseChats.push().getKey();
                        ChatMessage chatMessage = new ChatMessage(input.getText().toString(), user.getDisplayName(),user.getUid(),currentUserEmail,chatUserEmail);
                        databaseChats.child(id).setValue(chatMessage);
                        input.setText("");
                    }
                }

            }
        });
    }
    private void showAllOldMessages() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Log.d("Main", "user id: " + loggedInUserName);
        Query query = databaseChats.child("chats").limitToLast(50);
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>().setQuery(query, ChatMessage.class).build();

        databaseChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatMessageList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = postSnapshot.getValue(ChatMessage.class);
                    if(currentUserEmail!=null && chatUserEmail!=null &&
                            chatMessage.getCurrentUserEmail()!=null &&chatMessage.getCurrentUserEmail()!=null  ){
                        if((currentUserEmail.equals(chatMessage.getCurrentUserEmail()) && chatUserEmail.equals(chatMessage.getChatUserEmail()))
                                || (chatUserEmail.equals(chatMessage.getCurrentUserEmail()) && currentUserEmail.equals(chatMessage.getChatUserEmail()))){

                            chatMessageList.add(chatMessage);
                        }
                    }
                }
                ChartAdapter chartAdapter = new ChartAdapter(UsersChatActivity.this,  chatMessageList);
                listView.setAdapter(chartAdapter);
                chartAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public String getLoggedInUserName() {
        return loggedInUserName;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
