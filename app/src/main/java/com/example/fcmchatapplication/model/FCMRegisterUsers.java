package com.example.fcmchatapplication.model;

public class FCMRegisterUsers {
    public String getFcmUserId() {
        return fcmUserId;
    }

    public void setFcmUserId(String fcmUserId) {
        this.fcmUserId = fcmUserId;
    }

    private String fcmUserId;
    private String userName;
    private String userEmail;
    private String userAddress;
    private String createDate;

    public FCMRegisterUsers(){

    }


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }




    public FCMRegisterUsers(String fcmUserId,String userName, String userEmail, String userAddress,String createDate) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.createDate=createDate;
        this.fcmUserId=fcmUserId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }


}
