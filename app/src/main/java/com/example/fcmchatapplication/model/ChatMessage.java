package com.example.fcmchatapplication.model;
import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String messageUserId;
    private long messageTime;
    private String currentUserEmail;
    private String chatUserEmail;
    public ChatMessage(String messageText, String messageUser, String messageUserId,String currentUserEmail,String chatUserEmail) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
        this.messageUserId = messageUserId;
        this.currentUserEmail=currentUserEmail;
        this.chatUserEmail=chatUserEmail;
    }

    public ChatMessage(){

    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageText='" + messageText + '\'' +
                ", messageUser='" + messageUser + '\'' +
                ", messageUserId='" + messageUserId + '\'' +
                ", messageTime=" + messageTime +
                '}';
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
    }

    public String getChatUserEmail() {
        return chatUserEmail;
    }

    public void setChatUserEmail(String chatUserEmail) {
        this.chatUserEmail = chatUserEmail;
    }


}
